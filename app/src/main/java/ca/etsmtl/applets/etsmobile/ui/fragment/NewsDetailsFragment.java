package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 12/19/14.
 */
public class NewsDetailsFragment extends HttpFragment {

    public static String FROM = "FROM";
    public static String IMAGE = "IMAGE";
    public static String TITLE = "TITLE";
    public static String CREATED_TIME = "CREATED_TIME";
    public static String FACEBOOK_LINK = "FACEBOOK_LINK";
    public static String UPDATED_TIME = "UPDATED_TIME";
    public static String MESSAGE = "MESSAGE";
    public static String ID = "ID";
    public static String ICON_LINK = "ICON_LINK";

    private String from;
    private String image;
    private String title;
    private String created_time;
    private String facebook_link;
    private String updated_time;
    private String message;
    private String id;
    private String icon_link;

    private TextView tvTest;
    
    
    public static NewsDetailsFragment newInstance(String from, String image, String title, String created_time, String facebook_link, String updated_time, String message, String id, String icon_link) {
        NewsDetailsFragment fragment = new NewsDetailsFragment();
        Bundle args = new Bundle();

        args.putString(FROM, from);
        args.putString(IMAGE, image);
        args.putString(TITLE, title);
        args.putString(CREATED_TIME, created_time);
        args.putString(FACEBOOK_LINK, facebook_link);
        args.putString(UPDATED_TIME, updated_time);
        args.putString(MESSAGE, message);
        args.putString(ID, id);
        args.putString(ICON_LINK, icon_link);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();

            from = bundle.getString(FROM);
            image = bundle.getString(IMAGE);
            title = bundle.getString(TITLE);
            created_time = bundle.getString(CREATED_TIME);
            facebook_link = bundle.getString(FACEBOOK_LINK);
            updated_time = bundle.getString(UPDATED_TIME);
            message = bundle.getString(MESSAGE);
            id = bundle.getString(ID);
            icon_link = bundle.getString(ICON_LINK);

        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news_details, container, false);

        tvTest = (TextView) v.findViewById(R.id.tv_news_test);

        tvTest.setText(message);



        return v;
    }


    @Override
    void updateUI() {
        // TODO Auto-generated method stub

    }


}



