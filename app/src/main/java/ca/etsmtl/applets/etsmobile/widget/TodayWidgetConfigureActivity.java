package ca.etsmtl.applets.etsmobile.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;

import ca.etsmtl.applets.etsmobile.ui.adapter.ColorSpinnerAdapter;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * The configuration screen for the {@link TodayWidgetProvider TodayWidget} AppWidget.
 */
public class TodayWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "ca.etsmtl.applets.etsmobile.TodayWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final String PREF_TRANSLUCENT_PREFIX_KEY = "translucent_widget_";
    private static final String PREF_BG_COLOR_PREFIX_KEY = "bg_color_widget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    //EditText mAppWidgetText;
    CheckBox mTranslucentCheckBox;
    Spinner mColorSpinner;
    Spinner mTextColorSpinner;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = TodayWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            //String widgetText = mAppWidgetText.getText().toString();
            //saveTitlePref(context, mAppWidgetId, widgetText);

            saveTranslucentPref(context, mAppWidgetId, mTranslucentCheckBox.isChecked());

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            //TodayWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
            TodayWidgetProvider.updateAllWidgets(context);

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

    static void saveTranslucentPref(Context context, int appWidgetId, boolean value) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(PREF_TRANSLUCENT_PREFIX_KEY + appWidgetId, value);
        prefs.apply();
    }

    static boolean loadTranslucentPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);

        return prefs.getBoolean(PREF_TRANSLUCENT_PREFIX_KEY + appWidgetId, false);
    }

    static void deleteTranslucentPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_TRANSLUCENT_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.widget_today_configure);
        //mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
        mTranslucentCheckBox = (CheckBox) findViewById(R.id.translucent_checkbox);
        int[] colorsArray = getResources().getIntArray(R.array.widget_bg_colors);
        Integer[] colors = new Integer[colorsArray.length];
        for (int i = 0; i < colorsArray.length; i++) {
            colors[i] = Integer.valueOf(colorsArray[i]);
        }
        ColorSpinnerAdapter colorSpinnerAdapter = new ColorSpinnerAdapter(this, colors);
        // TODO save colors prefs listen select
        mColorSpinner = (Spinner) findViewById(R.id.bg_color_spinner);
        mColorSpinner.setAdapter(colorSpinnerAdapter);
        mColorSpinner.setSelection(0);
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

        //mAppWidgetText.setText(loadTitlePref(TodayWidgetConfigureActivity.this, mAppWidgetId));

    }
}

