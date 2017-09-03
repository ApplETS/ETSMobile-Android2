package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;

import com.google.gson.Gson;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentSubmission;
import ca.etsmtl.applets.etsmobile2.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Sonphil on 02-09-17.
 */

public class MoodleAssignmentSubmissionRequest extends SpringAndroidSpiceRequest<MoodleAssignmentSubmission> {

    private Context context;
    private String token;
    private int assignId;

    public MoodleAssignmentSubmissionRequest(Context context, String token, int assignId) {
        super(MoodleAssignmentSubmission.class);

        this.context = context;
        this.token = token;
        this.assignId = assignId;
    }

    @Override
    public MoodleAssignmentSubmission loadDataFromNetwork() throws Exception {
        String url = context.getString(R.string.moodle_api_assign_submission, token, String.valueOf(assignId));

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        String calendarJson = response.body().string();

        return new Gson().fromJson(calendarJson, MoodleAssignmentSubmission.class);
    }
}
