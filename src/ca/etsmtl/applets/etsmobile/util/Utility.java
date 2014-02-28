package ca.etsmtl.applets.etsmobile.util;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.ui.activity.MainActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

public class Utility {
	
	public static boolean isTabletDevice(Context context){
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
		
	}
	
public static void deconnexion(final Activity activity){
		
		final Editor editor = PreferenceManager
				.getDefaultSharedPreferences(activity).edit();
		editor.clear();
		editor.commit();
	
		ApplicationManager.userCredentials = null;
		Intent intent = new Intent(activity, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
	    new Thread(new Runnable() {
			
			@Override
			public void run() {
				activity.finish();
			}
		}).start();

	}

}
