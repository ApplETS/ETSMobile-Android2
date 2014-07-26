package ca.etsmtl.applets.etsmobile.ui.fragment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.FicheEmploye;
import ca.etsmtl.applets.etsmobile.ui.adapter.ExpandableListAdapter;
import ca.etsmtl.applets.etsmobile.views.LoadingView;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * 
 * @author Philippe David, Thibau
 */
public class BottinFragment extends HttpFragment {

	private ExpandableListAdapter listAdapter;

	ExpandableListView expListView;

	private List<String> listDataHeader;
	private HashMap<String, List<FicheEmploye>> listDataChild;
	private ProgressDialog mProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//layoutId = R.layout.fragment_bottin;
	}

	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the options menu from XML
		Activity activity = getActivity();
		inflater.inflate(R.menu.menu_bottin, menu);

		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_botin_search).getActionView();
		// Assumes current activity is the searchable activity
		searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
		searchView.setIconifiedByDefault(false); // Do not iconify the widget;
													// expand it by default
													// return true;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup)inflater.inflate(R.layout.fragment_bottin, container, false);
        super.onCreateView(inflater, v, savedInstanceState);
		// get the listview
		expListView = (ExpandableListView) v.findViewById(R.id.expandableListView_service_employe);

		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

				List<FicheEmploye> listeEmployes = listDataChild.get(listDataHeader.get(groupPosition));

				FicheEmploye ficheEmploye = listeEmployes.get(childPosition);

				Fragment fragment = BottinDetailsFragment.newInstance(ficheEmploye);

				showFragment(fragment);

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
            LoadingView.hideLoadingView(loadingView);

		} catch (FileNotFoundException e) {

			try {
				mProgressDialog = ProgressDialog.show(getActivity(), null, "Chargement du bottin en cours", true);
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				dataManager.getDataFromSignet(SignetMethods.BOTTIN_GET_LIST_SERVICE_AND_EMP,
						ApplicationManager.userCredentials, this);
			} catch (Exception e1) {
				e1.printStackTrace();
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

	private void showFragment(final Fragment fragment) {
		if (fragment == null)
			return;

		// Begin a fragment transaction.
		final FragmentManager fm = getActivity().getFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();
		// We can also animate the changing of fragment.
		// ft.setCustomAnimations(android.R.anim.slide_in_left,
		// android.R.anim.slide_out_right);
		// Replace current fragment by the new one.
		ft.replace(R.id.content_frame, fragment);
		// Null on the back stack to return on the previous fragment when user
		// press on back button.
		ft.addToBackStack(null);

		// Commit changes.
		ft.commit();
	}

}
