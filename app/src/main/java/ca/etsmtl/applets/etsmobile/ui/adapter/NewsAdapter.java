package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.octo.android.robospice.request.listener.RequestListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Locale;

import ca.etsmtl.applets.etsmobile.model.Nouvelle;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 12/12/14.
 */
public class NewsAdapter extends ArrayAdapter<Nouvelle> {

    private LayoutInflater inflater;
    private RequestListener<Object> listener;

    public NewsAdapter(Context context,int rowLayoutResourceId, ArrayList<Nouvelle> list, RequestListener<Object> listener) {
        super(context, rowLayoutResourceId, list);
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.row_news, parent, false);
            holder = new ViewHolder();
            holder.tvTitre = (TextView) view.findViewById(R.id.tv_row_news_titre);
            holder.tvDate = (TextView) view.findViewById(R.id.tv_row_news_date);
            holder.imageSource = (ImageView) view.findViewById(R.id.iv_news_source);
            view.setTag(holder);
        }

        Nouvelle item = getItem(position);

        holder.tvTitre.setText(item.getTitle());
        String updatedTime = item.getUpdated_time();

        String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
        DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);

        DateTime date = dtf.parseDateTime(updatedTime);

        /*DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime date = dateTimeFormatter.parseDateTime(updatedTime);
        DateTimeFormatter dateToDisplay = DateTimeFormat.forPattern("dd MMM yyyy");*/

        holder.tvDate.setText(date.toString("dd MMM yyyy", Locale.CANADA_FRENCH));
        holder.tvTitre.setText(item.getTitle());
        holder.imageSource.setImageResource(item.getImageResource());

        return view;
    }

    static class ViewHolder {
        TextView tvTitre;
        TextView tvDate;
        ImageView imageSource;
    }
}