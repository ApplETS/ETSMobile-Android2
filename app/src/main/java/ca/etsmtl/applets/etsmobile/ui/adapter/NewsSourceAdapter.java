package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ca.etsmtl.applets.etsmobile.model.NewsSource;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Steven on 2016-04-11.
 */
public class NewsSourceAdapter extends ArrayAdapter<NewsSource> {

    private LayoutInflater inflater;
    private Context context;

    public NewsSourceAdapter(Context context, int rowLayoutResourceId, ArrayList<NewsSource> list) {
        super(context, rowLayoutResourceId, list);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.row_news_source, parent, false);
            holder = new ViewHolder();
            holder.tvName = (TextView) view.findViewById(R.id.tv_source_name);
            holder.imageSource = (ImageView) view.findViewById(R.id.iv_news_source);
            view.setTag(holder);
        }

        NewsSource item = getItem(position);

        String urlImage = item.getType().equals("facebook") ?
                "http://graph.facebook.com/" + item.getValue() + "/picture" :
                item.getUrlImage();

        Picasso.with(context)
                .load(urlImage)
                .placeholder(R.drawable.loading_spinner)
                .resize(200, 200)
                .into(holder.imageSource);

        holder.tvName.setText(item.getName());

        return view;
    }

    static class ViewHolder {
        TextView tvName;
        ImageView imageSource;
    }
}
