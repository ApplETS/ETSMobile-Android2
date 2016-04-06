package ca.etsmtl.applets.etsmobile.ui.calendar_decorator;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by komlan on 28/03/16.
 */
public class EventDecorator implements DayViewDecorator {

    private HashSet<CalendarDay> dates;
     private int color;


    public EventDecorator(HashSet<CalendarDay> dates, int color) {
        this.dates = dates;
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day) && !day.getDate().equals(new Date());
    }

    @Override
    public void decorate(DayViewFacade view) {

        view.addSpan(new StyleSpan(Typeface.BOLD));
        ForegroundColorSpan fcs = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.dark_blue));
        view.addSpan(fcs);

    }
}
