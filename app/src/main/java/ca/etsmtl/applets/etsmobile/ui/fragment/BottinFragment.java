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
import android.util.Log;
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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.FicheEmploye;
import ca.etsmtl.applets.etsmobile.ui.activity.BottinDetailsActivity;
import ca.etsmtl.applets.etsmobile.ui.adapter.ExpandableListAdapter;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
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

    private static BottinFragment instance;


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
    public String getFragmentTitle() {
        return getString(R.string.menu_section_2_bottin);
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
                        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                        try {
                            Dao<FicheEmploye, ?> ficheEmployeDao = dbHelper.getDao(FicheEmploye.class);
                            List<FicheEmploye> ficheEmployeList = ficheEmployeDao.queryForAll();
                            for (FicheEmploye ficheEmploye : ficheEmployeList) {
                                ficheEmployeDao.delete(ficheEmploye);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        //Mise à jour de la liste
                        listDataHeader = new ArrayList<>();
                        listDataChild = new HashMap<>();

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
        listDataChild = new HashMap<>();
        listDataHeader = new ArrayList<>();

        // create custom adapter
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        return v;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    void updateUI() {
        // Get le contenu dans la bd
        Activity activity = getActivity();
        DatabaseHelper dbHelper = new DatabaseHelper(activity);

        try {
            // Création du queryBuilder, permettant de lister les employés par leur nom de service
            QueryBuilder<FicheEmploye, String> queryBuilder = (QueryBuilder<FicheEmploye, String>) dbHelper.getDao(FicheEmploye.class).queryBuilder();

            queryBuilder.orderBy("Service", true);
            PreparedQuery<FicheEmploye> preparedQuery = queryBuilder.prepare();
            List<FicheEmploye> listEmployes = dbHelper.getDao(FicheEmploye.class).query(preparedQuery);

            // Si le contenu n'est pas vide, l'ajouter au listDataHeader et listDataChild
            if (listEmployes.size() > 0) {
                String nomService = "";
                String previousNomService = "";
                listDataHeader.clear();

                ArrayList<FicheEmploye> listEmployesOfService = new ArrayList<>();
                // Pour le premier élément dans la liste
                FicheEmploye employe = listEmployes.get(0);
                nomService = employe.Service;
                listDataHeader.add(nomService);
                listEmployesOfService.add(employe);
                previousNomService = nomService;
                // Pour les prochains éléments dans la liste
                for (int i = 1; i < listEmployes.size(); i++) {
                    employe = listEmployes.get(i);
                    nomService = employe.Service;
                    if (!listDataHeader.contains(nomService)) {
                        listDataHeader.add(nomService);
                        listDataChild.put(previousNomService, listEmployesOfService);
                        listEmployesOfService = new ArrayList<>();
                        previousNomService = nomService;
                    }
                    listEmployesOfService.add(employe);
                }
                // Pour les derniers éléments dans la liste
                listDataChild.put(previousNomService, listEmployesOfService);

                listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
                expListView.setAdapter(listAdapter);

                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                }
                // Si le contenu est vide, télécharger le bottin
            } else {
                // Si l'appareil est connecté sur internet, télécharger le bottin en ligne
                if (Utility.isNetworkAvailable(getActivity())) {
                    try {
                        mProgressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.dialog_Loading_Directory), true);
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        DataManager datamanager = DataManager.getInstance(getActivity());
                        datamanager.getDataFromSignet(
                                DataManager.SignetMethods.BOTTIN_GET_LIST_SERVICE_AND_EMP,
                                ApplicationManager.userCredentials, this);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    // Si l'appareil n'est pas connecté sur internet, envoyer un message
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_Connection_Required), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Log.e("BD FicheEmploye", e.getMessage());
        }
    }

    @Override
    public void onRequestSuccess(Object o) {
        super.onRequestSuccess(o);
        if (o instanceof HashMap<?, ?>) {
            mProgressDialog.hide();
            @SuppressWarnings("unchecked")
            HashMap<String, List<FicheEmploye>> listeEmployeByService = (HashMap<String, List<FicheEmploye>>) o;

            // Écriture dans la base de données
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

            for (String nomService : listeEmployeByService.keySet()) {

                List<FicheEmploye> listeEmployes = listeEmployeByService.get(nomService);

                if (listeEmployes.size() > 0) {
                    for (FicheEmploye ficheEmploye : listeEmployeByService.get(nomService)) {
                        try {
                            dbHelper.getDao(FicheEmploye.class).createOrUpdate(ficheEmploye);
                        } catch (SQLException e) {
                            Log.e(DatabaseHelper.class.getName(), "SQLException", e);
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            updateUI();
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
        if (newText.equals("")) {
            listAdapter.filterData(newText);
            expListView.collapseGroup(0);
        } else {
            listAdapter.filterDataWithOneHeader(newText);
            expListView.expandGroup(0);
        }
        return true;
    }
}
