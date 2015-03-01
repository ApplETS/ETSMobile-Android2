package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SearchView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.FicheEmploye;
import ca.etsmtl.applets.etsmobile.ui.activity.BottinDetailsActivity;
import ca.etsmtl.applets.etsmobile.ui.adapter.ExpandableListAdapter;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * 
 * @author Thibaut
 */
public class BottinFragment extends HttpFragment implements SearchView.OnQueryTextListener  {

	private SearchView searchView;
	private ExpandableListAdapter listAdapter;

	ExpandableListView expListView;

	private List<String> listDataHeader;
	private HashMap<String, List<FicheEmploye>> listDataChild;
	private ProgressDialog mProgressDialog;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_bottin, menu);
		
		// Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menuitem_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);
		
		super.onCreateOptionsMenu(menu, inflater);

	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		
		case R.id.menu_item_update:
			
			final Dialog dialog = new Dialog(getActivity());
		    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		    dialog.setContentView(R.layout.dialog_bottin);
		    Button btn_yes = (Button)dialog.findViewById(R.id.btn_dialog_bottin_yes);
		    
		    //Rechargement du bottin
		    btn_yes.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {

					//Suppression du bottin
	            	File dir = getActivity().getFilesDir();
	    			File file = new File(dir, "bottin.ser");
	    			boolean deleted = file.delete();
	    			
	    			//Mise à jour de la liste
	    			listDataHeader = new ArrayList<String>();
	    			listDataChild = new HashMap<String, List<FicheEmploye>>();
	    			
	    			listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
	    			
	    			dialog.dismiss();
	    			expListView.setAdapter(listAdapter);
	    			updateUI();
					
				}
			});
		    
		    Button btn_no = (Button) dialog.findViewById(R.id.btn_dialog_bottin_no);
		    btn_no.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		    dialog.show();
			
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup)inflater.inflate(R.layout.fragment_bottin, container, false);
        super.onCreateView(inflater, v, savedInstanceState);
		// get the listview
		expListView = (ExpandableListView) v.findViewById(R.id.expandableListView_service_employe);
		
		//Ouverture du détail
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				FicheEmploye ficheEmploye = (FicheEmploye) listAdapter.getChild(groupPosition, childPosition);





                Intent i = new Intent(getActivity(), BottinDetailsActivity.class);

                i.putExtra("nom", ficheEmploye.Nom);
                i.putExtra("prenom", ficheEmploye.Prenom);
                i.putExtra("telBureau", ficheEmploye.TelBureau);
                i.putExtra("emplacement", ficheEmploye.Emplacement);
                i.putExtra("courriel", ficheEmploye.Courriel);
                i.putExtra("service", ficheEmploye.Service);
                i.putExtra("titre", ficheEmploye.Titre);


                getActivity().startActivity(i);




//                Fragment fragment = BottinDetailsFragment.newInstance(ficheEmploye);
//
//				showFragment(fragment);
				
				return true;
			}
		});

		// create empty data
		listDataChild = new HashMap<String, List<FicheEmploye>>();
		listDataHeader = new ArrayList<String>();

		// create custom adapter
		listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

		// setting list adapter
		expListView.setAdapter(listAdapter);


		return v;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	void updateUI() {
		try {
			Activity activity = getActivity();
			FileInputStream input = activity.openFileInput("bottin.ser");

			ObjectInputStream ois = new ObjectInputStream(input);
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

			if (activity != null) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						listAdapter.notifyDataSetChanged();
					}
				});
			}


		} catch (FileNotFoundException e) {
		  
			if (Utility.isNetworkAvailable(getActivity())) {
				try {

					mProgressDialog = ProgressDialog.show(getActivity(), null,"Chargement du bottin en cours", true);
					mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					DataManager datamanager = DataManager.getInstance(getActivity());
					datamanager.getDataFromSignet(
							SignetMethods.BOTTIN_GET_LIST_SERVICE_AND_EMP,
							ApplicationManager.userCredentials, this);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {
				Toast.makeText(getActivity(), "Une connexion internet est requise pour télécharger le bottin", Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onRequestSuccess(Object o) {
		super.onRequestSuccess(o);
		if (o instanceof HashMap<?, ?>) {
			@SuppressWarnings("unchecked")
			HashMap<String, List<FicheEmploye>> listeEmployeByService = (HashMap<String, List<FicheEmploye>>) o;

			for (String nomService : listeEmployeByService.keySet()) {

				List<FicheEmploye> listeEmployes = listeEmployeByService.get(nomService);

				if (listeEmployes.size() != 0) {

					listDataHeader.add(nomService);
					listDataChild.put(nomService, listeEmployeByService.get(nomService));
				}

			}
			Collections.sort(listDataHeader);

			FileOutputStream output;
			try {
				output = getActivity().openFileOutput("bottin.ser", getActivity().MODE_PRIVATE);

				ObjectOutputStream oos = new ObjectOutputStream(output);
				oos.writeObject(listDataChild);
				oos.close();
				if (output != null)
					output.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

			Activity activity = getActivity();
			if (activity != null) {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						listAdapter.notifyDataSetChanged();
						mProgressDialog.dismiss();
					}
				});
			}
		}
	}


    @Override
    public void onRequestFailure(SpiceException e) {
        super.onRequestFailure(e);
    }

    private void showFragment(final Fragment fragment) {
		if (fragment == null)
			return;

		final FragmentManager fm = getActivity().getFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.content_frame, fragment);
//		ft.addToBackStack(null);
		ft.commit();
	}




	@Override
	public boolean onQueryTextSubmit(String query) {
//		listAdapter.filterData(query);
		return false;
	}



	@Override
	public boolean onQueryTextChange(String newText) {
//		if (newText.equals("")) {
			listAdapter.filterData(newText);
//        }
//		listAdapter.filterData(newText);
		return true;
	}
	

}
