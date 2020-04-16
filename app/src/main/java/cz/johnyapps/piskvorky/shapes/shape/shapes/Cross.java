package cz.johnyapps.piskvorky.shapes.shape.shapes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import cz.johnyapps.piskvorky.R;
import cz.johnyapps.piskvorky.entities.Field;
import cz.johnyapps.piskvorky.shapes.shape.Shape;

public class Cross extends Shape {
    public static final int ID = 1;

    @Override
    public void draw(Context context, Canvas canvas, Field field, float shapeWidth, float shapePadding) {
        float start = field.getWidthEnd();
        float top = field.getHeightEnd();
        float bottom = field.getHeightStart();
        float end = field.getWidthStart();
        float size = field.getSize();

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(shapeWidth);

        canvas.drawLine(start - shapePadding,
                top - shapePadding,
                end + shapePadding,
                bottom + shapePadding,
                paint);

        canvas.drawLine(start - size + shapePadding,
                top - shapePadding,
                end + size - shapePadding,
                bottom + shapePadding,
                paint);
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public int getDrawable() {
        return R.drawable.cross;
    }
}
