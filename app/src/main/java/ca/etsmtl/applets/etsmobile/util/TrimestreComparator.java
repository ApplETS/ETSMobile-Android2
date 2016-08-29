package ca.etsmtl.applets.etsmobile.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Comparator;

import ca.etsmtl.applets.etsmobile.model.Trimestre;

/**
 * Created by gnut3ll4 on 8/29/16.
 */
public class TrimestreComparator implements Comparator<Trimestre> {
    @Override
    public int compare(Trimestre lTrimestre, Trimestre rTrimestre) {
        DateTimeFormatter formatter = null;
        formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime lDateTime = formatter.parseDateTime(lTrimestre.dateFin);
        DateTime rDateTime = formatter.parseDateTime(rTrimestre.dateFin);

        if (lDateTime.isEqual(rDateTime)) {
            return 0;
        } else if (lDateTime.isBefore(rDateTime)) {
            return -1;
        } else {
            return 1;
        }
    }
}
