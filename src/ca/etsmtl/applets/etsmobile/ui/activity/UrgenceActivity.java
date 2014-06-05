/*******************************************************************************
 * Copyright 2013 Club ApplETS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ca.etsmtl.applets.etsmobile.ui.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.XMLReader;

import ca.etsmtl.applets.etsmobile2.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.Html.TagHandler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


public class UrgenceActivity extends Activity {

	private static final String APPLICATION_PDF = "application/pdf";
	private static final String SDCARD = Environment
			.getExternalStorageDirectory().getPath();
	private int id;
	private String pdf_raw;

	private String[] urgence;
	private WebView webView;

	private void copyAssets() {
		final AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (final IOException e) {
			Log.e("tag", e.getMessage());
		}
		for (final String filename : files) {
			InputStream in = null;
			OutputStream out = null;
			try {

				final File f = new File(UrgenceActivity.SDCARD + "/" + filename);
				if (!f.exists()) {
					in = assetManager.open(filename);
					out = new FileOutputStream(UrgenceActivity.SDCARD + "/"
							+ filename);
					copyFile(in, out);
					in.close();
					in = null;
					out.flush();
					out.close();
					out = null;
				}
			} catch (final Exception e) {
				Log.e("tag", e.getMessage());
			}
		}
		openPdf();
	}

	private void copyFile(final InputStream in, final OutputStream out)
			throws IOException {
		final byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.urgence);
		id = getIntent().getExtras().getInt("id");
		webView = (WebView) findViewById(R.id.web_view);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);		
		urgence = getResources().getStringArray(R.array.secu_urgence);
		String url = "";
		
		switch (--id) {
		case 0:
			url = "file:///android_asset/urgence_resum_bombe.html";
			pdf_raw = "appel_a_la_bombe_2009_04_01.pdf";
			break;
		case 1:
			url = "file:///android_asset/urgence_resum_colis.html";
			pdf_raw = "colis_suspect_et_nrbc_2009_04_01.pdf";
			break;
		case 2:
			url = "file:///android_asset/urgence_resum_feu.html";
			pdf_raw = "incendie_evacuation_urgence.pdf";
			break;
		case 3:
			url = "file:///android_asset/urgence_resum_odeur.html";
			pdf_raw = "odeur_suspecte_et_fuite_gaz_2009_04_01.pdf";
			break;
		case 4:
			url = "file:///android_asset/urgence_resum_pane_asc.html";
			pdf_raw = "panne_assenceur_2009_04_01.pdf";
			break;
		case 5:
			url = "file:///android_asset/urgence_resum_panne_elec.html";
			pdf_raw = "panne_electrique_2009_04_01.pdf";
			break;
		case 6:
			url = "file:///android_asset/urgence_resum_pers_arm.html";
			pdf_raw = "personne_armee_2009_04_01.pdf";
			break;
		case 7:
			url = "file:///android_asset/urgence_resum_medic.html";
			pdf_raw = "urgence_cedicale_2009_04_01.pdf";
			break;
		default:
			break;
		}
	
		webView.loadUrl(url);
		
		webView.requestFocus();

		actionBar.setTitle(urgence[id]);
		findViewById(R.id.voirPDF_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(final View v) {
						copyAssets();
					}
				});
		findViewById(R.id.urgence_appel_urgence).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(final View v) {
						final String uri = "tel:"
								+ getString(R.string.secu_phone_lbl);
						final Intent intent = new Intent(Intent.ACTION_DIAL);
						intent.setData(Uri.parse(uri));
						startActivity(intent);
					}
				});
	}

	private boolean isCallable(Intent intent) {
		final List<ResolveInfo> list = getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	private void openPdf() {

		final Intent intent = new Intent(Intent.ACTION_VIEW);

		final Uri data = Uri.fromFile(new File(UrgenceActivity.SDCARD + "/"
				+ pdf_raw));
		intent.setDataAndType(data, UrgenceActivity.APPLICATION_PDF);
		if (isCallable(intent)) {
			startActivityForResult(intent, Activity.RESULT_OK);
		}
	}
	
	@Override 
	public boolean onOptionsItemSelected (MenuItem item){
		finish();
		return true;
	}
	
   private class UrgenceAdapter extends ArrayAdapter<String>{

	public UrgenceAdapter(Context context, int resource,
			int textViewResourceId, List<String> objects) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}


	private Context ctx;
	
	
	 @Override
     public View getView(int position, View convertView, ViewGroup parent) {
		 String text ="";
		 text = getItem(position);
         if(convertView==null){
             // inflate the layout
             LayoutInflater inflater =  (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(text.contains("\u0020■")){
          	 text = text.substring(0,2);
          	 convertView = inflater.inflate(R.layout.second_bullet_row, parent, false);
            }
//             }else if(text.contains("■")){
//            	 text = text.substring(0,1);
//            	 convertView = inflater.inflate(R.layout.bullet_row, parent, false);
//             }else{
//            	 convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
//             }
         }
         
        TextView textView = (TextView) convertView.findViewById(R.id.textElem);
        if(textView!=null){
        	textView.setText(text);
        }else{
        	((TextView)convertView.findViewById(android.R.id.text1)).setText(text);
        }
        return convertView;
	 }
   
   }
}
