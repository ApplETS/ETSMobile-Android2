package ca.etsmtl.applets.etsmobile.ui.calendar_decorator;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
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
public class FinalExamDecorator implements DayViewDecorator {

    private HashSet<CalendarDay> dates;
    private Context context;


    public FinalExamDecorator(Context context, Collection<CalendarDay> dates) {

        this.dates = new HashSet<>(dates);
        this.context = context;

    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day) && !day.getDate().equals(new Date());
    }

    @Override
    public void decorate(DayViewFacade view) {

        view.addSpan(new DotSpan(20, ContextCompat.getColor(context, R.color.ets_red)));
        view.addSpan(new StyleSpan(Typeface.BOLD));


    }
}
