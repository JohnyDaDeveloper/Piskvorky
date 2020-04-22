package cz.johnyapps.piskvorky.services;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import cz.johnyapps.piskvorky.entities.Player;
import cz.johnyapps.piskvorky.shapes.shape.Shape;

public class PlayersService {
    private static final String TAG = "PlayersService";
    private static final PlayersService ourInstance = new PlayersService();

    public static PlayersService getInstance() {
        return ourInstance;
    }

    private String myUid;

    private Player myPlayer;
    private Player enemyPlayer;

    private MutableLiveData<Player> lastGameWon;
    private MutableLiveData<Player> playingPlayer;

    private PlayersService() {
        lastGameWon = new MutableLiveData<>();
        playingPlayer = new MutableLiveData<>();
        myUid = null;
    }

    public Player getPlayerByShape(Shape shape) {
        Player myPlayer = PlayersService.getInstance().getMyPlayer();
        Player enemyPlayer = PlayersService.getInstance().getEnemyPlayer();

        if (myPlayer != null) {
            if (myPlayer.getPlayingAsShape().getId() == shape.getId()) {
                return myPlayer;
            }
        }

        if (enemyPlayer != null) {
            if (enemyPlayer.getPlayingAsShape().getId() == shape.getId()) {
                return enemyPlayer;
            }
        }

        return null;
    }

    public LiveData<Player> getPlayingPlayer() {
        return playingPlayer;
    }

    public void setPlayingPlayer(Player playingPlayer) {
        this.playingPlayer.setValue(playingPlayer);
    }

    public LiveData<Player> getLastGameWon() {
        return lastGameWon;
    }

    public void setLastGameWon(Player lastGameWon) {
        this.lastGameWon.setValue(lastGameWon);
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
        Log.v(TAG, "setMyPlayer: " + myPlayer.getUid() + " " + myPlayer.getPlayingAsShape());

        if (onMyPlayerChangedListener != null) {
            onMyPlayerChangedListener.onChange(myPlayer);
        }

        this.myPlayer = myPlayer;
    }

    private OnMyPlayerChangedListener onMyPlayerChangedListener;
    public interface OnMyPlayerChangedListener {
        void onChange(Player myPlayer);
    }

    public void setOnMyPlayerChangedListener(OnMyPlayerChangedListener onMyPlayerChangedListener) {
        this.onMyPlayerChangedListener = onMyPlayerChangedListener;
    }

    public Player getEnemyPlayer() {
        return enemyPlayer;
    }

    public void setEnemyPlayer(Player enemyPlayer) {
        Log.v(TAG, "setEnemyPlayer: " + enemyPlayer.getUid() + " " + enemyPlayer.getPlayingAsShape());
        this.enemyPlayer = enemyPlayer;
    }
}
