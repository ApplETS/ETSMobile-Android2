package ca.etsmtl.applets.etsmobile.view_model;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleProfile;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleRepository;
import ca.etsmtl.applets.etsmobile.model.RemoteResource;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Sonphil on 09-09-17.
 */
@RunWith(JUnit4.class)
public class MoodleViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private MoodleViewModel viewModel;
    private MoodleRepository repository;

    @Before
    public void setUp() {
        repository = mock(MoodleRepository.class);
        viewModel = new MoodleViewModel(new Application(), repository);
    }

    @Test
    public void testNull() {
        assertThat(repository, notNullValue());
        assertThat(viewModel, notNullValue());
    }

    @Test
    public void getProfile() {
        MutableLiveData<RemoteResource<MoodleProfile>> liveData = new MutableLiveData<>();
        when(repository.getProfile()).thenReturn(liveData);
        Observer<RemoteResource<MoodleProfile>> observer = mock(Observer.class);
        viewModel.getProfile().observeForever(observer);
        verify(observer, never()).onChanged(any(RemoteResource.class));
        RemoteResource<MoodleProfile> remoteRes = RemoteResource.loading(null);
        liveData.setValue(remoteRes);
        verify(observer).onChanged(remoteRes);
        reset(observer);
        remoteRes = RemoteResource.success(new MoodleProfile());
        liveData.setValue(remoteRes);
        verify(observer).onChanged(remoteRes);
    }
}
