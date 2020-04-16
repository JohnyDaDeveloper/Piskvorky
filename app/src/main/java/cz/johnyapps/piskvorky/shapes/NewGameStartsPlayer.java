package cz.johnyapps.piskvorky.shapes;

import cz.johnyapps.piskvorky.entities.Player;
import cz.johnyapps.piskvorky.services.PlayersService;

public class NewGameStartsPlayer implements Shapes {
    public static Player get() {
        PlayersService playersService = PlayersService.getInstance();

        Player player = playersService.getLastGameWon().getValue();
        Player myPlayer = playersService.getMyPlayer();
        Player enemyPlayer = playersService.getEnemyPlayer();

        if (player != null) {
            if (player.getUid().equals(myPlayer.getUid())) {
                return enemyPlayer;
            } else {
                return myPlayer;
            }
        } else {
            return myPlayer;
        }
    }
}
