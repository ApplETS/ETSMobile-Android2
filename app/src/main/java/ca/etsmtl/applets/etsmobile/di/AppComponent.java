package ca.etsmtl.applets.etsmobile.di;

import javax.inject.Singleton;

import ca.etsmtl.applets.etsmobile.ui.activity.MoodleAssignmentsActivity;
import ca.etsmtl.applets.etsmobile.ui.fragment.MoodleFragment;
import dagger.Component;

/**
 * Created by Sonphil on 07-09-17.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MoodleFragment moodleFragment);

    void inject(MoodleAssignmentsActivity moodleAssignmentsActivity);
}
