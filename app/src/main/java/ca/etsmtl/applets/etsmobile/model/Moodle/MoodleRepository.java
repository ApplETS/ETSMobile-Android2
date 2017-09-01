package ca.etsmtl.applets.etsmobile.model.Moodle;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import java.util.List;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.MoodleAssignmentCoursesRequest;
import ca.etsmtl.applets.etsmobile.model.RemoteResource;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Sonphil on 31-08-17.
 */

public class MoodleRepository {

    private Context context;
    private DataManager dataManager;
    private MutableLiveData<RemoteResource<List<MoodleAssignmentCourse>>> assignmentCourses = new MutableLiveData<>();
    private MutableLiveData<RemoteResource<MoodleToken>> token = new MutableLiveData<>();
    private MutableLiveData<RemoteResource<MoodleProfile>> profile = new MutableLiveData<>();

    public MoodleRepository(Context context) {
        this.context = context;
        dataManager = DataManager.getInstance(context);
    }

    public LiveData<RemoteResource<List<MoodleAssignmentCourse>>> getAssignmentCourses(final int[] coursesIds) {

        assignmentCourses.setValue(RemoteResource.<List<MoodleAssignmentCourse>>loading(null));

        RemoteResource<MoodleToken> remoteToken = token.getValue();

        if (remoteToken != null && remoteToken.data != null && remoteToken.status != RemoteResource.LOADING) {
            dataManager.sendRequest(new MoodleAssignmentCoursesRequest(context, coursesIds, ApplicationManager.userCredentials.getMoodleToken()), new RequestListener<Object>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    assignmentCourses.setValue(RemoteResource.<List<MoodleAssignmentCourse>>error(spiceException.getLocalizedMessage(), null));
                }

                @Override
                public void onRequestSuccess(Object o) {
                    List<MoodleAssignmentCourse> courses = ((MoodleAssignmentCourses) o).getCourses();
                    assignmentCourses.setValue(RemoteResource.success(courses));
                }
            });
        } else {
            getToken().observeForever(new Observer<RemoteResource<MoodleToken>>() {
                @Override
                public void onChanged(@Nullable RemoteResource<MoodleToken> moodleTokenRemoteResource) {
                    if (moodleTokenRemoteResource == null || moodleTokenRemoteResource.status == RemoteResource.ERROR) {
                        assignmentCourses.setValue(RemoteResource.<List<MoodleAssignmentCourse>>error(moodleTokenRemoteResource.message, null));
                        getAssignmentCourses(coursesIds);
                        token.removeObserver(this);
                    } else if (moodleTokenRemoteResource.status == RemoteResource.SUCCESS) {
                        getAssignmentCourses(coursesIds);
                        token.removeObserver(this);
                    }
                }
            });
        }

        return assignmentCourses;
    }

    public LiveData<RemoteResource<MoodleToken>> getToken() {

        if(context != null) {
            token.setValue(RemoteResource.<MoodleToken>loading(null));

            SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {

                @Override
                public MoodleToken loadDataFromNetwork() throws Exception {
                    String url = context.getString(R.string.moodle_api_get_token, ApplicationManager.userCredentials.getUsername(), ApplicationManager.userCredentials.getPassword());

                    return getRestTemplate().getForObject(url, MoodleToken.class);
                }
            };

            dataManager.sendRequest(request, new RequestListener<Object>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    token.setValue(RemoteResource.<MoodleToken>error(spiceException.getLocalizedMessage(), null));
                }

                @Override
                public void onRequestSuccess(Object o) {
                    MoodleToken moodleToken = (MoodleToken) o;

                    SecurePreferences securePreferences = new SecurePreferences(context);
                    securePreferences.edit().putString(UserCredentials.MOODLE_TOKEN, moodleToken.getToken()).commit();

                    ApplicationManager.userCredentials.setMoodleToken(moodleToken.getToken());

                    if (moodleToken.getToken().equals("")) {
                        onRequestFailure(new SpiceException("Impossible de se connecter"));
                    }

                    token.setValue(RemoteResource.success((MoodleToken) o));
                }
            });
        }

        return token;
    }

    public LiveData<RemoteResource<MoodleProfile>> getProfile(final MoodleToken moodleToken) {

        profile.setValue(RemoteResource.<MoodleProfile>loading(null));

        SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {

            @Override
            public MoodleProfile loadDataFromNetwork() throws Exception {
                String url = context.getString(R.string.moodle_api_core_get_siteinfo, moodleToken.getToken());

                return getRestTemplate().getForObject(url, MoodleProfile.class);
            }
        };

        dataManager.sendRequest(request, new RequestListener<Object>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                profile.setValue(RemoteResource.<MoodleProfile>error(spiceException.getLocalizedMessage(), null));
            }

            @Override
            public void onRequestSuccess(Object o) {
                profile.setValue(RemoteResource.success((MoodleProfile) o));
            }
        });

        return profile;
    }
}
