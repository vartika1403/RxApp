package entertainment.rxandroidapp.viewpackage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import entertainment.rxandroidapp.R;

public class Circle extends View {
    private static final int SQUARE_SIZE = 100;
    private Rect rect = new Rect();
    private Paint paint = new Paint();
    private int squareColor;
    private int squareSize;

    public Circle(Context context) {
        super(context);
        init(null);
    }

    public Circle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public Circle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public Circle(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        rect = new Rect();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (attrs == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Circle);
        squareColor = typedArray.getColor(R.styleable.Circle_square_color, Color.GREEN);
        squareSize = typedArray.getDimensionPixelSize(R.styleable.Circle_square_size, SQUARE_SIZE);
        paint.setColor(squareColor);

        typedArray.recycle();
    }

    public void swapColor() {
        paint.setColor(paint.getColor() == squareColor? Color.RED : squareColor);
        // invalidate();
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        rect.left = squareSize;
        rect.top = squareSize;
        rect.right = rect.left + squareSize;
        rect.bottom = rect.top + squareSize;

        canvas.drawRect(rect, paint);
    }

}
