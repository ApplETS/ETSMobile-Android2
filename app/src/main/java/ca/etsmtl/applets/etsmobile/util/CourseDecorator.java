package ca.etsmtl.applets.etsmobile.util;

import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by komlan on 28/03/16.
 */
public class CourseDecorator implements DayViewDecorator {

    private int color;
    private HashSet<CalendarDay> dates;

    public CourseDecorator(int color, Collection<CalendarDay> dates) {
        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(10, color));
        view.addSpan(new StyleSpan(Typeface.BOLD));


    }
}
