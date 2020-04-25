package cz.johnyapps.piskvorky.entities.shapes.shape.custom;

import cz.johnyapps.piskvorky.R;

public class Flame extends CustomShape {
    public static final int ID = 5;

    public Flame() {
        super(R.drawable.flame);
    }

    @Override
    public int getId() {
        return ID;
    }
}
