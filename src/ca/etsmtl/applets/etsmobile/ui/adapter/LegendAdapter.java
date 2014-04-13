package ca.etsmtl.applets.etsmobile.ui.adapter;

import ca.etsmtl.applets.etsmobile2.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LegendAdapter extends BaseAdapter{

	private Context ctx; 
	private String[] elem;
	private int[] color;
	public LegendAdapter(Context context,String[] elem, int[] color) {
		ctx = context;
		this.elem = elem;
		this.color = color;
	}
	@Override
	public int getCount() {
		if(elem!=null){
			return elem.length;
		}
		return 0;
	}
	@Override
	public Object getItem(int position) {
		return elem[position];
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.legend, parent, false);
		} else {
			v = convertView;
		}

		TextView text = (TextView)v.findViewById(R.id.legend_text);
		text.setText(elem[position]);
		text.setTextColor(color[position]);

		return v;
	}

}
