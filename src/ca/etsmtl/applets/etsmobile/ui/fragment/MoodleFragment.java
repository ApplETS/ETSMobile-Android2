package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Open the moodle Application
 * 
 * @author Laurence
 * 
 */
public class MoodleFragment extends BaseFragment implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		openMoodle();
		View v = inflater.inflate(R.layout.fragment_moodle, container, false);
		((Button) v.findViewById(R.id.activity_moodle_button))
				.setOnClickListener(this);

		return v;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.activity_moodle_button) {
			openMoodle();
		}
	}

	private void openMoodle() {
		Intent intent = getActivity().getPackageManager()
				.getLaunchIntentForPackage(getString(R.string.moodle));
		if (intent != null) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			// bring user to the market
			// or let them choose an app?
			intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("market://details?id="
					+ getString(R.string.moodle)));
			startActivity(intent);
		}
	}

}
