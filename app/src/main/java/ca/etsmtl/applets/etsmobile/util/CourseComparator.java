package ca.etsmtl.applets.etsmobile.util;

import java.util.Comparator;

import ca.etsmtl.applets.etsmobile.model.moodle.MoodleCourse;

/**
 * Created by Steven on 2016-01-14.
 */
public class CourseComparator implements Comparator<MoodleCourse> {

    @Override
    public int compare(MoodleCourse course1, MoodleCourse course2) {
        String shortname1 = course1.getShortname().substring(3,5) + "." + course1.getShortname().substring(5,6);
        String shortname2 = course2.getShortname().substring(3,5) + "." + course2.getShortname().substring(5,6);

        if (shortname1.equals("cu.l") || shortname2.equals("cu.l"))
            return 0;

        if (Double.valueOf(shortname1) > Double.valueOf(shortname2)) {
            return 1;
        }

        if (Double.valueOf(shortname1) < Double.valueOf(shortname2)) {
            return -1;
        }

        return 0;
    }
}
