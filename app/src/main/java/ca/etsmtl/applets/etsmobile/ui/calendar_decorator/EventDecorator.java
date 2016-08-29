package ca.etsmtl.applets.etsmobile.ui.calendar_decorator;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by komlan on 28/03/16.
 */
public class EventDecorator implements DayViewDecorator {


    private HashSet<CalendarDay> dates;
    private int color;


    public EventDecorator(ArrayList<CalendarDay> dates, int color) {
        this.dates = new HashSet<>(dates);
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day) ;
    }

    @Override
    public void decorate(DayViewFacade view) {

        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new TriangleSpan(color));

    }
}
