package cz.johnyapps.piskvorky.shapes.shape.shapes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import cz.johnyapps.piskvorky.R;
import cz.johnyapps.piskvorky.entities.Field;
import cz.johnyapps.piskvorky.shapes.shape.Shape;

public class Circle extends Shape {
    public static final int ID = 2;

    @Override
    public void draw(Context context, Canvas canvas, Field field, float shapeWidth, float shapePadding) {
        float size = field.getSize() / 2;
        float start = field.getWidthEnd() - size;
        float top = field.getHeightEnd() - size;

        size -= shapePadding;

        Paint paint = new Paint();
        paint.setColor(Color.RED);

        Paint painInner = new Paint();
        painInner.setColor(Color.WHITE);

        canvas.drawCircle(start, top, size, paint);
        canvas.drawCircle(start, top, size - shapeWidth, painInner);
    }



    @Override
    public int getId() {
        return ID;
    }

    @Override
    public int getDrawable() {
        return R.drawable.circle;
    }
}
