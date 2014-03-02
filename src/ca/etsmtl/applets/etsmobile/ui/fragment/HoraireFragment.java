package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ca.etsmtl.applets.etsmobile2.R;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Phil on 17/11/13. content create by Laurence on 07/02/14
 */
public class HoraireFragment extends HttpFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_calendar, container, false);

		long startMillis = java.lang.System.currentTimeMillis();
		Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
		builder.appendPath("time");
		ContentUris.appendId(builder, startMillis);

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(builder.build());

		startActivity(intent);
		return v;
	}

	@Override
	public void onRequestFailure(SpiceException arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestSuccess(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	void updateUI() {
		// TODO Auto-generated method stub

	}

}
