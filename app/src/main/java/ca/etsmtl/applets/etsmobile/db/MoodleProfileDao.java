package ca.etsmtl.applets.etsmobile.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import ca.etsmtl.applets.etsmobile.model.moodle.MoodleProfile;

/**
 * Created by Sonphil on 26-12-17.
 */
@Dao
public interface MoodleProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MoodleProfile moodleProfile);

    @Query("SELECT * FROM moodleprofile")
    LiveData<MoodleProfile> find();


}
