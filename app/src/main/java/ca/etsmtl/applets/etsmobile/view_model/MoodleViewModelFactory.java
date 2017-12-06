package ca.etsmtl.applets.etsmobile.view_model;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleRepository;

/**
 * Created by Sonphil on 07-09-17.
 */
@Singleton
public class MoodleViewModelFactory implements ViewModelProvider.Factory {

    private static final String TAG = "MoodleViewModelFactory";

    private Application application;
    private MoodleRepository repository;

    public MoodleViewModelFactory(Application application, MoodleRepository repository) {
        this.application = application;
        this.repository = repository;
    }

    @NonNull
    @Override
    public MoodleViewModel create(@NonNull Class modelClass) {
        return new MoodleViewModel(application, repository);
    }
}
