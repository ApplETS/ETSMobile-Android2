package ca.etsmtl.applets.etsmobile.model.Moodle;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.MoodleCourseDao;
import ca.etsmtl.applets.etsmobile.db.MoodleProfileDao;
import ca.etsmtl.applets.etsmobile.http.MoodleWebService;
import ca.etsmtl.applets.etsmobile.model.ApiResponse;
import ca.etsmtl.applets.etsmobile.model.NetworkBoundResource;
import ca.etsmtl.applets.etsmobile.model.RemoteResource;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile2.R;

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

            LiveData<ApiResponse<MoodleToken>> apiResponseLiveData = moodleWebService.getToken(ApplicationManager.userCredentials.getUsername(), ApplicationManager.userCredentials.getPassword());
            apiResponseLiveData.observeForever(new Observer<ApiResponse<MoodleToken>>() {
                @Override
                public void onChanged(@Nullable ApiResponse<MoodleToken> moodleTokenApiResponse) {
                    if (moodleTokenApiResponse != null) {
                        MoodleToken moodleToken = moodleTokenApiResponse.body;
                        if (moodleTokenApiResponse.isSuccessful() && moodleToken != null && moodleToken.getToken() != null && !moodleToken.getToken().isEmpty()) {
                            ApplicationManager.userCredentials.setMoodleToken(moodleToken.getToken());
                            token.setValue(RemoteResource.success(moodleToken));
                        } else
                            token.setValue(RemoteResource.error(context.getString(R.string.moodle_error_cant_get_token), moodleTokenApiResponse.body));

                        apiResponseLiveData.removeObserver(this);
                    }
                }
            });
        }
        return token;
    }

    public LiveData<RemoteResource<MoodleProfile>> getProfile() {
        return new NetworkBoundResource<MoodleProfile, MoodleProfile>() {
            @Override
            protected void saveCallResult(@NonNull MoodleProfile item) {
                moodleProfileDao.insert(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable MoodleProfile data) {
                return Utility.isNetworkAvailable(context);
            }

            @NonNull
            @Override
            protected LiveData<MoodleProfile> loadFromDb() {
                return moodleProfileDao.find();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MoodleProfile>> createCall() {
                MediatorLiveData<ApiResponse<MoodleProfile>> mediatorLiveData = new MediatorLiveData<>();
                LiveData<RemoteResource<MoodleToken>> tokenLiveData = getToken();
                mediatorLiveData.addSource(tokenLiveData, token -> {
                    if (token != null && token.status == RemoteResource.SUCCESS) {
                        mediatorLiveData.removeSource(tokenLiveData);
                        mediatorLiveData.addSource(moodleWebService.getProfile(ApplicationManager.userCredentials.getMoodleToken()), new Observer<ApiResponse<MoodleProfile>>() {
                            @Override
                            public void onChanged(@Nullable ApiResponse<MoodleProfile> moodleProfileApiResponse) {
                                mediatorLiveData.setValue(moodleProfileApiResponse);
                            }
                        });
                    } else if (token != null && token.status == RemoteResource.ERROR) {
                        String errorMsg = token.message != null ? token.message : context.getString(R.string.moodle_error_cant_get_token);
                        mediatorLiveData.setValue(new ApiResponse<>(new Throwable(errorMsg)));
                        mediatorLiveData.removeSource(tokenLiveData);
                    }
                });

                return mediatorLiveData;
            }
        }.asLiveData();
    }

    public LiveData<RemoteResource<List<MoodleCourse>>> getCourses() {

        return new NetworkBoundResource<List<MoodleCourse>, List<MoodleCourse>>() {
            @Override
            protected void saveCallResult(@NonNull List<MoodleCourse> item) {

                moodleCourseDao.insertAll(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<MoodleCourse> data) {
                return Utility.isNetworkAvailable(context);
            }

            @NonNull
            @Override
            protected LiveData<List<MoodleCourse>> loadFromDb() {
                return moodleCourseDao.getAll();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<MoodleCourse>>> createCall() {
                MediatorLiveData<ApiResponse<List<MoodleCourse>>> mediatorLiveData = new MediatorLiveData<>();
                LiveData<RemoteResource<MoodleToken>> tokenLiveData = getToken();
                mediatorLiveData.addSource(tokenLiveData, moodleToken -> {
                    if (moodleToken != null && moodleToken.status == RemoteResource.SUCCESS) {
                        mediatorLiveData.removeSource(tokenLiveData);
                        LiveData<RemoteResource<MoodleProfile>> profileLiveData = getProfile();
                        mediatorLiveData.addSource(profileLiveData, moodleProfile -> {
                            if (moodleProfile != null && moodleProfile.status == RemoteResource.SUCCESS) {
                                mediatorLiveData.removeSource(profileLiveData);
                                mediatorLiveData.addSource(moodleWebService.getCourses(moodleToken.data.getToken(), moodleProfile.data.getUserId()), listApiResponse -> mediatorLiveData.setValue(listApiResponse));
                            } else if (moodleProfile != null && moodleProfile.status == RemoteResource.ERROR) {
                                String errorMsg = moodleProfile.message != null ? moodleProfile.message : context.getString(R.string.moodle_error_cant_get_profile);
                                mediatorLiveData.setValue(new ApiResponse<>(new Throwable(errorMsg)));
                                mediatorLiveData.removeSource(profileLiveData);
                            }
                        });
                    } else if (moodleToken != null && moodleToken.status == RemoteResource.ERROR) {
                        String errorMsg = moodleToken.message != null ? moodleToken.message : context.getString(R.string.moodle_error_cant_get_token);
                        mediatorLiveData.setValue(new ApiResponse<>(new Throwable(errorMsg)));
                        mediatorLiveData.removeSource(tokenLiveData);
                    }
                });

                return mediatorLiveData;

            }
        }.asLiveData();
    }

    public LiveData<RemoteResource<List<MoodleAssignmentCourse>>> getAssignmentCourses(final int[] coursesIds) {
        MutableLiveData<RemoteResource<List<MoodleAssignmentCourse>>> coursesMutableLiveData = new MutableLiveData<>();
        coursesMutableLiveData.setValue(RemoteResource.loading(null));

        String tokenStr = ApplicationManager.userCredentials.getMoodleToken();
        LiveData<ApiResponse<MoodleAssignmentCourses>> assignmentCoursesLiveData = moodleWebService.getAssignmentCourses(tokenStr, coursesIds);
        assignmentCoursesLiveData.observeForever(new Observer<ApiResponse<MoodleAssignmentCourses>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<MoodleAssignmentCourses> response) {
                if (response != null && response.isSuccessful() && response.body.getCourses() != null && response.body.getCourses().size() > 0) {
                    coursesMutableLiveData.setValue(RemoteResource.success(response.body.getCourses()));
                } else {
                    coursesMutableLiveData.setValue(RemoteResource.error(response.errorMessage, null));
                }

                assignmentCoursesLiveData.removeObserver(this);
            }
        });

        return coursesMutableLiveData;
    }

    public LiveData<RemoteResource<MoodleAssignmentSubmission>> getAssignmentSubmission(final int assignId) {
        final MutableLiveData<RemoteResource<MoodleAssignmentSubmission>> submission = new MutableLiveData<>();
        submission.setValue(RemoteResource.loading(null));

        String token = ApplicationManager.userCredentials.getMoodleToken();
        LiveData<ApiResponse<MoodleAssignmentSubmission>> submissionApi = moodleWebService.getAssignmentSubmission(token, assignId);
        submissionApi.observeForever(new Observer<ApiResponse<MoodleAssignmentSubmission>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<MoodleAssignmentSubmission> submissionApiResponse) {
                if (submissionApiResponse != null && submissionApiResponse.isSuccessful() && submissionApiResponse.body != null)
                    submission.setValue(RemoteResource.success(submissionApiResponse.body));
                else {
                    if (submissionApiResponse != null && submissionApiResponse.errorMessage != null)
                        submission.setValue(RemoteResource.error(submissionApiResponse.errorMessage, submissionApiResponse.body));
                    else
                        submission.setValue(RemoteResource.error("", null));
                }

                submissionApi.removeObserver(this);
            }
        });

        return submission;
    }
}
