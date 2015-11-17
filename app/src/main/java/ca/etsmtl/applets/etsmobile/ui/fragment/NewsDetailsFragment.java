package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

import ca.etsmtl.applets.etsmobile.util.Utility;
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

    private TextView tvFrom;
    private ImageView ivImage;
    private TextView tvTitle;
    private TextView tvFacebook_link;
    private TextView tvUpdatedTime;
    private TextView tvMessage;

    
    
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

        tvFrom = (TextView) v.findViewById(R.id.tv_news_details_from);
        ivImage = (ImageView) v.findViewById(R.id.iv_news_details_image);
        tvTitle = (TextView) v.findViewById(R.id.tv_news_details_title);
        tvFacebook_link = (TextView) v.findViewById(R.id.tv_news_details_fb_link);
        tvUpdatedTime = (TextView) v.findViewById(R.id.tv_news_details_updated_time);
        tvMessage = (TextView) v.findViewById(R.id.tv_news_details_message);

        tvFrom.setText(from);
        tvTitle.setText(title);
        tvFacebook_link.setText(facebook_link);

        Date date = Utility.getDateFromString(updated_time);
        String dateText = DateFormatUtils.format(date, "dd MMM yyyy", Locale.CANADA_FRENCH);
        tvUpdatedTime.setText(dateText);
        tvMessage.setText(message);

        new DownloadImage().execute(image);

        return v;
    }


    @Override
    void updateUI() {
        // TODO Auto-generated method stub

    }


    /**
     * Simple functin to set a Drawable to the image View
     * @param drawable
     */
    private void setImage(Drawable drawable)
    {
        ivImage.setImageDrawable(drawable);
    }


    public class DownloadImage extends AsyncTask<String, Integer, Drawable> {

        @Override
        protected Drawable doInBackground(String... arg0) {
            // This is done in a background thread
            return downloadImage(arg0[0]);
        }

        /**
         * Called after the image has been downloaded
         * -> this calls a function on the main thread again
         */
        protected void onPostExecute(Drawable image)
        {
            setImage(image);
        }


        /**
         * Actually download the Image from the _url
         * @param _url
         * @return
         */
        private Drawable downloadImage(String _url)
        {
            //Prepare to download image
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            //BufferedInputStream buf;
            try {
                url = new URL(_url);
                in = url.openStream();



                // Read the inputstream
                buf = new BufferedInputStream(in);

                // Convert the BufferedInputStream to a Bitmap
                Bitmap bMap = BitmapFactory.decodeStream(buf);
                if (in != null) {
                    in.close();
                }
                if (buf != null) {
                    buf.close();
                }

                return new BitmapDrawable(getResources(),bMap);

            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
            }

            return null;
        }

    }



}



