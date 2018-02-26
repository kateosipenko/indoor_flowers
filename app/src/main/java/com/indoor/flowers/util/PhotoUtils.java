package com.indoor.flowers.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.text.TextUtils;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.model.Flower;

import java.util.ArrayList;
import java.util.List;

public class PhotoUtils {

    private static final int MAX_BITMAPS_FOR_COMBINATION = 4;

    public static String tryGenerateGroupPhoto(int resultImageWidth, int resultImageHeight,
                                               List<Flower> flowers) {
        List<Bitmap> bitmaps = new ArrayList<>();
        for (Flower flower : flowers) {
            if (!TextUtils.isEmpty(flower.getImagePath())) {
                bitmaps.add(BitmapFactory.decodeFile(flower.getImagePath()));
            }

            if (bitmaps.size() == MAX_BITMAPS_FOR_COMBINATION) {
                break;
            }
        }

        return bitmaps.size() > 0
                ? generateCombination(resultImageWidth, resultImageHeight, bitmaps) : null;
    }

    public static String generateCombination(int resultImageWidth, int resultImageHeight,
                                             List<Bitmap> bitmaps) {
        if (bitmaps == null || bitmaps.size() == 0) {
            return null;
        }

        Bitmap result = Bitmap.createBitmap(resultImageWidth, resultImageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawColor(Res.getColor(R.color.accent50));
        float centerX = resultImageWidth / 2f;
        float centerY = resultImageHeight / 2f;
        RectF temp = new RectF();
        if (bitmaps.size() == 1) {
            temp.set(0, 0, resultImageWidth, resultImageHeight);
            drawBitmap(canvas, temp, resultImageWidth, resultImageHeight, bitmaps.get(0));
        } else if (bitmaps.size() == 2) {
            temp.set(0, 0, centerX, resultImageHeight);
            drawBitmap(canvas, temp, resultImageWidth, resultImageHeight, bitmaps.get(0));
            temp.set(centerX, 0, resultImageWidth, resultImageHeight);
            drawBitmap(canvas, temp, resultImageWidth, resultImageHeight, bitmaps.get(1));
        } else if (bitmaps.size() == 3) {
            temp.set(0, 0, centerX, centerY);
            drawBitmap(canvas, temp, resultImageWidth, resultImageHeight, bitmaps.get(0));
            temp.set(centerX, 0, resultImageWidth, centerY);
            drawBitmap(canvas, temp, resultImageWidth, resultImageHeight, bitmaps.get(1));
            temp.set(0, centerY, resultImageWidth, resultImageHeight);
            drawBitmap(canvas, temp, resultImageWidth, resultImageHeight, bitmaps.get(2));
        } else {
            temp.set(0, 0, centerX, centerY);
            drawBitmap(canvas, temp, resultImageWidth, resultImageHeight, bitmaps.get(0));
            temp.set(centerX, 0, resultImageWidth, centerY);
            drawBitmap(canvas, temp, resultImageWidth, resultImageHeight, bitmaps.get(1));
            temp.set(0, centerY, centerX, resultImageHeight);
            drawBitmap(canvas, temp, resultImageWidth, resultImageHeight, bitmaps.get(2));
            temp.set(centerX, centerY, resultImageWidth, resultImageHeight);
            drawBitmap(canvas, temp, resultImageWidth, resultImageHeight, bitmaps.get(3));
        }

        return FilesUtils.saveBitmapToFile(FilesUtils.getRandomFileName(), result);
    }

    private static void drawBitmap(Canvas canvas, RectF drawRect, int resultWidth, int resultHeight,
                                   Bitmap bitmap) {
        canvas.save();
        canvas.clipRect(drawRect);
        Matrix matrix = new Matrix();
        float scale = bitmap.getHeight() > bitmap.getWidth()
                ? resultWidth / (float) bitmap.getWidth()
                : resultHeight / (float) bitmap.getHeight();

        float centerX = (drawRect.width() - bitmap.getWidth() * scale) / 2f;
        float centerY = (drawRect.height() - bitmap.getHeight() * scale) / 2f;

        matrix.preTranslate(centerX, centerY);
        matrix.preScale(scale, scale);
        matrix.postTranslate(drawRect.left, drawRect.top);
        canvas.drawBitmap(bitmap, matrix, null);
        canvas.restore();
    }
}
