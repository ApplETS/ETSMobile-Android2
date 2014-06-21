package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import ca.etsmtl.applets.etsmobile.ui.activity.UrgenceActivity;
import ca.etsmtl.applets.etsmobile2.R;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SecuriteFragment extends BaseFragment {
	
	
	private ListView listView;
	private MapView mapView;
	GoogleMap map;
	
	double lat = 45.494498;
	double lng = -73.563124;

	
	@Override
	public void onCreate(final android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.security, container, false);
		
		mapView = (MapView) v.findViewById(R.id.map);
	    mapView.onCreate(savedInstanceState);
	    map = mapView.getMap();
	    map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);
 
        MapsInitializer.initialize(this.getActivity()); 
// 
        // Updates the location and zoom of the MapView 
       
        CameraUpdate cameraUpdate =  CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 17);
        map.animateCamera(cameraUpdate);
        
        final MarkerOptions etsMarker = new MarkerOptions();
		etsMarker.position(new LatLng(lat, lng));
		etsMarker.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.ets));
		map.addMarker(etsMarker);

		final Activity activity = getActivity();

		listView = (ListView) v.findViewById(android.R.id.list);

		final ViewGroup viewGroup = (ViewGroup) inflater.inflate(
				R.layout.secu_list_header,
				(ViewGroup) v.findViewById(R.id.secu_list_header_layout));
		listView.addHeaderView(viewGroup, null, false);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> arg0, final View arg1,
					final int arg2, final long arg3) {
				final Intent intent = new Intent(activity,
						UrgenceActivity.class);
				intent.putExtra("id", arg2);
				startActivity(intent);

			}
		});

		listView.setAdapter(new ArrayAdapter<String>(activity,R.layout.row_text_with_arrow, R.id.titleTextView, activity.getResources().getStringArray(R.array.secu_urgence)));

		viewGroup.findViewById(R.id.secu_list_header_phone).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(final View v) {
						final String phoneNumber = ((TextView) v).getText()
								.toString();
						final String uri = "tel:" + phoneNumber.trim();
						final Intent intent = new Intent(Intent.ACTION_DIAL);
						intent.setData(Uri.parse(uri));
						startActivity(intent);
					}
				});
				
		return v; 
	}
}
