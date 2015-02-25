package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import ca.etsmtl.applets.etsmobile.ui.activity.NotesDetailsActivity;
import ca.etsmtl.applets.etsmobile2.R;

public class NoteAdapter extends ArrayAdapter<NotesSessionItem> {

	private Context context;

	public NoteAdapter(Context context, int resource, NotesSessionItem[] notesSession) {
		super(context, resource, notesSession);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.row_note_menu, parent, false);
		final NotesSessionItem notesSession = getItem(position);
		((TextView) v.findViewById(R.id.row_note_menu_session_text)).setText(notesSession.sessionName);
		final GridView gridview = (GridView) v.findViewById(R.id.row_note_menu_gridview);
		gridview.setAdapter(notesSession.arrayAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				SessionCoteItem sessionCote = (SessionCoteItem) notesSession.arrayAdapter.getItem(position);
				String cote = " ";
				if (sessionCote.cote != null) {
					cote = sessionCote.cote;
				}


                Intent i = new Intent(context, NotesDetailsActivity.class);
                i.putExtra("sigle", sessionCote.sigle);
                i.putExtra("sessionName", notesSession.sessionName);
                i.putExtra("cote", cote);
                i.putExtra("groupe", sessionCote.groupe);
                i.putExtra("titreCours", sessionCote.titreCours);

                context.startActivity(i);



//				Fragment fragment = NotesDetailsFragment.newInstance(sessionCote.sigle, notesSession.sessionName, cote,
//						sessionCote.groupe,sessionCote.titreCours);
//				FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
//				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "NotesDetailsFragment")
//						.addToBackStack(null).commit();
			}
		});
		return v;
	}

}
