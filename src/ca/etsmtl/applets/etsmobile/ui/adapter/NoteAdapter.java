package ca.etsmtl.applets.etsmobile.ui.adapter;

import ca.etsmtl.applets.etsmobile.ui.fragment.NotesFragment.NotesSession;
import ca.etsmtl.applets.etsmobile2.R;
import ca.etsmtl.applets.etsmobile2.R.id;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class NoteAdapter extends ArrayAdapter<NotesSession>{

	private Context context;
	public NoteAdapter(Context context, int resource, NotesSession[] notesSession) {
		super(context, resource, notesSession);
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View v = inflater.inflate(R.layout.row_note_menu, parent, false);
		    NotesSession  notesSession = getItem(position);
		    ((TextView) v.findViewById(R.id.row_note_menu_session_text)).setText(notesSession.sessionName);
		    ((GridView) v.findViewById(R.id.row_note_menu_gridview)).setAdapter(notesSession.arrayAdapter);
		return v;
	}


}
