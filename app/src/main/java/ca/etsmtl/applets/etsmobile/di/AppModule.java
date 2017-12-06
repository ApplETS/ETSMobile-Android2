package ca.etsmtl.applets.etsmobile.di;

import android.app.Application;

import javax.inject.Singleton;

import ca.etsmtl.applets.etsmobile.http.MoodleWebService;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleRepository;
import ca.etsmtl.applets.etsmobile.view_model.MoodleViewModelFactory;
import ca.etsmtl.applets.etsmobile2.R;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sonphil on 07-09-17.
 */
@Module
public class AppModule {
    private Application app;

    public AppModule(Application application) {
        app = application;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    public MoodleRepository provideMoodleRepository(Application app, MoodleWebService moodleWebService) {
        return new MoodleRepository(app, moodleWebService);
    }

    @Provides
    @Singleton
    public MoodleViewModelFactory provideMoodleViewModelFactory(Application app, MoodleRepository moodleRepository) {
        return new MoodleViewModelFactory(app, moodleRepository);
    }

    @Provides
    @Singleton
    public MoodleWebService provideMoodleService() {
        return new Retrofit.Builder()
                .baseUrl(app.getString(R.string.moodle_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MoodleWebService.class);
    }
}
