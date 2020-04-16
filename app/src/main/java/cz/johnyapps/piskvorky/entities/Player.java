package cz.johnyapps.piskvorky.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.johnyapps.piskvorky.shapes.Shapes;
import cz.johnyapps.piskvorky.shapes.shape.Shape;

public class Player {
    private static final String PREFERRED_SHAPE = "preferredShape";

    private String uid;
    private Shape preferredShape;
    private Shape playingAsShape;

    public Player(String uid, Shape preferredShape) {
        this.uid = uid;
        this.playingAsShape = preferredShape;
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

        return map;
    }

    public static Player fromMap(String uid, Map<String, Object> map) {
        Shape preferredShape = null;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            switch (entry.getKey()) {
                case PREFERRED_SHAPE: {
                    preferredShape = Shape.idToShape(Integer.parseInt(String.valueOf(entry.getValue()))); //Long na int
                    break;
                }
            }
        }

        return new Player(uid, preferredShape);
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
}
