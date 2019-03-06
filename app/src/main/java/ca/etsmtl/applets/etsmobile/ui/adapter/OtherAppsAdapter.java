package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ca.etsmtl.applets.etsmobile.model.Apps;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 18/05/15.
 */
public class OtherAppsAdapter extends ArrayAdapter<Apps> {


    private LayoutInflater inflater;

    public OtherAppsAdapter(Context context, int resource, ArrayList<Apps> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.square_other_app, parent, false);
            holder = new ViewHolder();
            holder.textViewTitle = (TextView) view.findViewById(R.id.tv_app_title);
            holder.imageViewIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
            view.setTag(holder);
        }

        Apps item = getItem(position);

        Resources r = inflater.getContext().getResources();
        int drawableId = r.getIdentifier(item.getImageResourceId(), "drawable", inflater.getContext().getPackageName());

        int stringID = r.getIdentifier(item.getAppName(), "string", inflater.getContext().getPackageName());

        holder.textViewTitle.setText(inflater.getContext().getString(stringID));


        holder.imageViewIcon.setImageResource(drawableId);

        return view;
    }

    static class ViewHolder {
        TextView textViewTitle;
        ImageView imageViewIcon;

    }
}
