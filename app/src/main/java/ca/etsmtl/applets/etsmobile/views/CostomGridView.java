/*******************************************************************************
 * Copyright 2013 Club ApplETS
 * 
 * Created by Laurence on 03/02/14.
 */
package ca.etsmtl.applets.etsmobile.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

public class CostomGridView extends GridView {

	boolean expended = true;

	public CostomGridView(Context context) {
		super(context);
	}

	public CostomGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CostomGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean isExpanded() {
		return expended;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		if (isExpanded()) {
			int expandSize = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSize);
			ViewGroup.LayoutParams layoutParams = getLayoutParams();
			layoutParams.height = getMeasuredHeight();
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public void setExpanded(boolean expanded) {
		this.expended = expanded;
	}

}
