package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ca.etsmtl.applets.etsmobile.model.Programme;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 10/08/15.
 */
public class ProfileSpinnerAdapter extends ArrayAdapter<Programme> {

    public ProfileSpinnerAdapter(Context context) {
        super(context, R.layout.row_profile_spinner, new ArrayList<Programme>());
    }

    public View getCustomView(int position, View convertView, ViewGroup parent, int layoutId) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.tvProgramName = (TextView) convertView.findViewById(R.id.tv_program);
            holder.tvProgramStatus = (TextView) convertView.findViewById(R.id.tv_program_status);

            convertView.setTag(holder);
        }

        Programme item = getItem(position);

        holder.tvProgramName.setText(item.libelle);
        holder.tvProgramStatus.setText(item.statut);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_profile_spinner_dropdown, parent, false);
            holder = new ViewHolder();
            holder.tvProgramName = (TextView) convertView.findViewById(R.id.tv_program);
            holder.tvProgramStatus = (TextView) convertView.findViewById(R.id.tv_program_status);

            convertView.setTag(holder);
        }

        Programme item = getItem(position);

        holder.tvProgramName.setText(item.libelle);
        holder.tvProgramStatus.setText(item.statut);

        return convertView;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_profile_spinner, parent, false);
            holder = new ViewHolder();
            holder.tvProgramName = (TextView) convertView.findViewById(R.id.tv_program);
//            holder.tvProgramStatus = (TextView) convertView.findViewById(R.id.tv_program_status);

            convertView.setTag(holder);
        }

        Programme item = getItem(position);

        holder.tvProgramName.setText(item.libelle);
//        holder.tvProgramStatus.setText(item.statut);

        return convertView;
    }

    static class ViewHolder {
        TextView tvProgramName;
        TextView tvProgramStatus;
    }

}
