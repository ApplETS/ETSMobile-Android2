package ca.etsmtl.applets.etsmobile.ui.fragment;

import org.achartengine.GraphicalView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ca.etsmtl.applets.etsmobile2.R;
import ca.etsmtl.applets.etsmobile2.R.id;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Phil on 17/11/13.
 */
public class BandwithFragment extends HttpFragment {

	private GraphicalView mGraphicalView;
	private String app; 
	private String phase;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_bandwith, container, false);
		mGraphicalView =(GraphicalView) v.findViewById(R.id.chart);
		final EditText editTextApp = (EditText) v.findViewById(R.id.bandwith_editText_app);
		final EditText editTextPhase = (EditText) v.findViewById(R.id.bandwith_editText_phase);
		
		editTextApp.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() >= 1) {
					if (editTextApp.getText().length() > 4) {
						if(editTextPhase.length()>0){
							//Call service 
						}
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
		});
		
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
