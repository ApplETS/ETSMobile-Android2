package ca.etsmtl.applets.etsmobile.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ca.etsmtl.applets.etsmobile.model.FicheEmploye;
import ca.etsmtl.applets.etsmobile2.R;

public class BottinAdapter extends ArrayAdapter<FicheEmploye> {

	private Context context;

	public BottinAdapter(Context context, int resource, List<FicheEmploye> items) {
		super(context, resource,items);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 View v = convertView;

		    if (v == null) {

		    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        v = inflater.inflate(R.layout.row_bottin_menu, null);

		    }

		    FicheEmploye p = getItem(position);

		    if (p != null) {

		        TextView tv_nom = (TextView) v.findViewById(R.id.textview_bottin_nom);
		        TextView tv_prenom = (TextView) v.findViewById(R.id.textview_bottin_prenom);

		        if (tv_nom != null) {
		        	tv_nom.setText(p.Nom);
		        }
		        if (tv_prenom != null) {

		        	tv_prenom.setText(p.Prenom);
		        }

		    }

		    return v;
	}

}