package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

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
import ca.etsmtl.applets.etsmobile.ui.adapter.MoodleCoursesDetailsAdapter;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 10/30/14.
 */
public class MoodleCourseDetailsFragment extends HttpFragment {

    public static String COURSE_ID = "COURSE_ID";

    private MoodleCoursesDetailsAdapter moodleCoursesDetailsAdapter;
    private ListView moodleCoreListView;
    private String moodleCourseId;

    private ExpandableListMoodleAdapter expandableListMoodleAdapter;

    private ExpandableListView expListView;

    private List<MoodleCoreModule> listDataHeader;
    private HashMap<MoodleCoreModule, List<MoodleModuleContent>> listDataChild;

    private ArrayList<MoodleCoreModule> listMoodleLinkModules;


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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moodle_details, container, false);

        expListView = (ExpandableListView) v.findViewById(R.id.expandableListView_moodle_courses_details);

        moodleCoreListView = (ListView) v.findViewById(R.id.listView_moodle_courses_details);

        queryMoodleCoreCourses(moodleCourseId);
        return v;
    }

    @Override
    public void onStart() {
//        Log.v("NotesDetailsFragment", "Note detailsFragement pwd = " + ApplicationManager.userCredentials.getPassword());
//        DataManager.getInstance(getActivity()).getDataFromSignet(DataManager.SignetMethods.LIST_EVAL,
//                ApplicationManager.userCredentials, this, session, groupe, sigle);
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
            listDataChild = new HashMap<MoodleCoreModule, List<MoodleModuleContent>>();
            listDataHeader = new ArrayList<MoodleCoreModule>();
            listMoodleLinkModules = new ArrayList<MoodleCoreModule>();

            int position = 0;

            for(MoodleCoreCourse coreCourse : moodleCoreCourses) {
                for(MoodleCoreModule coreModule : coreCourse.getModules()) {
                    coreModule.setPosition(position);
                    position++;

                    if(coreModule.getModname().equals("folder")) {
                        listDataChild.put(coreModule,coreModule.getContents());
                    } else if (coreModule.getModname().equals("url") || coreModule.getModname().equals("forum")) {
                        listMoodleLinkModules.add(coreModule);
                    }

                }
            }



            listDataHeader.addAll(listDataChild.keySet());

            Collections.sort(listDataHeader, new Comparator<MoodleCoreModule>() {
                @Override
                public int compare(MoodleCoreModule moodleCoreModule1, MoodleCoreModule MoodleCoreModule2) {

                    if(moodleCoreModule1.getPosition() < MoodleCoreModule2.getPosition()) {
                        return -1;
                    } else if (moodleCoreModule1.getPosition() == MoodleCoreModule2.getPosition()) {
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

                    MoodleModuleContent item = (MoodleModuleContent) expandableListMoodleAdapter.getChild(groupPosition, childPosition);

                    String url = item.getFileurl()+"&token="+ ApplicationManager.userCredentials.getMoodleToken();
                    Uri uri = Uri.parse(url);
                    DownloadManager.Request r = new DownloadManager.Request(uri);

                    r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, item.getFilename());

//                    r.allowScanningByMediaScanner();

                    r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    MimeTypeMap mimetype = MimeTypeMap.getSingleton();
                    String extension = FilenameUtils.getExtension(item.getFilename());

                    r.setMimeType(mimetype.getMimeTypeFromExtension(extension));




                    DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    dm.enqueue(r);

                    return true;
                }
            });


            moodleCoursesDetailsAdapter = new MoodleCoursesDetailsAdapter(getActivity(), R.layout.row_moodle_course_detail, listMoodleLinkModules, this);
            moodleCoreListView.setAdapter(moodleCoursesDetailsAdapter);

            moodleCoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                    MoodleCoreModule item = (MoodleCoreModule) moodleCoreListView.getItemAtPosition(position);

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
            });




        }

    }


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
        // TODO Auto-generated method stub

    }
}
