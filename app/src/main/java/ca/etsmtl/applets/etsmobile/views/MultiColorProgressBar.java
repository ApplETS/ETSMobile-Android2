package ca.etsmtl.applets.etsmobile.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imad on 18/01/15.
 */
public class MultiColorProgressBar extends ProgressBar  {
    private List<ProgressItem> mProgressItemsList = new ArrayList<ProgressItem>();

    public MultiColorProgressBar(Context context) {
        super(context);
    }

    public MultiColorProgressBar (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiColorProgressBar (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addProgressItem(ProgressItem item) {
        mProgressItemsList.add(item);
    }

    public void clearProgressItems() {
        mProgressItemsList.clear();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int progressBarWidth = getWidth();
        int progressBarHeight = getHeight();
        int lastProgressX = 0;
        int progressItemWidth, progressItemRight;

        super.onDraw(canvas);

        for (int i = 0; i < mProgressItemsList.size(); i++) {
            ProgressItem progressItem = mProgressItemsList.get(i);
            Paint progressPaint = new Paint();
            Rect progressRect = new Rect();

            progressPaint.setColor(progressItem.color);
            progressItemWidth = (int) (progressItem.percentage
                    * progressBarWidth / 100);

            progressItemRight = lastProgressX + progressItemWidth;

            // for last item give right of the progress item to width of the
            // progress bar
            if (i == mProgressItemsList.size() - 1
                    && progressItemRight != progressBarWidth) {
                progressItemRight = progressBarWidth;
            }

            progressRect.set(lastProgressX, 0, progressItemRight,
                    progressBarHeight);
            canvas.drawRect(progressRect, progressPaint);
            lastProgressX = progressItemRight;
        }
    }
}
