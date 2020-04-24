package cz.johnyapps.piskvorky.entities.shapes.shape;

import cz.johnyapps.piskvorky.entities.shapes.Shapes;
import cz.johnyapps.piskvorky.entities.shapes.shape.shapes.Circle;
import cz.johnyapps.piskvorky.entities.shapes.shape.shapes.Cross;
import cz.johnyapps.piskvorky.entities.shapes.shape.shapes.Hearth;

public abstract class Shape implements ShapeInterface, Shapes {
    public static Shape idToShape(int id) {
        switch (id) {
            case Cross.ID: {
                return CROSS;
            }

            case Circle.ID: {
                return CIRCLE;
            }

            case Hearth.ID: {
                return HEARTH;
            }

            default: {
                return NO_SHAPE;
            }
        }
    }
}
