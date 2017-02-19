package ca.etsmtl.applets.etsmobile.view;


/*
* Copyright (C) 2015 Mert Şimşek
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import org.joda.time.DateTime;

import ca.etsmtl.applets.etsmobile.R;

public class ProgressLayout extends View implements Animatable {

    private static final int COLOR_EMPTY_DEFAULT = 0x00000000;
    private static final int COLOR_LOADED_DEFAULT = 0x11FFFFFF;
    private static final int COLOR_LOADING_DEFAULT = 0xFF757575;
    private static final int PROGRESS_SECOND_MS = 1000;

    private static Paint paintProgressLoaded;
    private static Paint paintProgressEmpty;
    private static Paint paintProgressLoading;

    private boolean isPlaying = false;
    private boolean isAutoProgress;

    private int mHeight;
    private int mWidth;
    private int maxProgress;
    private int currentProgress = 0;

    private Handler handlerProgress;

    private ProgressLayoutListener progressLayoutListener;
    private DateTime startDate;
    private DateTime endDate;
    private boolean isSquare = false;

    public ProgressLayout(Context context) {
        this(context, null);
    }

    public ProgressLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    public boolean isRunning() {
        return isPlaying;
    }

    @Override
    public void start() {
        if (isAutoProgress) {
            isPlaying = true;
            handlerProgress.removeCallbacksAndMessages(null);
            handlerProgress.postDelayed(mRunnableProgress, 0);
        }
    }

    @Override
    public void stop() {
        isPlaying = false;
        handlerProgress.removeCallbacks(mRunnableProgress);
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, mWidth, mHeight, paintProgressLoading);


        if (isSquare) {

            Path path = new Path();


            RectF square = new RectF(-mWidth, -mHeight, mWidth * 2, mHeight * 2);
            int angle = calculateAngle(currentProgress);
            path.setFillType(Path.FillType.EVEN_ODD);

            if (angle == 360) {
                path.addRect(0, 0, mWidth, mHeight, Path.Direction.CCW);
                path.close();
            } else {


                path.moveTo(mWidth / 2, mHeight / 2);
                path.lineTo(mWidth / 2, 0);

                path.arcTo(square, -90, calculateAngle(currentProgress));
                path.lineTo(mWidth / 2, mHeight / 2);
                path.close();

            }
            canvas.drawPath(path, paintProgressLoaded);

            canvas.drawRoundRect(20, 20, mWidth - 20, mHeight - 20, 10, 10, paintProgressEmpty);

        } else {
            RectF oval = new RectF(0, 0, mWidth, mHeight);
            int angle = calculateAngle(currentProgress);
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);

            if (angle == 360) {
                path.addCircle(mWidth / 2, mHeight / 2, mWidth / 2, Path.Direction.CCW);
                path.close();
            } else {

                path.moveTo(mWidth / 2, mHeight / 2);
                path.lineTo(mWidth / 2, 0);
                path.arcTo(oval, -90, calculateAngle(currentProgress));
                path.lineTo(mWidth / 2, mHeight / 2);
                path.close();

            }
            canvas.drawPath(path, paintProgressLoaded);

            canvas.drawOval(20, 20, mWidth - 20, mHeight - 20, paintProgressEmpty);
        }


//        canvas.drawArc(oval, 0, 90, true, paintProgressLoaded);


//        canvas.drawRect(0, 0, calculatePositionIndex(currentProgress), mHeight, paintProgressLoaded);
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.progressLayout);
        isAutoProgress = a.getBoolean(R.styleable.progressLayout_autoProgress, true);
        maxProgress = a.getInt(R.styleable.progressLayout_maxProgress, 0);
        maxProgress = maxProgress * 10;
        int loadedColor = a.getColor(R.styleable.progressLayout_loadedColor, COLOR_LOADED_DEFAULT);
        int emptyColor = a.getColor(R.styleable.progressLayout_emptyColor, COLOR_EMPTY_DEFAULT);
        int loadingColor = COLOR_LOADING_DEFAULT;
        a.recycle();

        paintProgressEmpty = new Paint();
        paintProgressEmpty.setColor(emptyColor);
        paintProgressEmpty.setStyle(Paint.Style.FILL);
        paintProgressEmpty.setAntiAlias(true);
//        paintProgressEmpty.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        paintProgressLoading = new Paint();
        paintProgressLoading.setColor(loadingColor);
        paintProgressLoading.setStyle(Paint.Style.FILL);
        paintProgressLoading.setAntiAlias(true);

        paintProgressLoaded = new Paint();
        paintProgressLoaded.setColor(loadedColor);
        paintProgressLoaded.setStyle(Paint.Style.FILL);
        paintProgressLoaded.setAntiAlias(true);

        handlerProgress = new Handler();
    }

    public void setSquareDisplay(boolean isSquare) {
        this.isSquare = isSquare;
    }

    private int calculatePositionIndex(int currentProgress) {
        return (currentProgress * mWidth) / maxProgress;
    }

    private int calculateAngle(int currentProgress) {
        return 360 * currentProgress / maxProgress;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void cancel() {
        isPlaying = false;
        currentProgress = 0;
        handlerProgress.removeCallbacks(mRunnableProgress);
        postInvalidate();
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress * 10;
        postInvalidate();
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress * 10;
        postInvalidate();
    }

    public void setAutoProgress(boolean isAutoProgress) {
        this.isAutoProgress = isAutoProgress;
    }

    public void setProgressLayoutListener(ProgressLayoutListener progressLayoutListener) {
        this.progressLayoutListener = progressLayoutListener;
    }

    public void setBoundsCourse(DateTime startDate, DateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private final Runnable mRunnableProgress = new Runnable() {
        @Override
        public void run() {
            if (isPlaying && startDate != null & endDate != null) {
                if (currentProgress >= maxProgress) {
                    if (progressLayoutListener != null) {
                        progressLayoutListener.onProgressCompleted();
                    }
                    setCurrentProgress(currentProgress);
                    stop();
                } else {
                    postInvalidate();
                    DateTime dateTime = new DateTime();
                    float percent = ((float) (dateTime.getMillis() - startDate.getMillis())
                            / (float) (endDate.getMillis() - startDate.getMillis()));


                    currentProgress = (int) (percent * 100) * 10;

                    if (progressLayoutListener != null) {
                        progressLayoutListener.onProgressChanged(currentProgress / 10);
                    }
                    handlerProgress.postDelayed(mRunnableProgress, PROGRESS_SECOND_MS * 60);
                }
            }
        }
    };

}
