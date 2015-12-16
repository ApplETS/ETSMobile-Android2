package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.octo.android.robospice.request.listener.RequestListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ca.etsmtl.applets.etsmobile.model.MonETSNotification;
import ca.etsmtl.applets.etsmobile.model.Nouvelle;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 12/12/14.
 */
public class NotificationsAdapter extends ArrayAdapter<MonETSNotification> {

    private LayoutInflater inflater;

    public NotificationsAdapter(Context context, int rowLayoutResourceId, ArrayList<MonETSNotification> list) {
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

        MonETSNotification item = getItem(position);

        holder.tvNomApplication.setText(item.getNotificationApplicationNom());
        holder.tvTexteNotification.setText(item.getNotificationTexte());
//        holder.linearLayoutNotification

        return view;
    }

    @Override
    public void addAll(Collection<? extends MonETSNotification> collection) {
        ArrayList<MonETSNotification> list = new ArrayList<>(collection);
        Collections.sort(list);
        super.addAll(list);
    }

    static class ViewHolder {
        TextView tvNomApplication;
        TextView tvTexteNotification;
        LinearLayout linearLayoutNotification;
    }
}