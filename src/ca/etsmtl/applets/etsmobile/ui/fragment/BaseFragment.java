package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.Fragment;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;

/**
 * Created by Phil on 17/11/13.
 */
public abstract class BaseFragment extends Fragment {
	public BaseFragment() {
		// TODO Auto-generated constructor stub
	}

	protected UserCredentials getUserCredentials() {
		return null;
	}

}
