package ca.etsmtl.applets.etsmobile.ui.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import ca.etsmtl.applets.etsmobile.model.FicheEmploye;
import ca.etsmtl.applets.etsmobile2.R;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
 
public class ExpandableListAdapter extends BaseExpandableListAdapter {
 
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<FicheEmploye>> _listDataChild;
    
    private List<String> _listDataHeaderOriginal;
    private HashMap<String, List<FicheEmploye>> _listDataChildOriginal;
 
    public ExpandableListAdapter(Context context, List<String> listDataHeader,
            HashMap<String, List<FicheEmploye>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        
        this._listDataChildOriginal = new HashMap<String, List<FicheEmploye>>();
        this._listDataHeaderOriginal = new ArrayList<String>();
        
        this._listDataChildOriginal = (HashMap<String, List<FicheEmploye>>) listChildData.clone();
        this._listDataHeaderOriginal.addAll(listDataHeader);
        
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
    	
    	
    	FicheEmploye ficheEmploye = this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    	ficheEmploye.Nom = ficheEmploye.Nom == null ? "" : ficheEmploye.Nom;
    	ficheEmploye.Prenom = ficheEmploye.Prenom == null ? "" : ficheEmploye.Prenom;
    	
        return ficheEmploye;
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
    	FicheEmploye ficheEmploye = (FicheEmploye) getChild(groupPosition, childPosition); 
    	
    	ficheEmploye.Nom = ficheEmploye.Nom == null ? "" : ficheEmploye.Nom;
    	ficheEmploye.Prenom = ficheEmploye.Prenom == null ? "" : ficheEmploye.Prenom;
    	
    	
        final String childText = ficheEmploye.Nom + " "+ficheEmploye.Prenom;
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
 
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
 
        txtListChild.setText(childText);
        
   
        
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
    
	public void filterData(String query) {

		query = query.toLowerCase();
//		Log.v("MyListAdapter", String.valueOf(_listDataHeader.size()));
		_listDataChild.clear();
		_listDataHeader.clear();

		if (query.isEmpty()) {
			_listDataChild = (HashMap<String, List<FicheEmploye>>) _listDataChildOriginal.clone();
			_listDataHeader.addAll(_listDataHeaderOriginal);
			System.out.println("VIDE\n _listDataHeader.size : "+_listDataHeader.size()+"\n_listDataChlid : "+_listDataChild.size());
		} else {
			System.out.println("PAS VIDE");
			List<FicheEmploye> newList= new ArrayList<FicheEmploye>();

			for (Entry<String, List<FicheEmploye>> entry : _listDataChildOriginal.entrySet()) {
				String departement = entry.getKey();
//				System.out.println(departement);
				List<FicheEmploye> listeFicheEmploye = entry.getValue();


				for (FicheEmploye ficheEmploye : listeFicheEmploye) {
					String nom = ficheEmploye.Nom == null ? "" : ficheEmploye.Nom.toLowerCase();
					String prenom = ficheEmploye.Prenom == null ? "" : ficheEmploye.Prenom.toLowerCase();
					if (nom.contains(query) || prenom.contains(query)) {
						newList.add(ficheEmploye);
					}
				}

				if (!newList.isEmpty()) {
					_listDataChild.put(departement, newList);
					_listDataHeader.add(departement);
					newList= new ArrayList<FicheEmploye>();
				}

			}

			Collections.sort(_listDataHeader);
		}

		notifyDataSetChanged();

	}

}