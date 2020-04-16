package cz.johnyapps.piskvorky.shapes.shape.base;

import android.content.Context;
import android.graphics.Canvas;

import cz.johnyapps.piskvorky.entities.Field;

public class NoShape extends BaseShape {
    public static final int ID = 0;

    @Override
    public void draw(Context context, Canvas canvas, Field field, float shapeWidth, float shapePadding) {

    }

    @Override
    public int getId() {
        return ID;
    }
}
