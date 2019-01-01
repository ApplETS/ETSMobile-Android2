package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Sonphil on 2017-03-16.
 */

public class ColorSpinnerAdapter extends ArrayAdapter implements SpinnerAdapter {

    private Integer[] colors;
    private Activity activity;

    public ColorSpinnerAdapter(Activity activity, Integer[] colors) {
        super(activity, R.layout.color_spinner_item, colors);
        this.activity = activity;
        this.colors = colors;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        super.getDropDownView(position, convertView, parent);

        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.color_spinner_item, null);
        }

        rowView.setBackgroundColor(colors[position].intValue());
        TextView textView = (TextView) rowView.findViewById(R.id.color_spinner_item_tv);
        textView.setText("");

        return rowView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        super.getView(position, convertView, parent);

        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.color_spinner_item, null);
        }

        rowView.setBackgroundColor(colors[position].intValue());

        return rowView;
    }
}
