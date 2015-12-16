package ca.etsmtl.applets.etsmobile.ui.activity;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.j256.ormlite.dao.Dao;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.sql.SQLException;
import java.util.ArrayList;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.model.MonETSNotification;
import ca.etsmtl.applets.etsmobile.model.MonETSNotificationList;
import ca.etsmtl.applets.etsmobile.ui.adapter.NotificationsAdapter;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 15/12/15.
 */
public class NotificationActivity extends Activity implements RequestListener<Object> {

    private ProgressBar progressBar;
    private ListView listView;
    private DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private NotificationsAdapter notificationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_notifications);
        listView = (ListView) findViewById(R.id.listView_notifications);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.ets_red_fonce),
                PorterDuff.Mode.MULTIPLY);

        progressBar.setVisibility(View.VISIBLE);

        notificationsAdapter = new NotificationsAdapter(this, R.layout.row_notification, new ArrayList<MonETSNotification>());
        listView.setAdapter(notificationsAdapter);

       syncAdapterWithDB();


        Utility.loadNotifications(this, this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        progressBar.setVisibility(View.GONE);
        spiceException.printStackTrace();
    }

    @Override
    public void onRequestSuccess(Object o) {
        progressBar.setVisibility(View.GONE);
        syncAdapterWithDB();

    }

    private void syncAdapterWithDB() {
        try {
            Dao<MonETSNotification, ?> dao = databaseHelper.getDao(MonETSNotification.class);
            notificationsAdapter.clear();
            notificationsAdapter.addAll(dao.queryForAll());
            notificationsAdapter.notifyDataSetChanged();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
