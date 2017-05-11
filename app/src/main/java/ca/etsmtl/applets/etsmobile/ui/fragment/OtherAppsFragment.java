package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import org.springframework.core.io.Resource;

import java.util.ArrayList;

import ca.etsmtl.applets.etsmobile.model.Apps;
import ca.etsmtl.applets.etsmobile.ui.adapter.OtherAppsAdapter;
import ca.etsmtl.applets.etsmobile2.R;

public class OtherAppsFragment extends WebFragment {


    private GridView gridView;
    private ArrayList<Apps> othersApps;
    private OtherAppsAdapter otherAppsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v  = inflater.inflate(R.layout.fragment_other_apps,  container, false);

        gridView = (GridView) v.findViewById(R.id.gridview);

        othersApps = new ArrayList<>();

        String[] apps = getResources().getStringArray(R.array.other_apps);

        for (String app : apps) {
            String[] tempArray = app.split(";");
            othersApps.add(new Apps(tempArray[0],tempArray[1],tempArray[2]));
        }


        otherAppsAdapter = new OtherAppsAdapter(getActivity(),R.layout.square_other_app,othersApps);

        gridView.setAdapter(otherAppsAdapter);

        otherAppsAdapter.notifyDataSetChanged();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Apps app = otherAppsAdapter.getItem(position);
                openApp(app);
            }
        });

        return v;
	}


    public void openApp(Apps app){
        Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(app.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // bring user to the market or let them choose an app
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + app.getPackageName()));
            startActivity(intent);
        }
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.menu_section_3_apps);
    }
}
