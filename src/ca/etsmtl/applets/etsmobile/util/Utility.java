package ca.etsmtl.applets.etsmobile.util;

import android.content.Context;
import android.content.res.Configuration;

public class Utility {

	public static boolean isTabletDevice(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;

	}

}
