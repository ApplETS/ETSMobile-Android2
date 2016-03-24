package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
public class BandwithFragment extends Fragment {

    private PieChartView chart;
    private PieChartData data;
    static double limit;
    static String phase, app;
    ArrayList<Double> upDownList = new ArrayList<>();
    private double[] values;
    private String[] rooms;
    private MultiColorProgressBar progressBar;
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
    private GridView grid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bandwith, container, false);
        progressBar = (MultiColorProgressBar) v.findViewById(R.id.bandwith_progress);
        editTextApp = (EditText) v.findViewById(R.id.bandwith_editText_app);
        editTextPhase = (EditText) v.findViewById(R.id.bandwith_editText_phase);
        grid = (GridView) v.findViewById(R.id.bandwith_grid);
        loadProgressBar = (ProgressBar)v.findViewById(R.id.progressBarLoad);

        chart = (PieChartView) v.findViewById(R.id.chart);
        chart.setVisibility(View.INVISIBLE);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        phase = defaultSharedPreferences.getString("Phase", "");
        app = defaultSharedPreferences.getString("App", "");

        if (phase.length() > 0 && app.length() > 0) {
            editTextApp.setHint(app);
            editTextPhase.setHint(phase);
            getBandwith(phase, app);
        }
        editTextPhase.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    reset();
                    Pattern p = Pattern.compile("[1,2,3,4]");
                    Matcher m = p.matcher(s);
                    if (m.find()) {
                        editTextApp.requestFocus();
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
        editTextApp.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    if (editTextPhase.length() > 0) {
                        String phase = editTextPhase.getText().toString();
                        if (phase.equals("1") || phase.equals("2") || phase.equals("4")) {
                            if (editTextApp.getText().length() > 2) {
                                if (editTextPhase.length() > 0) {
                                    String app = editTextApp.getText().toString();
                                    getBandwith(phase, app);
                                }
                            }
                        } else if (phase.equals("3")) {
                            if (editTextApp.getText().length() > 3) {
                                if (editTextPhase.length() > 0) {
                                    String app = editTextApp.getText().toString();
                                    getBandwith(phase, app);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        return v;
    }

    private void setError(final EditText edit, final String messageError) {

        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    edit.setError(messageError);
                    edit.requestFocus();
                    edit.setHint(edit.getText());
                    edit.setText("");
                }
            });
        }
    }

    private void getBandwith(String phase, String app) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        savePhaseAppPreferences(phase, app);
        String url = "https://api3.clubapplets.ca/cooptel?phase="+phase+"&appt="+app;
        if(Utility.isNetworkAvailable(getActivity())){
            loadProgressBar.setVisibility(View.VISIBLE);
            new BandwithAsyncTask().execute(url);
        }
    }

    private void savePhaseAppPreferences(String phase, String app) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Editor editor = defaultSharedPreferences.edit();
        editor.putString("Phase", phase);
        editor.putString("App", app);
        editor.commit();

    }

    private void updateProgressBarColorItems(double bandwidthQuota) {
        final int[] colorChoice = new int[]{R.color.red_bandwith, R.color.blue_bandwith, R.color.green_bandwith, R.color.purple_bandwith};
        int[] legendColors = new int[values.length];
        final Activity activity = getActivity();

        progressBar.clearProgressItems();

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
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    progressBar.setMax((int) quota);
                    progressBar.setProgress((int) total);
                    View v = getView();
                    String gb = getString(R.string.gigaoctetx);
                    double reste = Math.round((quota - total) * 100) / 100.0;
                    ((TextView) v.findViewById(R.id.bandwith_used_lbl)).setText(getString(R.string.utilise) + " "
                            + reste + gb);
                    ((TextView) v.findViewById(R.id.bandwith_max)).setText(quota + gb);
                }
            });
        }
    }

    private void reset() {
        View v = getView();
        if (v != null) {
            progressBar.setProgress(0);
            chart.setVisibility(View.INVISIBLE);
            String gb = getString(R.string.gigaoctetx);
            ((TextView) v.findViewById(R.id.bandwith_used_lbl)).setText("");
            ((TextView) v.findViewById(R.id.bandwith_max)).setText(gb);
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
                autreChambre.add(item);
            }
        }
        if(autreChambre.size()>0){
            chambreTot = chambreUpTot/1024 + chambreDownTot/1024;
            upDownList.add(chambreTot);
            bandePassanteParChambre(autreChambre);
        }else{
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
                    JSONArray Jarray = Jobject.getJSONArray("consommations");
                    ArrayList<ConsommationBandePassante> consommationList = new ArrayList<>();
                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject object = Jarray.getJSONObject(i);
                        ConsommationBandePassante consommationBandePassante = new ConsommationBandePassante(object);
                        consommationList.add(consommationBandePassante);
                    }
                    limit = Jobject.getDouble("restant");

                    downloadTot = 0;
                    uploadTot = 0;
                    for (int i = 0; i < consommationList.size(); i++) {
                        uploadTot = uploadTot + consommationList.get(i).getUpload();
                        downloadTot = downloadTot + consommationList.get(i).getDownload();
                    }
                    uploadTot = uploadTot / 1024;
                    downloadTot = downloadTot / 1024;
                    limit = limit / 1024;
                    bandePassanteParChambre(consommationList);
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
            }
            return map;
        }

        @Override
        protected void onPostExecute(HashMap<String, Double> map) {
            if(map !=null) {
                loadProgressBar.setVisibility(View.GONE);
                updatePieChart(map);
                setProgressBar(map.get("total"), map.get("limit"));
            }else{
                setError(editTextApp, getString(R.string.error_invalid_phase));
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