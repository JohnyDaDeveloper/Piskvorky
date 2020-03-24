package cz.johnyapps.piskvorky.shapes;

import android.graphics.Canvas;

import cz.johnyapps.piskvorky.entities.Field;

public class ShapeDrawer implements Shapes {
    private float shapeWidth;
    private float shapePadding;

    public ShapeDrawer(float shapeWidth, float shapePadding) {
        this.shapeWidth = shapeWidth;
        this.shapePadding = shapePadding;
    }

    public void drawShape(Canvas canvas, Field field) {
        field.getShape().draw(canvas, field, shapeWidth, shapePadding);
    }
}
