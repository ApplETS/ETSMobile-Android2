package ca.etsmtl.applets.etsmobile.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.model.Sponsor;
import ca.etsmtl.applets.etsmobile.model.SponsorList;
import ca.etsmtl.applets.etsmobile.ui.fragment.SponsorsFragment;

/**
 * Created by Steven on 2015-11-05.
 */
public class SponsorManager extends Observable implements RequestListener<Object> {

    Context context;
    private boolean synchListeDeSponsor = false;

    public SponsorManager(Context context) {
        this.context = context;
    }

    public void updateSponsor(List<Sponsor> sponsorList) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        try {
            for (Sponsor sponsor : sponsorList) {
                dbHelper.getDao(Sponsor.class).createOrUpdate(sponsor);
            }
        } catch (SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
    }

    public ArrayList<Sponsor> getSponsorList() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        ArrayList<Sponsor> sponsorList = null;
        try {
            sponsorList = (ArrayList<Sponsor>) dbHelper.getDao(Sponsor.class).queryForAll();
        } catch (SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
        return sponsorList;
    }

    public void remove() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        try {
            dbHelper.getDao(Sponsor.class).deleteBuilder().delete();
        } catch (SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
    }

    public void deleteExpiredSponsor(SponsorList sponsorList) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        HashMap<String, Sponsor> sponsorHashMap = new HashMap<String, Sponsor>();
        for (Sponsor sponsor : sponsorList) {
            sponsorHashMap.put(sponsor.getName(), sponsor);
        }

        ArrayList<Sponsor> dbSponsor;
        try {
            dbSponsor = (ArrayList<Sponsor>) dbHelper.getDao(Sponsor.class).queryForAll();
            for (Sponsor sponsorNew : dbSponsor) {

                if (!sponsorHashMap.containsKey(sponsorNew.getName())) {
                    Dao<Sponsor, String> sponsorDao = dbHelper.getDao(Sponsor.class);
                    sponsorDao.deleteById(sponsorNew.getName());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        spiceException.printStackTrace();
    }

    @Override
    public void onRequestSuccess(final Object o) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                //ListeDeSponsor
                if (o instanceof SponsorList) {
                    SponsorList sponsorList = (SponsorList) o;

                    deleteExpiredSponsor(sponsorList);
                    updateSponsor(sponsorList);

                    synchListeDeSponsor = true;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (synchListeDeSponsor) {
                    SponsorManager.this.setChanged();
                    SponsorManager.this.notifyObservers(SponsorsFragment.class.getName());
                }
            }


        }.execute();
    }
}
