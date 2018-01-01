package ca.etsmtl.applets.etsmobile.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentSubmission;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleProfile;

/**
 * Created by Sonphil on 26-12-17.
 */
@Database(entities = {MoodleProfile.class, MoodleCourse.class, MoodleAssignmentCourse.class,
        MoodleAssignmentSubmission.class}, version = 1)
public abstract class MoodleDb extends RoomDatabase {

    public abstract MoodleProfileDao moodleProfileDao();

    public abstract MoodleCourseDao moodleCourseDao();

    public abstract MoodleAssignmentCourseDao moodleAssignmentCourseDao();

    public abstract MoodleAssignmentSubmissionDao moodleAssignmentSubmissionDao();
}
