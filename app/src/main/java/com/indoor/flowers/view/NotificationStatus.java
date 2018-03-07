package com.indoor.flowers.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationStatus extends RelativeLayout {

    private static final int ALPHA_MIN = 55;
    private static final int ALPHA_MAX = 255;

    @BindView(R.id.vns_progress)
    ProgressBar progressBar;
    @BindView(R.id.vns_title)
    TextView titleView;

    private int tintColor = Color.WHITE;

    private int frequency = 0;
    private int daysLeft = 0;

    public NotificationStatus(Context context) {
        super(context);
        initialize(null);
    }

    public NotificationStatus(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public NotificationStatus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    public NotificationStatus(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(attrs);
    }

    public void setProgress(int frequency, int daysLeft) {
        int progress = (int) (((double) daysLeft / frequency) * 100);
        progressBar.setProgress(progress);
        titleView.setText(String.valueOf(daysLeft));
        onProgressChanged();
    }

    public void setProgressTint(@ColorInt int color) {
        tintColor = color;
        onProgressChanged();
    }

    private void initialize(AttributeSet attributeSet) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_nutrition_status, this, true);
        ButterKnife.bind(this);
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.NotificationStatus);
            if (typedArray.hasValue(R.styleable.NotificationStatus_progress)) {
                int progress = typedArray.getInt(R.styleable.NotificationStatus_progress, 0);
                progressBar.setProgress(progress);
            }
            if (typedArray.hasValue(R.styleable.NotificationStatus_progressTint)) {
                tintColor = typedArray.getColor(R.styleable.NotificationStatus_progressTint, tintColor);
            }
            if (typedArray.hasValue(R.styleable.NotificationStatus_textSize) && !isInEditMode()) {
                int textSize = typedArray.getDimensionPixelSize(R.styleable.NotificationStatus_textSize,
                        Res.getDimensionPixelSize(R.dimen.text_body_normal));
                titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            if (typedArray.hasValue(R.styleable.NotificationStatus_textColor)) {
                int color = typedArray.getColor(R.styleable.NotificationStatus_textColor, Color.BLACK);
                titleView.setTextColor(color);
            }
            typedArray.recycle();
        }

        onProgressChanged();
    }

    private void onProgressChanged() {
        int progress = progressBar.getProgress();
        int colorAlpha = (int) (ALPHA_MIN + (ALPHA_MAX - ALPHA_MIN) * (progress / 100d));

        int currentColor = ColorUtils.setAlphaComponent(tintColor, colorAlpha);
        int backgroundColor = ColorUtils.setAlphaComponent(currentColor, (int) (colorAlpha / 2f));

        progressBar.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
        progressBar.setProgressTintList(ColorStateList.valueOf(currentColor));
    }
}
