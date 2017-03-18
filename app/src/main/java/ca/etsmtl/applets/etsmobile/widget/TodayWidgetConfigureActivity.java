package ca.etsmtl.applets.etsmobile.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Spinner;

import ca.etsmtl.applets.etsmobile.ui.adapter.ColorSpinnerAdapter;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * The configuration screen for the {@link TodayWidgetProvider TodayWidget} AppWidget.
 */
public class TodayWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "ca.etsmtl.applets.etsmobile.TodayWidget";
    private static final String PREF_BG_COLOR_PREFIX_KEY = "bg_color_widget_";
    private static final String PREF_TEXT_COLOR_PREFIX_KEY = "text_color_widget_";
    private static final String PREF_OPACITY_PREFIX_KEY = "opacity_widget_";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    SeekBar mOpacitySeekBar;
    Spinner mBgColorSpinner;
    Spinner mTextColorSpinner;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = TodayWidgetConfigureActivity.this;

            int bgColor = Integer.parseInt(mBgColorSpinner.getSelectedItem().toString());
            saveBgColorPref(context, mAppWidgetId, bgColor);
            int textColor = Integer.parseInt(mTextColorSpinner.getSelectedItem().toString());
            saveTextColorPref(context, mAppWidgetId, textColor);
            saveOpacityPref(context, mAppWidgetId, mOpacitySeekBar.getProgress());

            // It is the responsibility of the configuration activity to update the app widget
            TodayWidgetProvider.updateWidget(context, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public TodayWidgetConfigureActivity() {
        super();
    }

    static void saveBgColorPref(Context context, int appWidgetId, int value) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_BG_COLOR_PREFIX_KEY + appWidgetId, value);
        prefs.apply();
    }

    static int loadBgColorPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);

        return prefs.getInt(PREF_BG_COLOR_PREFIX_KEY + appWidgetId, Color.BLACK);
    }

    static void saveTextColorPref(Context context, int appWidgetId, int value) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_TEXT_COLOR_PREFIX_KEY + appWidgetId, value);
        prefs.apply();
    }

    static int loadTextColorPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);

        return prefs.getInt(PREF_TEXT_COLOR_PREFIX_KEY + appWidgetId, Color.WHITE);
    }

    static void saveOpacityPref(Context context, int appWidgetId, int value) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_OPACITY_PREFIX_KEY + appWidgetId, value);
        prefs.apply();
    }

    static int loadOpacityPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);

        return prefs.getInt(PREF_OPACITY_PREFIX_KEY + appWidgetId, 155);
    }

    static void deleteAllPreferences(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_BG_COLOR_PREFIX_KEY + appWidgetId);
        prefs.remove(PREF_TEXT_COLOR_PREFIX_KEY + appWidgetId);
        prefs.remove(PREF_OPACITY_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.widget_today_configure);

        mOpacitySeekBar = (SeekBar) findViewById(R.id.opacity_seekbar);

        int[] colorsArray = getResources().getIntArray(R.array.widget_bg_colors);
        Integer[] colors = new Integer[colorsArray.length];
        for (int i = 0; i < colorsArray.length; i++) {
            colors[i] = Integer.valueOf(colorsArray[i]);
        }
        ColorSpinnerAdapter colorSpinnerAdapter = new ColorSpinnerAdapter(this, colors);

        mBgColorSpinner = (Spinner) findViewById(R.id.bg_color_spinner);
        mBgColorSpinner.setAdapter(colorSpinnerAdapter);
        mBgColorSpinner.setSelection(0);
        mTextColorSpinner = (Spinner) findViewById(R.id.bg_text_color_spinner);
        mTextColorSpinner.setAdapter(colorSpinnerAdapter);
        mTextColorSpinner.setSelection(0);

        findViewById(R.id.ok_btn).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }
}

