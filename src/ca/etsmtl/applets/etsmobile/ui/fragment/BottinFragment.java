package ca.etsmtl.applets.etsmobile.ui.fragment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
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
	private ProgressDialog mProgressDialog;
	
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
		
		try {
			Activity activity = getActivity();
			FileInputStream input = activity.openFileInput("bottin.ser");
			
			
			int value;
			ObjectInputStream ois = new ObjectInputStream(input);
//			listDataChild.clear();
			listDataChild = (HashMap) ois.readObject();
			ois.close();
			
			listDataHeader.clear();
			
			listDataHeader.addAll(listDataChild.keySet());
			
			Collections.sort(listDataHeader);
			
			listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
			 
	        // setting list adapter
	        expListView.setAdapter(listAdapter);
			
			if (input != null)
				input.close();
			
			
			
			if(activity!=null){
				activity.runOnUiThread( new Runnable() {
					public void run() {
						listAdapter.notifyDataSetChanged();
					}
				});
			}
			
			
		} catch (FileNotFoundException e) {
		  
			try {
				mProgressDialog = ProgressDialog.show(getActivity(), null,"Chargement du bottin en cours", true);
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				DataManager datamanager = DataManager.getInstance(getActivity());
//				datamanager.getDataFromSignet(SignetMethods.BOTTIN_LIST_DEPT, ApplicationManager.userCredentials, this);
				datamanager.getDataFromSignet(SignetMethods.BOTTIN_GET_LIST_SERVICE_AND_EMP, ApplicationManager.userCredentials, this);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		} catch (Exception e) {
		  e.printStackTrace();
		}

	}
	
	@Override
	public void onRequestFailure(SpiceException arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestSuccess(Object o) {
		
		
		
		if(o instanceof HashMap<?, ?>){
			
			HashMap<String, List<FicheEmploye>> listeEmployeByService = (HashMap<String, List<FicheEmploye>>) o; 
			
			for(String nomService : listeEmployeByService.keySet()) {
				
				List<FicheEmploye> listeEmployes = listeEmployeByService.get(nomService);
				
				if(listeEmployes.size() != 0) {
					
					listDataHeader.add(nomService);
					listDataChild.put(nomService,listeEmployeByService.get(nomService));
				}
				
			}
			
			Collections.sort(listDataHeader);
			
			FileOutputStream output;
			try {
				output = getActivity().openFileOutput("bottin.ser",
						getActivity().MODE_PRIVATE);

				ObjectOutputStream oos = new ObjectOutputStream(output);
				oos.writeObject(listDataChild);
				oos.close();
				if (output != null)
					output.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			Activity activity = getActivity();
			if(activity!=null){
				getActivity().runOnUiThread( new Runnable() {
					public void run() {
						listAdapter.notifyDataSetChanged();
						mProgressDialog.dismiss();
					}
				});
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
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("onPause");
		
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("onStop");
		
	}
}
