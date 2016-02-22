package ca.etsmtl.applets.etsmobile;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Comparator;

/**
 * Created by gnut3ll4 on 10/09/15.
 */
public class SeanceComparator implements Comparator<Seances> {
    @Override
    public int compare(Seances seance1, Seances seance2) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTime seanceDay1 = formatter.parseDateTime(seance1.dateDebut);
        DateTime seanceDay2 = formatter.parseDateTime(seance2.dateDebut);

        if (seanceDay1.isAfter(seanceDay2)) {
            return 1;
        }

        if (seanceDay1.isBefore(seanceDay2)) {
            return -1;
        }

        return 0;
    }
}
