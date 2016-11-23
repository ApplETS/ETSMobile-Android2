package ca.etsmtl.applets.etsmobile.ui;

import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.ui.activity.MainActivity;
import ca.etsmtl.applets.etsmobile2.R;

import static ca.etsmtl.applets.etsmobile2.R.drawable.ic_ico_security;

/**
 * Created by club on 21/11/16.
 */

public class NavigationDrawer {
    private Context context;
    private Toolbar toolbar;

    private AccountHeader accountHeader;
    private Drawer drawer;

    public static final int SCHEDULE_FRAGMENT = 1;
    public static final int COURSE_FRAGMENT = 2;
    public static final int MOODLE_FRAGMENT = 3;
    public static final int PROFILE_FRAGMENT = 4;
    public static final int MONETS_FRAGMENT = 5;
    public static final int BANDWIDTH_FRAGMENT = 7;
    public static final int NEWS_FRAGMENT = 8;
    public static final int DIRECTORY_FRAGMENT = 9;
    public static final int LIBRARY_FRAGMENT = 10;
    public static final int SECURITY_FRAGMENT = 11;
    public static final int ACHIEVEMENTS_FRAGMENT = 12;
    public static final int ABOUT_FRAGMENT = 13;
    public static final int COMMENTS_FRAGMENT = 14;
    public static final int FAQ_FRAGMENT = 15;
    public static final int SPONSOR_FRAGMENT = 16;
    public static final int TODAY_FRAGMENT = 17;

    public static final long LOGIN_SETTING =  18;

    private Drawer.OnDrawerItemClickListener drawerItemListener = null;

    private boolean isUserLoggedIn;
    private AccountHeader.OnAccountHeaderListener accountHeaderListener;

    public NavigationDrawer(Context context, Toolbar toolbar) {
        this.context = context;
        this.toolbar = toolbar;

        if (this.toolbar != null) {
            ((AppCompatActivity) this.context).setSupportActionBar(this.toolbar);
            ((AppCompatActivity) this.context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        isUserLoggedIn = ApplicationManager.userCredentials != null;
    }

    public void create() {
        makeHeader();
        makeDrawer();
    }
    final IProfile profile = new ProfileDrawerItem().withName("Etudiant").withEmail("Futur ingenieur(e)").withIcon(R.drawable.ets);
    final IProfile login = new ProfileSettingDrawerItem().withName("LOGIN");
    final IProfile logout = new ProfileSettingDrawerItem().withName("LOGOUT");

    private void makeHeader() {
        AccountHeaderBuilder builder = new AccountHeaderBuilder()
                .withActivity((MainActivity) context)
                .withHeaderBackground(R.drawable.ets_background)
                .withCompactStyle(true)
                .addProfiles(profile,login,logout);


        accountHeader = builder.build();
    }

    private void makeDrawer() {
        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity((MainActivity) context)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .withSelectedItem(TODAY_FRAGMENT)
                .withDisplayBelowStatusBar(true)
//                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new ExpandableDrawerItem().withName(context.getString(R.string.menu_section_1_moi)).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_ajd)).withIdentifier(TODAY_FRAGMENT).withIcon(R.drawable.ic_ico_aujourdhui).withEnabled(isUserLoggedIn),
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_horaire)).withIdentifier(SCHEDULE_FRAGMENT).withIcon(R.drawable.ic_ico_schedule).withEnabled(isUserLoggedIn),
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_notes)).withIdentifier(COURSE_FRAGMENT).withIcon(R.drawable.ic_ico_notes).withEnabled(isUserLoggedIn),
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_2_moodle)).withIdentifier(MOODLE_FRAGMENT).withIcon(R.drawable.ic_moodle_icon_small).withEnabled(isUserLoggedIn),
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_profil)).withIdentifier(PROFILE_FRAGMENT).withIcon(R.drawable.ic_ico_profil).withEnabled(isUserLoggedIn),
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_monETS)).withIdentifier(MONETS_FRAGMENT).withIcon(R.drawable.ic_monets).withEnabled(isUserLoggedIn),
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_bandwith)).withIdentifier(BANDWIDTH_FRAGMENT).withIcon(R.drawable.ic_ico_internet)
                                ),
                        new ExpandableDrawerItem().withName(context.getString(R.string.menu_section_2_ets)).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_2_news)).withIdentifier(NEWS_FRAGMENT).withIcon(R.drawable.ic_ico_news),
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_2_bottin)).withIdentifier(DIRECTORY_FRAGMENT).withIcon(R.drawable.ic_ico_bottin),
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_monETS)).withIdentifier(LIBRARY_FRAGMENT).withIcon(R.drawable.ic_ico_library),
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_bandwith)).withIdentifier(SECURITY_FRAGMENT).withIcon(R.drawable.ic_ico_security)
                                ),
                        new ExpandableDrawerItem().withName(context.getString(R.string.menu_section_3_applets)).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_3_apps)).withIdentifier(ACHIEVEMENTS_FRAGMENT).withIcon(R.drawable.ic_star_60x60),
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_3_about)).withIdentifier(ABOUT_FRAGMENT).withIcon(R.drawable.ic_logo_icon_final),
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_3_comms)).withIdentifier(COMMENTS_FRAGMENT).withIcon(R.drawable.ic_ico_comment),
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_3_sponsors)).withIdentifier(SPONSOR_FRAGMENT).withIcon(R.drawable.ic_ico_partners),
                                new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_3_faq)).withIdentifier(FAQ_FRAGMENT).withIcon(R.drawable.ic_ico_faq)
                        )

                );

        if (drawerItemListener != null)
            drawerBuilder.withOnDrawerItemClickListener(drawerItemListener);


        drawer = drawerBuilder.build();
    }


    public Drawer getDrawer() {
        return drawer;
    }


    public void setDrawerItemListener(Drawer.OnDrawerItemClickListener drawerItemListener) {
        this.drawerItemListener = drawerItemListener;
    }
    public void setAccountHeaderListener(AccountHeader.OnAccountHeaderListener listener) {
        this.accountHeaderListener = listener;
    }

}
