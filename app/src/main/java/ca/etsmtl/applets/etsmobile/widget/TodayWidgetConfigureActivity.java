package ca.etsmtl.applets.etsmobile.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.Locale;

import ca.etsmtl.applets.etsmobile.ui.adapter.ColorSpinnerAdapter;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * The configuration screen for the {@link TodayWidgetProvider TodayWidget} AppWidget.
 */
public class TodayWidgetConfigureActivity extends AppCompatActivity {

    //******************************
    //CONSTANTES
    //******************************
    private static final String PREFS_NAME = "ca.etsmtl.applets.etsmobile.TodayWidget";
    private static final String PREF_BG_COLOR_PREFIX_KEY = "bg_color_widget_";
    private static final String PREF_BG_COLOR_DEFAULT_PREFIX_KEY = "bg_color_widget";
    private static final String PREF_TEXT_COLOR_PREFIX_KEY = "text_color_widget_";
    private static final String PREF_TEXT_COLOR_DEFAULT_PREFIX_KEY = "text_color_widget";
    private static final String PREF_OPACITY_PREFIX_KEY = "opacity_widget_";
    private static final String PREF_OPACITY_DEFAULT_PREFIX_KEY = "opacity_widget";
    private static final String PREF_LANGUAGE_PREFIX_KEY = "language_widget_";

    //******************************
    //ATTRIBUTS
    //******************************
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private SeekBar mOpacitySeekBar;
    private ColorSpinnerAdapter mColorSpinnerAdapter;
    private Spinner mBgColorSpinner;
    private Spinner mTextColorSpinner;
    private RelativeLayout mWidgetPreviewLayout;
    private TextView mWidgetTodaysNameTv;
    private ImageButton syncBtn;

    //******************************
    //ÉCOUTEURS
    //******************************
    View.OnClickListener mOkBtnOnClickListener = new View.OnClickListener() {
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

    SeekBar.OnSeekBarChangeListener mOpacityListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int bgColor = Integer.parseInt(mBgColorSpinner.getSelectedItem().toString());
            bgColor = ColorUtils.setAlphaComponent(bgColor, progress);

            mWidgetPreviewLayout.setBackgroundColor(bgColor);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    Spinner.OnItemSelectedListener mBgColorSpinnerListener = new Spinner.OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int bgColor = Integer.parseInt(mBgColorSpinner.getSelectedItem().toString());
            int opacity = mOpacitySeekBar.getProgress();
            bgColor = ColorUtils.setAlphaComponent(bgColor, opacity);

            mWidgetPreviewLayout.setBackgroundColor(bgColor);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    Spinner.OnItemSelectedListener mTextColorSpinnerListener = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int textColor = Integer.parseInt(mTextColorSpinner.getSelectedItem().toString());
            DateTime dateActuelle = new DateTime();
            DateTime.Property pDoW = dateActuelle.dayOfWeek();
            DateTime.Property pDoM = dateActuelle.dayOfMonth();
            DateTime.Property pMoY = dateActuelle.monthOfYear();
            Locale locale = TodayWidgetConfigureActivity.this.getResources().getConfiguration()
                    .locale;
            String dateActuelleStr = TodayWidgetConfigureActivity.this.getString(R.string.horaire,
                    pDoW.getAsText(locale),
                    pDoM.getAsText(locale), pMoY.getAsText(locale));

            mWidgetTodaysNameTv.setText(dateActuelleStr);
            mWidgetTodaysNameTv.setTextColor(textColor);

            Bitmap icon = BitmapFactory
                    .decodeResource(TodayWidgetConfigureActivity.this.getResources(),
                            R.drawable.ic_sync);

            // Copie mutable de l'icône
            icon = icon.copy(Bitmap.Config.ARGB_8888, true);
            Paint paint = new Paint();
            ColorFilter filter = new PorterDuffColorFilter(textColor, PorterDuff.Mode.SRC_IN);
            paint.setColorFilter(filter);
            Canvas canvas = new Canvas(icon);
            canvas.drawBitmap(icon, 0, 0, paint);

            syncBtn.setImageBitmap(icon);
            syncBtn.setBackgroundColor(Color.TRANSPARENT);
            syncBtn.setVisibility(View.VISIBLE);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * <h1>Constructeur par défaut</h1>
     */
    public TodayWidgetConfigureActivity() {
        super();
    }

    //******************************
    //PROCÉDURES DE PRÉFÉRENCES
    //******************************
    static void saveBgColorPref(Context context, int appWidgetId, int value) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.putInt(PREF_BG_COLOR_PREFIX_KEY + appWidgetId, value);
        prefs.putInt(PREF_BG_COLOR_DEFAULT_PREFIX_KEY, value);
        prefs.apply();
    }

    static int loadBgColorPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        return prefs.getInt(PREF_BG_COLOR_PREFIX_KEY + appWidgetId, Color.BLACK);
    }

    private int loadBgColorDefaultPref() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(PREF_BG_COLOR_DEFAULT_PREFIX_KEY, Color.BLACK);
    }

    static void saveTextColorPref(Context context, int appWidgetId, int value) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.putInt(PREF_TEXT_COLOR_PREFIX_KEY + appWidgetId, value);
        prefs.putInt(PREF_TEXT_COLOR_DEFAULT_PREFIX_KEY, value);
        prefs.apply();
    }

    static int loadTextColorPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        return prefs.getInt(PREF_TEXT_COLOR_PREFIX_KEY + appWidgetId, Color.WHITE);
    }

    private int loadTextColorDefaultPref() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(PREF_TEXT_COLOR_DEFAULT_PREFIX_KEY, Color.WHITE);
    }

    static void saveOpacityPref(Context context, int appWidgetId, int value) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.putInt(PREF_OPACITY_PREFIX_KEY + appWidgetId, value);
        prefs.putInt(PREF_OPACITY_DEFAULT_PREFIX_KEY, value);
        prefs.apply();
    }

    static int loadOpacityPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        return prefs.getInt(PREF_OPACITY_PREFIX_KEY + appWidgetId, 155);
    }

    private int loadOpacityDefaultPref() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(PREF_OPACITY_DEFAULT_PREFIX_KEY, 155);
    }

    static void saveLanguagePref(Context context, int appWidgetId, String value) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.putString(PREF_LANGUAGE_PREFIX_KEY + appWidgetId, value);
        prefs.apply();
    }

    static String loadLanguagePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        return prefs.getString(PREF_LANGUAGE_PREFIX_KEY + appWidgetId, "fr");
    }

    static void deleteAllPreferences(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.remove(PREF_BG_COLOR_PREFIX_KEY + appWidgetId);
        prefs.remove(PREF_TEXT_COLOR_PREFIX_KEY + appWidgetId);
        prefs.remove(PREF_OPACITY_PREFIX_KEY + appWidgetId);
        prefs.remove(PREF_LANGUAGE_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.widget_today_configure);

        setUpActionBar();

        mWidgetPreviewLayout = (RelativeLayout) findViewById(R.id.today_widget);
        mWidgetTodaysNameTv = (TextView) findViewById(R.id.widget_todays_name);
        syncBtn = (ImageButton) findViewById(R.id.widget_sync_btn);

        mBgColorSpinner = (Spinner) findViewById(R.id.bg_color_spinner);
        setUpColorSpinner(mBgColorSpinner, mBgColorSpinnerListener);
        mBgColorSpinner.setSelection(getSpinnerAdapterPosition(loadBgColorDefaultPref()));

        mTextColorSpinner = (Spinner) findViewById(R.id.bg_text_color_spinner);
        setUpColorSpinner(mTextColorSpinner, mTextColorSpinnerListener);
        mTextColorSpinner.setSelection(getSpinnerAdapterPosition(loadTextColorDefaultPref()));

        setUpOpacitySeekBar();

        findViewById(R.id.ok_btn).setOnClickListener(mOkBtnOnClickListener);

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

    private void setUpActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.widget_config_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.widget_configure));
    }

    private void setUpColorSpinner(Spinner spinner, AdapterView.OnItemSelectedListener onItemSelectedListener) {
        if (mColorSpinnerAdapter == null) {
            int[] colorsArray = getResources().getIntArray(R.array.widget_bg_colors);
            Integer[] colors = new Integer[colorsArray.length];
            for (int i = 0; i < colorsArray.length; i++) {
                colors[i] = Integer.valueOf(colorsArray[i]);
            }
            mColorSpinnerAdapter = new ColorSpinnerAdapter(this, colors);
        }

        spinner.setAdapter(mColorSpinnerAdapter);
        spinner.setOnItemSelectedListener(onItemSelectedListener);
        spinner.setSelection(0);
    }

    private int getSpinnerAdapterPosition(int color) {
        int[] colorsArray = getResources().getIntArray(R.array.widget_bg_colors);

        for (int i = 0; i < colorsArray.length; i++) {
            if (colorsArray[i] == color) {
                return (i);
            }
        }
        return (0);
    }

    private void setUpOpacitySeekBar() {
        int mOpacitySeekBarColor = ContextCompat.getColor(this, R.color.ets_red_fonce);
        mOpacitySeekBar = (SeekBar) findViewById(R.id.opacity_seekbar);

        mOpacitySeekBar.setProgress(loadOpacityDefaultPref());

        mOpacitySeekBar.setOnSeekBarChangeListener(mOpacityListener);

        mOpacitySeekBar.getProgressDrawable().setColorFilter(mOpacitySeekBarColor,
                PorterDuff.Mode.SRC_IN);

        if (android.os.Build.VERSION.SDK_INT < 16) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(GradientDrawable.OVAL);
            gradientDrawable.setSize(50, 50);
            gradientDrawable.setColor(mOpacitySeekBarColor);
            mOpacitySeekBar.setThumb(gradientDrawable);
        } else {
            mOpacitySeekBar.getThumb().setColorFilter(mOpacitySeekBarColor, PorterDuff.Mode.SRC_IN);
        }
    }
}

