package ca.etsmtl.applets.etsmobile.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import ca.etsmtl.applets.etsmobile.model.moodle.MoodleAssignmentSubmission;

/**
 * Created by Sonphil on 31-12-17.
 */
@Dao
public interface MoodleAssignmentSubmissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MoodleAssignmentSubmission moodleAssignmentSubmission);

    @Query("SELECT * FROM moodleassignmentsubmission WHERE assignId = :assignmentId LIMIT 1")
    LiveData<MoodleAssignmentSubmission> getByAssignmentId(int assignmentId);
}
