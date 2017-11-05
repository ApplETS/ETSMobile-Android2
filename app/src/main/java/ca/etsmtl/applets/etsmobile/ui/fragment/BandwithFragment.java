package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.etsmtl.applets.etsmobile.model.ConsommationBandePassante;
import ca.etsmtl.applets.etsmobile.ui.adapter.LegendAdapter;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile.views.MultiColorProgressBar;
import ca.etsmtl.applets.etsmobile.views.ProgressItem;
import ca.etsmtl.applets.etsmobile2.R;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by Phil on 17/11/13. Coded by Laurence 26/03/14
 */
public class BandwithFragment extends BaseFragment {

    private static final String PHASE_PREF_KEY = "Phase";
    private static final String APP_PREF_KEY = "App";
    private static final String CHAMBRE_PREF_KEY = "Chambre";

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
    private EditText editTextApp;
    public OnFocusChangeListener onFocusChangeColorEditText = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v == editTextApp) {
                editTextApp.setTextColor(Color.RED);
            }

        }
    };
    private EditText editTextPhase;
    private EditText editTextChambre;
    private GridView grid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_bandwith, container, false);

        editTextApp = v.findViewById(R.id.bandwith_editText_app);
        editTextPhase = v.findViewById(R.id.bandwith_editText_phase);
        editTextChambre = v.findViewById(R.id.bandwith_editText_chambre);
        grid = v.findViewById(R.id.bandwith_grid);
        progressBar = v.findViewById(R.id.bandwith_progress);
        progressBarTv = v.findViewById(R.id.bandwith_progress_tv);
        loadProgressBar = v.findViewById(R.id.progressBarLoad);

        chart = v.findViewById(R.id.chart);
        chart.setVisibility(View.INVISIBLE);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        phase = defaultSharedPreferences.getString(PHASE_PREF_KEY, "");
        app = defaultSharedPreferences.getString(APP_PREF_KEY, "");
        chambre = defaultSharedPreferences.getString(CHAMBRE_PREF_KEY, "");

        if (phase.length() > 0 && app.length() > 0) {
            editTextApp.setText(app);
            editTextPhase.setText(phase);
            editTextChambre.setText(chambre);
            getBandwith(phase, app, chambre);
        }
        editTextPhase.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    reset();
                    Pattern p = Pattern.compile("[1,2,3,4]");
                    Matcher m = p.matcher(s);
                    if (m.find()) {
                        verifyInputsAndGetBandwidth();
                    } else {
                        setError(editTextPhase, getString(R.string.error_invalid_phase));
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
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

                if (!hasFocus && editText.length() == 0)
                    editText.setError(getString(R.string.error_field_required));
            }
        };
        editTextPhase.setOnFocusChangeListener(editTextFocusChangeListener);
        editTextApp.setOnFocusChangeListener(editTextFocusChangeListener);
        editTextChambre.setOnFocusChangeListener(editTextFocusChangeListener);

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        return v;
    }

    private void verifyInputsAndGetBandwidth() {
        String phase = editTextPhase.getText().toString();
        String app = editTextApp.getText().toString();
        String chambre = editTextChambre.getText().toString();

        if (phase.length() > 0) {
            if (app.length() > 0) {
                if (chambre.length() > 0) {
                    if (phase.equals("1") || phase.equals("2") || phase.equals("4")) {
                        if (editTextApp.getText().length() > 2) {
                            getBandwith(phase, app, chambre);
                        }
                    } else if (phase.equals("3")) {
                        if (editTextApp.getText().length() > 3) {
                            getBandwith(phase, app, chambre);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.menu_section_1_bandwith);
    }

    private void setError(final EditText edit, final String messageError) {

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    reset();
                    loadProgressBar.setVisibility(View.GONE);
                    edit.setError(messageError);
                    edit.requestFocus();
                    //edit.setHint(edit.getText());
                    edit.setText("");
                }
            });
        }
    }

    private void getBandwith(String phase, String app, String chambre) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        savePhaseAppPreferences(phase, app, chambre);
        reset();
        String url = String.format(getString(R.string.bandwith), phase, app ,chambre);
        if(Utility.isNetworkAvailable(getActivity())){
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
        final AppCompatActivity activity = (AppCompatActivity)getActivity();

        progressBar.clearProgressItems();
        bandwidthQuota = bandwidthQuota / 1024;
        for (int i = 0, color = 0; i < values.length-1; ++i) {
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


        if (activity != null) {
            final int[] colors = legendColors;

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    grid.setAdapter(new LegendAdapter(activity, rooms, colors));
                }
            });
        }

    }

    private void setProgressBar(final double total, final double quota) {
        AppCompatActivity activity =(AppCompatActivity) getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    progressBar.setMax((int) quota);
                    progressBar.setProgress((int) total);
                    String gb = getString(R.string.gigaoctetx);
                    double reste = Math.round((quota - total) * 100) / 100.0;
                    String text = getString(R.string.bandwith_used)
                            + getString(R.string.deux_points) + String.format("%.2f", total) + "/"
                            + String.format("%.2f", quota) + " " + gb;
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
        }
    }

    private void bandePassanteParChambre(ArrayList<ConsommationBandePassante> list){
        ArrayList<ConsommationBandePassante> autreChambre = new ArrayList<ConsommationBandePassante>();
        double chambreUpTot=0, chambreDownTot=0, chambreTot;
        for(int i=0;i<list.size(); i++){
            ConsommationBandePassante item = list.get(i);
            int id = list.get(0).getIdChambre();
            if(id == item.getIdChambre()){
                chambreUpTot = chambreUpTot + item.getUpload();
                chambreDownTot = chambreDownTot + item.getDownload();
            }else{
                //on met tous les chambres différent dans une liste
                autreChambre.add(item);
            }
        }
        if(autreChambre.size()>0){
            // S'il y a plusieur chambres, on enregistre le total de cette chambre ci et on refait
            // une itération de la méthode pour les autres chambres
            chambreTot = chambreUpTot/1024 + chambreDownTot/1024;
            upDownList.add(chambreTot);
            bandePassanteParChambre(autreChambre);
        }else{
            //updownlist contient une liste de tous les totaux upload/download de tous les chambres
            chambreTot = chambreUpTot/1024 + chambreDownTot/1024;
            upDownList.add(chambreTot);
            rooms = new String[upDownList.size()];
            values = new double[upDownList.size()+1];
            double tot=0;
            for(int i=0; i<upDownList.size(); i++){
                values[i] = upDownList.get(i);
                tot = tot + values[i];
                int j =i+1;
                rooms[i] = "■ Chambre" + j + " " + String.format("%.2f",values[i]) + " Go";
                if(i == upDownList.size()-1){
                    values[i+1] = limit - tot;
                }
            }
            upDownList.clear();
            updateProgressBarColorItems(limit);
        }
    }

    private class BandwithAsyncTask extends AsyncTask<String, Void, HashMap<String, Double>> {

        @Override
        protected HashMap<String,Double> doInBackground(String... param) {
            double total[] = new double[2];
            ArrayList<ConsommationBandePassante> consommationList;
            HashMap<String,Double> map = new HashMap<>();
            try {
                double uploadTot, downloadTot;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(param[0])
                        .get()
                        .addHeader("cache-control", "no-cache")
                        .build();
                Response response = client.newCall(request).execute();
                if(response.code() == 200) {
                    String jsonData = response.body().string();
                    JSONObject Jobject = new JSONObject(jsonData);
                    limit = Jobject.getDouble("restant");
                    Object consommations = Jobject.get("consommations");
                    if(consommations instanceof JSONArray){
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
                    }else{
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
                }else{
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

            if(map !=null) {
                updatePieChart(map);
                setProgressBar(map.get("total"), map.get("limit"));
            }else{
                Toast t = Toast.makeText(getContext(), getString(R.string.error_JSON_PARSING),
                        Toast.LENGTH_SHORT);
                t.show();
            }
            super.onPostExecute(map);
        }
    }

    public void updatePieChart(HashMap<String, Double> map){
        List<SliceValue> values = new ArrayList<SliceValue>();
        double rest;
        rest = map.get("limit")-map.get("total");
        double upload, download;
        upload = map.get("uploadTot");
        download = map.get("downloadTot");
        //TODO put labels
        values.add(new SliceValue((float) upload).setLabel("Upload : " + String.format("%.2f",upload) + " Go").setColor(Color.rgb(217,119,37)));
        values.add(new SliceValue((float) download).setLabel("Download : " + String.format("%.2f",download) + " Go").setColor(Color.rgb(217,52,37)));
        values.add(new SliceValue((float) rest).setLabel("Restant : " + String.format("%.2f",rest) + " Go").setColor(Color.rgb(105,184,57)));

        data = new PieChartData(values);

        chart.setVisibility(View.VISIBLE);
        data.setHasLabels(true);
        chart.setPieChartData(data);
    }
}