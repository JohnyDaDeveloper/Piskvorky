package cz.johnyapps.piskvorky.shapes;

import cz.johnyapps.piskvorky.shapes.shape.shapes.Circle;
import cz.johnyapps.piskvorky.shapes.shape.shapes.Cross;
import cz.johnyapps.piskvorky.shapes.shape.Shape;
import cz.johnyapps.piskvorky.shapes.shape.shapes.NoShape;
import cz.johnyapps.piskvorky.shapes.shape.shapes.Hearth;

public interface Shapes {
    Shape NO_SHAPE = new NoShape();
    Shape CROSS = new Cross();
    Shape CIRCLE = new Circle();
    Shape HEARTH = new Hearth();

    Shape[] ALL_SHAPES = {CROSS, CIRCLE, HEARTH};
}
