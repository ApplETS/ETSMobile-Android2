package ca.etsmtl.applets.etsmobile.model.moodle;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Submission of an assignment
 * <p>
 * An instance of {@link MoodleAssignmentSubmission} contains an instance of
 * {@link MoodleAssignmentLastAttempt} which gives information about the last attempt.
 * The instance of {@link MoodleAssignmentSubmission} may also contains a non null instance of
 * {@link MoodleAssignmentFeedback}.
 * <p>
 * Created by Sonphil on 02-09-17.
 */
@Entity
@TypeConverters(MoodleAssignmentSubmission.Covnerters.class)
public class MoodleAssignmentSubmission {
    @PrimaryKey
    private int assignId;
    @SerializedName("lastattempt")
    private MoodleAssignmentLastAttempt lastAttempt;
    @SerializedName("feedback")
    private MoodleAssignmentFeedback feedback;

    public int getAssignId() {
        return assignId;
    }

    public void setAssignId(int assignId) {
        this.assignId = assignId;
    }

    public MoodleAssignmentLastAttempt getLastAttempt() {
        return lastAttempt;
    }

    public void setLastAttempt(MoodleAssignmentLastAttempt lastAttempt) {
        this.lastAttempt = lastAttempt;
    }

    public MoodleAssignmentFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(MoodleAssignmentFeedback feedback) {
        this.feedback = feedback;
    }

    public static final class Covnerters {
        @TypeConverter
        public static MoodleAssignmentLastAttempt stringToLastAttempt(String json) {
            Gson gson = new Gson();
            Type type = new TypeToken<MoodleAssignmentLastAttempt>() {}.getType();
            return gson.fromJson(json, type);
        }

        @TypeConverter
        public static String lastAttemptToString(MoodleAssignmentLastAttempt lastAttempt) {
            Gson gson = new Gson();
            Type type = new TypeToken<MoodleAssignmentLastAttempt>() {}.getType();
            return gson.toJson(lastAttempt, type);
        }

        @TypeConverter
        public static MoodleAssignmentFeedback stringToFeedback(String json) {
            Gson gson = new Gson();
            Type type = new TypeToken<MoodleAssignmentFeedback>() {}.getType();
            return gson.fromJson(json, type);
        }

        @TypeConverter
        public static String feedbackToString(MoodleAssignmentFeedback feedback) {
            Gson gson = new Gson();
            Type type = new TypeToken<MoodleAssignmentFeedback>() {}.getType();
            return gson.toJson(feedback, type);
        }
    }
}
