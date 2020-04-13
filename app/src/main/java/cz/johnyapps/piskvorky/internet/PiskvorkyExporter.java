package cz.johnyapps.piskvorky.internet;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.johnyapps.piskvorky.entities.BoardSettings;
import cz.johnyapps.piskvorky.entities.Field;
import cz.johnyapps.piskvorky.services.PiskvorkyService;
import cz.johnyapps.piskvorky.shapes.shape.base.NoShape;

public class PiskvorkyExporter {
    private static final String TAG = "PiskvorkyExporter";

    public PiskvorkyExporter() {

    }

    public DocumentReference createGame(BoardSettings boardSettings) {
        Log.v(TAG, "createGame");

        if (boardSettings == null) {
            Log.e(TAG, "createGame: export failed because game setting are null");
            return null;
        }

        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();

        CollectionReference games = piskvorkyService.getGamesCollection();
        DocumentReference gameReference = games.document();

        Map<String, Object> map = new HashMap<>();
        map.put("playingPlayer", piskvorkyService.getPlayingPlayerId());
        map.put("settings", boardSettings.toMap());
        map.put("fields", fieldsToMap());
        map.put("newGame", true);

        gameReference.set(map);
        return gameReference;
    }

    public void updateGame(DocumentReference gameReference, boolean newGame) {
        if (gameReference == null) {
            Log.e(TAG, "updateGame: Document reference is null");
            return;
        }

        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
        Map<String, Object> fields = fieldsToMap();

        int playingPlayerId = piskvorkyService.getPlayingPlayerId();

        Log.v(TAG, "updateGame: " + fields.size() + " fields, playing player: " + playingPlayerId);

        Map<String, Object> map = new HashMap<>();
        map.put("fields", fields);
        map.put("playingPlayer", playingPlayerId);
        map.put("newGame", newGame);
        gameReference.update(map);
    }

    private Map<String, Object> fieldsToMap() {
        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
        ArrayList<Field> fields = piskvorkyService.getFieldsArray();

        if (fields == null) {
            Log.w(TAG, "exportBoard: fields are null");
            return new HashMap<>();
        }

        Map<String, Object> fieldsMap = new HashMap<>();

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);

            if (field.getShape().getId() != NoShape.ID) {
                fieldsMap.put(String.valueOf(i), field.getShape().getId());
            }
        }

        return fieldsMap;
    }
}
