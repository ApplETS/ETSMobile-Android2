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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ca.etsmtl.applets.etsmobile.model.Sponsor;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Steven on 2015-10-05.
 */
public class SponsorAdapter extends ArrayAdapter<Sponsor> {

    private LayoutInflater inflater;
    private RequestListener<Object> listener;
    private Context context;

    public SponsorAdapter(Context context, int rowLayoutResourceId, ArrayList<Sponsor> list, RequestListener<Object> listener) {
        super(context, rowLayoutResourceId, list);
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.context = context;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.row_sponsor, parent, false);
            holder = new ViewHolder();
            holder.tvName = (TextView) view.findViewById(R.id.tv_row_sponsor_name);
            holder.imageSource = (ImageView) view.findViewById(R.id.iv_sponsor_source);

            view.setTag(holder);
        }
        int imageSize;

        Sponsor item = getItem(position);
        switch (item.getIndex()) {
            case 6:
                imageSize = 500;
                break;
            case 5 | 4:
                imageSize = 400;
                break;
            default:
                imageSize = 300;
                break;
        }
        Picasso.with(context)
                .load(item.getImageUrl())
                .resize(imageSize, imageSize)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(holder.imageSource);
        holder.tvName.setText(item.getName());

        return view;
    }

    static class ViewHolder {
        TextView tvName;
        ImageView imageSource;
    }

}
