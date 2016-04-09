package ca.etsmtl.applets.etsmobile.ui.calendar_decorator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by komlan on 08/04/16.
 */
public class TodayDecorator implements DayViewDecorator {

    private static final int DEFAULT_RADIUS = 85;
    private CalendarDay today;
    private int color;
    private float radius;
    private Context context;

    public TodayDecorator(Context context) {
        this.color = color;

        today = CalendarDay.today();
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(today);
    }

    @Override
    public void decorate(DayViewFacade view) {

        view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.calendar_today_circle));

        view.addSpan(new StyleSpan(Typeface.BOLD));

    }
}
