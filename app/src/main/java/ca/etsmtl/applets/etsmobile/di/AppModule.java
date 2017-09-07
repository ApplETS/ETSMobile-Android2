package ca.etsmtl.applets.etsmobile.di;

import android.app.Application;

import com.google.gson.Gson;

import javax.inject.Singleton;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleRepository;
import ca.etsmtl.applets.etsmobile.view_model.MoodleViewModelFactory;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Sonphil on 07-09-17.
 */
@Module
public class AppModule {
    Application app;

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
    public MoodleRepository provideMoodleRepository() {
        return new MoodleRepository(app);
    }


    @Provides
    @Singleton
    public MoodleViewModelFactory provideMoodleViewModelFactory() {
        return new MoodleViewModelFactory(app, provideMoodleRepository());
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return new Gson();
    }
}
