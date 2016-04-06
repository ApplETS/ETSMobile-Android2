package ca.etsmtl.applets.etsmobile.ui.calendar_decorator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

/**
 * Span to draw a dot centered under a section of text
 */
public class SquareSpan implements LineBackgroundSpan {

    /**
     * Default length used
     */
    public static final float DEFAULT_LENGTH = 3;

    private final float length;
    private final int color;

    /**
     * Create a span to draw a dot using default length and color
     *
     * @see #SquareSpan(float, int)
     * @see #DEFAULT_LENGTH
     */
    public SquareSpan() {
        this.length = DEFAULT_LENGTH;
        this.color = 0;
    }

    /**
     * Create a span to draw a dot using a specified color
     *
     * @param color color of the dot
     * @see #SquareSpan(float, int)
     * @see #DEFAULT_LENGTH
     */
    public SquareSpan(int color) {
        this.length = DEFAULT_LENGTH;
        this.color = color;
    }

    /**
     * Create a span to draw a dot using a specified length
     *
     * @param length length for the dot
     * @see #SquareSpan(float, int)
     */
    public SquareSpan(float length) {
        this.length = length;
        this.color = 0;
    }

    /**
     * Create a span to draw a dot using a specified length and color
     *
     * @param length length for the dot
     * @param color  color of the dot
     */
    public SquareSpan(float length, int color) {
        this.length = length;
        this.color = color;
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

        float Left = (left+right)/2 - (length/2);
        float Top = bottom +length;
        canvas.drawRect(Left,Top,Left+length,Top+length,paint);
        paint.setColor(oldColor);
    }
}
