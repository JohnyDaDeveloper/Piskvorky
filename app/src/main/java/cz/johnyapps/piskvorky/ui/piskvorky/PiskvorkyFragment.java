package cz.johnyapps.piskvorky.ui.piskvorky;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

import cz.johnyapps.piskvorky.R;
import cz.johnyapps.piskvorky.entities.Field;
import cz.johnyapps.piskvorky.GameModes;
import cz.johnyapps.piskvorky.services.PiskvorkyService;
import cz.johnyapps.piskvorky.shapes.Shapes;
import cz.johnyapps.piskvorky.shapes.shape.Shape;
import cz.johnyapps.piskvorky.shapes.shape.base.Circle;
import cz.johnyapps.piskvorky.shapes.shape.base.Cross;
import cz.johnyapps.piskvorky.views.PiskvorkyView;

@SuppressLint("SetTextI18n")
public class PiskvorkyFragment extends Fragment implements Shapes, GameModes {
    private static final String TAG = "PiskvorkyFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_piskvorky, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setupPiskvorkyView();
        setupGame();
        setupPlayingAs();

        PiskvorkyService.getInstance().setFirestoreGameChangedListener();
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
            Shape playingAs = PiskvorkyService.getInstance().getMyPlayer();

            if (playingAs == Shapes.CROSS) {
                playingAsTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0);
            } else {
                playingAsTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.circle, 0);
            }
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
                shapeWon(shape);
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

        piskvorkyService.getPlayingPlayer().observe(this, new Observer<Shape>() {
            @Override
            public void onChanged(Shape shape) {
                if (shape != null) {
                    showPlayingShape(shape);
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

        if (documentReference == null) {
            roomIdTextView.setText(roomId + ": " + getString(R.string.no_room));
            roomIdTextView.setOnClickListener(null);
            shareRoomButton.setVisibility(View.GONE);
        } else {
            roomIdTextView.setText(roomId + ": " + documentReference.getId());
            roomIdTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = getContext();

                    if (context == null) {
                        Log.w(TAG, "setupRoomId: onClick: context is null");
                        return;
                    }

                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

                    if (clipboard == null) {
                        Log.w(TAG, "setupRoomId: onClick: clipboard is null");
                        return;
                    }

                    String roomId = getString(R.string.room_id);
                    ClipData clip = ClipData.newPlainText(roomId, PiskvorkyService.getInstance().getGameId());
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(context, R.string.coppied_to_clipboard, Toast.LENGTH_LONG).show();
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

    private void showPlayingShape(Shape shape) {
        View root = getView();

        if (root == null) {
            Log.w(TAG, "showPlayingShape: root is null!");
            return;
        }

        TextView txtPlayingPlayer = root.findViewById(R.id.txtPlayingPlayer);

        switch (shape.getId()) {
            case Cross.ID: {
                txtPlayingPlayer.setText(R.string.cross);
                break;
            }

            case Circle.ID: {
                txtPlayingPlayer.setText(R.string.circle);
                break;
            }

            default: {
                txtPlayingPlayer.setText(R.string.chyba);
                break;
            }
        }
    }

    private void shapeWon(Shape shape) {
        PiskvorkyService.getInstance().setLastGameWonShape(shape);

        View root = getView();

        if (root == null) {
            Log.w(TAG, "shapeWon: root is null!");
            return;
        }

        TextView txtPlayingPlayer = root.findViewById(R.id.txtPlayingPlayer);

        switch (shape.getId()) {
            case Cross.ID: {
                Toast.makeText(getContext(), R.string.cross_won, Toast.LENGTH_SHORT).show();
                txtPlayingPlayer.setText(R.string.cross_won);
                break;
            }

            case Circle.ID: {
                Toast.makeText(getContext(), R.string.circle_won, Toast.LENGTH_SHORT).show();
                txtPlayingPlayer.setText(R.string.circle_won);
                break;
            }

            default: {
                Toast.makeText(getContext(), R.string.chyba, Toast.LENGTH_SHORT).show();
                txtPlayingPlayer.setText(R.string.chyba);
                break;
            }
        }
    }
}
