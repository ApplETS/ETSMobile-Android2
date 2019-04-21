package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import ca.etsmtl.applets.etsmobile.model.AppMonETSNotification;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 12/12/14.
 */
public class NotificationsAdapter extends ArrayAdapter<AppMonETSNotification> {

    private LayoutInflater inflater;

    public NotificationsAdapter(Context context, int rowLayoutResourceId, ArrayList<AppMonETSNotification> list) {
        super(context, rowLayoutResourceId, list);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.row_notification, parent, false);
            holder.tvNomApplication = (TextView) view.findViewById(R.id.tv_notif_application_name);
            holder.tvTexteNotification = (TextView) view.findViewById(R.id.tv_notif_text);
            holder.linearLayoutNotification = (LinearLayout) view.findViewById(R.id.linearLayout_notif);
            view.setTag(holder);
        }

        AppMonETSNotification item = getItem(position);

        String nomApplication = item.getNotificationApplicationNom();
        int colour = Utility.stringToColour(nomApplication, 60);

        holder.tvNomApplication.setText(nomApplication);
        holder.tvTexteNotification.setText(item.getNotificationTexte());
        holder.linearLayoutNotification.setBackgroundColor(colour);

        return view;
    }

    @Override
    public void addAll(Collection<? extends AppMonETSNotification> collection) {
        ArrayList<AppMonETSNotification> list = new ArrayList<>(collection);
        Collections.sort(list);
        super.addAll(list);
    }

    static class ViewHolder {
        TextView tvNomApplication;
        TextView tvTexteNotification;
        LinearLayout linearLayoutNotification;
    }
}