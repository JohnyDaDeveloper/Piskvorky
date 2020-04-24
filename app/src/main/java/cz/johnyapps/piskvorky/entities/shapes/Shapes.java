package cz.johnyapps.piskvorky.entities.shapes;

import cz.johnyapps.piskvorky.entities.shapes.shape.shapes.Circle;
import cz.johnyapps.piskvorky.entities.shapes.shape.shapes.Cross;
import cz.johnyapps.piskvorky.entities.shapes.shape.Shape;
import cz.johnyapps.piskvorky.entities.shapes.shape.shapes.NoShape;
import cz.johnyapps.piskvorky.entities.shapes.shape.shapes.Hearth;

public interface Shapes {
    Shape NO_SHAPE = new NoShape();
    Shape CROSS = new Cross();
    Shape CIRCLE = new Circle();
    Shape HEARTH = new Hearth();

    Shape[] ALL_SHAPES = {CROSS, CIRCLE, HEARTH};
}
