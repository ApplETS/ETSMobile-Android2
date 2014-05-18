package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.etsmtl.applets.etsmobile.model.MyMenuItem;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Phil on 18/11/13.
 */
public class MenuAdapter extends ArrayAdapter<MyMenuItem> {

	private static final int SEPARATOR = 0;
	private static final int ITEM = 1;
	private final LayoutInflater layoutInflater;
	private final Resources resources;
	private final MyMenuItem[] objects;

	public MenuAdapter(Context context, MyMenuItem[] objects) {
		super(context, R.layout.menu_item, objects);
		this.objects = objects;
		layoutInflater = LayoutInflater.from(getContext());
		resources = getContext().getResources();
	}

	@Override
	public MyMenuItem getItem(int position) {
		return objects[position];
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).mClass == null ? SEPARATOR : ITEM;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItemViewType(position) == ITEM;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		int itemViewType = getItemViewType(position);

		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			if (itemViewType == ITEM) {
				convertView = layoutInflater.inflate(R.layout.menu_item, parent, false);
			} else {
				convertView = layoutInflater.inflate(R.layout.menu_item_separator, parent, false);
			}

			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}

		final MyMenuItem myMenuItem = getItem(position);
		if (myMenuItem != null) {
			holder.title.setText(myMenuItem.title);
		}

		if (itemViewType == ITEM) {
			holder.title.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(myMenuItem.resId), null, null,
					null);
		} else {
			holder.title.setCompoundDrawables(null, null, null, null);
		}

		return convertView;
	}

	class ViewHolder {
		@InjectView(R.id.text1)
		TextView title;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
