package cz.johnyapps.piskvorky.shapes.shape;

import android.graphics.Canvas;

import cz.johnyapps.piskvorky.entities.Field;

public interface ShapeInterface {
    void draw(Canvas canvas, Field field, float shapeWidth, float shapePadding);
    int getId();
    boolean isBaseShape();
}
