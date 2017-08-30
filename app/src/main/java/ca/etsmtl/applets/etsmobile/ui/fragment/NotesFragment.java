package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.ListeDeCours;
import ca.etsmtl.applets.etsmobile.model.ListeDeSessions;
import ca.etsmtl.applets.etsmobile.ui.adapter.NoteAdapter;
import ca.etsmtl.applets.etsmobile.ui.adapter.NotesSessionItem;
import ca.etsmtl.applets.etsmobile.ui.adapter.SessionCoteAdapter;
import ca.etsmtl.applets.etsmobile.ui.adapter.SessionCoteItem;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.NoteManager;
import ca.etsmtl.applets.etsmobile.views.LoadingView;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Laurence
 */
public class NotesFragment extends HttpFragment implements Observer {


    private static String TAG = NotesFragment.class.getName();
    private ListView mListView;
    private NoteAdapter adapter;

    private SessionCoteItem[] sessionCoteItemArray;
    private NotesSessionItem[] notesSession;

    private ListeDeCours listeDeCours;
    private ListeDeSessions listeDeSessions;
    private HashMap<String, String> mapNoteACeJour;

    private NoteManager mNoteManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_note, container, false);
        super.onCreateView(inflater, v, savedInstanceState);


        mListView = (ListView) v.findViewById(R.id.activity_note_listview);

        listeDeCours = new ListeDeCours();
        listeDeSessions = new ListeDeSessions();

        mNoteManager = new NoteManager(getActivity());
        mNoteManager.addObserver(this);

        mapNoteACeJour = new HashMap<>();
        loadingView.showLoadingView();

        if (isAdded())
            refreshList();

        dataManager.getDataFromSignet(SignetMethods.LIST_COURS, ApplicationManager.userCredentials, this, "");
        dataManager.getDataFromSignet(SignetMethods.LIST_SESSION, ApplicationManager.userCredentials, this, "");

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        return v;
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.menu_section_1_notes);
    }

    @Override
    public void onRequestFailure(SpiceException e) {

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingView.hideLoadingView(loadingView);
                }
            });

            Toast.makeText(getActivity(), R.string.toast_Sync_Fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestSuccess(Object o) {
        super.onRequestSuccess(o);

        mNoteManager.onRequestSuccess(o);
    }

    @Override
    void updateUI() {
        // TODO Auto-generated method stub
    }

    private void refreshList() {

        initCollectionsNotes();

        try {
            listeDeCours.liste.addAll(mNoteManager.getCours());
            listeDeSessions.liste.addAll(mNoteManager.getTrimestres());

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (listeDeCours.liste.size() != 0) {

            sessionCoteItemArray = new SessionCoteItem[listeDeCours.liste.size()];
            HashMap<String, ArrayList<SessionCoteItem>> mapSession = new HashMap<String, ArrayList<SessionCoteItem>>();

            regrouperCoursParSession(mapSession);
            notesSession = new NotesSessionItem[listeDeSessions.liste.size()];
            genererSessionCote(mapSession);

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        adapter = new NoteAdapter(getActivity(), R.layout.row_note_menu, notesSession);
                        mListView.setAdapter(adapter);
                    }

                });
            }
        }

    }

    private void initCollectionsNotes() {
        listeDeCours.liste.clear();
        listeDeSessions.liste.clear();
        mapNoteACeJour.clear();
    }

    private void genererSessionCote(HashMap<String, ArrayList<SessionCoteItem>> mapSession) {
        int l = 0;
        for (int j = listeDeSessions.liste.size() - 1; j >= 0; j--) {
            ArrayList<SessionCoteItem> array = mapSession.get(listeDeSessions.liste.get(j).abrege);
            SessionCoteItem[] sessionCoteItems = new SessionCoteItem[array.size()];
            int k = 0;
            for (SessionCoteItem sessionCote : array) {
                sessionCoteItems[k] = sessionCote;
                k++;
            }
            notesSession[l] = new NotesSessionItem(listeDeSessions.liste.get(j).auLong,
                    listeDeSessions.liste.get(j).abrege,
                    new SessionCoteAdapter(getActivity(),
                            sessionCoteItems, listeDeSessions.liste.get(j).abrege));
            l++;
        }
    }

    private void regrouperCoursParSession(HashMap<String, ArrayList<SessionCoteItem>> mapSession) {

        for (int i = 0; i < listeDeCours.liste.size(); i++) {

            // Permet le regroupement par session, on ajoute le vecteur
            // des cours dans la session
            String sigle = listeDeCours.liste.get(i).sigle;
            String session = listeDeCours.liste.get(i).session;
            String cote = listeDeCours.liste.get(i).cote;
            String groupe = listeDeCours.liste.get(i).groupe;
            String titreCours = listeDeCours.liste.get(i).titreCours;
            String id = listeDeCours.liste.get(i).id;

            if (mapSession.containsKey(session)) {
                ArrayList<SessionCoteItem> arrayList = mapSession.get(session);

                arrayList.add(new SessionCoteItem(sigle, cote,
                        groupe, titreCours));

                mapSession.put(session, arrayList);
            } else {
                ArrayList<SessionCoteItem> arrayList = new ArrayList<SessionCoteItem>();
                arrayList.add(new SessionCoteItem(sigle, cote,
                        groupe, titreCours));
                mapSession.put(session, arrayList);
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof String)
            if ((data).equals(this.getClass().getName())) {
                refreshList();
            }
    }
}
