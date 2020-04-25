package cz.johnyapps.piskvorky.entities.shapes.shape.custom;

import cz.johnyapps.piskvorky.R;

public class Hearth extends CustomShape {
    public static final int ID = 3;

    public Hearth() {
        super(R.drawable.hearth);
    }

    @Override
    public int getId() {
        return ID;
    }
}
