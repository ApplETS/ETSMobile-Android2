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

import java.util.ArrayList;
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
    private Context context;

    public CourseDecorator(Context context,ArrayList<CalendarDay>dates, int color) {
        this.context = context;
        this.dates = new HashSet<>(dates);
        this.color = color;
        this.radius = radius;
    }

    public CourseDecorator(ArrayList<CalendarDay> dates, int color, float radius) {
        this.dates = new HashSet<>(dates);
        this.color = color;
        this.radius = radius;
    }

    public CourseDecorator(ArrayList<CalendarDay> dates, int color) {
        this.dates = new HashSet<>(dates);
        this.color = color;
        this.radius = DEFAULT_RADIUS;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {

        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {



        view.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.calendar_course_circle));


        view.addSpan(new StyleSpan(Typeface.BOLD));

    }

}
