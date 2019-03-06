package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.databinding.DataBindingUtil;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleAssignment;
import ca.etsmtl.applets.etsmobile.view_model.MoodleViewModel;
import ca.etsmtl.applets.etsmobile2.R;
import ca.etsmtl.applets.etsmobile2.databinding.RowMoodleAssignmentBinding;

/**
 * Created by Sonphil on 13-08-17.
 */

public class ExpandableListMoodleAssignmentsAdapter extends BaseExpandableListAdapter {

    private Context context;
    private MoodleViewModel moodleViewModel;
    private List<String> headers;
    private HashMap<String, List<MoodleAssignment>> childs;

    public ExpandableListMoodleAssignmentsAdapter(Context context, MoodleViewModel viewModel) {
        this.context = context;
        this.moodleViewModel = viewModel;
        headers = new ArrayList<>();
        childs = new HashMap<>();
    }

    public void setData(List<String> headers, HashMap<String, List<MoodleAssignment>> childs) {
        this.headers.clear();
        this.headers.addAll(headers);
        this.childs.clear();
        this.childs.putAll(childs);
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return headers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childs.get(headers.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return childs.get(headers.get(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String header = headers.get(groupPosition);

        return childs.get(header);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String header = headers.get(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_separator_moodle, null);
        }

        TextView tv = convertView.findViewById(R.id.textViewSeparator);
        tv.setText(header);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String header = headers.get(groupPosition);
        MoodleAssignment assignment = childs.get(header).get(childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RowMoodleAssignmentBinding binding = RowMoodleAssignmentBinding.inflate(inflater, parent, false);
            binding.setViewModel(moodleViewModel);
            convertView = binding.getRoot();
        }

        RowMoodleAssignmentBinding binding = DataBindingUtil.getBinding(convertView);
        binding.setAssignment(assignment);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
