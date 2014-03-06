package ca.etsmtl.applets.etsmobile.ui.activity;

import java.util.Collection;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.MyMenuItem;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.ui.adapter.MenuAdapter;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Main Activity for ÉTSMobile, handles the login and the menu
 * 
 * @author Philippe David
 * 
 */
public class MainActivity extends Activity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private CharSequence mTitle;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		int stringSet = ApplicationManager.mMenu.keySet().size();
		final Collection<MyMenuItem> myMenuItems = ApplicationManager.mMenu
				.values();

		MyMenuItem[] menuItems = new MyMenuItem[stringSet];
		mDrawerList.setAdapter(new MenuAdapter(this, myMenuItems
				.toArray(menuItems)));

		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(getString(R.string.drawer_title));
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		MyMenuItem ajdItem = ApplicationManager.mMenu
				.get(getString(R.string.menu_section_1_ajd));

		// Select Aujourd'Hui
		selectItem(ajdItem.title, 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	protected void onStart() {
		super.onStart();
		DataManager.getInstance(this).start();
	}

	@Override
	protected void onStop() {
		DataManager.getInstance(this).stop();
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ApplicationManager.userCredentials == null) {

			Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, 0);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode > 0) {
				Bundle extras = data.getExtras();
				String codeU = extras.getString(UserCredentials.CODE_U);
				String codeP = extras.getString(UserCredentials.CODE_P);
				ApplicationManager.userCredentials = new UserCredentials(codeU,
						codeP);
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@SuppressWarnings("rawtypes")
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			final Object itemAtPosition = parent.getItemAtPosition(position);
			MyMenuItem myMenuItem = (MyMenuItem) itemAtPosition;

			if (myMenuItem.resId == R.drawable.ic_ico_comment) {
				// contact; Ask to open email app; prefill email info
				Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(
						android.content.Intent.EXTRA_EMAIL,
						new String[] { getString(R.string.applets_ens_etsmtl_ca) });
				intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						getString(R.string.etsmobile_android_commentaire));
				intent.putExtra(android.content.Intent.EXTRA_TEXT,
						getString(R.string.default_comment));
			} else {
				selectItem(myMenuItem.title, position);
			}
		}
	}

	/**
	 * Swaps fragments in the main content view
	 */
	@SuppressWarnings("rawtypes")
	private void selectItem(String key, int position) {
		// Create a new fragment and specify the planet to show based on
		// position
		Fragment fragment = null;
		Class aClass = ApplicationManager.mMenu.get(key).mClass;
		try {
			fragment = (Fragment) aClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		// Bundle args = new Bundle();
		// args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
		// fragment.setArguments(args);

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment, aClass.getName())
				.addToBackStack(null).commit();

		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(key);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}
}
