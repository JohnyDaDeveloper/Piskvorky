package cz.johnyapps.piskvorky.entities.shapes.shape;

import cz.johnyapps.piskvorky.entities.shapes.Shapes;
import cz.johnyapps.piskvorky.entities.shapes.shape.custom.Flame;
import cz.johnyapps.piskvorky.entities.shapes.shape.custom.Paw;
import cz.johnyapps.piskvorky.entities.shapes.shape.custom.Poop;
import cz.johnyapps.piskvorky.entities.shapes.shape.custom.Star;
import cz.johnyapps.piskvorky.entities.shapes.shape.shapes.Circle;
import cz.johnyapps.piskvorky.entities.shapes.shape.shapes.Cross;
import cz.johnyapps.piskvorky.entities.shapes.shape.custom.Hearth;

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

            case Paw.ID: {
                return PAW;
            }

            case Star.ID: {
                return STAR;
            }

            case Flame.ID: {
                return FLAME;
            }

            case Poop.ID: {
                return POOP;
            }

            default: {
                return NO_SHAPE;
            }
        }
    }
}
