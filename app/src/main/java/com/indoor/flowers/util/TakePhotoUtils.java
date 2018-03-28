package com.indoor.flowers.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import com.evgeniysharafan.utils.ExifHelper;
import com.evgeniysharafan.utils.IO;
import com.evgeniysharafan.utils.L;
import com.evgeniysharafan.utils.PrefUtils;
import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.BuildConfig;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TakePhotoUtils {

    /**
     * Enable the Photo button when you get one of these callbacks.
     */
    public interface OnPhotoTakenListener {
        void onPhotoTaken(File photo);

        void onPhotoError();
    }

    private static final int MAX_IMAGE_SIZE = 1920;

    private static final String PHOTOS_CACHE_DIR = "images_cache";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final String VIDEO_UNSPECIFIED = "video/*";

    private static final String EXTRA_DATA = "data";

    public static final int REQUEST_CODE_SYSTEM_CHOOSER = 141;
    public static final int REQUEST_CODE_CAMERA = 142;
    public static final int REQUEST_CODE_GALLERY = 143;

    private static final TakePhotoUtils instance = new TakePhotoUtils();
    private File photoFile;
    private OnPhotoTakenListener photoTakenListener;
    // we use this file if we get the result between onStop() and onStart(), in this case listener is null.
    private File completedFile;
    // we use this flag if we get an error between onStop() and onStart(), in this case listener is null.
    private boolean hasError;
    private boolean isInProgress;
    private boolean isCancelled;
    private TargetImpl picassoTarget;
    private int maxImageSize = MAX_IMAGE_SIZE;

    private TakePhotoUtils() {
    }

    public Bitmap checkImageSize(Bitmap bitmap) {
        BitmapFactory.Options options = validateImageSize(bitmap.getWidth(), bitmap.getHeight());
        if (bitmap.getHeight() != options.outHeight || bitmap.getWidth() > options.outWidth) {
            bitmap = Bitmap.createScaledBitmap(bitmap, options.outWidth, options.outHeight, false);
        }

        return bitmap;
    }

    public static TakePhotoUtils getInstance() {
        return instance;
    }

    public void setPhotoTakenListener(OnPhotoTakenListener listener) {
        photoTakenListener = listener;
    }

    public void setMaxImageSize(int maxImageSize) {
        this.maxImageSize = maxImageSize;
    }

    public void showSystemChooser(Activity activity) {
        if (createPhotoFile()) {
            Intent intent = getSystemChooserIntent(activity.getPackageManager());
            activity.startActivityForResult(intent, REQUEST_CODE_SYSTEM_CHOOSER);
        }
    }

    public void showSystemChooser(Fragment fragment) {
        if (createPhotoFile()) {
            Intent intent = getSystemChooserIntent(fragment.getActivity().getPackageManager());
            fragment.startActivityForResult(intent, REQUEST_CODE_SYSTEM_CHOOSER);
        }
    }

    public void setPhotoTakenListenerIfNeeded(OnPhotoTakenListener listener) {
        if (listener != null && !isCancelled) {
            if (isInProgress()) {
                photoTakenListener = listener;
            } else if (completedFile != null) {
                photoTakenListener = listener;
                fireSuccess(completedFile);
            } else if (hasError) {
                photoTakenListener = listener;
                fireError();
            }
        } else {
            photoTakenListener = null;
        }
    }

    public boolean isInProgress() {
        return isInProgress;
    }

    public File getNewPhotoFile() {
        createPhotoFile();
        return photoFile;
    }

    public File getTempFile(String name) {
        File albumDir = getAlbumDir();
        return new File(albumDir, name);
    }

    public File getTempFile(String name, Bitmap bitmap) {
        File albumDir = getAlbumDir();
        File file = new File(albumDir, name);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (IOException ex) {
            L.w(ex);
        } finally {
            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException ignore) {
                }
        }

        return file;
    }

    public File getPhotoFile() {
        if (photoFile == null) {
            createPhotoFile();
        }

        return photoFile;
    }

    public void cancelCurrentProcessingIfNeeded() {
        if (isInProgress()) {
            isCancelled = true;
            isInProgress = false;
        }
    }

    public String addBitmapToCache(Bitmap bitmap, String fileName) {
        File tempFile = getTempFile(fileName);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (IOException ex) {
            L.w(ex);
        } finally {
            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException ignore) {
                }
        }
        return tempFile.getAbsolutePath();
    }

    public void clearImagesCache() {
        File imagesCache = getAlbumDir();
        if (imagesCache.exists()) {
            File[] images = imagesCache.listFiles();

            if (images == null) {
                return;
            }

            for (File image : images) {
                image.delete();
            }

            imagesCache.delete();
        }
    }

    public void removeImageFromCache(File file) {
        if (file != null) {
            file.delete();
        }
    }

    private boolean createPhotoFile() {
        photoFile = null;
        File albumDir = getAlbumDir();
        if (albumDir != null) {
            String imageFileName = JPEG_FILE_PREFIX + Calendar.getInstance().getTimeInMillis();
            photoFile = new File(albumDir, imageFileName + JPEG_FILE_SUFFIX);
            PrefUtils.put(JPEG_FILE_PREFIX, photoFile.getAbsolutePath());
        } else {
            L.e("Storage is unmounted");
        }

        return photoFile != null;
    }

    public File getAlbumDir() {
        File storageDir = null;
        if (IO.isMediaStorageMounted()) {
            storageDir = new File(Utils.getApp().getExternalFilesDir(null), PHOTOS_CACHE_DIR);
            storageDir.mkdirs();
        } else {
            L.e("External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private Intent getSystemChooserIntent(PackageManager packageManager) {
        // Camera
        List<Intent> cameraIntents = new ArrayList<>();
        if (PermissionUtil.hasAllPermissions(PermissionUtil.CAMERA_PERMISSIONS)) {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            List<ResolveInfo> listCameraActivities = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCameraActivities) {
                Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(res.activityInfo.packageName);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, this.generateTempPhotoUri());

                cameraIntents.add(intent);
            }
        }

        // Gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        galleryIntent.setType(IMAGE_UNSPECIFIED);

        Intent chooserIntent = Intent.createChooser(galleryIntent, null);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(
                new Parcelable[cameraIntents.size()]));
        return chooserIntent;
    }

    public void takePhoto(Fragment fragment) {
        createPhotoFile();
        if (photoFile != null) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.generateTempPhotoUri());
            fragment.startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
        }
    }

    public void takePhoto(Activity activity) {
        createPhotoFile();
        if (photoFile != null) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.generateTempPhotoUri());
            activity.startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
        }
    }

    public void launchGallery(Fragment fragment) {
        createPhotoFile();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType(MimeTypes.MIME_TYPE_IMAGE_ANY);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.generateTempPhotoUri());
        fragment.startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    public void launchGallery(Activity activity) {
        createPhotoFile();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType(MimeTypes.MIME_TYPE_IMAGE_ANY);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.generateTempPhotoUri());
        activity.startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    public boolean isPhotoRequestOk(int requestCode, int resultCode) {
        return (requestCode == REQUEST_CODE_SYSTEM_CHOOSER || requestCode == REQUEST_CODE_CAMERA
                || requestCode == REQUEST_CODE_GALLERY)
                && resultCode == Activity.RESULT_OK;
    }

    /**
     * Call isPhotoRequestOk() before, if true disable the Photo button until you get OnPhotoTakenListener callback
     */
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent data, final OnPhotoTakenListener listener) {
        if (!isPhotoRequestOk(requestCode, resultCode)) {
            throw new IllegalStateException("isPhotoRequestOk() should be called before onActivityResult");
        }

        photoTakenListener = listener;
        isInProgress = true;
        isCancelled = false;
        completedFile = null;
        hasError = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPhoto(requestCode, resultCode, data);
            }
        }).start();
    }

    private Uri generateTempPhotoUri() {
        Uri result = null;
        try {
            result = FileProvider.getUriForFile(Utils.getApp(), BuildConfig.FILE_PROVIDER_AUTHORITY, photoFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // gets the image in background thread
    private void getPhoto(int requestCode, int resultCode, Intent data) {
        // system chooser
        if (requestCode == REQUEST_CODE_SYSTEM_CHOOSER) {
            boolean isCamera;

            if (data == null || data.getData() == null) {
                isCamera = true;
            } else {
                final String action = data.getAction();
                isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }

            requestCode = isCamera ? REQUEST_CODE_CAMERA : REQUEST_CODE_GALLERY;
        }

        if (photoFile == null) {
            createPhotoFile();
        }

        // we need to create a copy because user can press the Photo button again when current process is
        // in progress (if developer forgot to disable this button until current processing is finished).
        File currentFile = new File(photoFile.getPath());
        if (data != null && data.getData() != null) {
            try {
                if (isMediaStorage(data.getData()) || isFile(data.getData())) {
                    String path = getPathFromContentUri(data.getData());
                    if (path != null) {
                        if (!path.equals(currentFile.getPath())) {
                            IO.copyFile(new File(path), currentFile);
                        }

                        rotateIfNeeded(currentFile);
                    } else {
                        fireError();
                    }
                } else {
                    getImageFromExternalContentProvider(data.getData(), currentFile);
                }
            } catch (Exception e) {
                L.e(e);
                fireError();
            }
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            if (data != null && data.hasExtra(EXTRA_DATA)) {
                Bitmap bitmap = data.getParcelableExtra(EXTRA_DATA);
                saveBitmapToPhotoFile(bitmap, currentFile);
            }

            rotateIfNeeded(currentFile);
        } else {
            fireError();
        }
    }

    private boolean isFile(Uri uri) {
        return ContentResolver.SCHEME_FILE.equals(uri.getScheme());
    }

    private boolean isMediaStorage(Uri uri) {
        return ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) &&
                MediaStore.AUTHORITY.equals(uri.getAuthority());
    }

    // Convert the image URI to the direct file system path of the image file
    private String getPathFromContentUri(Uri contentUri) {
        String path = null;

        if (isMediaStorage(contentUri)) {
            Cursor cursor = null;
            try {
                cursor = Utils.getApp().getContentResolver().query(contentUri,
                        new String[]{MediaStore.Images.Media.DATA}, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                }
            } catch (Exception e) {
                L.e(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (isFile(contentUri)) {
            path = contentUri.getPath();
        }

        return path;
    }

    private void getImageFromExternalContentProvider(Uri uri, File file) {
        InputStream is = null;
        OutputStream outStream = null;

        if (uri.getAuthority() != null) {
            try {
                is = Utils.getApp().getContentResolver().openInputStream(uri);
                outStream = new FileOutputStream(file);
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }

                rotateIfNeeded(file);
            } catch (Exception ex) {
                L.e(ex);
                if (photoTakenListener != null) {
                    Utils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            photoTakenListener.onPhotoError();
                        }
                    });
                }
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }

                    if (outStream != null) {
                        outStream.close();
                    }
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private void rotateIfNeeded(final File file) {
        int orientation = getOrientationFromContentUri(Uri.fromFile(file));
        if ((orientation % 360) != 0) {
            Utils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // we need to have a strong reference to the target
                    picassoTarget = new TargetImpl(file);
                    Picasso.with(Utils.getApp()).load(file).into(picassoTarget);
                }
            });
        } else {
            fireSuccess(file);
        }
    }

    private void saveBitmapToPhotoFile(Bitmap data, File file) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            data.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (IOException ex) {
            L.w(ex);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private void fireSuccess(final File file) {
        checkImageSize(file);
        Utils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (photoTakenListener != null && !isCancelled) {
                    photoTakenListener.onPhotoTaken(file);
                }

                completedFile = (photoTakenListener == null && !isCancelled) ? new File(file.getPath()) : null;
                setPhotoTakenListenerIfNeeded(null);
                hasError = false;
                isInProgress = false;

                maxImageSize = MAX_IMAGE_SIZE;
            }
        });
    }

    public void checkImageSize(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        BitmapFactory.Options checked = validateImageSize(options.outWidth, options.outHeight);
        if (options.outHeight != checked.outHeight || options.outWidth != checked.outWidth) {
            checked.inSampleSize = (int) ((double) options.outHeight / (double) checked.outHeight);
            int scaledWidth = checked.outWidth;
            int scaledHeight = checked.outHeight;
            Bitmap decoded = BitmapFactory.decodeFile(file.getPath(), checked);
            Bitmap scaled = Bitmap.createScaledBitmap(decoded, scaledWidth, scaledHeight, false);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                scaled.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            } catch (IOException ex) {
                L.e(ex);
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
            if (decoded != null) {
                decoded.recycle();
            }
            if (scaled != null) {
                scaled.recycle();
            }
        }
    }

    private BitmapFactory.Options validateImageSize(int originalWidth, int originalHeight) {
        BitmapFactory.Options result = new BitmapFactory.Options();
        result.outWidth = originalWidth;
        result.outHeight = originalHeight;
        if (result.outHeight > maxImageSize || result.outWidth > maxImageSize) {
            int height = 0;
            int width = 0;
            if (result.outHeight > result.outWidth) {
                height = maxImageSize;
                width = (result.outWidth * maxImageSize) / result.outHeight;
            } else {
                width = maxImageSize;
                height = (result.outHeight * maxImageSize) / result.outWidth;
            }

            result.inScaled = true;


            result.outHeight = height;
            result.outWidth = width;
        }

        return result;
    }

    private void fireError() {
        if (photoFile != null) {
            photoFile.delete();
            photoFile = null;
        }

        maxImageSize = MAX_IMAGE_SIZE;
        Utils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (photoTakenListener != null && !isCancelled) {
                    photoTakenListener.onPhotoError();
                }

                hasError = photoTakenListener == null && !isCancelled;
                setPhotoTakenListenerIfNeeded(null);
                completedFile = null;
                isInProgress = false;
            }
        });
    }

    private int getOrientationFromContentUri(Uri contentUri) {
        int orientation = 0;

        if (isMediaStorage(contentUri)) {
            Cursor cursor = null;
            try {
                cursor = Utils.getApp().getContentResolver().query(contentUri,
                        new String[]{MediaStore.Images.Media.ORIENTATION}, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    orientation = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION));
                }
            } catch (Exception e) {
                L.e(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (isFile(contentUri)) {
            orientation = ExifHelper.getExifOrientation(contentUri.getPath());
        }

        return orientation;
    }

    private void saveRotatedBitmap(File file, Bitmap rotatedBitmap) throws IOException {
        FileOutputStream out = null;
        try {
            ExifHelper exifHelper = new ExifHelper();
            exifHelper.readExifData(file.getPath());

            out = new FileOutputStream(file);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            exifHelper.setOrientation(String.valueOf(ExifInterface.ORIENTATION_NORMAL));
            exifHelper.setImageWidth(String.valueOf(rotatedBitmap.getWidth()));
            exifHelper.setImageLength(String.valueOf(rotatedBitmap.getHeight()));
            exifHelper.writeExifData(file.getPath());
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                L.e(e);
            }
        }
    }

    private class TargetImpl implements Target {

        private final File file;

        public TargetImpl(File file) {
            this.file = file;
        }

        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        saveRotatedBitmap(file, bitmap);
                        fireSuccess(file);
                    } catch (IOException e) {
                        L.e(e);
                        fireError();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            L.e("onBitmapFailed");
            fireError();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }

}