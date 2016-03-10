package ca.etsmtl.applets.etsmobile.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Comparator;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourses;
import ca.etsmtl.applets.etsmobile.model.Seances;

/**
 * Created by Steven on 2016-01-14.
 */
public class CourseComparator implements Comparator<MoodleCourse> {

    @Override
    public int compare(MoodleCourse course1, MoodleCourse course2) {
        String shortname1 = course1.getShortname().substring(3,5) + "." + course1.getShortname().substring(5,6);
        String shortname2 = course2.getShortname().substring(3,5) + "." + course2.getShortname().substring(5,6);

        if (Double.valueOf(shortname1) > Double.valueOf(shortname2)) {
            return 1;
        }

        if (Double.valueOf(shortname1) < Double.valueOf(shortname2)) {
            return -1;
        }

        return 0;
    }
}
