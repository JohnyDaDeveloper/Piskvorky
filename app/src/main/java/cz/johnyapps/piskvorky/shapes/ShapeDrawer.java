package cz.johnyapps.piskvorky.shapes;

import android.content.Context;
import android.graphics.Canvas;

import cz.johnyapps.piskvorky.entities.Field;

public class ShapeDrawer implements Shapes {
    private Context context;
    private float shapeWidth;
    private float shapePadding;

    public ShapeDrawer(Context context, float shapeWidth, float shapePadding) {
        this.context = context;
        this.shapeWidth = shapeWidth;
        this.shapePadding = shapePadding;
    }

    public void drawShape(Canvas canvas, Field field) {
        field.getShape().draw(context, canvas, field, shapeWidth, shapePadding);
    }
}
