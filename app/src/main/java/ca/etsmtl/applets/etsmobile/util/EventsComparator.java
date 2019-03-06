package ca.etsmtl.applets.etsmobile.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Comparator;

import ca.etsmtl.applets.etsmobile.model.applets_events.EvenementCommunaute;

/**
 * Created by club on 01/09/16.
 */
public class EventsComparator implements Comparator<EvenementCommunaute> {
    @Override
    public int compare(EvenementCommunaute event1, EvenementCommunaute event2) {

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dt1 = formatter.parseDateTime(event1.getDebut());
        DateTime dt2 = formatter.parseDateTime(event2.getDebut());

        if (dt1.isAfter(dt2)) {
            return 1;
        }

        if (dt1.isBefore(dt2)) {
            return -1;
        }

        return 0;
        
    }
}
