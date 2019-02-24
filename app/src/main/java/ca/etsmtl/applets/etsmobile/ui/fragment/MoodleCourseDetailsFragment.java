package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.MoodleWebService;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleCoreCourse;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleCoreModule;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleModuleContent;
import ca.etsmtl.applets.etsmobile.ui.adapter.ExpandableListMoodleSectionAdapter;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile2.R;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Displays downloadable resources for a Moodle course
 */
@RuntimePermissions
public class MoodleCourseDetailsFragment extends HttpFragment {

    public static final String TELECHARGE_FICHIER_MOODLE = "A téléchargé un fichier de moodle";
    public static final String CONSULTE_PAGE_MOODLE = "A consulté une page sur Moodle";
    public static String COURSE_ID = "COURSE_ID";

    private long enqueue;
    private DownloadManager dm;

    private String moodleCourseId;

    private ExpandableListMoodleSectionAdapter expandableListMoodleAdapter;

    private ExpandableListView expListView;

    private BroadcastReceiver receiver = null;


    public static MoodleCourseDetailsFragment newInstance(int moodleCourseId) {
        MoodleCourseDetailsFragment fragment = new MoodleCourseDetailsFragment();
        Bundle args = new Bundle();
        args.putString(COURSE_ID, Integer.toString(moodleCourseId));

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && savedInstanceState == null) {
            Bundle bundle = getArguments();
            moodleCourseId = bundle.getString(COURSE_ID);
        } else {
            moodleCourseId = savedInstanceState.getString("moodleCourseId");
        }
    }

    @Override
    public void onActivityCreated(Bundle onSavedInstanceState) {
        super.onActivityCreated(onSavedInstanceState);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {

                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            MimeTypeMap map = MimeTypeMap.getSingleton();
                            String ext = MimeTypeMap.getFileExtensionFromUrl(uriString);
                            String type = map.getMimeTypeFromExtension(ext);
                            Uri uri = Uri.parse(uriString);

                            if (type == null)
                                type = "*/*";

                            Intent openFile = new Intent(Intent.ACTION_VIEW);
                            openFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                File file = new File(uri.getPath());
                                Uri fileProviderUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                                openFile.setDataAndType(fileProviderUri, type);
                            } else {
                                openFile.setDataAndType(uri, type);
                            }
                            try {
                                startActivity(openFile);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(getActivity(), getString(R.string.cannot_open_file), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        queryMoodleCoreCourses(moodleCourseId);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("moodleCourseId", moodleCourseId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_moodle_details, container, false);
        super.onCreateView(inflater, v, savedInstanceState);
        expListView = v.findViewById(R.id.expandableListView_moodle_courses_details);
        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());
        return v;
    }

    @Override
    public String getFragmentTitle() {
        return "";
    }

    /**
     * Takes moodle courses for a given class and displays them in an {@link ExpandableListView}
     *
     * @param courses the list of courses for a given class
     */
    private void displayMoodleCourses(List<MoodleCoreCourse> courses) {

        // create empty data
        HashMap<HeaderText, Object[]> listDataSectionName = new HashMap<>();
        List<HeaderText> listDataHeader = new ArrayList<>();

        int positionSection = 0;

        for (MoodleCoreCourse coreCourse : courses) {


            ArrayList<MoodleCoreModule> listMoodleLinkModules = new ArrayList<>();
            ArrayList<MoodleModuleContent> listMoodleResourceContents = new ArrayList<>();

            for (MoodleCoreModule coreModule : coreCourse.getModules()) {

                ArrayList<MoodleModuleContent> contents = coreModule.getContents();

                switch (coreModule.getModname()) {
                    case "folder":
                        if (contents != null)
                            listMoodleResourceContents.addAll(contents);
                        break;
                    case "url":
                    case "forum":
                        listMoodleLinkModules.add(coreModule);
                        break;
                    case "resource":
                        if (contents != null)
                            listMoodleResourceContents.addAll(contents);
                        break;
                }
            }

            Object[] finalArray = ArrayUtils.addAll(listMoodleLinkModules.toArray(), listMoodleResourceContents.toArray());
            if (finalArray.length != 0)
                listDataSectionName.put(new HeaderText(coreCourse.getName(), positionSection), finalArray);

            positionSection++;
        }


        listDataHeader.addAll(listDataSectionName.keySet());

        Collections.sort(listDataHeader, (headerText1, headerText2) -> Integer.compare(headerText1.getPosition(), headerText2.getPosition()));

        if (getActivity() == null)
            return;

        expandableListMoodleAdapter = new ExpandableListMoodleSectionAdapter(getActivity(), listDataHeader, listDataSectionName);
        expListView.setAdapter(expandableListMoodleAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Object object = expandableListMoodleAdapter.getChild(groupPosition, childPosition);

                if (object instanceof MoodleModuleContent) {
                    MoodleCourseDetailsFragmentPermissionsDispatcher.downloadMoodleObjectWithPermissionCheck(MoodleCourseDetailsFragment.this, (MoodleModuleContent) object);

                    AnalyticsHelper.getInstance(getActivity())
                            .sendActionEvent(getClass().getSimpleName(), TELECHARGE_FICHIER_MOODLE);
                }

                if (object instanceof MoodleCoreModule) {
                    MoodleCoreModule item = (MoodleCoreModule) object;

                    String url = "";
                    if (item.getModname().equals("url")) {
                        url = item.getContents().get(0).getFileurl();
                    } else {
                        url = item.getUrl();
                    }


                    AnalyticsHelper.getInstance(getActivity())
                            .sendActionEvent(getClass().getSimpleName(), CONSULTE_PAGE_MOODLE);

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }

                return true;
            }
        });
        onRequestSuccess(null);
    }

    /**
     * Downloads an given Moodle item into the user's device.
     * @param item the item to be downloaded.
     */
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void downloadMoodleObject(MoodleModuleContent item) {

        String url = item.getFileurl() + "&token=" + ApplicationManager.userCredentials.getMoodleToken();
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, item.getFilename());

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        MimeTypeMap mimetype = MimeTypeMap.getSingleton();
        String extension = FilenameUtils.getExtension(item.getFilename());

        request.setMimeType(mimetype.getMimeTypeFromExtension(extension));

        dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        enqueue = dm.enqueue(request);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();

    }


    /**
     * Query all resources for a Moodle course
     *
     * @param idCourse
     */
    private void queryMoodleCoreCourses(final String idCourse) {
        MoodleWebService service = dataManager.getMoodleService();
        Call<List<MoodleCoreCourse>> courseCall = service.getCoreCourses(ApplicationManager.userCredentials.getMoodleToken(), Integer.parseInt(idCourse));

        courseCall.enqueue(new Callback<List<MoodleCoreCourse>>() {
            @Override
            public void onResponse(Call<List<MoodleCoreCourse>> call, Response<List<MoodleCoreCourse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayMoodleCourses(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<MoodleCoreCourse>> call, Throwable t) {
                Snackbar.make(getView(), R.string.moodle_error_cant_get_courses, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    void updateUI() {
        loadingView.showLoadingView();
    }

    /**
     * Shows a snackbar in case he/she needs to allow the app to write to external storage
     * in order to download a file coming from Moodle.
     */
    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showPermissionsSnackbar() {
        Snackbar snackbar = Snackbar.make(getView(), R.string.moodle_allow_storage_permissions, Snackbar.LENGTH_SHORT)
                .setAction(R.string.action_settings, (listener) -> Utility.goToAppSettings(listener.getContext()))
                .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.ets_red));
        snackbar.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MoodleCourseDetailsFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * Holder for headers in ExpandableListView
     */
    public class HeaderText {
        String headerName;
        int position;

        public HeaderText(String headerName, int position) {
            this.headerName = headerName;
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }
    }
}
