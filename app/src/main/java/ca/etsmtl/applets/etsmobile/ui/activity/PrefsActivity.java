package ca.etsmtl.applets.etsmobile.ui.activity;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 12/14/14.
 */
public class PrefsActivity extends PreferenceActivity {
    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment(),PrefsFragment.class.getName()).addToBackStack(null).commit();

        setTitle("Préférences");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This fragment shows the preferences for the first header.
     */
    public static class PrefsFragment extends PreferenceFragment {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

            String[] selections = {"ets"};
            Set<String> selectionSet = new HashSet<String>();
            selectionSet.addAll(Arrays.asList(selections));

            MultiSelectListPreference multiSelectPref = new MultiSelectListPreference(getActivity());
            multiSelectPref.setKey("multi_pref");
            multiSelectPref.setTitle("Choix des sources");
            multiSelectPref.setEntries(R.array.sources_news);
            multiSelectPref.setEntryValues(R.array.sources_news_values);
            multiSelectPref.setDefaultValue(selectionSet);
            getPreferenceScreen().addPreference(multiSelectPref);





            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.
//            PreferenceManager.setDefaultValues(getActivity(),R.xml.advanced_preferences, false);

            // Load the preferences from an XML resource

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    // app icon in action bar clicked; goto parent activity.
                    getActivity().onBackPressed();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }


        }
    }




}
