package cz.johnyapps.piskvorky.shapes;

import cz.johnyapps.piskvorky.services.PiskvorkyService;
import cz.johnyapps.piskvorky.shapes.shape.Shape;

public class NewGameStartsShape implements Shapes {
    public static Shape get() {
        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();

        int lastGameWon = piskvorkyService.getLastGameWonShape();
        Shape myPlayer = piskvorkyService.getMyPlayer();

        if (lastGameWon != myPlayer.getId()) {
            return myPlayer;
        }

        if (lastGameWon == CROSS.getId()) {
            return CIRCLE;
        } else if (lastGameWon == CIRCLE.getId()) {
            return CROSS;
        } else {
            return CROSS;
        }
    }
}
