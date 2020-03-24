package cz.johnyapps.piskvorky.shapes.shape;

import cz.johnyapps.piskvorky.shapes.Shapes;
import cz.johnyapps.piskvorky.shapes.shape.base.Circle;
import cz.johnyapps.piskvorky.shapes.shape.base.Cross;

public abstract class Shape implements ShapeInterface, Shapes {
    public static Shape idToShape(int id) {
        switch (id) {
            case Cross.ID: {
                return CROSS;
            }

            case Circle.ID: {
                return CIRCLE;
            }

            default: {
                return NO_SHAPE;
            }
        }
    }
}
