package com.indoor.flowers.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;

import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.R;

public class NameView extends android.support.v7.widget.AppCompatEditText {

    private Drawable editIcon;
    private NameChangeListener listener;

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

    public void setListener(NameChangeListener listener) {
        this.listener = listener;
    }

    public void startEditing() {
        setEnabled(true);
        requestFocus();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.showKeyboard(NameView.this);
            }
        }, 100);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && !isEnabled()) {
            int editIconPosition = getRight() - getEditIconWidth();
            if (event.getRawX() >= editIconPosition) {
                setEnabled(true);
                requestFocus();
                setSelection(getText().length());
                return true;
            }
        }
        return super.onTouchEvent(event);
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
        if (listener != null) {
            listener.onNameChanged(getText().toString().trim());
        }
    }

    private void init() {
        editIcon = getResources().getDrawable(R.drawable.ic_edit);
        editIcon.setTintList(getHintTextColors());
        setInputType(EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS);
        setCompoundDrawablesWithIntrinsicBounds(null, null, editIcon, null);
        setEnabled(false);
        setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    private int getEditIconWidth() {
        return editIcon != null ? editIcon.getBounds().width() : 0;
    }

    public interface NameChangeListener {
        void onNameChanged(String name);
    }
}
