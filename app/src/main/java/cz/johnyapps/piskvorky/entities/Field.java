package cz.johnyapps.piskvorky.entities;

import cz.johnyapps.piskvorky.shapes.Shapes;
import cz.johnyapps.piskvorky.shapes.shape.Shape;

public class Field implements Shapes {
    private float widthStart;
    private float widthEnd;

    private float heightStart;
    private float heightEnd;

    private Shape shape;
    private float size;

    public Field(float widthStart, float widthEnd, float heightStart, float heightEnd, float size) {
        this.widthStart = widthStart;
        this.widthEnd = widthEnd;

        this.heightStart = heightStart;
        this.heightEnd = heightEnd;

        this.shape = NO_SHAPE;
        this.size = size;
    }

    public float getWidthStart() {
        return widthStart;
    }

    public float getWidthEnd() {
        return widthEnd;
    }

    public float getHeightStart() {
        return heightStart;
    }

    public float getHeightEnd() {
        return heightEnd;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public float getSize() {
        return size;
    }
}
