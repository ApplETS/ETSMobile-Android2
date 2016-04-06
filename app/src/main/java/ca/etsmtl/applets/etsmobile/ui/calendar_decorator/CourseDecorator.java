package ca.etsmtl.applets.etsmobile.ui.calendar_decorator;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by komlan on 28/03/16.
 */
public class CourseDecorator implements DayViewDecorator {

    private static final float DEFAULT_RADIUS = 20;
    private HashSet<CalendarDay> dates;
    private int color;
    private float radius;

    public CourseDecorator(HashSet<CalendarDay> dates, int color, float radius) {
        this.dates = dates;
        this.color = color;
        this.radius = radius;
    }

    public CourseDecorator(HashSet<CalendarDay> dates, int color) {
        this.dates = dates;
        this.color = color;
        this.radius = DEFAULT_RADIUS;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day) && !day.getDate().equals(new Date());
    }

    @Override
    public void decorate(DayViewFacade view) {


        view.addSpan(new DotSpan(radius, color));
        view.addSpan(new StyleSpan(Typeface.BOLD));



    }
}
