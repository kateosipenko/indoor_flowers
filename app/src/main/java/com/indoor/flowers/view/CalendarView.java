package com.indoor.flowers.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evgeniysharafan.utils.Res;
import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.CalendarDaysAdapter;
import com.indoor.flowers.adapter.CalendarDaysAdapter.OnDayClickedListener;
import com.indoor.flowers.model.NotificationWithTarget;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalendarView extends LinearLayout {

    @BindView(R.id.vc_month_title)
    TextView currentMonthView;
    @BindView(R.id.vc_week_days)
    LinearLayout weekDaysContainer;
    @BindView(R.id.vc_days_list)
    RecyclerView daysList;

    private Calendar currentDate;
    private CalendarDaysAdapter daysAdapter;
    private OnMonthChangedListener monthChangedListener;

    private boolean animateNextMonth = true;

    public CalendarView(Context context) {
        super(context);
        initialize();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public Calendar getStartDate() {
        return daysAdapter.getStartDate();
    }

    public Calendar getEndDate() {
        return daysAdapter.getEndDate();
    }

    public Calendar getCurrentDate() {
        return currentDate;
    }

    public List<NotificationWithTarget> getNotificationsPerCurrentDay() {
        return daysAdapter.getNotificationsPerDay(currentDate);
    }

    @OnClick(R.id.vc_previous_month)
    void onPreviousMonthClicked() {
        animateNextMonth = false;
        currentDate.add(Calendar.MONTH, -1);
        onMonthUpdated(true);
    }

    @OnClick(R.id.vc_next_month)
    void onNextMonthClicked() {
        animateNextMonth = true;
        currentDate.add(Calendar.MONTH, 1);
        onMonthUpdated(true);
    }

    public void setMonthChangedListener(OnMonthChangedListener listener) {
        this.monthChangedListener = listener;
        if (listener != null) {
            listener.onMonthChanged(daysAdapter.getStartDate(), daysAdapter.getEndDate());
        }
    }

    public void setDayClickListener(OnDayClickedListener listener) {
        this.daysAdapter.setDayClickListener(listener);
    }

    public void setEventsForMonth(SparseArray<List<NotificationWithTarget>> events) {
        daysAdapter.setEvents(events);
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_calendar, this, true);
        ButterKnife.bind(this);
        setOrientation(VERTICAL);

        if (!isInEditMode()) {
            currentDate = Calendar.getInstance();
            setupWeekDays();
            setupDaysList();
            onMonthUpdated(false);
        }
    }

    private void setupDaysList() {
        daysAdapter = new CalendarDaysAdapter();
        daysList.setLayoutManager(new GridLayoutManager(getContext(), 7));
        daysList.setAdapter(daysAdapter);
        daysAdapter.setMonthDate(currentDate);
    }

    private void setupWeekDays() {
        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.DAY_OF_WEEK, temp.getActualMinimum(Calendar.DAY_OF_WEEK));
        for (int i = 0; i < weekDaysContainer.getChildCount(); i++) {
            TextView textView = (TextView) weekDaysContainer.getChildAt(i);
            textView.setText(temp.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
            temp.add(Calendar.DAY_OF_WEEK, 1);
        }
    }

    private void onMonthUpdated(boolean playAnimation) {
        if (playAnimation) {
            Bitmap monthDrawable = getDrawingCache(currentMonthView);
            Bitmap daysDrawable = getDrawingCache(daysList);
            animateViewMonthChanged(currentMonthView, monthDrawable).start();
            animateViewMonthChanged(daysList, daysDrawable).start();
        }

        currentMonthView.setText(Res.getString(R.string.month_title_format,
                getTextForMonth(currentDate.get(Calendar.MONTH)),
                currentDate));
        daysAdapter.setMonthDate(currentDate);

        if (monthChangedListener != null) {
            monthChangedListener.onMonthChanged(daysAdapter.getStartDate(), daysAdapter.getEndDate());
        }
    }

    private String getTextForMonth(int monthNumber) {
        String result = null;
        try {
            int id = Res.get().getIdentifier("month_" + monthNumber, "string", Utils.getPackageName());
            result = Res.getString(id);
        } catch (Exception ignore) {
        }

        return result;
    }

    private AnimatorSet animateViewMonthChanged(final View animatedView, final Bitmap startViewDrawingCache) {
        float translationStep = animatedView.getMeasuredWidth() / 2f;

        if (startViewDrawingCache != null) {
            BitmapDrawable start = new BitmapDrawable(getResources(), startViewDrawingCache);
            start.setBounds(0, 0, animatedView.getMeasuredWidth(), animatedView.getMeasuredHeight());
            animatedView.getOverlay().add(start);
        }

        AnimatorSet hidePrevious = new AnimatorSet();
        hidePrevious.playTogether(ObjectAnimator.ofFloat(animatedView, View.TRANSLATION_X,
                animatedView.getTranslationX(), animateNextMonth ? -translationStep : translationStep),
                ObjectAnimator.ofFloat(animatedView, View.ALPHA, 1f, 0f));
        hidePrevious.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (startViewDrawingCache != null) {
                    startViewDrawingCache.recycle();
                }

                animatedView.getOverlay().clear();
            }
        });

        AnimatorSet showNext = new AnimatorSet();
        showNext.playTogether(ObjectAnimator.ofFloat(animatedView, View.TRANSLATION_X,
                animateNextMonth ? translationStep : -translationStep, 0),
                ObjectAnimator.ofFloat(animatedView, View.ALPHA, 0f, 1f));

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(hidePrevious, showNext);
        set.setDuration(500);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        return set;
    }

    private Bitmap getDrawingCache(View view) {
        Bitmap result = null;
        if (view.getMeasuredWidth() > 0 && view.getMeasuredHeight() > 0) {
            result = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            canvas.drawColor(Color.WHITE);
            view.draw(canvas);
        }
        return result;
    }

    public interface OnMonthChangedListener {
        void onMonthChanged(Calendar startDate, Calendar endDate);
    }
}
