package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.model.MoodleProfile;
import ca.etsmtl.applets.etsmobile.model.MoodleToken;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Open the moodle Application
 * 
 * @author Laurence
 * 
 */
public class MoodleFragment extends HttpFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_moodle, container, false);
		((Button) v.findViewById(R.id.activity_moodle_button)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoodle();
            }
        });


        queryMoodleToken();

		return v;
	}



    @Override
    void updateUI() {

    }

    @Override
    public void onRequestFailure(SpiceException e) {
        super.onRequestFailure(e);
    }

    @Override
    public void onRequestSuccess(Object o) {
        if(o instanceof MoodleToken){
            MoodleToken moodleToken = (MoodleToken) o;

            queryMoodleProfile(moodleToken);

            Log.e("Token Moodle","TOKEN : "+moodleToken.getToken());
        }

        if(o instanceof MoodleProfile) {
            MoodleProfile moodleProfile = (MoodleProfile) o;
            Log.e("Profil Moodle","PROFIL : "+moodleProfile.getUsername()+" "+moodleProfile.getUserId());
        }

    }

    private void queryMoodleProfile(final MoodleToken moodleToken) {
        SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {

            @Override
            public MoodleProfile loadDataFromNetwork() throws Exception {
                String url = getActivity().getString(R.string.moodle_api_get_siteinfo, moodleToken.getToken());

                return getRestTemplate().getForObject(url, MoodleProfile.class);
            }
        };

        dataManager.sendRequest(request, MoodleFragment.this);
    }

    private void queryMoodleToken() {
        SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {

            @Override
            public MoodleToken loadDataFromNetwork() throws Exception {
                String url = getActivity().getString(R.string.moodle_api_get_token, ApplicationManager.userCredentials.getUsername(), ApplicationManager.userCredentials.getPassword());

                return getRestTemplate().getForObject(url, MoodleToken.class);
            }
        };

        dataManager.sendRequest(request, MoodleFragment.this);

    }


    private void openMoodle() {
		Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getString(R.string.moodle));
		if (intent != null) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			// bring user to the market
			// or let them choose an app?
			intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("market://details?id=" + getString(R.string.moodle)));
			startActivity(intent);
		}
	}

}
