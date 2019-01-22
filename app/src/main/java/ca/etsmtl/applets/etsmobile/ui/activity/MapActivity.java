package ca.etsmtl.applets.etsmobile.ui.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.ImageButton;
import android.widget.SearchView;

import ca.etsmtl.applets.etsmobile2.R;

public class MapActivity extends Activity {

    SurfaceView surfaceView;
    ImageButton resetBtn;
    ImageButton zoomBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        surfaceView = (SurfaceView) findViewById(R.id.activity_map_surfaceView);
        resetBtn = (ImageButton) findViewById(R.id.activity_map_reset);
        zoomBtn = (ImageButton) findViewById(R.id.activity_map_zoom);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_map_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget;
        // expand it by default

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_map_search:

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
