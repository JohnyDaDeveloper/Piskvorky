package cz.johnyapps.piskvorky.services;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import cz.johnyapps.piskvorky.entities.BoardSettings;
import cz.johnyapps.piskvorky.entities.Field;
import cz.johnyapps.piskvorky.GameModes;
import cz.johnyapps.piskvorky.internet.PiskvorkyExporter;
import cz.johnyapps.piskvorky.internet.PiskvorkyImporter;
import cz.johnyapps.piskvorky.shapes.Shapes;
import cz.johnyapps.piskvorky.shapes.shape.Shape;
import cz.johnyapps.piskvorky.shapes.shape.base.NoShape;

public class PiskvorkyService implements Shapes, GameModes {
    private static final String TAG = "PiskvorkyService";
    private static final PiskvorkyService instance = new PiskvorkyService();

    private BoardSettings boardSettings;
    private PiskvorkyImporter piskvorkyImporter;
    private PiskvorkyExporter piskvorkyExporter;

    private MutableLiveData<DocumentReference> gameReference;
    private MutableLiveData<ArrayList<Field>> fields;
    private MutableLiveData<Shape> playingPlayer;

    private Shape myPlayer;
    private String gameMode;

    private PiskvorkyService() {
        boardSettings = new BoardSettings();
        piskvorkyImporter = new PiskvorkyImporter();
        piskvorkyExporter = new PiskvorkyExporter();

        gameReference = new MutableLiveData<>();
        fields = new MutableLiveData<>();
        playingPlayer = new MutableLiveData<>();
        playingPlayer.setValue(CROSS);
    }

    public static PiskvorkyService getInstance() {
        return instance;
    }

    public Shape getMyPlayer() {
        return myPlayer;
    }

    public void setMyPlayer(Shape myPlayer) {
        this.myPlayer = myPlayer;
    }

    public CollectionReference getGamesCollection() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        return database.collection("games");
    }

    public void createOfflineGame() {
        gameMode = OFFLINE;
    }

    public void createOnlineGame() {
        gameMode = ONLINE;

        DocumentReference documentReference = piskvorkyExporter.createGame(boardSettings);
        gameReference.setValue(documentReference);
    }

    public void updateGame() {
        if (getGameMode().equals(ONLINE)) {
            piskvorkyExporter.updateGame(gameReference.getValue());
        }
    }

    public LiveData<ArrayList<Field>> getFields() {
        return fields;
    }

    public void setFields(ArrayList<Field> fields) {
        this.fields.setValue(fields);
    }

    public void removeFields() {
        this.fields.setValue(null);
    }

    public ArrayList<Field> getFieldsArray() {
        return fields.getValue();
    }

    public LiveData<Shape> getPlayingPlayer() {
        return playingPlayer;
    }

    public void setPlayingPlayer(Shape playingPlayer) {
        this.playingPlayer.setValue(playingPlayer);
    }

    public int getPlayingPlayerId() {
        Shape shape = playingPlayer.getValue();

        if (shape != null) {
            return shape.getId();
        }

        return NoShape.ID;
    }

    public Shape getPlayingPlayerShape() {
        Shape shape = playingPlayer.getValue();
        return shape == null ? NO_SHAPE : shape;
    }

    public void setFirestoreGameChangedListener() {
        DocumentReference documentReference = gameReference.getValue();

        if (documentReference == null) {
            Log.w(TAG, "setFirestoreGameChangedListener: start game first!");
            return;
        }

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    e.printStackTrace();
                    return;
                }

                piskvorkyImporter.processDocumentSnapshot(documentSnapshot);
            }
        });
    }

    public BoardSettings getBoardSettings() {
        return boardSettings;
    }

    public LiveData<DocumentReference> getGameReference() {
        return gameReference;
    }

    public void setGameReference(DocumentReference gameReference) {
        this.gameReference.setValue(gameReference);
    }

    public String getGameId() {
        DocumentReference documentReference = gameReference.getValue();

        if (documentReference == null) {
            return null;
        }

        return documentReference.getId();
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }
}
