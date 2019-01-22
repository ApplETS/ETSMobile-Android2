package ca.etsmtl.applets.etsmobile.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleAssignmentCourse;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleAssignmentSubmission;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleCourse;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleProfile;

/**
 * Created by Sonphil on 26-12-17.
 */
@Database(entities = {MoodleProfile.class, MoodleCourse.class, MoodleAssignmentCourse.class,
        MoodleAssignmentSubmission.class}, version = 1, exportSchema = false)
public abstract class MoodleDb extends RoomDatabase {

    public abstract MoodleProfileDao moodleProfileDao();

    public abstract MoodleCourseDao moodleCourseDao();

    public abstract MoodleAssignmentCourseDao moodleAssignmentCourseDao();

    public abstract MoodleAssignmentSubmissionDao moodleAssignmentSubmissionDao();
}
