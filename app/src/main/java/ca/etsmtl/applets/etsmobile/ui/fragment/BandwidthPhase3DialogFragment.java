package ca.etsmtl.applets.etsmobile.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import ca.etsmtl.applets.etsmobile2.R;
/**
 * A simple {@link Fragment} subclass.
 */
public class BandwidthPhase3DialogFragment extends DialogFragment {


    static BandwidthPhase3DialogFragment newInstance() {

        BandwidthPhase3DialogFragment fragment = new BandwidthPhase3DialogFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bandwidth_phase3_dialog, container, false);

        TextView msgTv = v.findViewById(R.id.bandwith_phase_3_dialog_tv);
        msgTv.setText(Html.fromHtml(getString(R.string.bandwidth_phase_3_msg)));

        ViewGroup phoneNumberView = v.findViewById(R.id.bandwidth_phase_3_dialog_phone_number);
        ViewGroup emailAddressView = v.findViewById(R.id.bandwidth_phase_3_dialog_email);
        Button dismissBtn = v.findViewById(R.id.bandwidth_phase_3_dialog_dismiss_btn);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.bandwidth_phase_3_dialog_phone_number:
                        call();
                        break;
                    case R.id.bandwidth_phase_3_dialog_email:
                        sendEmail();
                        break;
                    case R.id.bandwidth_phase_3_dialog_dismiss_btn:
                        dismiss();
                        break;
                }
            }
        };

        phoneNumberView.setOnClickListener(onClickListener);
        emailAddressView.setOnClickListener(onClickListener);
        dismissBtn.setOnClickListener(onClickListener);

        return v;
    }

    private void call() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString(R.string.cooptel_phone_number)));
        startActivity(intent);
    }

    private void sendEmail() {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.cooptel_email_address)});
        startActivity(emailIntent);
    }
}
