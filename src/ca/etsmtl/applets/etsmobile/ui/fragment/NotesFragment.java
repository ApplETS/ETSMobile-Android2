package ca.etsmtl.applets.etsmobile.ui.fragment;


import ca.etsmtl.applets.etsmobile.ui.adapter.NoteAdapter;
import ca.etsmtl.applets.etsmobile2.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Laurence 
 */
public class NotesFragment extends BaseFragment{
	
	
	private ListView mListView;
	private NoteAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.activity_note, container);
		NotesSession[] notesSesssion = new NotesSession[4];
		adapter = new NoteAdapter(getActivity(), R.layout.row_note_menu,notesSesssion);
		mListView = (ListView) v.findViewById(R.id.activity_note_listview);
		
		mListView.setAdapter(adapter);
		return v;	
	}
	
	public class NotesSession{
		public String sessionName;
		public ArrayAdapter<SessionCote> arrayAdapter;
	}
	
	public class SessionCote{
		public String course;
		public String cote;
	}
}
