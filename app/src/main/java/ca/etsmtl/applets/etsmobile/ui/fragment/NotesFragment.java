package ca.etsmtl.applets.etsmobile.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.ListeDeCours;
import ca.etsmtl.applets.etsmobile.model.ListeDeSessions;
import ca.etsmtl.applets.etsmobile.model.Trimestre;
import ca.etsmtl.applets.etsmobile.ui.adapter.NoteAdapter;
import ca.etsmtl.applets.etsmobile.ui.adapter.NotesSessionItem;
import ca.etsmtl.applets.etsmobile.ui.adapter.SessionCoteAdapter;
import ca.etsmtl.applets.etsmobile.ui.adapter.SessionCoteItem;
import ca.etsmtl.applets.etsmobile2.R;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Laurence
 */
public class NotesFragment extends HttpFragment {


    private static String TAG = NotesFragment.class.getName();
    private ListView mListView;
    private NoteAdapter adapter;

    private SessionCoteItem[] sessionCoteItemArray;
    private NotesSessionItem[] notesSession;

    private ListeDeCours listeDeCours;
    private ListeDeSessions listeDeSessions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_note, container, false);
        super.onCreateView(inflater, v, savedInstanceState);
        mListView = (ListView) v.findViewById(R.id.activity_note_listview);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadingView.showLoadingView();
        DataManager datamanager = DataManager.getInstance(getActivity());
        datamanager.getDataFromSignet(SignetMethods.LIST_COURS, ApplicationManager.userCredentials, this, "");
        datamanager.getDataFromSignet(SignetMethods.LIST_SESSION, ApplicationManager.userCredentials, this, "");

    }


    @Override
    public void onRequestFailure(SpiceException e) {

    }

    @Override
    public void onRequestSuccess(Object o) {
        super.onRequestSuccess(o);

        if (o != null)
            if (o instanceof ListeDeCours) {
                listeDeCours = (ListeDeCours) o;
                refreshList();
            } else if (o instanceof ListeDeSessions) {
                listeDeSessions = (ListeDeSessions) o;
                refreshList();
            }


    }

    @Override
    void updateUI() {
        // TODO Auto-generated method stub
    }

    private void refreshList() {
        if (listeDeCours != null && listeDeSessions != null) {
            if (listeDeCours.liste.size() != 0) {
                sessionCoteItemArray = new SessionCoteItem[listeDeCours.liste.size()];
                HashMap<String, ArrayList<SessionCoteItem>> mapSession = new HashMap<String, ArrayList<SessionCoteItem>>();
                for (int i = 0; i < listeDeCours.liste.size(); i++) {
                    // Permet le regroupement par session, on ajoute le vecteur
                    // des cours dans la session
                    if (mapSession.containsKey(listeDeCours.liste.get(i).session)) {
                        ArrayList<SessionCoteItem> arrayList = mapSession.get(listeDeCours.liste.get(i).session);
                       // listeDeCours.liste.get(i).titreCours
                        arrayList.add(new SessionCoteItem(listeDeCours.liste.get(i).sigle, listeDeCours.liste.get(i).cote,
                                listeDeCours.liste.get(i).groupe,listeDeCours.liste.get(i).titreCours));

                       // arrayList.add(new SessionCoteItem(listeDeCours.liste.get(i).sigle, listeDeCours.liste.get(i).cote,
                         //       listeDeCours.liste.get(i).groupe));
                        mapSession.put(listeDeCours.liste.get(i).session, arrayList);
                    } else {
                        ArrayList<SessionCoteItem> arrayList = new ArrayList<SessionCoteItem>();
                        arrayList.add(new SessionCoteItem(listeDeCours.liste.get(i).sigle, listeDeCours.liste.get(i).cote,
                                listeDeCours.liste.get(i).groupe,listeDeCours.liste.get(i).titreCours));
                        mapSession.put(listeDeCours.liste.get(i).session, arrayList);
                    }
                }

                notesSession = new NotesSessionItem[listeDeSessions.liste.size()];
                int l = 0;
                for (int j = listeDeSessions.liste.size() - 1; j >= 0; j--) {
                    ArrayList<SessionCoteItem> array = mapSession.get(listeDeSessions.liste.get(j).abrege);
                    SessionCoteItem[] sessionCoteItems = new SessionCoteItem[array.size()];
                    int k = 0;
                    for (SessionCoteItem sessionCote : array) {
                        sessionCoteItems[k] = sessionCote;
                        k++;
                    }
                    notesSession[l] = new NotesSessionItem(listeDeSessions.liste.get(j).auLong, new SessionCoteAdapter(getActivity(), sessionCoteItems));
                    l++;
                }

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

}
