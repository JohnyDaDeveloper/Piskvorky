package cz.johnyapps.piskvorky.shapes.shape.shapes;

import android.content.Context;
import android.graphics.Canvas;

import cz.johnyapps.piskvorky.entities.Field;
import cz.johnyapps.piskvorky.shapes.shape.Shape;

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
        return -1;
    }
}
