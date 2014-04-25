package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ButterKnife.inject(view);
	}
}
