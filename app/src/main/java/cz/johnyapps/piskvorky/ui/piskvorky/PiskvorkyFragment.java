package cz.johnyapps.piskvorky.ui.piskvorky;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

import cz.johnyapps.piskvorky.R;
import cz.johnyapps.piskvorky.SharedPreferencesNames;
import cz.johnyapps.piskvorky.entities.Field;
import cz.johnyapps.piskvorky.GameModes;
import cz.johnyapps.piskvorky.entities.Player;
import cz.johnyapps.piskvorky.entities.shapes.shape.custom.Paw;
import cz.johnyapps.piskvorky.services.PiskvorkyService;
import cz.johnyapps.piskvorky.services.PlayersService;
import cz.johnyapps.piskvorky.entities.shapes.Shapes;
import cz.johnyapps.piskvorky.entities.shapes.shape.Shape;
import cz.johnyapps.piskvorky.entities.shapes.shape.shapes.Circle;
import cz.johnyapps.piskvorky.entities.shapes.shape.shapes.Cross;
import cz.johnyapps.piskvorky.entities.shapes.shape.custom.Hearth;
import cz.johnyapps.piskvorky.views.PiskvorkyView;

@SuppressLint("SetTextI18n")
public class PiskvorkyFragment extends Fragment implements Shapes, GameModes {
    private static final String TAG = "PiskvorkyFragment";

    private SharedPreferences prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = requireContext().getSharedPreferences(SharedPreferencesNames.NAME, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_piskvorky, container, false);

        setupHighlightLastMove(root);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupPiskvorkyView();
        setupGame();
        setupPlayingAs();

        if (PiskvorkyService.getInstance().getGameMode().equals(GameModes.ONLINE)) {
            waitForEnemy();
        }

        PiskvorkyService.getInstance().setFirestoreGameChangedListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
        if (piskvorkyService.amIHost()) {
            piskvorkyService.destroyGame();
        }
    }

    private void waitForEnemy() {
        View root = getView();

        if (root == null) {
            Log.w(TAG, "waitForEnemy: root is null!");
            return;
        }

        Player enemyPlayer = PlayersService.getInstance().getEnemyPlayer();

        if (enemyPlayer == null) {
            final PiskvorkyView piskvorkyView = root.findViewById(R.id.board);
            piskvorkyView.waitForOpponent();

            PlayersService.getInstance().setOnEnemyPlayerChangedListener(new PlayersService.OnEnemyPlayerChangedListener() {
                @Override
                public void onChange(Player enemyPlayer) {
                    if (enemyPlayer != null) {
                        piskvorkyView.opponentJoined();
                    }
                }
            });
        }
    }

    private void setupHighlightLastMove(@NonNull View root) {
        Switch highlightLastSwitch = root.findViewById(R.id.highlightLastSwitch);
        highlightLastSwitch.setChecked(PiskvorkyService.getInstance().getHighlightLastMove());
        highlightLastSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(SharedPreferencesNames.HIGHLIGHT_LAST_MOVE, b).apply();
                PiskvorkyService.getInstance().setHighlightLastMove(b);
            }
        });
    }

    private void setupPlayingAs() {
        View root = getView();

        if (root == null) {
            Log.w(TAG, "setupPlayingAs: root is null!");
            return;
        }

        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
        TextView playingAsTextView = root.findViewById(R.id.playingAsTextView);

        if (piskvorkyService.getGameMode().equals(OFFLINE)) {
            playingAsTextView.setVisibility(View.GONE);
        } else {
            playingAsTextView.setVisibility(View.VISIBLE);
        }

        PlayersService.getInstance().setOnMyPlayerChangedListener(new PlayersService.OnMyPlayerChangedListener() {
            @Override
            public void onChange(Player myPlayer) {
                showPlayingAs();
            }
        });
    }

    private void showPlayingAs() {
        View root = getView();

        if (root == null) {
            Log.w(TAG, "showPlayingAs: root is null!");
            return;
        }

        TextView playingAsTextView = root.findViewById(R.id.playingAsTextView);

        Shape playingAs = PlayersService.getInstance().getMyPlayer().getPlayingAsShape();
        int drawable = playingAs.getDrawable();

        if (drawable != -1) {
            playingAsTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
        }
    }

    private void setupPiskvorkyView() {
        View root = getView();

        if (root == null) {
            Log.w(TAG, "setupPiskvorkyView: root is null!");
            return;
        }

        PiskvorkyView piskvorkyView = root.findViewById(R.id.board);
        piskvorkyView.setOnShapeWonListener(new PiskvorkyView.OnShapeWonListener() {
            @Override
            public void onShapeWon(Shape shape) {
                playerWon(PlayersService.getInstance().getPlayerByShape(shape));
            }
        });
    }

    private void setupGame() {
        final View root = getView();

        if (root == null) {
            Log.w(TAG, "setupGame: root is null!");
            return;
        }

        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
        PlayersService playersService = PlayersService.getInstance();

        playersService.getPlayingPlayer().observe(this, new Observer<Player>() {
            @Override
            public void onChanged(Player player) {
                if (player != null) {
                    showPlayingShape(player.getPlayingAsShape());
                } else {
                    showPlayingShape(NO_SHAPE);
                }
            }
        });

        piskvorkyService.getFields().observe(this, new Observer<ArrayList<Field>>() {
            @Override
            public void onChanged(ArrayList<Field> fields) {
                PiskvorkyView piskvorkyView = root.findViewById(R.id.board);
                piskvorkyView.invalidate();
            }
        });

        if (piskvorkyService.getGameMode().equals(OFFLINE)) {
            setupRoomId(null);
        } else {
            piskvorkyService.getGameReference().observe(this, new Observer<DocumentReference>() {
                @Override
                public void onChanged(DocumentReference documentReference) {
                    setupRoomId(documentReference);
                }
            });
        }
    }

    private void setupRoomId(DocumentReference documentReference) {
        View root = getView();

        if (root == null) {
            Log.w(TAG, "setupRoomId: root is null!");
            return;
        }

        TextView roomIdTextView = root.findViewById(R.id.roomIdTextView);
        View shareRoomButton = root.findViewById(R.id.shareRoomButton);
        String roomId = getString(R.string.room_id);

        if (PiskvorkyService.getInstance().getGameMode().equals(GameModes.OFFLINE)) {
            roomIdTextView.setVisibility(View.GONE);
            shareRoomButton.setVisibility(View.GONE);
        } else if (documentReference == null) {
            roomIdTextView.setText(roomId + ": " + getString(R.string.no_room));
            roomIdTextView.setOnClickListener(null);
            roomIdTextView.setVisibility(View.VISIBLE);
            shareRoomButton.setVisibility(View.GONE);
        } else {
            roomIdTextView.setVisibility(View.VISIBLE);
            roomIdTextView.setText(roomId + ": " + documentReference.getId());
            roomIdTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    copyRoomIdToClipboard();
                }
            });

            shareRoomButton.setVisibility(View.VISIBLE);
            shareRoomButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, PiskvorkyService.getInstance().getGameId());
                    sendIntent.setType("text/plain");

                    String roomId = getString(R.string.room_id);
                    Intent shareIntent = Intent.createChooser(sendIntent, roomId);
                    startActivity(shareIntent);
                }
            });
        }
    }

    private void copyRoomIdToClipboard() {
        View root = getView();

        if (root == null) {
            Log.w(TAG, "copyRoomIdToClipboard: root is null!");
            return;
        }

        Context context = getContext();

        if (context == null) {
            Log.w(TAG, "copyRoomIdToClipboard: onClick: context is null");
            return;
        }

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard == null) {
            Log.w(TAG, "copyRoomIdToClipboard: onClick: clipboard is null");
            return;
        }

        String roomId = getString(R.string.room_id);
        ClipData clip = ClipData.newPlainText(roomId, PiskvorkyService.getInstance().getGameId());
        clipboard.setPrimaryClip(clip);

        Snackbar.make(root, R.string.copied_to_clipboard, Snackbar.LENGTH_LONG).show();
    }

    private void showPlayingShape(Shape shape) {
        Context context = getContext();

        if (context == null) {
            Log.w(TAG, "showPlayingShape: context is null");
            return;
        }

        View root = getView();

        if (root == null) {
            Log.w(TAG, "showPlayingShape: root is null!");
            return;
        }

        TextView txtPlayingPlayer = root.findViewById(R.id.txtPlayingPlayer);

        int drawable;

        switch (shape.getId()) {
            case Cross.ID: {
                drawable = Shapes.CROSS.getDrawable();
                break;
            }

            case Circle.ID: {
                drawable = Shapes.CIRCLE.getDrawable();
                break;
            }

            case Hearth.ID: {
                drawable = Shapes.HEARTH.getDrawable();
                break;
            }

            case Paw.ID: {
                drawable = Shapes.PAW.getDrawable();
                break;
            }

            default: {
                drawable = Shape.NO_SHAPE.getDrawable();
                break;
            }
        }

        txtPlayingPlayer.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
    }

    private void playerWon(Player player) {
        PlayersService.getInstance().setLastGameWon(player);

        View root = getView();

        if (root == null) {
            Log.w(TAG, "shapeWon: root is null!");
            return;
        }

        TextView txtPlayingPlayer = root.findViewById(R.id.txtPlayingPlayer);

        if (player == null) {
            Log.e(TAG, "playerWon: unknown player");
            Toast.makeText(getContext(), R.string.unknown_won, Toast.LENGTH_SHORT).show();
            txtPlayingPlayer.setText(R.string.unknown_won);
            return;
        }

        Shape shape = player.getPlayingAsShape();
        switch (shape.getId()) {
            case Cross.ID: {
                txtPlayingPlayer.setText(R.string.cross_won);
                break;
            }

            case Circle.ID: {
                txtPlayingPlayer.setText(R.string.circle_won);
                break;
            }

            case Hearth.ID: {
                txtPlayingPlayer.setText(R.string.heart_won);
                break;
            }

            case Paw.ID: {
                txtPlayingPlayer.setText(R.string.paws_won);
                break;
            }

            default: {
                txtPlayingPlayer.setText(R.string.unknown_won);
                break;
            }
        }

        txtPlayingPlayer.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
}
