package cz.johnyapps.piskvorky.entities.shapes.shape.custom;

import cz.johnyapps.piskvorky.R;

public class Paw extends CustomShape {
    public static final int ID = 4;

    public Paw() {
        super(R.drawable.paw);
    }

    @Override
    public int getId() {
        return ID;
    }
}
