package com.indoor.flowers.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import com.indoor.flowers.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatusView extends View {

    private static final String PERCENT_FORMAT = "%1$s%%";
    private static final float CIRCLES_IN_ROW = 30f;

    private Paint potPaint = new Paint();
    private Paint groundPaint = new Paint();
    private Paint waterPaint = new Paint();
    private Paint fertilizerPaint = new Paint();
    private Paint circlePaint = new Paint();
    private TextPaint textPaint = new TextPaint();

    private Path potPath = new Path();
    private Path waterPath = new Path();
    private Path groundPath = new Path();

    private Float waterLevel = null;
    private Float fertilizerLevel = null;

    private RectF drawingRect = new RectF();
    private Rect textBounds = new Rect();

    private int circleColor = Color.WHITE;
    private int circleMiddleColor = Color.WHITE;
    private int circleMinimumColor = Color.WHITE;
    private int fertilizerColor = Color.GRAY;
    private int fertilizerGroundColor = Color.GRAY;

    public StatusView(Context context) {
        super(context);
        TypedArray typedArray = null;
        if (context instanceof ContextThemeWrapper) {
            ContextThemeWrapper wrapper = (ContextThemeWrapper) context;
            typedArray = getContext().obtainStyledAttributes(wrapper.getThemeResId(),
                    R.styleable.StatusView);
        }

        init(typedArray);
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StatusView);
        init(typedArray);
    }

    public StatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(defStyleAttr, R.styleable.StatusView);
        init(typedArray);
    }

    public void setWaterLevel(Float value) {
        this.fertilizerLevel = null;
        this.waterLevel = value;
        if (waterLevel != null) {
            if (waterLevel > 1f) {
                waterLevel = 1f;
            } else if (waterLevel < 0f) {
                waterLevel = 0f;
            }
        }
        refreshData();
        invalidate();
    }

    public void setFertilizerLevel(Float value) {
        this.waterLevel = null;
        this.fertilizerLevel = value;
        if (fertilizerLevel != null) {
            if (fertilizerLevel > 1f) {
                fertilizerLevel = 1f;
            } else if (fertilizerLevel < 0f) {
                fertilizerLevel = 0f;
            }
        }

        refreshData();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(potPath, potPaint);
        if (fertilizerLevel != null) {
            canvas.drawPath(waterPath, fertilizerPaint);
        } else if (waterLevel != null) {
            canvas.drawPath(groundPath, groundPaint);
            canvas.save();
            canvas.clipRect(0, getMeasuredHeight() * (1 - waterLevel), getMeasuredWidth(), getMeasuredHeight());
            canvas.drawPath(waterPath, waterPaint);
            canvas.restore();
        }

        circlePaint.setColor(getCircleColor());
        canvas.drawCircle(drawingRect.centerX(), drawingRect.centerY(),
                drawingRect.width() < drawingRect.height() ?
                        drawingRect.width() / 3f : drawingRect.height() / 3f, circlePaint);
        canvas.drawText(getText(), textBounds.left, textBounds.bottom, textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        refreshData();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshData();
    }

    private int getCircleColor() {
        int result = circleColor;
        Float currentValue = fertilizerLevel != null ? fertilizerLevel : waterLevel;
        if (currentValue != null) {
            if (currentValue < 0.3f) {
                result = circleMinimumColor;
            } else if (currentValue < 0.6f) {
                result = circleMiddleColor;
            }
        }
        return result;
    }

    private String getText() {
        if (fertilizerLevel != null) {
            return String.format(PERCENT_FORMAT, (int) (fertilizerLevel * 100));
        }
        if (waterLevel != null) {
            return String.format(PERCENT_FORMAT, (int) (waterLevel * 100));
        }

        return "";
    }

    private void refreshData() {
        float strokeWidth = potPaint.getStrokeWidth();
        drawingRect.set(strokeWidth, strokeWidth, getMeasuredWidth() - strokeWidth,
                getMeasuredHeight() - strokeWidth);
        refreshPotPath();
        refreshWaterPath();
        refreshGroundPath();
        refreshTextBounds();

        int groundSourceColor = fertilizerLevel != null ? fertilizerGroundColor : waterPaint.getColor();
        int groundColor = ColorUtils.compositeColors(ColorUtils.setAlphaComponent(groundSourceColor, 60),
                ColorUtils.setAlphaComponent(Color.WHITE, 210));
        groundPaint.setColor(groundColor);

        refreshFertilizerPaint();
    }

    private void refreshFertilizerPaint() {
        if (fertilizerLevel == null || drawingRect.isEmpty()) {
            return;
        }

        List<Pair<Integer, Integer>> filledIndexes = new ArrayList<>();
        float radius = (int) (drawingRect.width() / CIRCLES_IN_ROW);
        float step = radius * 2f;
        int verticalCount = (int) (((getMeasuredHeight() * 2) / (step)));
        int horizontalCount = (int) (((getMeasuredWidth() * 2) / (step)));
        int totalCircleCount = verticalCount * horizontalCount;
        int filledCount = (int) (totalCircleCount * fertilizerLevel);

        List<Pair<Integer, Integer>> allIndexes = new ArrayList<>();
        for (int j = 0; j < verticalCount; j++) {
            for (int i = 0; i < horizontalCount; i++) {
                allIndexes.add(new Pair<>(i, j));
            }
        }

        Collections.shuffle(allIndexes);
        for (int i = 0; i < filledCount; i++) {
            filledIndexes.add(allIndexes.get(i));
        }

        Bitmap shaderBitmap = Bitmap.createBitmap(getMeasuredWidth(),
                getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        fertilizerPaint.setShader(null);
        Canvas canvas = new Canvas(shaderBitmap);
        for (int y = 0; y < verticalCount; y++) {
            canvas.save();
            if (y % 2 == 0) {
                canvas.translate(-radius, 0);
            }
            for (int x = 0; x < horizontalCount; x++) {
                if (filledIndexes.contains(new Pair<>(x, y))) {
                    fertilizerPaint.setColor(fertilizerColor);
                } else {
                    fertilizerPaint.setColor(fertilizerGroundColor);
                }

                canvas.drawCircle(x * step, y * step, radius, fertilizerPaint);
            }

            canvas.restore();
        }

        BitmapShader bitmapShader = new BitmapShader(shaderBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        fertilizerPaint.setShader(bitmapShader);
    }

    private void refreshTextBounds() {
        int smallerSize = getMeasuredHeight() < getMeasuredWidth()
                ? getMeasuredHeight() : getMeasuredWidth();
        textPaint.setTextSize(smallerSize / 4f);

        String text = getText();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        textBounds.offsetTo((int) (drawingRect.left + (drawingRect.width() - textBounds.width()) / 2f),
                (int) (drawingRect.top + (drawingRect.height() - textBounds.height()) / 2f));
    }

    private void refreshPotPath() {
        float strokeWidth = potPaint.getStrokeWidth();
        float offset = drawingRect.width() * 0.1f;

        potPath.reset();
        potPath.moveTo(drawingRect.left, drawingRect.top);
        potPath.quadTo(drawingRect.left, drawingRect.bottom + strokeWidth, drawingRect.left + offset, drawingRect.bottom);
        potPath.lineTo(drawingRect.right - offset, drawingRect.bottom);
        potPath.quadTo(drawingRect.right, drawingRect.bottom + strokeWidth, drawingRect.right, drawingRect.top);
        potPath.lineTo(drawingRect.right - strokeWidth, drawingRect.top);
        potPath.quadTo(drawingRect.right - strokeWidth, drawingRect.bottom, drawingRect.right - strokeWidth - offset, drawingRect.bottom - strokeWidth);
        potPath.lineTo(drawingRect.left + strokeWidth + offset, drawingRect.bottom - strokeWidth);
        potPath.quadTo(drawingRect.left + strokeWidth, drawingRect.bottom, drawingRect.left + strokeWidth, drawingRect.top);
        potPath.lineTo(drawingRect.left, drawingRect.top);
        potPath.close();
    }

    private void refreshWaterPath() {
        float strokeWidth = potPaint.getStrokeWidth();
        float offset = drawingRect.width() * 0.1f;

        waterPath.reset();
        waterPath.moveTo(drawingRect.left, drawingRect.top);
        waterPath.quadTo(drawingRect.left, drawingRect.bottom + strokeWidth, drawingRect.left + offset, drawingRect.bottom);
        waterPath.lineTo(drawingRect.right - offset, drawingRect.bottom);
        waterPath.quadTo(drawingRect.right, drawingRect.bottom + strokeWidth, drawingRect.right, drawingRect.top);
        waterPath.lineTo(drawingRect.left, drawingRect.top);
        waterPath.close();

        Matrix scale = new Matrix();
        scale.preTranslate(0, -potPaint.getStrokeWidth());

        float horizontalScale = (drawingRect.width() - 2 * strokeWidth) / drawingRect.width();
        float verticalScale = (drawingRect.height() - strokeWidth) / drawingRect.height();
        scale.postScale(horizontalScale, verticalScale, drawingRect.centerX(), drawingRect.bottom);
        waterPath.transform(scale);
    }

    private void refreshGroundPath() {
        groundPath.reset();

        groundPath.set(waterPath);
        groundPath.close();
    }

    private void init(TypedArray typedArray) {
        setWillNotDraw(false);
        setupPaints();
        if (typedArray != null) {
            if (typedArray.hasValue(R.styleable.StatusView_potColor)) {
                int color = typedArray.getColor(R.styleable.StatusView_potColor, Color.GRAY);
                potPaint.setColor(color);
            }
            if (typedArray.hasValue(R.styleable.StatusView_potSize)) {
                float size = typedArray.getDimension(R.styleable.StatusView_potSize, 10f);
                potPaint.setStrokeWidth(size);
            }
            if (typedArray.hasValue(R.styleable.StatusView_waterColor)) {
                int color = typedArray.getColor(R.styleable.StatusView_waterColor, Color.BLUE);
                waterPaint.setColor(color);
            }
            if (typedArray.hasValue(R.styleable.StatusView_waterLevel)) {
                waterLevel = typedArray.getFloat(R.styleable.StatusView_waterLevel, 1f);
                if (waterLevel > 1f) {
                    waterLevel = 1f;
                } else if (waterLevel < 0f) {
                    waterLevel = 0f;
                }
            }
            if (typedArray.hasValue(R.styleable.StatusView_fertilizerLevel)) {
                fertilizerLevel = typedArray.getFloat(R.styleable.StatusView_fertilizerLevel, 1f);
                if (fertilizerLevel > 1f) {
                    fertilizerLevel = 1f;
                } else if (fertilizerLevel < 0f) {
                    fertilizerLevel = 0f;
                }
            }
            if (typedArray.hasValue(R.styleable.StatusView_circleColor)) {
                circleColor = typedArray.getColor(R.styleable.StatusView_circleColor, Color.WHITE);
            }
            if (typedArray.hasValue(R.styleable.StatusView_circleMiddleColor)) {
                circleMiddleColor = typedArray.getColor(R.styleable.StatusView_circleMiddleColor, Color.WHITE);
            }
            if (typedArray.hasValue(R.styleable.StatusView_circleMinimumColor)) {
                circleMinimumColor = typedArray.getColor(R.styleable.StatusView_circleMinimumColor, Color.WHITE);
            }
            if (typedArray.hasValue(R.styleable.StatusView_fertilizerColor)) {
                fertilizerColor = typedArray.getColor(R.styleable.StatusView_fertilizerColor, Color.GREEN);
            }
            if (typedArray.hasValue(R.styleable.StatusView_fertilizerGroundColor)) {
                fertilizerGroundColor = typedArray.getColor(R.styleable.StatusView_fertilizerGroundColor, Color.GRAY);
            }

            typedArray.recycle();
        }

        refreshData();
        invalidate();
    }

    private void setupPaints() {
        potPaint = new Paint();
        waterPaint = new Paint();
        fertilizerPaint = new Paint();
        groundPaint = new Paint();
        circlePaint = new Paint();
        textPaint = new TextPaint();


        initPaint(potPaint);
        initPaint(waterPaint);
        initPaint(fertilizerPaint);
        initPaint(groundPaint);
        initPaint(textPaint);
        initPaint(circlePaint);

        textPaint.setColor(Color.WHITE);
        circlePaint.setAlpha(110);
    }

    private void initPaint(Paint paint) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }
}
