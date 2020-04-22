package cz.johnyapps.piskvorky.internet;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

import cz.johnyapps.piskvorky.entities.Field;
import cz.johnyapps.piskvorky.entities.Player;
import cz.johnyapps.piskvorky.services.PiskvorkyService;
import cz.johnyapps.piskvorky.services.PlayersService;
import cz.johnyapps.piskvorky.shapes.Shapes;
import cz.johnyapps.piskvorky.shapes.shape.Shape;
import cz.johnyapps.piskvorky.shapes.shape.shapes.Circle;
import cz.johnyapps.piskvorky.shapes.shape.shapes.Cross;

public class PiskvorkyImporter {
    private static final String TAG = "PiskvorkyImporter";

    public PiskvorkyImporter() {

    }

    public void importGame(String gameId) {
        Log.v(TAG, "importGame");

        if (gameId == null || gameId.isEmpty()) {
            Log.e(TAG, "importGame: game ID is invalid");
            return;
        }

        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
        CollectionReference games = piskvorkyService.getGamesCollection();
        DocumentReference gameReference = games.document(gameId);

        gameReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                processGameSnapshot(documentSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

        piskvorkyService.setGameReference(gameReference);
    }

    @SuppressWarnings("unchecked")
    public void processGameSnapshot(@Nullable DocumentSnapshot documentSnapshot) {
        if (documentSnapshot != null && documentSnapshot.exists()) {
            Map<String, Object> fields = (Map<String, Object>) documentSnapshot.get("fields");

            if (fields != null) {
                mapToFields(fields);
            } else {
                Log.e(TAG, "processGameSnapshot: fields were null");
            }

            Map<String, Object> players = (Map<String, Object>) documentSnapshot.get("players");

            if (players != null) {
                mapToPlayers(players);
            } else {
                Log.e(TAG, "processGameSnapshot: players are null");
            }

            int playingPlayer = Integer.parseInt(String.valueOf(documentSnapshot.get("playingPlayer")));
            int lastMoveIndex = Integer.parseInt(String.valueOf(documentSnapshot.get("lastMove")));
            boolean newGame = Boolean.parseBoolean(String.valueOf(documentSnapshot.get("newGame")));

            PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
            piskvorkyService.setLastMoveIndex(lastMoveIndex);
            piskvorkyService.setNewGame(newGame);

            Shape shape = Shape.idToShape(playingPlayer);
            Player player = PlayersService.getInstance().getPlayerByShape(shape);
            PlayersService.getInstance().setPlayingPlayer(player);

            if (onImportFinishedListener != null) {
                onImportFinishedListener.onSuccess();
            }
        } else {
            Log.w(TAG, "processGameSnapshot: document not found");

            if (onImportFinishedListener != null) {
                onImportFinishedListener.onFail();
            }
        }

        if (onImportFinishedListener != null) {
            onImportFinishedListener.onComplete();
        }
    }

    @SuppressWarnings("unchecked")
    private void mapToPlayers(Map<String, Object> map) {
        String myUid = PlayersService.getInstance().getMyUid();
        boolean foundMyPlayer = false;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Player player = Player.fromMap(entry.getKey(), (Map<String, Object>) entry.getValue());

            if (player.getUid().equals(myUid)) {
                foundMyPlayer = true;
                PlayersService.getInstance().setMyPlayer(player);
            } else {
                PlayersService.getInstance().setEnemyPlayer(player);
            }
        }

        if (!foundMyPlayer) {
            Player myPlayer = PlayersService.getInstance().getMyPlayer();
            Shape preferred = myPlayer.getPreferredShape();

            Player enemyPlayer = PlayersService.getInstance().getEnemyPlayer();
            Shape enemyShape = enemyPlayer.getPlayingAsShape();

            if (preferred.getId() == enemyShape.getId()) {
                if (preferred.getId() == Cross.ID) {
                    myPlayer.setPlayingAsShape(new Circle());
                } else {
                    myPlayer.setPlayingAsShape(new Cross());
                }
            } else {
                myPlayer.setPlayingAsShape(preferred);
            }
        }
    }

    private void mapToFields(Map<String, Object> map) {
        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
        ArrayList<Field> fields = piskvorkyService.getFieldsArray();

        if (fields == null) {
            Log.e(TAG, "mapToFields: fields are null!");
            return;
        }

        for (Field field : fields) {
            field.setShape(Shapes.NO_SHAPE);
        }

        Log.v(TAG, "mapToFields: " + map.entrySet().size());

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            int id = Integer.parseInt(entry.getKey());
            int shape = Integer.parseInt(String.valueOf(entry.getValue()));

            if (id < fields.size()) {
                Field field = fields.get(id);
                field.setShape(Shape.idToShape(shape));
            }
        }

        piskvorkyService.setFields(fields);
    }

    private OnImportFinishedListener onImportFinishedListener;
    public interface OnImportFinishedListener {
        void onSuccess();
        void onFail();
        void onComplete();
    }

    public void setOnImportFinishedListener(OnImportFinishedListener onImportFinishedListener) {
        this.onImportFinishedListener = onImportFinishedListener;
    }
}
