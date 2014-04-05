package ca.etsmtl.applets.etsmobile.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.ArrayOfFicheEmploye;
import ca.etsmtl.applets.etsmobile.model.ArrayOfService;
import ca.etsmtl.applets.etsmobile.model.FicheEmploye;
import ca.etsmtl.applets.etsmobile.ui.adapter.ExpandableListAdapter;
import ca.etsmtl.applets.etsmobile2.R;

import com.google.android.gms.internal.fi;
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
	private HashMap<String, List<FicheEmploye>> listDataChild;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		View v = inflater.inflate(R.layout.fragment_bottin, container, false);
//		mListView = (ListView) v.findViewById(R.id.activity_bottin_listview);
		
		// get the listview
        expListView = (ExpandableListView) v.findViewById(R.id.expandableListView_service_employe);
 
        expListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				List<FicheEmploye> listeEmployes = listDataChild.get(listDataHeader.get(groupPosition)); 
				
				FicheEmploye ficheEmploye = listeEmployes.get(childPosition);
				
//				Log.e("test",ficheEmploye.Nom+" "+ficheEmploye.Prenom);
				
				Fragment fragment = BottinDetailsFragment.newInstance(ficheEmploye);
				
				
				
				showFragment(fragment);
				
				return true;
			}
		});
				
				
//        // preparing list data
//        prepareListData();
 
        listDataChild = new HashMap<String, List<FicheEmploye>>();
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

				
				List<FicheEmploye> listeEmploye = new ArrayList<FicheEmploye>();
				
				for (int i = 0; i < arrayOfFicheEmploye.size(); i++) {
					// System.out.println(arrayOfFicheEmploye.get(i).Nom+
					// " "+arrayOfFicheEmploye.get(i).Prenom);

					
					listeEmploye.add(arrayOfFicheEmploye.get(i));

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
	
	private void showFragment(final Fragment fragment) {
		if (fragment == null)
			return;

		// Begin a fragment transaction.
		final FragmentManager fm = getActivity().getFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();
		// We can also animate the changing of fragment.
//		ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		// Replace current fragment by the new one.
		ft.replace(R.id.content_frame, fragment);
		// Null on the back stack to return on the previous fragment when user
		// press on back button.
		ft.addToBackStack(null);

		// Commit changes.
		ft.commit();
	}

	@Override
	void updateUI() {
		// TODO Auto-generated method stub

	}
}
