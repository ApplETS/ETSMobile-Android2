package ca.etsmtl.applets.etsmobile.ui.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.ListeDesElementsEvaluation;
import ca.etsmtl.applets.etsmobile.ui.adapter.MyCourseDetailAdapter;
import ca.etsmtl.applets.etsmobile2.R;

import com.octo.android.robospice.persistence.exception.SpiceException;

public class NotesDetailsFragment extends HttpFragment{
	
	
	public static String SIGLE = "SIGLE";
	public static String SESSION= "SESSION";
	public static String COTE = "COTE";
	public static String GROUPE = "GROUPE";
	
	private ListView mlistView;
	private String cote;
	private String sigle;
	private String session; 
	private String groupe;
	

	
	public static NotesDetailsFragment newInstance(String sigle, String session, String cote, String groupe){
		NotesDetailsFragment fragment = new NotesDetailsFragment();
		Bundle args = new Bundle();
		args.putString(SIGLE, sigle);
		args.putString(SESSION, session);
		args.putString(COTE, cote);
		args.putString(GROUPE, groupe);
		fragment.setArguments(args);
		return fragment;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 if(getArguments()!=null){
				Bundle bundle = getArguments();
				sigle = bundle.getString(SIGLE);
				cote = bundle.getString(COTE);
				session = bundle.getString(SESSION);
				groupe = bundle.getString(GROUPE);
			}
			
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.notes_details_fragment, container, false);
		((TextView)v.findViewById(R.id.notes_fragment_sigle)).setText(sigle);
		mlistView = (ListView) v.findViewById(android.R.id.list);
		return v;
	}
	
	
	@Override
	public void onStart() {
		DataManager.getInstance(getActivity()).getDataFromSignet(SignetMethods.LIST_EVAL, ApplicationManager.userCredentials, this, session, groupe, sigle);
		super.onStart();
	}

	@Override
	public void onRequestFailure(SpiceException arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRequestSuccess(Object o) {
		if(o instanceof ListeDesElementsEvaluation){
			ListeDesElementsEvaluation courseEvaluation = (ListeDesElementsEvaluation)o;
			Log.v("NotesDetailsFragment", "NotesDetailsFragment: list ="+courseEvaluation.liste.size() +" cote="+cote);
				refresh(courseEvaluation, cote);
		}
		
	}

	private void refresh(ListeDesElementsEvaluation courseEvaluation, String cote ){
		final MyCourseDetailAdapter myCourseDetailAdapter = new MyCourseDetailAdapter(getActivity(), courseEvaluation, cote);
		Activity activity = getActivity();
		if(activity!=null){
			getActivity().runOnUiThread( new Runnable() {
				public void run() {
					mlistView.setAdapter(myCourseDetailAdapter);
				}
			});
		}
	}
	@Override
	void updateUI() {
		// TODO Auto-generated method stub
		
	}

}
