package ca.etsmtl.applets.etsmobile.ui.calendar_decorator;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by komlan on 28/03/16.
 */
public class FinalExamDecorator implements DayViewDecorator {

    private static float DEFAULT_LENGTH = 25;
    private HashSet<CalendarDay> dates;
    private int color;
    private float length;
    private Context context;

    public FinalExamDecorator(Context context,ArrayList<CalendarDay> dates, int color) {
        this.dates = new HashSet<>(dates);
        this.color = color;
        this.length = DEFAULT_LENGTH;
        this.context = context;
    }

    public FinalExamDecorator(ArrayList<CalendarDay> dates, int color, float length) {
        this.dates = new HashSet<>(dates);
        this.color = color;
        this.length = length;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day) && !day.getDate().equals(new Date());
    }

    @Override
    public void decorate(DayViewFacade view) {

        view.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.calendar_exam_circle));
        view.addSpan(new StyleSpan(Typeface.BOLD));


    }
}
