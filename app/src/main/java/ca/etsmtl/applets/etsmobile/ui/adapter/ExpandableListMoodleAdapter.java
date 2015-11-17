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
import ca.etsmtl.applets.etsmobile.ui.fragment.MoodleCourseDetailsFragment;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 10/30/14.
 */
public class ExpandableListMoodleAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<MoodleCourseDetailsFragment.HeaderText> _listDataHeader;
    private HashMap<MoodleCourseDetailsFragment.HeaderText, Object[]> _listDataChild;
//    List<MoodleModuleContent>


    public ExpandableListMoodleAdapter(Context context, List<MoodleCourseDetailsFragment.HeaderText> listDataHeader,
                                       HashMap<MoodleCourseDetailsFragment.HeaderText, Object[]> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;


    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {

        return this._listDataChild.get(this._listDataHeader.get(groupPosition))[childPosititon];

    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        String childText = "";
        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);

        Object object = getChild(groupPosition, childPosition);


        if(object instanceof MoodleModuleContent ) {
            MoodleModuleContent moodleModuleContent = (MoodleModuleContent) getChild(groupPosition, childPosition);
            childText = moodleModuleContent.getFilename();
        }

        if(object instanceof MoodleCoreModule){
            MoodleCoreModule moodleCoreModule = (MoodleCoreModule) getChild(groupPosition, childPosition);
            childText = moodleCoreModule.getName();
        }





        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).length;
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
        MoodleCourseDetailsFragment.HeaderText header = (MoodleCourseDetailsFragment.HeaderText) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(header.getHeaderName());

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
