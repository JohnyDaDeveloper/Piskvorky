package cz.johnyapps.piskvorky.entities.shapes.shape;

import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.DrawableRes;

import cz.johnyapps.piskvorky.entities.Field;

public interface ShapeInterface {
    void draw(Context context, Canvas canvas, Field field, float shapeWidth, float shapePadding);
    int getId();

    @DrawableRes
    int getDrawable();
}
