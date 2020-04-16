package cz.johnyapps.piskvorky.services;

import android.util.Log;

import cz.johnyapps.piskvorky.entities.Player;

public class PlayersService {
    private static final String TAG = "PlayersService";
    private static final PlayersService ourInstance = new PlayersService();

    public static PlayersService getInstance() {
        return ourInstance;
    }

    private String myUid;

    private Player myPlayer;
    private Player enemyPlayer;

    private PlayersService() {
        myUid = null;
    }

    public String getMyUid() {
        return myUid;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    public Player getMyPlayer() {
        return myPlayer;
    }

    public void setMyPlayer(Player myPlayer) {
        Log.v(TAG, "setMyPlayer: " + myPlayer.getUid());
        this.myPlayer = myPlayer;
    }

    public Player getEnemyPlayer() {
        return enemyPlayer;
    }

    public void setEnemyPlayer(Player enemyPlayer) {
        Log.v(TAG, "setEnemyPlayer: " + enemyPlayer.getUid());
        this.enemyPlayer = enemyPlayer;
    }
}
