package ca.etsmtl.applets.etsmobile.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ca.etsmtl.applets.etsmobile.model.moodle.MoodleAssignmentCourse;

/**
 * Created by Sonphil on 28-12-17.
 */
@Dao
public interface MoodleAssignmentCourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MoodleAssignmentCourse moodleCourse);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MoodleAssignmentCourse> moodleCourses);

    @Query("SELECT * FROM moodleassignmentcourse")
    LiveData<List<MoodleAssignmentCourse>> getAll();
}
