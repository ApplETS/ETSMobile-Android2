package ca.etsmtl.applets.etsmobile.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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
