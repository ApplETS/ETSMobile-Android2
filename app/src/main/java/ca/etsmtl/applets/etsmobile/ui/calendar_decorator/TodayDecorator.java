package ca.etsmtl.applets.etsmobile.ui.calendar_decorator;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import androidx.core.content.ContextCompat;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by komlan on 08/04/16.
 */
public class TodayDecorator implements DayViewDecorator {

    private CalendarDay today;
    private Context context;

    public TodayDecorator(Context context) {
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
