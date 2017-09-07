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
import ca.etsmtl.applets.etsmobile.http.MoodleAssignmentSubmissionRequest;
import ca.etsmtl.applets.etsmobile.model.RemoteResource;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Sonphil on 31-08-17.
 */

public class MoodleRepository {

    private static String TAG = "MoodleRepository";

    private Context context;
    private DataManager dataManager;
    private MutableLiveData<RemoteResource<List<MoodleAssignmentCourse>>> assignmentCourses = new MutableLiveData<>();
    private MutableLiveData<RemoteResource<MoodleToken>> token = new MutableLiveData<>();
    private MutableLiveData<RemoteResource<MoodleProfile>> profile = new MutableLiveData<>();
    private MutableLiveData<RemoteResource<MoodleCourses>> courses = new MutableLiveData<>();

    public MoodleRepository(Context context) {
        this.context = context.getApplicationContext();
        dataManager = DataManager.getInstance(context);
        getToken();
    }

    private LiveData<RemoteResource<MoodleToken>> getToken() {

        if (context != null && token.getValue() == null) {
            token.setValue(RemoteResource.<MoodleToken>loading(null));

            SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {

                @Override
                public MoodleToken loadDataFromNetwork() throws Exception {
                    String url = context.getString(R.string.moodle_api_get_token,
                            ApplicationManager.userCredentials.getUsername(),
                            ApplicationManager.userCredentials.getPassword());

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
                    securePreferences.edit().putString(UserCredentials.MOODLE_TOKEN, moodleToken.getToken()).apply();

                    ApplicationManager.userCredentials.setMoodleToken(moodleToken.getToken());

                    if (moodleToken.getToken().equals("")) {
                        onRequestFailure(new SpiceException(context.getString(R.string.moodle_error_connection_failure)));
                    }

                    token.setValue(RemoteResource.success((MoodleToken) o));
                }
            });
        }

        return token;
    }

    public LiveData<RemoteResource<List<MoodleAssignmentCourse>>> getAssignmentCourses() {

        if (assignmentCourses.getValue() == null|| assignmentCourses.getValue().status != RemoteResource.SUCCESS) {
            assignmentCourses.setValue(RemoteResource.<List<MoodleAssignmentCourse>>loading(null));

            RemoteResource<MoodleCourses> moodleCourses = courses.getValue();

            if (moodleCourses != null && moodleCourses.data != null && moodleCourses.status != RemoteResource.LOADING) {
                int[] coursesIds = new int[moodleCourses.data.size()];
                for (int i = 0; i < moodleCourses.data.size(); i++){
                    coursesIds[i] = moodleCourses.data.get(i).getId();
                }
                dataManager.sendRequest(new MoodleAssignmentCoursesRequest(context, coursesIds,
                        ApplicationManager.userCredentials.getMoodleToken()),
                        new RequestListener<Object>() {
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
                getCourses().observeForever(new Observer<RemoteResource<MoodleCourses>>() {
                    @Override
                    public void onChanged(@Nullable RemoteResource<MoodleCourses> moodleCoursesRemoteResource) {
                        if (moodleCoursesRemoteResource == null || moodleCoursesRemoteResource.status == RemoteResource.ERROR) {
                            String errorMessage = moodleCoursesRemoteResource == null ? context.getString(R.string.moodle_error_cant_get_courses) : moodleCoursesRemoteResource.message;
                            assignmentCourses.setValue(RemoteResource.<List<MoodleAssignmentCourse>>error(errorMessage, null));
                            courses.removeObserver(this);
                        } else if (moodleCoursesRemoteResource.status == RemoteResource.SUCCESS) {
                            getAssignmentCourses();
                            courses.removeObserver(this);
                        }
                    }
                });
            }
        }

        return assignmentCourses;
    }

    public LiveData<RemoteResource<MoodleProfile>> getProfile() {
        if (profile.getValue() == null || profile.getValue().status != RemoteResource.SUCCESS) {
            profile.setValue(RemoteResource.<MoodleProfile>loading(null));

            RemoteResource<MoodleToken> remoteToken = token.getValue();

            if (remoteToken != null && remoteToken.data != null && remoteToken.status != RemoteResource.LOADING) {
                SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {

                    @Override
                    public MoodleProfile loadDataFromNetwork() throws Exception {
                        String url = context.getString(R.string.moodle_api_get_siteinfo, ApplicationManager.userCredentials.getMoodleToken());

                        return getRestTemplate().getForObject(url, MoodleProfile.class);
                    }
                };

                dataManager.sendRequest(request, new RequestListener<Object>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        profile.setValue(RemoteResource.<MoodleProfile>error(context.getString(R.string.moodle_error_cant_get_profil), null));
                    }

                    @Override
                    public void onRequestSuccess(Object o) {
                        MoodleProfile moodleProfile = (MoodleProfile) o;
                        if (moodleProfile.getErrorcode() != null)
                            onRequestFailure(new SpiceException(moodleProfile.getException()));
                        else
                            profile.setValue(RemoteResource.success(moodleProfile));
                    }
                });
            } else {
                token.observeForever(new Observer<RemoteResource<MoodleToken>>() {
                    @Override
                    public void onChanged(@Nullable RemoteResource<MoodleToken> moodleTokenRemoteResource) {
                        if (moodleTokenRemoteResource == null || moodleTokenRemoteResource.status == RemoteResource.ERROR) {
                            String errorMessage = moodleTokenRemoteResource == null ? context.getString(R.string.moodle_error_cant_get_token) : moodleTokenRemoteResource.message;
                            profile.setValue(RemoteResource.<MoodleProfile>error(errorMessage, null));
                            token.removeObserver(this);
                        } else if (moodleTokenRemoteResource.status == RemoteResource.SUCCESS) {
                            getProfile();
                            token.removeObserver(this);
                        }
                    }
                });
            }
        }

        return profile;
    }

    public LiveData<RemoteResource<MoodleCourses>> getCourses() {
        if (courses.getValue() == null || courses.getValue().status != RemoteResource.SUCCESS) {
            courses.setValue(RemoteResource.<MoodleCourses>loading(null));

            final RemoteResource<MoodleProfile> remoteProfile = profile.getValue();

            if (remoteProfile != null && remoteProfile.data != null && remoteProfile.status != RemoteResource.LOADING) {
                SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {

                    @Override
                    public MoodleCourses loadDataFromNetwork() throws Exception {
                        String url = context.getString(R.string.moodle_api_enrol_get_users_courses,
                                ApplicationManager.userCredentials.getMoodleToken(),
                                String.valueOf(remoteProfile.data.getUserId()));
                        return getRestTemplate().getForObject(url, MoodleCourses.class);
                    }
                };

                dataManager.sendRequest(request, new RequestListener<Object>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        courses.setValue(RemoteResource.<MoodleCourses>error(spiceException.getLocalizedMessage(), null));
                    }

                    @Override
                    public void onRequestSuccess(Object o) {
                        courses.setValue(RemoteResource.success((MoodleCourses) o));
                    }
                });
            } else {
                profile.observeForever(new Observer<RemoteResource<MoodleProfile>>() {
                    @Override
                    public void onChanged(@Nullable RemoteResource<MoodleProfile> moodleProfileRemoteResource) {
                        if (moodleProfileRemoteResource == null || moodleProfileRemoteResource.status == RemoteResource.ERROR) {
                            String errorMessage = moodleProfileRemoteResource == null ? context.getString(R.string.moodle_error_cant_get_profil) : moodleProfileRemoteResource.message;
                            courses.setValue(RemoteResource.<MoodleCourses>error(errorMessage, null));
                            profile.removeObserver(this);
                        } else if (moodleProfileRemoteResource.status == RemoteResource.SUCCESS) {
                            getCourses();
                            profile.removeObserver(this);
                        }
                    }
                });
                getProfile();
            }
        }

        return courses;
    }

    public LiveData<RemoteResource<MoodleAssignmentSubmission>> getAssignmentSubmission(final int assignId) {
        final MutableLiveData<RemoteResource<MoodleAssignmentSubmission>> submission = new MutableLiveData<>();
        submission.setValue(RemoteResource.<MoodleAssignmentSubmission>loading(null));

        RemoteResource<MoodleToken> remoteToken = token.getValue();

        if (remoteToken != null && remoteToken.data != null && remoteToken.status != RemoteResource.LOADING) {
            dataManager.sendRequest(new MoodleAssignmentSubmissionRequest(context, ApplicationManager.userCredentials.getMoodleToken(), assignId), new RequestListener<Object>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    submission.setValue(RemoteResource.<MoodleAssignmentSubmission>error(spiceException.getLocalizedMessage(), null));
                }

                @Override
                public void onRequestSuccess(Object o) {
                    submission.setValue(RemoteResource.success((MoodleAssignmentSubmission) o));
                }
            });
        } else {
            token.observeForever(new Observer<RemoteResource<MoodleToken>>() {
                @Override
                public void onChanged(@Nullable RemoteResource<MoodleToken> moodleTokenRemoteResource) {
                    if (moodleTokenRemoteResource == null || moodleTokenRemoteResource.status == RemoteResource.ERROR) {
                        String errorMessage = moodleTokenRemoteResource == null ? context.getString(R.string.moodle_error_cant_get_token) : moodleTokenRemoteResource.message;
                        submission.setValue(RemoteResource.<MoodleAssignmentSubmission>error(errorMessage, null));
                        token.removeObserver(this);
                    } else if (moodleTokenRemoteResource.status == RemoteResource.SUCCESS) {
                        getProfile();
                        token.removeObserver(this);
                    }
                }
            });
        }

        return submission;
    }
}
