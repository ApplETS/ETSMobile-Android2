package ca.etsmtl.applets.etsmobile.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleProfile;

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
