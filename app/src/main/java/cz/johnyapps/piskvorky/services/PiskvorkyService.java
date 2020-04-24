package cz.johnyapps.piskvorky.services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import cz.johnyapps.piskvorky.entities.Player;
import cz.johnyapps.piskvorky.internet.PiskvorkyExporter;
import cz.johnyapps.piskvorky.internet.PiskvorkyImporter;
import cz.johnyapps.piskvorky.entities.shapes.Shapes;
import cz.johnyapps.piskvorky.entities.shapes.shape.shapes.Circle;
import cz.johnyapps.piskvorky.entities.shapes.shape.shapes.Cross;

public class PiskvorkyService implements Shapes, GameModes {
    private static final String TAG = "PiskvorkyService";
    private static final PiskvorkyService instance = new PiskvorkyService();

    private BoardSettings boardSettings;
    private PiskvorkyImporter piskvorkyImporter;
    private PiskvorkyExporter piskvorkyExporter;

    private MutableLiveData<DocumentReference> gameReference;
    private MutableLiveData<ArrayList<Field>> fields;

    private String gameMode;
    private boolean amIHost;
    private int lastMoveIndex;
    private boolean highlightLastMove;

    public static PiskvorkyService getInstance() {
        return instance;
    }

    private PiskvorkyService() {
        boardSettings = new BoardSettings();
        piskvorkyImporter = new PiskvorkyImporter();
        piskvorkyExporter = new PiskvorkyExporter();

        gameReference = new MutableLiveData<>();
        fields = new MutableLiveData<>();
        amIHost = false;
        lastMoveIndex = -1;
        highlightLastMove = true;
    }

    private OnHighlightLastMoveChangedListener onHighlightLastMoveChangedListener;
    public interface OnHighlightLastMoveChangedListener {
        void onChange(boolean highlight);
    }

    public void setOnHighlightLastMoveChangedListener(OnHighlightLastMoveChangedListener onHighlightLastMoveChangedListener) {
        this.onHighlightLastMoveChangedListener = onHighlightLastMoveChangedListener;
    }

    public boolean getHighlightLastMove() {
        return highlightLastMove;
    }

    public void setHighlightLastMove(boolean highlightLastMove) {
        this.highlightLastMove = highlightLastMove;

        if (onHighlightLastMoveChangedListener != null) {
            onHighlightLastMoveChangedListener.onChange(highlightLastMove);
        }
    }

    public int getLastMoveIndex() {
        return lastMoveIndex;
    }

    public void setLastMoveIndex(int lastMoveIndex) {
        this.lastMoveIndex = lastMoveIndex;
    }

    public boolean amIHost() {
        return amIHost;
    }

    public void  destroyGame() {
        DocumentReference game = gameReference.getValue();

        if (game != null) {
            final String gameId = game.getId();

            game.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "destroyGame: game " + gameId + " was " +
                                    (task.isSuccessful() ? "deleted successfully" : "not deleted"));
                        }
                    });
        }
    }

    public void setNewGame(boolean newGame) {
        if (newGame && onNewGameListener != null) {
            onNewGameListener.onNewGame();
        }
    }

    private OnNewGameListener onNewGameListener;
    public interface OnNewGameListener {
        void onNewGame();
    }

    public void setOnNewGameListener(OnNewGameListener onNewGameListener) {
        this.onNewGameListener = onNewGameListener;
    }

    public CollectionReference getGamesCollection() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        return database.collection("games");
    }

    public void createOfflineGame() {
        setGameMode(OFFLINE);
        amIHost = true;

        Player player1 = new Player("0", new Cross());
        player1.setPlayingAsShape(player1.getPreferredShape());
        Player player2 = new Player("1", new Circle());
        player2.setPlayingAsShape(player2.getPreferredShape());

        PlayersService.getInstance().setMyPlayer(player1);
        PlayersService.getInstance().setEnemyPlayer(player2);

        PlayersService.getInstance().setPlayingPlayer(player1);
    }

    public void createOnlineGame() {
        setGameMode(ONLINE);
        amIHost = true;

        Player myPlayer = PlayersService.getInstance().getMyPlayer();
        myPlayer.setPlayingAsShape(myPlayer.getPreferredShape());
        PlayersService.getInstance().setMyPlayer(myPlayer);

        DocumentReference documentReference = piskvorkyExporter.createGame(boardSettings);
        gameReference.setValue(documentReference);
    }

    public void joinOnlineGame(String roomId) {
        setGameMode(GameModes.ONLINE);
        amIHost = false;

        PiskvorkyImporter importer = new PiskvorkyImporter();
        importer.setOnImportFinishedListener(new PiskvorkyImporter.OnImportFinishedListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "joinOnlineGame: onSuccess");

                PiskvorkyExporter piskvorkyExporter = new PiskvorkyExporter();
                piskvorkyExporter.updateGame(gameReference.getValue(), true);
            }

            @Override
            public void onFail() {
                Log.w(TAG, "joinOnlineGame: onFail");
            }

            @Override
            public void onComplete() {

            }
        });

        importer.importGame(roomId);
    }

    public void updateGame(boolean newGame) {
        if (getGameMode().equals(ONLINE)) {
            piskvorkyExporter.updateGame(gameReference.getValue(), newGame);
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

    public Field getField(int i) {
        ArrayList<Field> fields = this.fields.getValue();

        if (fields == null || i < 0 || i > fields.size() - 1) {
            return null;
        }

        return fields.get(i);
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

                piskvorkyImporter.processGameSnapshot(documentSnapshot);
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
