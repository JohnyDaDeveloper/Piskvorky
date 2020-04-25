package cz.johnyapps.piskvorky.entities.shapes.shape.custom;

import cz.johnyapps.piskvorky.R;

public class Star extends CustomShape {
    public static final int ID = 6;

    public Star() {
        super(R.drawable.star);
    }

    @Override
    public int getId() {
        return ID;
    }
}
