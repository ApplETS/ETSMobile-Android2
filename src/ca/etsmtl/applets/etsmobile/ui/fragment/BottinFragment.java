package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.ArrayOfService;
import ca.etsmtl.applets.etsmobile2.R;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Phil on 17/11/13.
 */
public class BottinFragment extends HttpFragment {

	private ListView mListView;
	private ArrayOfService arrayOfService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		View v = inflater.inflate(R.layout.activity_bottin, container, false);
		mListView = (ListView) v.findViewById(R.id.activity_bottin_listview);
		
//		Personne p = new Personne(inObj, envelope)
		
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		DataManager datamanager = DataManager.getInstance(getActivity());
		datamanager.getDataFromSignet(SignetMethods.BOTTIN_LIST_DEPT, ApplicationManager.userCredentials, this);
	}
	
	@Override
	public void onRequestFailure(SpiceException arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestSuccess(Object o) {
		if(o instanceof ArrayOfService){
			
			arrayOfService = (ArrayOfService) o;
			
			for(int i = 0 ; i < arrayOfService.size(); i++) {
				Log.e("TEST", arrayOfService.get(i).Nom);
			}
			
			
			
//			arrayOfFicheEmploye = (ArrayOfFicheEmploye) o;
//			refresh();
			
		}
		

	} 
	
//	private void refresh(){
//		
//		final BottinAdapter bottinAdapter = new BottinAdapter(getActivity(),R.id.activity_bottin_listview, arrayOfService);
//		Activity activity = getActivity();
//		if(activity!=null){
//			getActivity().runOnUiThread( new Runnable() {
//				public void run() {
//					mListView.setAdapter(bottinAdapter);
//				}
//			});
//		}
//	}

	@Override
	void updateUI() {
		// TODO Auto-generated method stub

	}
}
