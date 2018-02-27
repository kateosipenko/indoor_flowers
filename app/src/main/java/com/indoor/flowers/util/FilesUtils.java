package com.indoor.flowers.util;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.evgeniysharafan.utils.IO;
import com.evgeniysharafan.utils.L;
import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.model.CalendarFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilesUtils {

    private static final String[] RESERVED_CHARS = new String[]{"|", "\\", "?", "*", "<", "\"", ":", ">", "+", "[", "]", "/", "'", ".", ","};

    private static final String PATTERN_FILE_NAME_COPY_NUMBER = "\\([0-9]+\\)";
    private static final Pattern NAME_COPY_PATTERN = Pattern.compile(PATTERN_FILE_NAME_COPY_NUMBER);
    private static final String CACHE_DIR = "data";

    private static final String FILE_NAME_FORMAT = "%1$tm-%1$td-%1$tY_%1$tH_%1$tM_%1$tS.png";

    private static final String FILE_CALENDAR_FILTER = "calendar_filter.dat";

    public static void saveCalendarFilter(CalendarFilter filter) {
        writeData(filter, FILE_CALENDAR_FILTER);
    }

    public static CalendarFilter getCalendarFilter() {
        CalendarFilter result = readData(FILE_CALENDAR_FILTER);
        if (result == null) {
            result = new CalendarFilter();
        }

        return result;
    }

    public static String getRandomFileName() {
        return String.format(new Locale("en-US"), FILE_NAME_FORMAT, Calendar.getInstance());
    }

    public static void addBitmapToTarget(@DataPart String dataPart, Long targetId, Bitmap bitmap) {
        File dataFolder = getFolderForTarget(dataPart, targetId);
        saveBitmapToFile(dataFolder, getRandomFileName(), bitmap);
    }

    @Nullable
    public static String saveBitmapToFile(String resultFileName, Bitmap bitmap) {
        File folder = getCacheDir();
        return saveBitmapToFile(folder, resultFileName, bitmap);
    }

    public static void deleteDataForTarget(@DataPart String dataPart, Long targetId) {
        File dir = getFolderForTarget(dataPart, targetId);
        if (dir != null && dir.exists()) {
            for (File file : dir.listFiles()) {
                deleteFile(file);
            }
        }
    }

    public static void deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        deleteFile(new File(filePath));
    }

    public static String copyFileForTarget(String path, @DataPart String part, long id) {
        File partFolder = getFolderForTarget(part, id);
        File result = null;
        if (partFolder != null) {
            result = checkFileNameAndCreate(partFolder, getRandomFileName(), false);
            File fileToCopy = new File(path);
            if (fileToCopy.exists()) {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                byte[] buf = new byte[1024];
                int len;
                try {
                    inputStream = new FileInputStream(fileToCopy);
                    outputStream = new FileOutputStream(result);
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }

                    outputStream.flush();
                } catch (IOException ex) {
                    L.w(ex);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Exception ignore) {
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (Exception ignore) {
                        }
                    }
                }
            }

        }

        return result != null ? result.getPath() : null;
    }

    @Nullable
    private static String saveBitmapToFile(File folder, String resultFileName, Bitmap bitmap) {
        File result = null;
        if (folder != null) {
            result = checkFileNameAndCreate(folder, resultFileName, false);
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(result);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            } catch (Exception ex) {
                L.w(ex);
            } finally {
                if (outputStream != null)
                    try {
                        outputStream.close();
                    } catch (IOException ignore) {
                    }
            }
        }

        return result != null ? result.getPath() : null;
    }

    private static void writeData(Serializable object, String fileName) {
        File cacheDir = getCacheDir();
        File resultFile = checkFileNameAndCreate(cacheDir, fileName, true);
        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(new FileOutputStream(resultFile));
            stream.writeObject(object);
        } catch (IOException ex) {
            L.w(ex);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private static <T extends Serializable> T readData(String fileName) {
        T result = null;
        File cacheDir = getCacheDir();
        File file = new File(cacheDir, fileName);
        if (file.exists()) {
            ObjectInputStream stream = null;
            try {
                stream = new ObjectInputStream(new FileInputStream(file));
                result = (T) stream.readObject();
            } catch (Exception ex) {
                L.w(ex);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception ignore) {
                    }
                }
            }
        }

        return result;
    }

    @Nullable
    private static File getFolderForTarget(@DataPart String part, Long targetId) {
        File partFolder = getFolderForDataPart(part);
        File result = null;
        if (partFolder != null) {
            result = new File(partFolder, String.valueOf(targetId));
            result.mkdirs();
        }

        return result;
    }

    @Nullable
    private static File getFolderForDataPart(@DataPart String part) {
        File cacheDir = getCacheDir();
        File result = null;
        if (cacheDir != null) {
            result = new File(cacheDir, part);
            result.mkdirs();
        }

        return result;
    }

    private static File getCacheDir() {
        File storageDir = null;
        if (IO.isMediaStorageMounted()) {
            storageDir = new File(Utils.getApp().getExternalFilesDir(null), CACHE_DIR);
            storageDir.mkdirs();
        } else {
            L.e("External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private static File checkFileNameAndCreate(File parent, String fileName, boolean replaceExisting) {
        fileName = replaceNotAllowedSymbols(fileName);
        File result = new File(parent, fileName);
        if (replaceExisting) {
            return result;
        }

        int extensionIndex = fileName.lastIndexOf(".");
        String extension = extensionIndex < 0 ? "" : fileName.substring(extensionIndex);
        String fileTitle = (extensionIndex < 0 ? fileName : fileName.substring(0, extensionIndex));
        result = new File(parent, fileTitle + extension);
        if (result.exists()) {
            Pair<String, Integer> titleWithNumber = getTitleWithNumber(fileTitle);
            fileTitle = titleWithNumber.first;
            final String pattern = fileTitle + PATTERN_FILE_NAME_COPY_NUMBER + extension;
            String[] files = parent.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.matches(pattern);
                }
            });

            int numberToAppend = 1;
            if (files != null && files.length > 0) {
                numberToAppend = -1;
                for (String file : files) {
                    Pair<String, Integer> title = getTitleWithNumber(file);
                    if (title.second != null && title.second > numberToAppend) {
                        numberToAppend = title.second;
                    }
                }

                numberToAppend++;
            }

            result = new File(parent, fileTitle + "(" + numberToAppend + ")" + extension);
        }

        return result;
    }

    private static Pair<String, Integer> getTitleWithNumber(String fileTitle) {
        Matcher matcher = NAME_COPY_PATTERN.matcher(fileTitle);
        int lastMatchIndex = -1;
        int matchLength = 0;
        while (matcher.find()) {
            lastMatchIndex = matcher.start();
            matchLength = matcher.end() - lastMatchIndex;
        }

        Integer number = null;
        if (lastMatchIndex > 0) {
            try {
                number = Integer.parseInt(fileTitle.substring(lastMatchIndex + 1, lastMatchIndex + matchLength - 1));
            } catch (Exception ignore) {
            }

            fileTitle = fileTitle.substring(0, lastMatchIndex) + fileTitle.substring(lastMatchIndex + matchLength);
        }

        return new Pair<>(fileTitle, number);
    }

    private static String replaceNotAllowedSymbols(String fileName) {
        String extension = getFileExtension(fileName);
        if (!TextUtils.isEmpty(extension)) {
            extension = "." + extension;
            fileName = fileName.replace(extension, "");
        }

        if (!TextUtils.isEmpty(fileName)) {
            for (String reserved : RESERVED_CHARS) {
                fileName = fileName.replace(reserved, "_");
            }
        }

        if (!TextUtils.isEmpty(extension)) {
            fileName += extension;
        }
        return fileName;
    }

    private static String getFileExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        File file = new File(filePath);
        int extensionIndex = file.getName().lastIndexOf(".");
        String extension = null;
        if (extensionIndex > 0) {
            extension = file.getName().substring(extensionIndex + 1).toLowerCase();
        }

        return extension;
    }

    private static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File contentFile : listFiles) {
                deleteFile(contentFile);
            }
        }

        boolean result = file.delete();
        invalidateEmpty();
        return result;
    }

    public static void invalidateEmpty() {
        deleteEmptyFolders(getCacheDir());
    }

    private static void deleteEmptyFolders(File folder) {
        if (folder == null) {
            return;
        }

        if (folder.isDirectory()) {
            File[] innerFiles = folder.listFiles();
            if (innerFiles != null) {
                for (File innerFile : innerFiles) {
                    deleteEmptyFolders(innerFile);
                }
            }

            innerFiles = folder.listFiles();
            if (innerFiles == null || innerFiles.length == 0) {
                // trick to prevent errors on asus: if try to create folder in case if it was removed
                // after usage, mkdir or mkdirs returns false and do not create a folder
                File renameToFile = new File(folder.getParentFile().getAbsolutePath(),
                        UUID.randomUUID().toString() + folder.getName());
                folder.renameTo(renameToFile);
                renameToFile.delete();
            }
        }
    }

    @StringDef({DataPart.FLOWERS, DataPart.GROUPS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DataPart {
        String FLOWERS = "Flowers";
        String GROUPS = "Groups";
    }
}
