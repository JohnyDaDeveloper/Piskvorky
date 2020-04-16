package cz.johnyapps.piskvorky.shapes.shape.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import cz.johnyapps.piskvorky.R;
import cz.johnyapps.piskvorky.entities.Field;

public class Hearth extends CustomShape {
    public static final int ID = 3;

    public Hearth() {

    }

    @Override
    public void draw(Context context, Canvas canvas, Field field, float shapeWidth, float shapePadding) {
        float size = field.getSize() / 2;
        float start = field.getWidthEnd() - size;
        float top = field.getHeightEnd() - size;

        Paint paint = new Paint();
        paint.setColor(Color.RED);

        Paint painInner = new Paint();
        painInner.setColor(Color.WHITE);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.hearth);
        canvas.drawBitmap(bitmap, start, top, paint);
    }

    @Override
    public int getId() {
        return ID;
    }
}
