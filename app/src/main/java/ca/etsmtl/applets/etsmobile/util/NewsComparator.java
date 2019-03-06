package ca.etsmtl.applets.etsmobile.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Comparator;

import ca.etsmtl.applets.etsmobile.model.Nouvelle;

/**
 * Created by club on 01/09/16.
 */
public class NewsComparator implements Comparator<Nouvelle> {
    @Override
    public int compare(Nouvelle nouvelle1, Nouvelle nouvelle2) {

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dt1 = formatter.parseDateTime(nouvelle1.getDate());
        DateTime dt2 = formatter.parseDateTime(nouvelle2.getDate());

        if (dt1.isAfter(dt2)) {
            return -1;
        }

        if (dt1.isBefore(dt2)) {
            return 1;
        }

        return 0;
        
    }
}
