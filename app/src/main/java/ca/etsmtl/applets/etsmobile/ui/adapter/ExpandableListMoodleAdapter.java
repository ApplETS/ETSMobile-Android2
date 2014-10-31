package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCoreModule;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleModuleContent;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 10/30/14.
 */
public class ExpandableListMoodleAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<MoodleCoreModule> _listDataHeader;
    private HashMap<MoodleCoreModule, List<MoodleModuleContent>> _listDataChild;



    public ExpandableListMoodleAdapter(Context context, List<MoodleCoreModule> listDataHeader,
                                       HashMap<MoodleCoreModule, List<MoodleModuleContent>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;


    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {


        MoodleModuleContent moodleModuleContent = this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);


        return moodleModuleContent;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        MoodleModuleContent moodleModuleContent = (MoodleModuleContent) getChild(groupPosition, childPosition);

        final String childText = moodleModuleContent.getFilename();

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
        try {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        } catch (NullPointerException e) {
            return 0;
        }

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
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        MoodleCoreModule headerMoodleCoreModule = (MoodleCoreModule) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerMoodleCoreModule.getName());

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
}
