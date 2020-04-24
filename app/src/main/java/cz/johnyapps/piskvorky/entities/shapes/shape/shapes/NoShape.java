package cz.johnyapps.piskvorky.entities.shapes.shape.shapes;

import android.content.Context;
import android.graphics.Canvas;

import cz.johnyapps.piskvorky.R;
import cz.johnyapps.piskvorky.entities.Field;
import cz.johnyapps.piskvorky.entities.shapes.shape.Shape;

public class NoShape extends Shape {
    public static final int ID = 0;

    @Override
    public void draw(Context context, Canvas canvas, Field field, float shapeWidth, float shapePadding) {

    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public int getDrawable() {
        return R.drawable.error;
    }
}
