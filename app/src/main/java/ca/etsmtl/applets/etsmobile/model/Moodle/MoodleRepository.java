package ca.etsmtl.applets.etsmobile.model.Moodle;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.MoodleCourseDao;
import ca.etsmtl.applets.etsmobile.db.MoodleProfileDao;
import ca.etsmtl.applets.etsmobile.http.MoodleWebService;
import ca.etsmtl.applets.etsmobile.model.RemoteResource;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobile2.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sonphil on 31-08-17.
 */
@Singleton
public class MoodleRepository {

    private static String TAG = "MoodleRepository";

    private Context context;
    private final MoodleWebService moodleWebService;
    private final MoodleProfileDao moodleProfileDao;
    private final MoodleCourseDao moodleCourseDao;
    private final Executor executor;

    @Inject
    public MoodleRepository(@NonNull Application application, MoodleWebService moodleWebService, MoodleProfileDao moodleProfileDao, MoodleCourseDao moodleCourseDao, Executor executor) {
        this.context = application;
        this.moodleWebService = moodleWebService;
        this.moodleProfileDao = moodleProfileDao;
        this.moodleCourseDao = moodleCourseDao;
        this.executor = executor;
    }

    private LiveData<RemoteResource<MoodleToken>> getToken() {

        String tokenStr = ApplicationManager.userCredentials.getMoodleToken();
        MutableLiveData<RemoteResource<MoodleToken>> token = new MutableLiveData<>();

        if (tokenStr != null && !tokenStr.isEmpty()) {
            token.setValue(RemoteResource.success(new MoodleToken(tokenStr)));
        } else {
            token.setValue(RemoteResource.loading(null));

            moodleWebService.getToken(ApplicationManager.userCredentials.getUsername(), ApplicationManager.userCredentials.getPassword()).enqueue(new Callback<MoodleToken>() {
                @Override
                public void onResponse(@NonNull Call<MoodleToken> call, @NonNull Response<MoodleToken> response) {
                    if (response.isSuccessful()) {
                        MoodleToken moodleToken = response.body();

                        SecurePreferences securePreferences = new SecurePreferences(context);
                        securePreferences.edit().putString(UserCredentials.MOODLE_TOKEN, moodleToken.getToken()).apply();

                        ApplicationManager.userCredentials.setMoodleToken(moodleToken.getToken());

                        if (moodleToken.getToken().equals("")) {
                            token.setValue(RemoteResource.error(context.getString(R.string.moodle_error_cant_get_token) + "\n" + response.message(), null));
                        } else {
                            token.setValue(RemoteResource.success(moodleToken));
                        }
                    } else {
                        token.setValue(RemoteResource.error(context.getString(R.string.moodle_error_cant_get_token) + "\n" + response.message(), null));
                    }
                }

                @Override
                public void onFailure(Call<MoodleToken> call, Throwable t) {
                    token.setValue(RemoteResource.error(context.getString(R.string.moodle_error_cant_get_token), null));
                }
            });
        }
        return token;
    }

    private void getAssignmentCourses(final int[] coursesIds, final MutableLiveData<RemoteResource<List<MoodleAssignmentCourse>>> assignmentCourses) {
        LiveData<RemoteResource<MoodleToken>> token = getToken();

        token.observeForever(new Observer<RemoteResource<MoodleToken>>() {
            @Override
            public void onChanged(@Nullable RemoteResource<MoodleToken> remoteToken) {

                if (remoteToken == null || remoteToken.status == RemoteResource.ERROR) {
                    assignmentCourses.setValue(RemoteResource.error(context.getString(R.string.moodle_error_cant_get_token), null));
                } else if (remoteToken.data != null && remoteToken.status != RemoteResource.LOADING) {
                    assignmentCourses.setValue(RemoteResource.loading(null));

                    moodleWebService.getAssignmentCourses(remoteToken.data.getToken(), coursesIds).enqueue(new Callback<MoodleAssignmentCourses>() {
                        @Override
                        public void onResponse(@NonNull Call<MoodleAssignmentCourses> call, @NonNull Response<MoodleAssignmentCourses> response) {
                            MoodleAssignmentCourses moodleAssignmentCourses = response.body();

                            if (response.isSuccessful() && moodleAssignmentCourses != null) {
                                List<MoodleAssignmentCourse> courses = moodleAssignmentCourses.getCourses();
                                assignmentCourses.setValue(RemoteResource.success(courses));
                            } else {
                                assignmentCourses.setValue(RemoteResource.error(response.message(), null));
                            }
                        }

                        @Override
                        public void onFailure(Call<MoodleAssignmentCourses> call, Throwable t) {
                            assignmentCourses.setValue(RemoteResource.error(context.getString(R.string.moodle_error_connection_failure), null));
                        }
                    });

                    token.removeObserver(this);
                }
            }
        });
    }

    public LiveData<RemoteResource<List<MoodleAssignmentCourse>>> getAssignmentCourses() {

        MutableLiveData<RemoteResource<List<MoodleAssignmentCourse>>> assignmentCourses = new MutableLiveData<>();
        assignmentCourses.setValue(RemoteResource.loading(null));

        LiveData<RemoteResource<MoodleCourses>> remoteCoursesLiveData = getCourses();
        remoteCoursesLiveData.observeForever(new Observer<RemoteResource<MoodleCourses>>() {
            @Override
            public void onChanged(@Nullable RemoteResource<MoodleCourses> remoteMoodleCourses) {
                if (remoteMoodleCourses != null && remoteMoodleCourses.data != null && remoteMoodleCourses.status == RemoteResource.SUCCESS) {
                    int[] coursesIds = new int[remoteMoodleCourses.data.size()];
                    for (int i = 0; i < remoteMoodleCourses.data.size(); i++) {
                        coursesIds[i] = remoteMoodleCourses.data.get(i).getId();
                    }

                    getAssignmentCourses(coursesIds, assignmentCourses);
                }

                if (remoteMoodleCourses != null && remoteMoodleCourses.status != RemoteResource.LOADING)
                    remoteCoursesLiveData.removeObserver(this);
            }
        });

        return assignmentCourses;
    }

    public LiveData<RemoteResource<MoodleProfile>> getProfile() {
        MutableLiveData<RemoteResource<MoodleProfile>> remoteMoodleProfileLiveData = new MutableLiveData<>();
        remoteMoodleProfileLiveData.setValue(RemoteResource.loading(null));

        refreshMoodleProfile();

        LiveData<MoodleProfile> moodleProfileLiveData = moodleProfileDao.find();

        moodleProfileLiveData.observeForever(new Observer<MoodleProfile>() {
            @Override
            public void onChanged(@Nullable MoodleProfile moodleProfile) {
                if (moodleProfile != null) {
                    remoteMoodleProfileLiveData.setValue(RemoteResource.success(moodleProfile));

                    moodleProfileLiveData.removeObserver(this);
                }
            }
        });

        return remoteMoodleProfileLiveData;
    }

    private void refreshMoodleProfile() {
        LiveData<RemoteResource<MoodleToken>> remoteTokenLiveData = getToken();
        remoteTokenLiveData.observeForever(new Observer<RemoteResource<MoodleToken>>() {
            @Override
            public void onChanged(@Nullable RemoteResource<MoodleToken> moodleTokenRemoteResource) {
                if (moodleTokenRemoteResource != null && moodleTokenRemoteResource.status == RemoteResource.SUCCESS) {
                    executor.execute(() -> {
                        try {
                            Response response = moodleWebService.getProfile(ApplicationManager.userCredentials.getMoodleToken()).execute();

                            moodleProfileDao.insert((MoodleProfile) response.body());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }

                if (moodleTokenRemoteResource != null && moodleTokenRemoteResource.status != RemoteResource.LOADING)
                    remoteTokenLiveData.removeObserver(this);
            }
        });
    }

    public LiveData<RemoteResource<MoodleCourses>> getCourses() {
        MutableLiveData<RemoteResource<MoodleCourses>> remoteCoursesMutableLiveData = new MutableLiveData<>();
        remoteCoursesMutableLiveData.setValue(RemoteResource.loading(null));

        refreshCourses();

        LiveData<List<MoodleCourse>> moodleCoursesLiveData = moodleCourseDao.getAll();
        moodleCoursesLiveData.observeForever(new Observer<List<MoodleCourse>>() {
            @Override
            public void onChanged(@Nullable List<MoodleCourse> moodleCoursesList) {
                if (moodleCoursesList != null && moodleCoursesList.size() > 0) {
                    MoodleCourses moodleCoursesObj = new MoodleCourses();
                    for (int i = moodleCoursesList.size() - 1; i >= 0; i--)
                        moodleCoursesObj.add(moodleCoursesList.get(i));

                    remoteCoursesMutableLiveData.setValue(RemoteResource.success(moodleCoursesObj));

                    moodleCoursesLiveData.removeObserver(this);
                }
            }
        });

        return remoteCoursesMutableLiveData;
    }

    private void refreshCourses() {
        LiveData<RemoteResource<MoodleProfile>> remoteProfileLiveData = getProfile();
        remoteProfileLiveData.observeForever(new Observer<RemoteResource<MoodleProfile>>() {
            @Override
            public void onChanged(@Nullable RemoteResource<MoodleProfile> remoteProfile) {
                if (remoteProfile != null && remoteProfile.data != null && remoteProfile.status != RemoteResource.LOADING) {
                    LiveData<RemoteResource<MoodleToken>> remoteTokenLiveData = getToken();
                    remoteTokenLiveData.observeForever(new Observer<RemoteResource<MoodleToken>>() {
                        @Override
                        public void onChanged(@Nullable RemoteResource<MoodleToken> moodleTokenRemoteResource) {

                            if (moodleTokenRemoteResource != null && moodleTokenRemoteResource.status == RemoteResource.SUCCESS) {
                                executor.execute(() -> {
                                    try {
                                        Response response = moodleWebService.getCourses(ApplicationManager.userCredentials.getMoodleToken(), remoteProfile.data.getUserId()).execute();

                                        moodleCourseDao.insertAll((MoodleCourses) response.body());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }

                            if (moodleTokenRemoteResource != null && moodleTokenRemoteResource.status != RemoteResource.LOADING)
                                remoteTokenLiveData.removeObserver(this);
                        }
                    });
                }

                if (remoteProfile != null && remoteProfile.status != RemoteResource.LOADING)
                    remoteProfileLiveData.removeObserver(this);
            }
        });
    }

    public LiveData<RemoteResource<MoodleAssignmentSubmission>> getAssignmentSubmission(final int assignId) {
        final MutableLiveData<RemoteResource<MoodleAssignmentSubmission>> submission = new MutableLiveData<>();
        submission.setValue(RemoteResource.loading(null));

        LiveData<RemoteResource<MoodleToken>> token = getToken();
        token.observeForever(new Observer<RemoteResource<MoodleToken>>() {
            @Override
            public void onChanged(@Nullable RemoteResource<MoodleToken> remoteToken) {
                if (remoteToken == null || remoteToken.status == RemoteResource.ERROR) {
                    submission.setValue(RemoteResource.error(context.getString(R.string.moodle_error_cant_get_token), null));
                } else if (remoteToken.data != null && remoteToken.status != RemoteResource.LOADING) {
                    moodleWebService.getAssignmentSubmission(remoteToken.data.getToken(), assignId).enqueue(new Callback<MoodleAssignmentSubmission>() {
                        @Override
                        public void onResponse(@NonNull Call<MoodleAssignmentSubmission> call, @NonNull Response<MoodleAssignmentSubmission> response) {
                            MoodleAssignmentSubmission assignmentSubmission = response.body();

                            if (response.isSuccessful() && assignmentSubmission != null) {
                                submission.setValue(RemoteResource.success(assignmentSubmission));
                            } else {
                                submission.setValue(RemoteResource.error(response.message(), assignmentSubmission));
                            }
                        }

                        @Override
                        public void onFailure(Call<MoodleAssignmentSubmission> call, Throwable t) {
                            submission.setValue(RemoteResource.error(context.getString(R.string.moodle_error_connection_failure), null));
                        }
                    });
                }

                token.removeObserver(this);
            }
        });

        return submission;
    }
}
