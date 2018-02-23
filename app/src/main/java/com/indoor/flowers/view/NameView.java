package com.indoor.flowers.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;

import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.R;

public class NameView extends android.support.v7.widget.AppCompatEditText
        implements OnTouchListener {

    private Drawable editIcon;

    public NameView(Context context) {
        super(context);
        init();
    }

    public NameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int editIconPosition = getRight() - getEditIconWidth();
            if (event.getRawX() >= editIconPosition) {
                setEnabled(true);
                requestFocus();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEditorAction(int actionCode) {
        super.onEditorAction(actionCode);
        if (actionCode == EditorInfo.IME_ACTION_DONE && !Utils.isEmpty(this)) {
            onEditDone();
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (editIcon == null) {
            return;
        }

        if (focused) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            Utils.showKeyboard(this);
        } else {
            onEditDone();
        }
    }

    private void onEditDone() {
        setEnabled(false);
        setCompoundDrawablesWithIntrinsicBounds(null, null, editIcon, null);
        Utils.hideKeyboard(this);
    }

    private void init() {
        editIcon = getResources().getDrawable(R.drawable.ic_edit);
        editIcon.setTintList(getHintTextColors());
        setCompoundDrawablesWithIntrinsicBounds(null, null, editIcon, null);
        setEnabled(false);
        setOnTouchListener(this);
        setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    private int getEditIconWidth() {
        return editIcon != null ? editIcon.getBounds().width() : 0;
    }
}
