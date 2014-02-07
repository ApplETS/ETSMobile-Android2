package ca.etsmtl.applets.etsmobile;

import android.app.Application;
import android.content.Intent;

import java.util.LinkedHashMap;

import ca.etsmtl.applets.etsmobile.model.MyMenuItem;
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
import ca.etsmtl.applets.etsmobile.ui.fragment.StagesFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.TodaysFragment;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Phil on 17/11/13.
 */
public class ApplicationManager extends Application {

    public static LinkedHashMap<String, MyMenuItem> mMenu = new LinkedHashMap<String, MyMenuItem>(17);

    @Override
    public void onCreate() {
        super.onCreate();

        //Section 1 - Moi
        String title = getString(R.string.menu_section_1_moi);
        mMenu.put(title, new MyMenuItem(title, null));

        title = getString(R.string.menu_section_1_ajd);
        mMenu.put(title, new MyMenuItem(title, TodaysFragment.class));

        title = getString(R.string.menu_section_1_horaire);
        mMenu.put(title, new MyMenuItem(title, HoraireFragment.class));

        title = getString(R.string.menu_section_1_notes);
        mMenu.put(title, new MyMenuItem(title, NotesFragment.class));

        title = getString(R.string.menu_section_1_stages);
        mMenu.put(title, new MyMenuItem(title, StagesFragment.class));

        title = getString(R.string.menu_section_1_profil);
        mMenu.put(title, new MyMenuItem(title, ProfilFragment.class));

        title = getString(R.string.menu_section_1_bandwith);
        mMenu.put(title, new MyMenuItem(title, BandwithFragment.class));

        // Section 2 - ??TS
        title = getString(R.string.menu_section_2_ets);
        mMenu.put(title, new MyMenuItem(title, null));

        title = getString(R.string.menu_section_2_news);
        mMenu.put(title, new MyMenuItem(title, NewsFragment.class));

        title = getString(R.string.menu_section_2_bottin);
        mMenu.put(title, new MyMenuItem(title, BottinFragment.class));

        title = getString(R.string.menu_section_2_biblio);
        mMenu.put(title, new MyMenuItem(title, BiblioFragment.class));
        
        title = getString(R.string.menu_section_2_moodle);
        mMenu.put(title, new MyMenuItem(title, MoodleFragment.class));

        title = getString(R.string.menu_section_2_radio);
        mMenu.put(title, new MyMenuItem(title, RadioFragment.class));

        title = getString(R.string.menu_section_2_securite);
        mMenu.put(title, new MyMenuItem(title, SecuriteFragment.class));

        // Section 3 - ApplETS
        title = getString(R.string.menu_section_3_applets);
        mMenu.put(title, new MyMenuItem(title, null));

        title = getString(R.string.menu_section_3_about);
        mMenu.put(title, new MyMenuItem(title, AboutFragment.class));

        title = getString(R.string.menu_section_3_comms);
        mMenu.put(title, new MyMenuItem(title, CommentairesFragment.class));

        title = getString(R.string.menu_section_3_sponsors);
        mMenu.put(title, new MyMenuItem(title, SponsorsFragment.class));
    }
}
