package ca.etsmtl.applets.etsmobile.ui.fragment;

import ca.etsmtl.applets.etsmobile2.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class RadioFragment extends WebFragment {

    private ImageView iv_radio;
    private Button bt_radio;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v  = inflater.inflate(R.layout.fragment_radio,  container, false);
        iv_radio = (ImageView) v.findViewById(R.id.iv_radio);
        bt_radio = (Button) v.findViewById(R.id.bt_radio);
        iv_radio.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRadio();
            }
        });

        bt_radio.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRadio();
            }
        });
        openRadio();
        return v;
	}


    public void openRadio(){
        Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getString(R.string.radio_piranha));
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // bring user to the market
            // or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + getString(R.string.moodle)));
            startActivity(intent);
        }
    }
}
