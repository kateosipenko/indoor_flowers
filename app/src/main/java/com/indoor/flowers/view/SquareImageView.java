package com.indoor.flowers.view;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class SquareImageView extends AppCompatImageView {

    private boolean useHeight = true;

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (useHeight) {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }
    }


    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        if (params != null) {
            useHeight = params.height == ViewGroup.LayoutParams.MATCH_PARENT
                    || params.width == ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }


}
