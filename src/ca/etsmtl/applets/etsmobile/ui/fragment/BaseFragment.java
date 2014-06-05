package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Base fragment 
 * 
 * @author Philippe
 */
public abstract class BaseFragment extends Fragment {

	@InjectView(R.id.base_layout_loading_pb)
	protected ProgressBar progressBar;

	@InjectView(R.id.base_layout_error_tv)
	protected TextView errorMessageTv;

	protected int layoutId = -1;


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	//View onCreateView = super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(layoutId, container, false);
		ButterKnife.inject(this, v);
		if(errorMessageTv!=null){
		   this.errorMessageTv.setVisibility(View.GONE);
		}
		return v;
	}

}
