package ca.etsmtl.applets.etsmobile.ui.calendar_decorator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.text.style.LineBackgroundSpan;

/**
 * Span to draw a triangle around  a section of text
 */
public class TriangleSpan implements LineBackgroundSpan {


    private final int color;
    private final int length;


    public TriangleSpan(int color) {
        this.color = color;
        length = 30;
    }

    @Override
    public void drawBackground(
            Canvas canvas, Paint paint,
            int left, int right, int top, int baseline, int bottom,
            CharSequence charSequence,
            int start, int end, int lineNum
    ) {
        int oldColor = paint.getColor();
        if (color != 0) {
            paint.setColor(color);
        }

        Point topCorner,botRight,botLeft;
        topCorner = new Point((left+right)/2,4*(bottom+top)/5);
        botLeft = new Point(topCorner.x -length, topCorner.y+length);
        botRight = new Point(topCorner.x + length, topCorner.y+length);

        Path path = new Path();
        path.moveTo(topCorner.x,topCorner.y);
        path.lineTo(botLeft.x, botLeft.y);
        path.lineTo(botRight.x, botRight.y);
        path.lineTo(topCorner.x, topCorner.y);
        path.close();



        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
        paint.setColor(oldColor);
    }
}
