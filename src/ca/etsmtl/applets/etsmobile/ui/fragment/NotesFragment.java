package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

	private ListView mListView;
	private NoteAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_note, container, false);
		NotesSessionItem[] notesSession = new NotesSessionItem[2];
		SessionCoteItem[] sessionCoteItem = new SessionCoteItem[3];
		sessionCoteItem[0] = new SessionCoteItem("GIA400", "A-");
		sessionCoteItem[1] = new SessionCoteItem("LOG750", "C");
		sessionCoteItem[2] = new SessionCoteItem("GIA400", "A-");

		SessionCoteItem[] sessionCoteItem2 = new SessionCoteItem[6];
		sessionCoteItem2[0] = new SessionCoteItem("GIA400", "A-");
		sessionCoteItem2[1] = new SessionCoteItem("LOG750", "C");
		sessionCoteItem2[2] = new SessionCoteItem("GIA400", "A-");
		sessionCoteItem2[3] = new SessionCoteItem("LOG750", "B+");
		sessionCoteItem2[4] = new SessionCoteItem("GTI785", "B+");
		sessionCoteItem2[5] = new SessionCoteItem("GTI785", "B+");

		SessionCoteAdapter sessionAdapter = new SessionCoteAdapter(
				getActivity(), sessionCoteItem);
		SessionCoteAdapter sessionAdapter2 = new SessionCoteAdapter(
				getActivity(), sessionCoteItem2);
		notesSession[0] = new NotesSessionItem("Hiver 2011", sessionAdapter);
		notesSession[1] = new NotesSessionItem("Automne 2013", sessionAdapter2);
		adapter = new NoteAdapter(getActivity(), R.layout.row_note_menu,
				notesSession);
		mListView = (ListView) v.findViewById(R.id.activity_note_listview);

		mListView.setAdapter(adapter);
		return v;
	}

	@Override
	void updateUI() {
	}

	@Override
	public void onRequestFailure(SpiceException e) {

	}  

	@Override
	public void onRequestSuccess(Object o) {

	}

}
