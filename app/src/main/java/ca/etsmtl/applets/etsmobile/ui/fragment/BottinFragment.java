package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.model.FicheEmploye;
import ca.etsmtl.applets.etsmobile.service.BottinService;
import ca.etsmtl.applets.etsmobile.ui.activity.BottinDetailsActivity;
import ca.etsmtl.applets.etsmobile.ui.adapter.ExpandableListAdapter;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * @author Thibaut
 */
public class BottinFragment extends BaseFragment implements SearchView.OnQueryTextListener {
    //TODO Change to activity to be able to search
    private SearchView searchView;
    private ExpandableListAdapter listAdapter;

    ExpandableListView expListView;

    private List<String> listDataHeader;
    private HashMap<String, List<FicheEmploye>> listDataChild;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * Récepteur attendant un intent de {@link ca.etsmtl.applets.etsmobile.service.BottinService}
     * signalant la fin de la synchronisation
     */
    private BottinFragmentReceiver receiver;

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
                if (!Utility.isNetworkAvailable(getActivity())) {
                    afficherMsgHorsLigne();
                } else {
                    afficherRafraichissementEtRechargerBottin();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Initialisatioin du récepteur
        receiver = new BottinFragmentReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();

        mSwipeRefreshLayout.setRefreshing(BottinService.isSyncEnCours());

        IntentFilter filter = new IntentFilter(BottinFragmentReceiver.ACTION_SYNC_BOTTIN);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_bottin, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.ets_red));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!Utility.isNetworkAvailable(getActivity())) {
                    afficherMsgHorsLigne();
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    afficherRafraichissementEtRechargerBottin();
                }
            }
        });

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

        updateUI();

        return v;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    void updateUI() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        try {
            DatabaseHelper dbHelper = new DatabaseHelper(activity);

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
                        Collections.sort(listEmployesOfService, new Comparator<FicheEmploye>() {
                            @Override
                            public int compare(FicheEmploye f1, FicheEmploye f2) {
                                return f1.Nom.compareTo(f2.Nom);
                            }
                        });
                        listDataChild.put(previousNomService, listEmployesOfService);
                        listEmployesOfService = new ArrayList<>();
                        previousNomService = nomService;
                    }
                    listEmployesOfService.add(employe);
                }
                // Pour les derniers éléments dans la liste
                listDataChild.put(previousNomService, listEmployesOfService);

                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            listAdapter = new ExpandableListAdapter(getActivity(),
                                    listDataHeader, listDataChild);
                            expListView.setAdapter(listAdapter);
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                }

                // Rétablissement du filtre de recherche
                CharSequence searchText = searchView.getQuery();
                if (searchText.length() != 0)
                    onQueryTextChange(searchText.toString());

                // Si le contenu est vide, télécharger le bottin
            } else { // Le contenu est vide.
                afficherRafraichissementEtRechargerBottin();
            }
        } catch (Exception e) {
            Log.e("BD FicheEmploye", e.getMessage());
        }
    }

    private void afficherRafraichissementEtRechargerBottin() {
        if (Utility.isNetworkAvailable(getActivity())) {
            mSwipeRefreshLayout.setRefreshing(true);

            /*
            Lancement du service permettant de synchroniser le bottin si celui-ci n'est pas en cours
            de synchronisation. Si c'est le cas, cela signifie que la job est est en cours
            d'exécution.
             */
            if (!BottinService.isSyncEnCours()) {
                // Lancement du service permettant de synchroniser le bottin
                Intent intent = new Intent(getContext(), BottinService.class);
                getActivity().startService(intent);
            }
        } else {
            afficherMsgHorsLigne();
        }
    }

    private void afficherMsgHorsLigne() {
        Toast.makeText(getActivity(), getString(R.string.toast_Connection_Required),
                Toast.LENGTH_LONG).show();
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

    /**
     * Récepteur recevant un intent lorsque {@link BottinService} a terminé la synchronisation
     */
    public class BottinFragmentReceiver extends BroadcastReceiver {
        public static final String ACTION_SYNC_BOTTIN = "SYNC_BOTTIN";
        public static final String EXCEPTION = "ERREUR_SYNC_BOTTIN";

        @Override
        public void onReceive(Context context, Intent intent) {
            // Obtention de l'exception
            Bundle extras = intent.getExtras();

            Exception e = null;
            if (extras != null)
                e = (Exception) extras.getSerializable(EXCEPTION);

            if (e == null)
                // S'il n'y a pas d'exception...
                updateUI();
            else {
                if (!Utility.isNetworkAvailable(getActivity()))
                    afficherMsgHorsLigne();
            }

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
