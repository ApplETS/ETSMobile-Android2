package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Phil on 18/11/13.
 */
public class SponsorsFragment extends WebFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_sponsors, container, false);
		return v;
	}
}
