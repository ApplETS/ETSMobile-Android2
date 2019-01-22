package ca.etsmtl.applets.etsmobile.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Comparator;

import ca.etsmtl.applets.etsmobile.model.Event;
import ca.etsmtl.applets.etsmobile.model.IHoraireRows;
import ca.etsmtl.applets.etsmobile.model.Seances;

/**
 * Created by Simon on 3/13/2016.
 */
public class HoraireComparator implements Comparator<IHoraireRows>{
    @Override
    public int compare(IHoraireRows lhs, IHoraireRows rhs) {
        DateTimeFormatter formatter = null;
        DateTime eventDay1 = null;
        DateTime eventDay2 = null;

        if(lhs.getClass().equals(Event.class)){
            formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            eventDay1 = formatter.parseDateTime(lhs.getDateDebut());
        }
        if(rhs.getClass().equals(Event.class)){
            formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            eventDay2 = formatter.parseDateTime(rhs.getDateDebut());
        }

        if (lhs.getClass().equals(Seances.class)){
            formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
            eventDay1 = formatter.parseDateTime(lhs.getDateDebut());
        }
        if (rhs.getClass().equals(Seances.class)){
            formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
            eventDay2 = formatter.parseDateTime(rhs.getDateDebut());
        }

        if (eventDay1.isAfter(eventDay2)) {
            return 1;
        }
        else if(eventDay1.isBefore(eventDay2)){
            return -1;
        }
        else if(eventDay1.isEqual(eventDay2)){
            return 0;
        }
        return 0;
    }
}
