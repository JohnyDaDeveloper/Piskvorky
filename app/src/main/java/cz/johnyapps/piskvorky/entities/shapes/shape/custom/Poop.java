package cz.johnyapps.piskvorky.entities.shapes.shape.custom;

import cz.johnyapps.piskvorky.R;

public class Poop extends CustomShape {
    public static final int ID = 7;

    public Poop() {
        super(R.drawable.poop);
    }

    @Override
    public int getId() {
        return ID;
    }
}
