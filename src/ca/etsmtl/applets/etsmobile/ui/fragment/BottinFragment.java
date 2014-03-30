package ca.etsmtl.applets.etsmobile.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.ArrayOfFicheEmploye;
import ca.etsmtl.applets.etsmobile.model.ArrayOfService;
import ca.etsmtl.applets.etsmobile.ui.adapter.ExpandableListAdapter;
import ca.etsmtl.applets.etsmobile2.R;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Phil on 17/11/13.
 */
public class BottinFragment extends HttpFragment {

	private ListView mListView;
	private ArrayOfService arrayOfService;
	private ArrayOfFicheEmploye arrayOfFicheEmploye;
	
	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private HashMap<String, List<String>> listDataChild;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		View v = inflater.inflate(R.layout.activity_bottin, container, false);
//		mListView = (ListView) v.findViewById(R.id.activity_bottin_listview);
		
		// get the listview
        expListView = (ExpandableListView) v.findViewById(R.id.expandableListView_service_employe);
 
//        // preparing list data
//        prepareListData();
 
        listDataChild = new HashMap<String, List<String>>();
        listDataHeader = new ArrayList<String>();
        
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
 
        // setting list adapter
        expListView.setAdapter(listAdapter);
		
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
				
				DataManager datamanager = DataManager.getInstance(getActivity());
				datamanager.getDataFromSignet(SignetMethods.BOTTIN_GET_FICHE_BY_SERVICE, ApplicationManager.userCredentials, this,""+arrayOfService.get(i).ServiceCode);
				
//				Log.e("TEST", arrayOfService.get(i).ServiceCode+" -- "+ arrayOfService.get(i).Nom);
			}
			
		}
		if(o instanceof ArrayOfFicheEmploye) {
			arrayOfFicheEmploye = (ArrayOfFicheEmploye) o ;
			
			if (arrayOfFicheEmploye.size() != 0) {
				listDataHeader.add(arrayOfFicheEmploye.get(0).Service);
				// System.out.println(arrayOfFicheEmploye.get(0).Service);

				
				List<String> listeEmploye = new ArrayList<String>();
				
				for (int i = 0; i < arrayOfFicheEmploye.size(); i++) {
					// System.out.println(arrayOfFicheEmploye.get(i).Nom+
					// " "+arrayOfFicheEmploye.get(i).Prenom);

					
					listeEmploye.add(arrayOfFicheEmploye.get(i).Nom + " "
							+ arrayOfFicheEmploye.get(i).Prenom);

					listDataChild.put(arrayOfFicheEmploye.get(0).Service,listeEmploye);
					

				}
				
//				for(String s : listDataChild.get(arrayOfFicheEmploye.get(0).Service)) {
//					System.out.println(s);
//				}
				
				Activity activity = getActivity();
				if(activity!=null){
					getActivity().runOnUiThread( new Runnable() {
						public void run() {
							listAdapter.notifyDataSetChanged();
						}
					});
				}
				
				
			}
			
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
