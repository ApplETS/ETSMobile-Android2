package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile.views.MultiColorProgressBar;
import ca.etsmtl.applets.etsmobile.views.ProgressItem;
import ca.etsmtl.applets.etsmobile.ui.adapter.LegendAdapter;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Phil on 17/11/13. Coded by Laurence 26/03/14
 */
public class BandwithFragment extends Fragment {

    private double[] values;
    private String[] rooms;
    private MultiColorProgressBar progressBar;
    private EditText editTextApp;
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

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String phase = defaultSharedPreferences.getString("Phase", "");
        String app = defaultSharedPreferences.getString("App", "");

        if (phase.length() > 0 && app.length() > 0) {
            editTextApp.setHint(app);
            editTextPhase.setHint(phase);
            System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);
            month += 1;
            String url = getActivity().getString(R.string.bandwith_query, phase, app, month);

            if(Utility.isNetworkAvailable(getActivity()))
                new BandwithAsyncTask().execute(url);
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

        return v;
    }

    public OnFocusChangeListener onFocusChangeColorEditText = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v == editTextApp) {
                editTextApp.setTextColor(Color.RED);
            }

        }
    };

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
        System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        month += 1;

        String url = getActivity().getString(R.string.bandwith_query, phase, app, month);

        savePhaseAppPreferences(phase, app);
        if(Utility.isNetworkAvailable(getActivity()))
            new BandwithAsyncTask().execute(url);
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
            String gb = getString(R.string.gigaoctetx);
            ((TextView) v.findViewById(R.id.bandwith_used_lbl)).setText("");
            ((TextView) v.findViewById(R.id.bandwith_max)).setText(gb);
        }
    }

    private class BandwithAsyncTask extends AsyncTask<String, Void, String> {

        private JSONObject query;

        @Override
        protected String doInBackground(String... param) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                URI uriWeb = new URI(param[0]);
                request.setURI(uriWeb);
                HttpResponse response = httpClient.execute(request);
                int code = response.getStatusLine().getStatusCode();
                if (code == 200) {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
                                .getContent(), "UTF-8"));
                        String json = reader.readLine();
                        JSONObject obj = new JSONObject(json);
                        query = (JSONObject) obj.get("query");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (isAdded()) {
                try {


                    if (!query.getString("results").equals("null")) {
                        JSONObject results = (JSONObject) query.get("results");
                        JSONArray arrayTable = results.getJSONArray("table");
                        JSONObject tableauElem = (JSONObject) arrayTable.get(0);
                        JSONObject quota = (JSONObject) arrayTable.get(1);
                        JSONArray arrayElem = tableauElem.getJSONArray("tr");
                        HashMap<String, Double> map = getBandwithUserFromPort(arrayElem);

                        int size = map.size();
                        values = new double[size];
                        rooms = new String[size];
                        Iterator<String> iter = map.keySet().iterator();
                        int i = 0;
                        while (iter.hasNext()) {
                            String entry = iter.next();
                            if (!entry.equals("total")) {
                                double value = map.get(entry);
                                values[i] = Math.round((value / 1024) * 100) / 100.0;
                                String[] stringArray = entry.split("-");
                                if (stringArray.length > 1) {
                                    rooms[i] = "■ " + stringArray[1].toString() + " " + values[i] + " Go";
                                } else {
                                    int j = i + 1;
                                    rooms[i] = "■ Chambre" + j + " " + values[i] + " Go";
                                }
                                i++;
                            }
                        }

                        JSONArray quotaJson = (JSONArray) quota.getJSONArray("tr");
                        JSONObject objectQuota = (JSONObject) quotaJson.get(1);
                        JSONArray arrayQuota = (JSONArray) objectQuota.getJSONArray("td");

                        double quotaValue = ((JSONObject) arrayQuota.get(1)).getDouble("p");
                        quotaValue = Math.round(quotaValue / 1024 * 100) / 100.0;
                        double total = map.get("total");
                        total = Math.round(total / 1024 * 100) / 100.0;
                        double rest = Math.round((quotaValue - total) * 100) / 100.0;
                        values[size - 1] = rest;
                        rooms[size - 1] = "■ Restant " + rest + " Go";
                        setProgressBar(total, quotaValue);
                        updateProgressBarColorItems(quotaValue);
                    } else {
                        setError(editTextApp, getString(R.string.error_invalid_app));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            super.onPostExecute(s);

        }

        private String containtPort(String port, HashMap<String, Double> map) {
            Iterator<String> iter = map.keySet().iterator();
            while (iter.hasNext()) {
                String entry = iter.next();
                if (entry.contains(port)) {
                    return entry;
                }
            }
            return null;
        }

        private HashMap<String, Double> getBandwithUserFromPort(JSONArray array) {
            HashMap<String, Double> map = new HashMap<String, Double>();
            try {
                for (int i = 1; i < array.length(); i++) {
                    JSONObject obj;
                    obj = (JSONObject) array.get(i);
                    JSONArray elem = obj.getJSONArray("td");

                    if (i < array.length() - 2) {
                        JSONObject port = (JSONObject) elem.get(0);
                        String portElem = port.getString("p");
                        if (containtPort(portElem, map) != null) {
                            portElem = containtPort(portElem, map);
                        }
                        JSONObject upload = (JSONObject) elem.get(2);
                        JSONObject downLoad = (JSONObject) elem.get(3);
                        double downUpLoad = upload.getDouble("p") + downLoad.getDouble("p");
                        if (map.containsKey(portElem)) {
                            double downUpLoadValue = map.get(portElem);
                            downUpLoad += downUpLoadValue;
                        }
                        map.put(portElem, downUpLoad);
                    } else if (i == array.length() - 1) {
                        JSONObject totalObject = (JSONObject) elem.get(1);
                        double total = totalObject.getDouble("p");
                        map.put("total", total);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return map;
        }

    }

}
