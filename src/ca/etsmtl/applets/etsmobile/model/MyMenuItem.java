package ca.etsmtl.applets.etsmobile.model;

import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Phil on 17/11/13.
 */
public class MyMenuItem {

	public String title = "";
	public int resId = R.drawable.ic_launcher;
	public Class mClass = null;

	public MyMenuItem(String title, Class mClass) {
		this.title = title;
		this.mClass = mClass;
	}

	public MyMenuItem(String title, Class mClass, int resId) {
		this.title = title;
		this.mClass = mClass;
		this.resId = resId;
	}
}
