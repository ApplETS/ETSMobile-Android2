package ca.etsmtl.applets.etsmobile.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourse;

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