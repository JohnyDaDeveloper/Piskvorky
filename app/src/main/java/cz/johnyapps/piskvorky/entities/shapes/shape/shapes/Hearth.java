package cz.johnyapps.piskvorky.entities.shapes.shape.shapes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import cz.johnyapps.piskvorky.R;
import cz.johnyapps.piskvorky.entities.Field;
import cz.johnyapps.piskvorky.entities.shapes.shape.Shape;

public class Hearth extends Shape {
    public static final int ID = 3;

    public Hearth() {

    }

    @Override
    public void draw(Context context, Canvas canvas, Field field, float shapeWidth, float shapePadding) {
        int start = (int) field.getWidthEnd();
        int top = (int) field.getHeightEnd();
        int size = (int) field.getSize();

        int padding = (int) shapePadding / 2;

        Paint paint = new Paint();
        paint.setColor(Color.RED);

        Paint painInner = new Paint();
        painInner.setColor(Color.WHITE);

        Drawable drawable = context.getDrawable(R.drawable.hearth);
        assert drawable != null;
        drawable.setBounds(start - size + padding,
                top - size + padding,
                start - padding,
                top - padding);
        drawable.draw(canvas);
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public int getDrawable() {
        return R.drawable.hearth;
    }
}
