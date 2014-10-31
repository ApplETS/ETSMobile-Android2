package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

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

    private ExpandableListMoodleAdapter listAdapter;

    private ExpandableListView expListView;

    private List<MoodleCoreModule> listDataHeader;
    private HashMap<MoodleCoreModule, List<MoodleModuleContent>> listDataChild;


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
        //moodleCoreListView = (ListView) v.findViewById(R.id.listView_moodle_courses_details);

        expListView = (ExpandableListView) v.findViewById(R.id.expandableListView_moodle_courses_details);

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

            int position = 0;

            for(MoodleCoreCourse coreCourse : moodleCoreCourses) {
                for(MoodleCoreModule coreModule : coreCourse.getModules()) {
                    coreModule.setPosition(position);
                    position++;

                    if(coreModule.getModname().equals("folder")) {
                        listDataChild.put(coreModule,coreModule.getContents());
                    } else {
                        //TODO ListView au dessus pour les liens
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


            listAdapter = new ExpandableListMoodleAdapter(getActivity(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);


//            moodleCoursesDetailsAdapter = new MoodleCoursesDetailsAdapter(getActivity(), R.layout.row_moodle_course_detail, modules, this);
//            moodleCoreListView.setAdapter(moodleCoursesDetailsAdapter);

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
