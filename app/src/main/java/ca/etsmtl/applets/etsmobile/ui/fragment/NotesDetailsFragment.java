package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;

import java.io.InputStream;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.soap.SignetsMobileSoap;
import ca.etsmtl.applets.etsmobile.model.ElementEvaluation;
import ca.etsmtl.applets.etsmobile.model.ListeDesElementsEvaluation;
import ca.etsmtl.applets.etsmobile.ui.adapter.MyCourseDetailAdapter;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.NoteManager;
import ca.etsmtl.applets.etsmobile2.R;

public class NotesDetailsFragment extends HttpFragment implements Observer {

    public static String SIGLE = "SIGLE";
    public static String SESSION = "SESSION";
    public static String SESSIONABREGE = "SESSIONABREGE";
    public static String COTE = "COTE";
    public static String GROUPE = "GROUPE";
    public static String TITLECOURS = "TITLECOURS";


    private ListView mlistView;
    private String cote;
    private String sigle;
    private String session;
    private String sessionAbrege;
    private String groupe;
    private String titreCours;
    private String id;

    private ListeDesElementsEvaluation mlisteDesElementsEvaluation;

    private NoteManager mNoteManager;

    private ProgressBar progressBarDetailsNotes;
    private NotesTask notesTask;

    public static NotesDetailsFragment newInstance(String sigle, String session, String sessionAbrege, String cote, String groupe, String titreCours) {
        NotesDetailsFragment fragment = new NotesDetailsFragment();
        Bundle args = new Bundle();
        args.putString(SIGLE, sigle);
        args.putString(SESSION, session);
        args.putString(SESSIONABREGE, sessionAbrege);
        args.putString(COTE, cote);
        args.putString(GROUPE, groupe);
        args.putString(TITLECOURS, titreCours);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            sigle = bundle.getString(SIGLE);
            cote = bundle.getString(COTE);
            session = bundle.getString(SESSION);
            sessionAbrege = bundle.getString(SESSIONABREGE);
            groupe = bundle.getString(GROUPE);
            titreCours = bundle.getString(TITLECOURS);
            id = sigle + sessionAbrege;
            System.out.print(titreCours);

            mNoteManager = new NoteManager(getActivity());
            mNoteManager.addObserver(this);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notes_details_fragment, container, false);
        ((TextView) v.findViewById(R.id.note_fragment_titre_cours)).setText(titreCours);
        mlistView = (ListView) v.findViewById(android.R.id.list);

        progressBarDetailsNotes = (ProgressBar) v.findViewById(R.id.progressBar_notes_details);
        progressBarDetailsNotes.setVisibility(ProgressBar.VISIBLE);
        refresh();

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        return v;
    }

    @Override
    public void onStart() {
        notesTask = new NotesTask(this);
        notesTask.execute(sigle, groupe, session);

        super.onStart();
    }

    @Override
    public void onRequestFailure(SpiceException arg0) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarDetailsNotes.setVisibility(ProgressBar.GONE);
                if (getActivity() != null)
                    Toast.makeText(getActivity(), getString(R.string.toast_Sync_Fail), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRequestSuccess(Object o) {

        if (o instanceof ListeDesElementsEvaluation) {
            mlisteDesElementsEvaluation = (ListeDesElementsEvaluation) o;

            mlisteDesElementsEvaluation.id = id;

            mNoteManager.onRequestSuccess(mlisteDesElementsEvaluation);
        }

    }

    private void refresh() {

        mlisteDesElementsEvaluation = mNoteManager.getListElementsEvaluation(id);

        if (mlisteDesElementsEvaluation != null) {
            List<ElementEvaluation> elementsEvaluationList = mNoteManager.getElementsEvaluation(mlisteDesElementsEvaluation);
            mlisteDesElementsEvaluation.liste.addAll(elementsEvaluationList);

            Activity activity = getActivity();
            if (activity != null) {
                final MyCourseDetailAdapter myCourseDetailAdapter = new MyCourseDetailAdapter(getActivity(), mlisteDesElementsEvaluation,
                        cote);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        mlistView.setAdapter(myCourseDetailAdapter);
                    }
                });
            }
        }

    }

    @Override
    void updateUI() {

    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof String)
            if (((String) data).equals(this.getClass().getName())) {
                progressBarDetailsNotes.setVisibility(ProgressBar.GONE);
                refresh();
            }
    }

    @Override
    public void onDetach() {
        notesTask.cancel(true);
        notesTask = null;

        super.onDetach();
    }

    @Override
    public String getFragmentTitle() {
        return "";
    }

    private static final class NotesTask extends AsyncTask {

        private HttpFragment httpFragment;

        NotesTask(HttpFragment httpFragment) {
            this.httpFragment = httpFragment;
        }

        @Override
        protected Object doInBackground(Object... params) {
            String username = ApplicationManager.userCredentials.getUsername();
            String password = ApplicationManager.userCredentials.getPassword();
            String sigle = (String) params[0];
            String groupe = (String) params[1];
            String session = (String) params[2];
            try {
                InputStream certificate = httpFragment.getResources().openRawResource(R.raw.ets_pub_cert);
                ListeDesElementsEvaluation listeDesElementsEvaluation = new SignetsMobileSoap(certificate)
                        .listeElementsEvaluation(username, password, sigle, groupe, session);
                httpFragment.onRequestSuccess(listeDesElementsEvaluation);
            } catch (Exception e) {
                httpFragment.onRequestFailure(new SpiceException(httpFragment.getString(R.string.toast_Sync_Fail)));
            }

            return null;
        }
    }
}
