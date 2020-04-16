package cz.johnyapps.piskvorky.shapes;

import cz.johnyapps.piskvorky.shapes.shape.base.Circle;
import cz.johnyapps.piskvorky.shapes.shape.base.Cross;
import cz.johnyapps.piskvorky.shapes.shape.Shape;
import cz.johnyapps.piskvorky.shapes.shape.base.NoShape;
import cz.johnyapps.piskvorky.shapes.shape.custom.Hearth;

public interface Shapes {
    Shape NO_SHAPE = new NoShape();
    Shape CROSS = new Cross();
    Shape CIRCLE = new Circle();
    Shape HEARTH = new Hearth();

    Shape[] ALL_SHAPES = {CROSS, CIRCLE, HEARTH};
}
