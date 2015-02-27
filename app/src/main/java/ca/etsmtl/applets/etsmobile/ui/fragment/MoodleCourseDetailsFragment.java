package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCoreCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCoreCourses;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCoreModule;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleModuleContent;
import ca.etsmtl.applets.etsmobile.ui.adapter.ExpandableListMoodleAdapter;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Displays downloadable resources for a Moodle course
 *
 * @author Thibaut
 */
public class MoodleCourseDetailsFragment extends HttpFragment {

    public static String COURSE_ID = "COURSE_ID";

    private long enqueue;
    private DownloadManager dm;

    private String moodleCourseId;

    private ExpandableListMoodleAdapter expandableListMoodleAdapter;

    private ExpandableListView expListView;

    private List<HeaderText> listDataHeader;
    private HashMap<HeaderText, Object[]> listDataChild;

    private ArrayList<MoodleCoreModule> listMoodleLinkModules;
    private ArrayList<MoodleModuleContent> listMoodleResourceContents;

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
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            moodleCourseId = bundle.getString(COURSE_ID);
        }

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

                            if (type == null)
                                type = "*/*";

                            Intent openFile = new Intent(Intent.ACTION_VIEW);
                            openFile.setDataAndType(Uri.parse(uriString), type);
                            try {
                                startActivity(openFile);
                            } catch(ActivityNotFoundException e) {
                                Toast.makeText(getActivity(),"Aucune application ne peut ouvrir ce fichier",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        };

        getActivity().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));



    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_moodle_details, container, false);
        super.onCreateView(inflater, v, savedInstanceState);

        expListView = (ExpandableListView) v.findViewById(R.id.expandableListView_moodle_courses_details);
        queryMoodleCoreCourses(moodleCourseId);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onRequestFailure(SpiceException arg0) {

    }

    @Override
    public void onRequestSuccess(Object o) {

        if(o instanceof MoodleCoreCourses) {

            MoodleCoreCourses moodleCoreCourses = (MoodleCoreCourses) o;

            // create empty data
            listDataChild = new HashMap<HeaderText, Object[]>();
            listDataHeader = new ArrayList<HeaderText>();
            listMoodleLinkModules = new ArrayList<MoodleCoreModule>();
            listMoodleResourceContents = new ArrayList<MoodleModuleContent>();

            int position = 2;

            for(MoodleCoreCourse coreCourse : moodleCoreCourses) {
                for(MoodleCoreModule coreModule : coreCourse.getModules()) {

                    if(coreModule.getModname().equals("folder")) {
                        listDataChild.put(new HeaderText(coreModule.getName(),position), coreModule.getContents().toArray());
                    } else if (coreModule.getModname().equals("url") || coreModule.getModname().equals("forum")) {
                        listMoodleLinkModules.add(coreModule);
                    } else if (coreModule.getModname().equals("resource")) {
                        listMoodleResourceContents.addAll(coreModule.getContents());
                    }

                    position++;
                }
            }


            listDataChild.put(new HeaderText("Liens",0),listMoodleLinkModules.toArray());
            listDataChild.put(new HeaderText("Ressources",1),listMoodleResourceContents.toArray());


            listDataHeader.addAll(listDataChild.keySet());

            Collections.sort(listDataHeader, new Comparator<HeaderText>() {
                @Override
                public int compare(HeaderText headerText1, HeaderText headerText2) {

                    if(headerText1.getPosition() < headerText2.getPosition()) {
                        return -1;
                    } else if (headerText1.getPosition() == headerText2.getPosition()) {
                        return 0;
                    } else {
                        return 1;
                    }

                }
            });


            expandableListMoodleAdapter = new ExpandableListMoodleAdapter(getActivity(), listDataHeader, listDataChild);
            expListView.setAdapter(expandableListMoodleAdapter);
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                    Object object = expandableListMoodleAdapter.getChild(groupPosition, childPosition);

                    if(object instanceof MoodleModuleContent) {
                        MoodleModuleContent item = (MoodleModuleContent) object;

                        String url = item.getFileurl()+"&token="+ ApplicationManager.userCredentials.getMoodleToken();
                        Uri uri = Uri.parse(url);
                        DownloadManager.Request request = new DownloadManager.Request(uri);

                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, item.getFilename());

//                      r.allowScanningByMediaScanner();

                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        MimeTypeMap mimetype = MimeTypeMap.getSingleton();
                        String extension = FilenameUtils.getExtension(item.getFilename());

                        request.setMimeType(mimetype.getMimeTypeFromExtension(extension));

                        dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                        enqueue = dm.enqueue(request);
                    }

                    if(object instanceof MoodleCoreModule) {
                        MoodleCoreModule item = (MoodleCoreModule) object;

                        String url = "";
                        if(item.getModname().equals("url")) {
                            url = item.getContents().get(0).getFileurl();
                        } else {
                            url = item.getUrl();
                        }


                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }

                    return true;
                }
            });

            super.onRequestSuccess(null);


        }

    }


    /**
     * Query all resources for a Moodle course
     * @param idCourse
     */
    private void queryMoodleCoreCourses(final String idCourse) {
        SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {

            @Override
            public MoodleCoreCourses loadDataFromNetwork() throws Exception {
                String url = getActivity().getString(R.string.moodle_api_core_course_get_contents, ApplicationManager.userCredentials.getMoodleToken(), idCourse);

                return getRestTemplate().getForObject(url, MoodleCoreCourses.class);
            }
        };

        dataManager.sendRequest(request, this);
    }



    @Override
    void updateUI() {
       loadingView.showLoadingView();
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
