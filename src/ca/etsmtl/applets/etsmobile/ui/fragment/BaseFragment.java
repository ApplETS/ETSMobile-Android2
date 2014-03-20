package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Phil on 17/11/13.
 */
public abstract class BaseFragment extends Fragment {

	@InjectView(R.id.progressBar)
	ProgressBar progressBar;
	
	public BaseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View onCreateView = super.onCreateView(inflater, container, savedInstanceState);
		ButterKnife.inject(onCreateView);
		return onCreateView;
	}
}
