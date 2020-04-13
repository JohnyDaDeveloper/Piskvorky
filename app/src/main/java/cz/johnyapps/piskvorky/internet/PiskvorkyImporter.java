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
import cz.johnyapps.piskvorky.services.PiskvorkyService;
import cz.johnyapps.piskvorky.shapes.Shapes;
import cz.johnyapps.piskvorky.shapes.shape.Shape;

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

    public void processGameSnapshot(@Nullable DocumentSnapshot documentSnapshot) {
        if (documentSnapshot != null && documentSnapshot.exists()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> fields = (Map<String, Object>) documentSnapshot.get("fields");

            if (fields != null) {
                mapToFields(fields);
            } else {
                Log.e(TAG, "processDocumentSnapshot: fields were null");
            }

            int playingPlayer = Integer.parseInt(String.valueOf(documentSnapshot.get("playingPlayer")));
            boolean newGame = Boolean.parseBoolean(String.valueOf(documentSnapshot.get("newGame")));

            PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
            piskvorkyService.setNewGame(newGame);
            piskvorkyService.setPlayingPlayer(Shape.idToShape(playingPlayer));
        } else {
            Log.w(TAG, "processDocumentSnapshot: document not found");
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
}
