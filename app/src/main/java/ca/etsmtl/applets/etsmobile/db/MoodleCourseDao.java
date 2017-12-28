package ca.etsmtl.applets.etsmobile.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourse;

/**
 * Created by Sonphil on 26-12-17.
 */
@Dao
public interface MoodleCourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MoodleCourse moodleCourse);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MoodleCourse> moodleCourses);

    @Query("SELECT * FROM moodlecourse")
    LiveData<List<MoodleCourse>> getAll();
}
