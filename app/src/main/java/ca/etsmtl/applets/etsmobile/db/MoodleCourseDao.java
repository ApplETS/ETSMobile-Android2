package ca.etsmtl.applets.etsmobile.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleCourse;

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
