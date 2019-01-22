package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ca.etsmtl.applets.etsmobile2.R;

public class SessionCoteAdapter extends BaseAdapter {

	private Context context;
	private SessionCoteItem[] sessionCote;
	private String sessionAbrege;

	public SessionCoteAdapter(Context context, SessionCoteItem[] sessionCoteItem, String sessionAbrege) {
		this.context = context;
		this.sessionCote = sessionCoteItem;
		this.sessionAbrege = sessionAbrege;
	}

	@Override
	public int getCount() {
		if (sessionCote != null) {
			return sessionCote.length;
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return sessionCote[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.square_note, parent, false);
		} else {
			v = convertView;
		}

		SessionCoteItem notesSession = sessionCote[position];

		((TextView) v.findViewById(R.id.square_note_course_name)).setText(notesSession.sigle);
		if (notesSession.cote !=null)
			((TextView) v.findViewById(R.id.square_note_course_cote)).setText(notesSession.cote);
		else
			((TextView) v.findViewById(R.id.square_note_course_cote)).setText("--");

		return v;
	}

	public SessionCoteItem[] getArray() {
		return sessionCote;
	}

}
