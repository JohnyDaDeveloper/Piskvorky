package cz.johnyapps.piskvorky.entities;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.johnyapps.piskvorky.shapes.Shapes;
import cz.johnyapps.piskvorky.shapes.shape.Shape;

public class Player {
    private static final String PREFERRED_SHAPE = "preferredShape";
    private static final String PLAYING_AS_SHAPE = "playingAsShape";

    private String uid;
    private Shape preferredShape;
    private Shape playingAsShape;

    public Player(String uid, Shape preferredShape) {
        this.uid = uid;
        this.preferredShape = preferredShape;
    }

    public Shape getPlayingAsShape() {
        return playingAsShape == null ? Shapes.NO_SHAPE : playingAsShape;
    }

    public void setPlayingAsShape(Shape playingAsShape) {
        this.playingAsShape = playingAsShape;
    }

    public String getUid() {
        return uid;
    }

    public Shape getPreferredShape() {
        return preferredShape;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(PREFERRED_SHAPE, preferredShape.getId());

        if (playingAsShape != null) {
            map.put(PLAYING_AS_SHAPE, playingAsShape.getId());
        }

        return map;
    }

    public void setPreferredShape(Shape preferredShape) {
        this.preferredShape = preferredShape;
    }

    public static Player fromMap(String uid, Map<String, Object> map) {
        Shape preferredShape = null;
        Shape playingAsShape = null;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            switch (entry.getKey()) {
                case PREFERRED_SHAPE: {
                    preferredShape = Shape.idToShape(Integer.parseInt(String.valueOf(entry.getValue()))); //Long na int
                    break;
                }

                case PLAYING_AS_SHAPE: {
                    playingAsShape = Shape.idToShape(Integer.parseInt(String.valueOf(entry.getValue()))); //Long na int
                    break;
                }
            }
        }

        Player player = new Player(uid, preferredShape);
        player.setPlayingAsShape(playingAsShape);

        return player;
    }

    public String toJSONString() throws JSONException {
        JSONObject player = new JSONObject();
        player.put(PREFERRED_SHAPE, preferredShape.getId());

        return player.toString();
    }

    public static Player fromJSONString(String json, String uid) throws JSONException {
        JSONObject player = new JSONObject(json);
        Shape preferredShape = Shape.idToShape(player.getInt(PREFERRED_SHAPE));

        return new Player(uid, preferredShape);
    }

    @NonNull
    @Override
    public String toString() {
        return "PLAYER " + uid +
                " PREFERRED SHAPE " + preferredShape.getId() +
                " PLAYING AS " + (playingAsShape == null ? "null" : playingAsShape.getId());
    }
}
