package ca.etsmtl.applets.etsmobile;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.util.LinkedHashMap;

import ca.etsmtl.applets.etsmobile.model.MyMenuItem;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.ui.activity.MainActivity;
import ca.etsmtl.applets.etsmobile.ui.fragment.AboutFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.BandwithFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.BiblioFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.BottinFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.CommentairesFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.HoraireFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.MoodleFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.NewsFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.NotesFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.ProfilFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.RadioFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.SecuriteFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.SponsorsFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.TodayFragment;
import ca.etsmtl.applets.etsmobile.util.NoteManager;
import ca.etsmtl.applets.etsmobile.util.ProfilManager;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Phil on 17/11/13.
 */
public class ApplicationManager extends Application {

	public static LinkedHashMap<String, MyMenuItem> mMenu = new LinkedHashMap<String, MyMenuItem>(17);
	public static UserCredentials userCredentials;

	@Override
	public void onCreate() {
		super.onCreate();

        //BugSenseHandler.initAndStartSession(this, getString(R.string.bugsense));


        // Section 1 - Moi
		String title = getString(R.string.menu_section_1_moi);
		mMenu.put(title, new MyMenuItem(title, null));

		title = getString(R.string.menu_section_1_ajd);
		mMenu.put(TodayFragment.class.getName(), new MyMenuItem(title, TodayFragment.class, R.drawable.ic_ico_aujourdhui));

		title = getString(R.string.menu_section_1_horaire);
		mMenu.put(HoraireFragment.class.getName(), new MyMenuItem(title, HoraireFragment.class, R.drawable.ic_ico_schedule));

		title = getString(R.string.menu_section_1_notes);
		mMenu.put(NotesFragment.class.getName(), new MyMenuItem(title, NotesFragment.class, R.drawable.ic_ico_notes));

		// title = getString(R.string.menu_section_1_stages);
		// mMenu.put(title, new MyMenuItem(title, StagesFragment.class,
		// R.drawable.ic_ico_stage));


		title = getString(R.string.menu_section_2_moodle);
		mMenu.put(MoodleFragment.class.getName(), new MyMenuItem(title, MoodleFragment.class, R.drawable.ic_moodle_icon_smal));

		title = getString(R.string.menu_section_1_profil);
		mMenu.put(ProfilFragment.class.getName(), new MyMenuItem(title, ProfilFragment.class, R.drawable.ic_ico_profil));

		title = getString(R.string.menu_section_1_bandwith);
		mMenu.put(BandwithFragment.class.getName(), new MyMenuItem(title, BandwithFragment.class, R.drawable.ic_ico_internet));
		
		

		// Section 2 - �TS
		title = getString(R.string.menu_section_2_ets);
		mMenu.put(title, new MyMenuItem(title, null));

        /*todo
		title = getString(R.string.menu_section_2_news);
		mMenu.put(NewsFragment.class.getName(), new MyMenuItem(title, NewsFragment.class, R.drawable.ic_ico_news));
        //*/

		title = getString(R.string.menu_section_2_bottin);
		mMenu.put(BottinFragment.class.getName(), new MyMenuItem(title, BottinFragment.class, R.drawable.ic_ico_bottin));

		title = getString(R.string.menu_section_2_biblio);
		mMenu.put(BiblioFragment.class.getName(), new MyMenuItem(title, BiblioFragment.class, R.drawable.ic_ico_library));

		title = getString(R.string.menu_section_2_radio);
		mMenu.put(RadioFragment.class.getName(), new MyMenuItem(title, RadioFragment.class, R.drawable.ic_ico_radio));

		title = getString(R.string.menu_section_2_securite);
		mMenu.put(SecuriteFragment.class.getName(), new MyMenuItem(title, SecuriteFragment.class, R.drawable.ic_ico_security));

		// Section 3 - ApplETS
		title = getString(R.string.menu_section_3_applets);
		mMenu.put(title, new MyMenuItem(title, null));

		title = getString(R.string.menu_section_3_about);
		mMenu.put(AboutFragment.class.getName(), new MyMenuItem(title, AboutFragment.class, R.drawable.ic_logo_icon_final));

		title = getString(R.string.menu_section_3_comms);
		mMenu.put(CommentairesFragment.class.getName(), new MyMenuItem(title, CommentairesFragment.class, R.drawable.ic_ico_comment));

		title = getString(R.string.menu_section_3_sponsors);
		mMenu.put(SponsorsFragment.class.getName(), new MyMenuItem(title, SponsorsFragment.class, R.drawable.ic_ico_partners));

		// DataManager instance = DataManager.getInstance(this);
		// Etudiant registeredEtudiant = instance.getRegisteredEtudiant();
		// if (registeredEtudiant != null) {
		// userCredentials = new UserCredentials("", "");
		// }
        SecurePreferences securePreferences = new SecurePreferences(this);
		String u = securePreferences.getString(UserCredentials.CODE_U, "");
		String p = securePreferences.getString(UserCredentials.CODE_P, "");

		if (u.length() > 0 && p.length() > 0) {
			userCredentials = new UserCredentials(u, p);
		}
	}

	public static void deconnexion(final Activity activity) {


		final Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
		editor.clear();
		editor.commit();

        // Enlever le profil de la DB SQLite
        new ProfilManager(activity).removeProfil();

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
