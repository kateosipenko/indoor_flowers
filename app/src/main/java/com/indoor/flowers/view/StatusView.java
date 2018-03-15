package com.indoor.flowers.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.graphics.ColorUtils;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.indoor.flowers.R;

public class StatusView extends View {

    private static final String PERCENT_FORMAT = "%1$s%%";

    private Paint potPaint = new Paint();
    private Paint groundPaint = new Paint();
    private Paint waterPaint = new Paint();
    private Paint circlePaint = new Paint();
    private TextPaint textPaint = new TextPaint();

    private Path potPath = new Path();
    private Path waterPath = new Path();
    private Path groundPath = new Path();

    private Float waterLevel = null;

    private RectF drawingRect = new RectF();
    private Rect textBounds = new Rect();

    private int circleColor = Color.WHITE;
    private int circleMiddleColor = Color.WHITE;
    private int circleMinimumColor = Color.WHITE;

    public StatusView(Context context) {
        super(context);
        init(null);
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public StatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void setWaterLevel(Float waterLevel) {
        this.waterLevel = waterLevel;
        updatePaths();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(potPath, potPaint);
        canvas.drawPath(groundPath, groundPaint);
        if (waterLevel != null) {
            canvas.save();
            canvas.clipRect(0, getMeasuredHeight() * (1 - waterLevel), getMeasuredWidth(), getMeasuredHeight());
            canvas.drawPath(waterPath, waterPaint);
            canvas.restore();
        }

        circlePaint.setColor(getCircleColor());
        canvas.drawCircle(drawingRect.centerX(), drawingRect.centerY(),
                drawingRect.width() / 3, circlePaint);
        canvas.drawText(getText(), textBounds.left, textBounds.bottom, textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updatePaths();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updatePaths();
    }

    private void updatePaths() {
        float strokeWidth = potPaint.getStrokeWidth();
        float width = getMeasuredWidth() - strokeWidth / 2;
        float height = getMeasuredHeight() - strokeWidth / 2;

        float left = strokeWidth + (width - height) / 2;
        float right = left + width;

        drawingRect.set(left, strokeWidth, right, height - strokeWidth);
        refreshPotPath();
        refreshWaterPath();
        refreshGroundPath();
        refreshTextBounds();
    }

    private void refreshTextBounds() {
        textPaint.setTextSize(getMeasuredHeight() / 4f);

        String text = getText();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        textBounds.bottom = (int) (textBounds.top + textBounds.height() / 2f);
        textBounds.offsetTo((int) drawingRect.centerX(), (int) drawingRect.centerY());
        textBounds.offset((int) (-textBounds.width() / 2f), (int) (-textBounds.height() / 2f));
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

    private int getCircleColor() {
        int result = circleColor;
        if (waterLevel < 0.3f) {
            result = circleMinimumColor;
        } else if (waterLevel < 0.6f) {
            result = circleMiddleColor;
        }
        return result;
    }

    private String getText() {
        return String.format(PERCENT_FORMAT, (int) (waterLevel * 100));
    }

    private void init(AttributeSet attrs) {
        setWillNotDraw(false);
        setupPaints();
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StatusView);
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

            int groundColor = ColorUtils.compositeColors(ColorUtils.setAlphaComponent(waterPaint.getColor(), 60),
                    ColorUtils.setAlphaComponent(Color.WHITE, 210));
            groundPaint.setColor(groundColor);

            typedArray.recycle();
        }

        updatePaths();
        invalidate();
    }

    private void setupPaints() {
        potPaint = new Paint();
        potPaint.setAntiAlias(true);
        potPaint.setStyle(Paint.Style.FILL);

        waterPaint = new Paint();
        waterPaint.setAntiAlias(true);
        waterPaint.setStyle(Paint.Style.FILL);

        groundPaint = new Paint();
        groundPaint.setAntiAlias(true);
        groundPaint.setStyle(Paint.Style.FILL);

        textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setAlpha(110);
    }
}
