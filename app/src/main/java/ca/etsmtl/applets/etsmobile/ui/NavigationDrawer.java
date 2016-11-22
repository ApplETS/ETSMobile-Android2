package ca.etsmtl.applets.etsmobile.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

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
    public static final int SPONSOR_FRAGMENT = 15;

    private Drawer.OnDrawerItemClickListener drawerItemListener = null;

    public NavigationDrawer(Context context, Toolbar toolbar) {
        this.context = context;
        this.toolbar = toolbar;

        if (this.toolbar != null) {
            ((AppCompatActivity) this.context).setSupportActionBar(this.toolbar);
            ((AppCompatActivity) this.context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void create() {
        makeHeader();
        makeDrawer();
    }

    private void makeDrawer() {
        AccountHeaderBuilder builder = new AccountHeaderBuilder()
                .withActivity((MainActivity) context)
                .withDividerBelowHeader(false)
                .withHeaderBackground(R.color.ets_red_fonce)
                .withSelectionListEnabled(false);


        accountHeader = builder.build();
    }

    private void makeHeader() {
        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity((MainActivity) context)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .withSelectedItem(SCHEDULE_FRAGMENT)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(context.getString(R.string.menu_section_1_moi)).withSelectable(false),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_horaire)).withIdentifier(SCHEDULE_FRAGMENT).withIcon(R.drawable.ic_ico_schedule),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_notes)).withIdentifier(COURSE_FRAGMENT).withIcon(R.drawable.ic_ico_notes),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_2_moodle)).withIdentifier(MOODLE_FRAGMENT).withIcon(R.drawable.ic_moodle_icon_small),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_profil)).withIdentifier(PROFILE_FRAGMENT).withIcon(R.drawable.ic_ico_profil),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_monETS)).withIdentifier(MONETS_FRAGMENT).withIcon(R.drawable.ic_monets),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_bandwith)).withIdentifier(BANDWIDTH_FRAGMENT).withIcon(R.drawable.ic_ico_internet),

                        new PrimaryDrawerItem().withName(context.getString(R.string.menu_section_2_ets)),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_2_news)).withIdentifier(NEWS_FRAGMENT).withIcon(R.drawable.ic_ico_news),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_2_bottin)).withIdentifier(DIRECTORY_FRAGMENT).withIcon(R.drawable.ic_ico_bottin),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_monETS)).withIdentifier(LIBRARY_FRAGMENT).withIcon(R.drawable.ic_ico_library),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_1_bandwith)).withIdentifier(SECURITY_FRAGMENT).withIcon(R.drawable.ic_ico_security),

                        new PrimaryDrawerItem().withName(context.getString(R.string.menu_section_3_applets)),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_3_apps)).withIdentifier(ACHIEVEMENTS_FRAGMENT).withIcon(R.drawable.ic_star_60x60),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_3_about)).withIdentifier(ABOUT_FRAGMENT).withIcon(R.drawable.ic_logo_icon_final),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_3_comms)).withIdentifier(COMMENTS_FRAGMENT).withIcon(R.drawable.ic_ico_comment),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_3_sponsors)).withIdentifier(SPONSOR_FRAGMENT).withIcon(R.drawable.ic_ico_partners),
                        new SecondaryDrawerItem().withName(context.getString(R.string.menu_section_3_faq)).withIdentifier(FAQ_FRAGMENT).withIcon(R.drawable.ic_ico_faq)

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
}
