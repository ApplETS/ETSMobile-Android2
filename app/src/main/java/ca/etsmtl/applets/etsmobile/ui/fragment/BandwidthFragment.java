package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;



import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import ca.etsmtl.applets.etsmobile.model.ConsommationBandePassante;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile.views.MultiColorProgressBar;
import ca.etsmtl.applets.etsmobile.views.ProgressItem;
import ca.etsmtl.applets.etsmobile2.R;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Phil on 17/11/13. Coded by Laurence 26/03/14
 */
public class BandwidthFragment extends BaseFragment {

    private static final String PHASE_PREF_KEY = "Phase";
    private static final String APP_PREF_KEY = "App";
    private static final String CHAMBRE_PREF_KEY = "Chambre";
    /**
     * Lettre assignée au champ « chambre » pour les appartements ayant qu'une seule chambre
     **/
    private static final String UNE_SEULE_CHAMBRE = "a";
    private static final String PHASE_3_DIALOG_TAG = "TagPhase3";

    private PieChartView chart;
    private PieChartData data;
    static double limit;
    static String phase, app, chambre;
    ArrayList<Double> upDownList = new ArrayList<>();
    private double[] values;
    private String[] rooms;
    private MultiColorProgressBar progressBar;
    private TextView progressBarTv;
    private ProgressBar loadProgressBar;
    private TextInputLayout textInputLayoutApp;
    private EditText editTextApp;
    private TextInputLayout textInputLayoutPhase;
    private EditText editTextPhase;
    private Spinner phaseSpinner;
    private TextInputLayout textInputLayoutChambre;
    private EditText editTextChambre;
    //private GridView grid;
    private ViewGroup progressLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_bandwith, container, false);

        textInputLayoutPhase = v.findViewById(R.id.text_input_layout_phase);
        editTextPhase = v.findViewById(R.id.bandwidth_editText_phase);
        phaseSpinner = v.findViewById(R.id.bandwidth_phase_spinner);
        textInputLayoutApp = v.findViewById(R.id.text_input_layout_app);
        editTextApp = v.findViewById(R.id.bandwidth_editText_app);
        textInputLayoutChambre = v.findViewById(R.id.text_input_layout_chambre);
        editTextChambre = v.findViewById(R.id.bandwidth_editText_chambre);
        //grid = v.findViewById(R.id.bandwith_grid);
        progressBar = v.findViewById(R.id.bandwidth_progress);
        progressBarTv = v.findViewById(R.id.bandwidth_progress_tv);
        loadProgressBar = v.findViewById(R.id.progressBarLoad);
        progressLayout = v.findViewById(R.id.bandwidth_progress_layout);
        chart = v.findViewById(R.id.chart);
        chart.setVisibility(View.INVISIBLE);
        progressLayout.setVisibility(View.GONE);

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        phase = defaultSharedPreferences.getString(PHASE_PREF_KEY, "");
        app = defaultSharedPreferences.getString(APP_PREF_KEY, "");
        chambre = defaultSharedPreferences.getString(CHAMBRE_PREF_KEY, "");

        if (chambre.length() > 0)
            editTextChambre.setText(chambre);
        else
            editTextChambre.setText(UNE_SEULE_CHAMBRE);

        if (phase.length() > 0 && app.length() > 0) {
            editTextApp.setText(app);
            phaseSpinner.setSelection(Integer.parseInt(phase) - 1);
            getBandwidth(phase, app, chambre);
        }

        editTextPhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phaseSpinner.performClick();
            }
        });

        phaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int phase = position + 1;
                editTextPhase.setText(String.valueOf(phase));
                if (phase == 3) {
                    displayPhase3Dialog();
                }

                verifyInputsAndGetBandwidth();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cS, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence cS, int start, int before, int count) {
                verifyInputsAndGetBandwidth();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        editTextApp.addTextChangedListener(textWatcher);
        editTextChambre.addTextChangedListener(textWatcher);

        OnFocusChangeListener editTextFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                EditText editText = (EditText) view;
                TextInputLayout textInputLayout = null;

                switch (view.getId()) {
                    case R.id.bandwidth_editText_phase:
                        textInputLayout = textInputLayoutPhase;
                        break;
                    case R.id.bandwidth_editText_app:
                        textInputLayout = textInputLayoutApp;
                        break;
                }

                if (textInputLayout != null) {
                    if (!hasFocus && editText.getText().toString().length() == 0)
                        textInputLayout.setError(getString(R.string.error_field_required));
                    else
                        textInputLayout.setError(null);
                }
            }
        };
        editTextPhase.setOnFocusChangeListener(editTextFocusChangeListener);
        editTextApp.setOnFocusChangeListener(editTextFocusChangeListener);

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        return v;
    }

    private void verifyInputsAndGetBandwidth() {
        String phase = editTextPhase.getText().toString();
        String app = editTextApp.getText().toString();
        String chambre = editTextChambre.getText().toString().toLowerCase();

        if (phase.length() > 0) {
            if (app.length() > 0) {
                if (phase.equals("1") || phase.equals("2") || phase.equals("4")) {
                    if (editTextApp.getText().length() > 2) {
                        getBandwidth(phase, app, chambre);
                    }
                } else if (phase.equals("3")) {
                    if (editTextApp.getText().length() > 3) {
                        getBandwidth(phase, app, chambre);
                    }
                }
            }
        }
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.menu_section_1_bandwith);
    }

    private void getBandwidth(String phase, String app, String chambre) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        savePhaseAppPreferences(phase, app, chambre);
        reset();
        /*
        Si l'appartement n'a qu'une seule chambre, la lettre correspondant à la chambre est « a ».
         */
        if (chambre.isEmpty())
            chambre = UNE_SEULE_CHAMBRE;
        String url = String.format(getString(R.string.bandwith), phase, app, chambre);
        if (Utility.isNetworkAvailable(getActivity())) {
            loadProgressBar.setVisibility(View.VISIBLE);
            new BandwithAsyncTask().execute(url);
        }
    }

    private void savePhaseAppPreferences(String phase, String app, String chambre) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Editor editor = defaultSharedPreferences.edit();
        editor.putString(PHASE_PREF_KEY, phase);
        editor.putString(APP_PREF_KEY, app);
        editor.putString(CHAMBRE_PREF_KEY, chambre);
        editor.commit();

    }

    private void updateProgressBarColorItems(double bandwidthQuota) {
        final int[] colorChoice = new int[]{R.color.red_bandwith, R.color.blue_bandwith, R.color.green_bandwith, R.color.purple_bandwith};
        int[] legendColors = new int[values.length];
        final AppCompatActivity activity = (AppCompatActivity) getActivity();

        progressBar.clearProgressItems();
        bandwidthQuota = bandwidthQuota / 1024;
        for (int i = 0, color = 0; i < values.length - 1; ++i) {
            ProgressItem progressItem = new ProgressItem(colorChoice[color], (values[i] / bandwidthQuota) * 100);

            progressBar.addProgressItem(progressItem);
            legendColors[i] = colorChoice[color];
            color++;

            if (color == colorChoice.length)
                color = 0;
        }

        if (values.length > 0) {
            int lastValue = values.length - 1;
            ProgressItem progressItem = new ProgressItem(R.color.grey_bandwith, (values[lastValue] / bandwidthQuota) * 100);
            legendColors[lastValue] = R.color.grey_bandwith;
            progressBar.addProgressItem(progressItem);
        }

        /*if (activity != null) {
            final int[] colors = legendColors;

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    grid.setAdapter(new LegendAdapter(activity, rooms, colors));
                }
            });
        }*/

    }

    private void setProgressBar(final double total, final double quota) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    progressBar.setMax((int) quota);
                    progressBar.setProgress((int) total);
                    String text = getString(R.string.bandwidth_used, total, quota, getString(R.string.gigaoctetx));
                    progressBarTv.setText(text);
                }
            });
        }
    }

    private void reset() {
        View v = getView();
        if (v != null) {
            progressBar.setProgress(0);
            chart.setVisibility(View.INVISIBLE);
            progressBarTv.setText("");
            progressLayout.setVisibility(View.GONE);
        }
    }

    private void bandePassanteParChambre(ArrayList<ConsommationBandePassante> list) {
        ArrayList<ConsommationBandePassante> autreChambre = new ArrayList<ConsommationBandePassante>();
        double chambreUpTot = 0, chambreDownTot = 0, chambreTot;
        for (int i = 0; i < list.size(); i++) {
            ConsommationBandePassante item = list.get(i);
            int id = list.get(0).getIdChambre();
            if (id == item.getIdChambre()) {
                chambreUpTot = chambreUpTot + item.getUpload();
                chambreDownTot = chambreDownTot + item.getDownload();
            } else {
                //on met tous les chambres différent dans une liste
                autreChambre.add(item);
            }
        }
        if (autreChambre.size() > 0) {
            // S'il y a plusieur chambres, on enregistre le total de cette chambre ci et on refait
            // une itération de la méthode pour les autres chambres
            chambreTot = chambreUpTot / 1024 + chambreDownTot / 1024;
            upDownList.add(chambreTot);
            bandePassanteParChambre(autreChambre);
        } else {
            //updownlist contient une liste de tous les totaux upload/download de tous les chambres
            chambreTot = chambreUpTot / 1024 + chambreDownTot / 1024;
            upDownList.add(chambreTot);
            rooms = new String[upDownList.size()];
            values = new double[upDownList.size() + 1];
            double tot = 0;
            for (int i = 0; i < upDownList.size(); i++) {
                values[i] = upDownList.get(i);
                tot = tot + values[i];
                int j = i + 1;
                rooms[i] = "■ Chambre" + j + " " + String.format("%.2f", values[i]) + " Go";
                if (i == upDownList.size() - 1) {
                    values[i + 1] = limit - tot;
                }
            }
            upDownList.clear();
            updateProgressBarColorItems(limit);
        }
    }

    private class BandwithAsyncTask extends AsyncTask<String, Void, HashMap<String, Double>> {

        @Override
        protected HashMap<String, Double> doInBackground(String... param) {
            double total[] = new double[2];
            ArrayList<ConsommationBandePassante> consommationList;
            HashMap<String, Double> map = new HashMap<>();
            try {
                double uploadTot, downloadTot;
                OkHttpClient client = new okhttp3.OkHttpClient();
                Request request = new Request.Builder()
                        .url(param[0])
                        .get()
                        .addHeader("cache-control", "no-cache")
                        .build();
                Response response = client.newCall(request).execute();
                if (response.code() == 200) {
                    String jsonData = response.body().string();
                    JSONObject Jobject = new JSONObject(jsonData);
                    limit = Jobject.getDouble("restant");
                    Object consommations = Jobject.get("consommations");
                    if (consommations instanceof JSONArray) {
                        JSONArray consommationsArray = (JSONArray) consommations;
                        consommationList = new ArrayList<>();
                        for (int i = 0; i < consommationsArray.length(); i++) {
                            JSONObject object = consommationsArray.getJSONObject(i);
                            ConsommationBandePassante consommationBandePassante = new ConsommationBandePassante(object);
                            consommationList.add(consommationBandePassante);
                        }
                        downloadTot = 0;
                        uploadTot = 0;
                        for (int i = 0; i < consommationList.size(); i++) {
                            uploadTot = uploadTot + consommationList.get(i).getUpload();
                            downloadTot = downloadTot + consommationList.get(i).getDownload();
                        }
                        bandePassanteParChambre(consommationList);
                    } else {
                        JSONObject consommationsObject = (JSONObject) consommations;
                        ConsommationBandePassante consommationBandePassante = new ConsommationBandePassante(consommationsObject);
                        uploadTot = consommationBandePassante.getUpload();
                        downloadTot = consommationBandePassante.getDownload();
                    }
                    uploadTot = uploadTot / 1024;
                    downloadTot = downloadTot / 1024;
                    limit = limit / 1024;
                    map.put("uploadTot", uploadTot);
                    map.put("downloadTot", downloadTot);
                    map.put("total", uploadTot + downloadTot);
                    map.put("limit", limit);
                    total[0] = uploadTot + downloadTot;
                    total[1] = limit;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return map;
        }

        @Override
        protected void onPostExecute(HashMap<String, Double> map) {
            loadProgressBar.setVisibility(View.GONE);

            if (map != null) {
                updatePieChart(map);
                setProgressBar(map.get("total"), map.get("limit"));
            } else {
                if (editTextPhase.getText().toString().equals("3"))
                    displayPhase3Dialog();
                else {
                    Toast t = Toast.makeText(getContext(), getString(R.string.error_JSON_PARSING),
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
            super.onPostExecute(map);
        }
    }

    public void updatePieChart(HashMap<String, Double> map) {
        List<SliceValue> values = new ArrayList<SliceValue>();
        double rest;
        rest = map.get("limit") - map.get("total");
        double upload, download;
        upload = map.get("uploadTot");
        download = map.get("downloadTot");
        //TODO put labels
        values.add(new SliceValue((float) upload).setLabel("Upload : " + String.format("%.2f", upload) + " Go").setColor(Color.rgb(217, 119, 37)));
        values.add(new SliceValue((float) download).setLabel("Download : " + String.format("%.2f", download) + " Go").setColor(Color.rgb(217, 52, 37)));
        values.add(new SliceValue((float) rest).setLabel("Restant : " + String.format("%.2f", rest) + " Go").setColor(Color.rgb(105, 184, 57)));

        data = new PieChartData(values);

        chart.setVisibility(View.VISIBLE);
        data.setHasLabels(true);
        chart.setPieChartData(data);
        progressLayout.setVisibility(View.VISIBLE);
    }

    private void displayPhase3Dialog() {
        FragmentTransaction fT = getFragmentManager().beginTransaction();
        DialogFragment fragment = (DialogFragment) getFragmentManager().findFragmentByTag(PHASE_3_DIALOG_TAG);

        if (fragment == null)
            fragment = BandwidthPhase3DialogFragment.newInstance();

        if (!fragment.isAdded())
            fragment.show(fT, PHASE_3_DIALOG_TAG);
    }
}