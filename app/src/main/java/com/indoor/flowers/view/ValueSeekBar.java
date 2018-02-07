package com.indoor.flowers.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ValueSeekBar extends ConstraintLayout implements OnSeekBarChangeListener {

    private static final int MAX_PROGRESS = 100;

    @BindView(R.id.vvsb_seek_bar)
    AppCompatSeekBar seekBar;
    @BindView(R.id.vvsb_title)
    TextView title;
    @BindView(R.id.vvsb_value)
    TextView value;

    private float minValue = 0;
    private float defaultValue = (float) MAX_PROGRESS / 2;
    private float maxValue = MAX_PROGRESS;

    public ValueSeekBar(Context context) {
        super(context);
        init(null);
    }

    public ValueSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ValueSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        value.setText(String.valueOf(Math.round(progressToValue(progress))));
        float percent = (float) progress / MAX_PROGRESS;
        value.setTranslationX(percent * seekBar.getMeasuredWidth());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public float getValue() {
        return progressToValue(seekBar.getProgress());
    }

    private void init(AttributeSet attributeSet) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_value_seek_bar, this, true);
        ButterKnife.bind(this);
        getDataFromAttributes(attributeSet);

        seekBar.setMax(MAX_PROGRESS);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(valueToProgress(defaultValue));
    }

    private int valueToProgress(float value) {
        float percent = (value - minValue) / (maxValue - minValue);
        return (int) (percent * MAX_PROGRESS);
    }

    private float progressToValue(int progress) {
        float percent = (float) progress / MAX_PROGRESS;
        return minValue + percent * (maxValue - minValue);
    }

    private void getDataFromAttributes(AttributeSet attributeSet) {
        TypedArray typedArray = getResources().obtainAttributes(attributeSet, R.styleable.ValueSeekBar);
        if (typedArray.hasValue(R.styleable.ValueSeekBar_header)) {
            title.setText(typedArray.getString(R.styleable.ValueSeekBar_header));
        }
        if (typedArray.hasValue(R.styleable.ValueSeekBar_headerTextColor)) {
            title.setTextColor(typedArray.getColor(R.styleable.ValueSeekBar_headerTextColor,
                    Res.getColor(R.color.black_disabled_text)));
        }
        if (typedArray.hasValue(R.styleable.ValueSeekBar_valueTextColor)) {
            value.setTextColor(typedArray.getColor(R.styleable.ValueSeekBar_valueTextColor,
                    Res.getColor(R.color.black_disabled_text)));
        }
        if (typedArray.hasValue(R.styleable.ValueSeekBar_headerTextSize)) {
            title.setTextSize(typedArray.getDimensionPixelSize(R.styleable.ValueSeekBar_headerTextSize,
                    Res.getDimensionPixelSize(R.dimen.text_headline_small)));
        }
        if (typedArray.hasValue(R.styleable.ValueSeekBar_valueTextSize)) {
            value.setTextSize(typedArray.getDimensionPixelSize(R.styleable.ValueSeekBar_valueTextSize,
                    Res.getDimensionPixelSize(R.dimen.text_body_normal)));
        }
        if (typedArray.hasValue(R.styleable.ValueSeekBar_minValue)) {
            minValue = typedArray.getFloat(R.styleable.ValueSeekBar_minValue, 0);
        }
        if (typedArray.hasValue(R.styleable.ValueSeekBar_maxValue)) {
            maxValue = typedArray.getFloat(R.styleable.ValueSeekBar_maxValue, 0);
        }
        if (typedArray.hasValue(R.styleable.ValueSeekBar_defaultValue)) {
            defaultValue = typedArray.getFloat(R.styleable.ValueSeekBar_defaultValue, 0);
        }

        typedArray.recycle();
    }
}
