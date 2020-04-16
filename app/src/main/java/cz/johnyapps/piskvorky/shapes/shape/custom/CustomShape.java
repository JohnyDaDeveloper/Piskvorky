package cz.johnyapps.piskvorky.shapes.shape.custom;

import cz.johnyapps.piskvorky.shapes.shape.Shape;

public abstract class CustomShape extends Shape {
    @Override
    public boolean isBaseShape() {
        return false;
    }
}
