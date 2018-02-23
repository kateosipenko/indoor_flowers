package com.indoor.flowers.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextUtils;

import com.indoor.flowers.model.Flower;

import java.util.ArrayList;
import java.util.List;

public class PhotoUtils {

    public static String tryGenerateGroupPhoto(int resultImageWidth, int resultImageHeight,
                                               List<Flower> flowers) {
        List<String> files = new ArrayList<>();
        for (Flower flower : flowers) {
            if (!TextUtils.isEmpty(flower.getImagePath())) {
                files.add(flower.getImagePath());
            }
        }

        String result = null;
        if (files.size() > 0) {
            if (files.size() == 1) {
                result = generateCombination(resultImageWidth, resultImageHeight,
                        files.get(0), null, null, null);
            } else if (files.size() == 2) {
                result = generateCombination(resultImageWidth, resultImageHeight,
                        files.get(0), files.get(1), null, null);
            } else if (files.size() == 3) {
                result = generateCombination(resultImageWidth, resultImageHeight,
                        files.get(0), files.get(1), files.get(2), null);
            } else {
                result = generateCombination(resultImageWidth, resultImageHeight,
                        files.get(0), files.get(1), files.get(2), files.get(3));
            }
        }

        return result;
    }

    public static String generateCombination(int resultImageWidth, int resultImageHeight,
                                             String firstImage, String secondImage,
                                             String thirdImage, String fourthImage) {
        Bitmap first = BitmapFactory.decodeFile(firstImage);
        Bitmap second = BitmapFactory.decodeFile(secondImage);
        Bitmap third = BitmapFactory.decodeFile(thirdImage);
        Bitmap fourth = BitmapFactory.decodeFile(fourthImage);

        Bitmap result = Bitmap.createBitmap(resultImageWidth, resultImageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        RectF temp = new RectF();
        if (first != null && second != null && third != null && fourth != null) {
            temp.set(0, 0, resultImageWidth / 2f, resultImageHeight / 2f);
            canvas.drawBitmap(first, null, temp, null);
            temp.set(resultImageWidth / 2f, 0, resultImageWidth, resultImageHeight / 2f);
            canvas.drawBitmap(second, null, temp, null);
            temp.set(resultImageWidth / 2f, resultImageHeight / 2f,
                    resultImageWidth, resultImageHeight);
            canvas.drawBitmap(third, null, temp, null);
            temp.set(0, resultImageHeight / 2f,
                    0, resultImageHeight);
            canvas.drawBitmap(fourth, null, temp, null);
        }

        return FilesUtils.saveBitmapToFile(FilesUtils.getRandomFileName(), result);
    }
}
