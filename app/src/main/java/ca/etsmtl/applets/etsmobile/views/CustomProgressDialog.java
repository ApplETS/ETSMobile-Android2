package ca.etsmtl.applets.etsmobile.views;

import ca.etsmtl.applets.etsmobile2.R;
import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {

	private ImageView rotatingImageView;
	private TextView textView;
		
	public CustomProgressDialog(Context context, int resourceIdOfImage, String textToDisplay) {
		super(context, R.style.CustomProgressDialog);
		setTitle(null);
		setCancelable(false);
		setOnCancelListener(null);
		
		RelativeLayout layout = new RelativeLayout(context);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		
		rotatingImageView = new ImageView(context);
		rotatingImageView.setImageResource(resourceIdOfImage);
		rotatingImageView.setId(1);
		
		layout.addView(rotatingImageView, params);
		
		textView = new TextView(context);
		textView.setText(textToDisplay);
		textView.setTextColor(context.getResources().getColor(R.color.white));
		
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.BELOW, rotatingImageView.getId());
		
		layout.addView(textView,params);
		
		addContentView(layout, params);
		
	}
		
	@Override
	public void show() {
		super.show();
		
		RotateAnimation anim = new RotateAnimation(0.0f, 359.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(3000);
		
		rotatingImageView.setAnimation(anim);
		rotatingImageView.startAnimation(anim);
		
	}
}
