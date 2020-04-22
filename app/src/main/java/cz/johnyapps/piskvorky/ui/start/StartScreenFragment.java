package cz.johnyapps.piskvorky.ui.start;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;

import cz.johnyapps.piskvorky.BuildConfig;
import cz.johnyapps.piskvorky.ChooseShapeDialog;
import cz.johnyapps.piskvorky.R;
import cz.johnyapps.piskvorky.SharedPreferencesNames;
import cz.johnyapps.piskvorky.entities.Player;
import cz.johnyapps.piskvorky.services.PiskvorkyService;
import cz.johnyapps.piskvorky.services.PlayersService;
import cz.johnyapps.piskvorky.shapes.shape.Shape;
import cz.johnyapps.piskvorky.ui.main.MainViewModel;

public class StartScreenFragment extends Fragment {
    private static final String TAG = "StartScreenFragment";

    private MainViewModel viewModel;
    private SharedPreferences prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getContext();
        assert context != null;
        prefs = context.getSharedPreferences(SharedPreferencesNames.NAME, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_screen, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setupViewModel();
        setupJoinButton();
        setupCreateButton();
        setupOfflineButton();
        setupVersion();
        setupChangePreferredShapeButton();
    }

    private void setupViewModel() {
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        viewModel = provider.get(MainViewModel.class);

        viewModel.getFirebaseUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                handleUser(firebaseUser);
            }
        });
    }

    private void setupChangePreferredShapeButton() {
        View root = getView();

        if (root == null) {
            Log.w(TAG, "setupChangePreferredShapeButton: root is null");
            return;
        }

        Player myPlayer = PlayersService.getInstance().getMyPlayer();
        int drawable = myPlayer.getPreferredShape().getDrawable();

        TextView shapeTextView = root.findViewById(R.id.preferredShapeTextView);
        shapeTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);

        Button changeButton = root.findViewById(R.id.changePreferredShapeButton);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick");

                ChooseShapeDialog chooseShapeDialog = new ChooseShapeDialog(getContext());
                chooseShapeDialog.setOnShapeSelectedListener(new ChooseShapeDialog.OnShapeSelectedListener() {
                    @Override
                    public void onShape(Shape shape) {
                        changePreferredShape(shape);
                    }
                });

                chooseShapeDialog.show();
            }
        });
    }

    private void changePreferredShape(Shape shape) {
        View root = getView();

        if (root == null) {
            Log.w(TAG, "changePreferredShape: root is null");
            return;
        }

        TextView shapeTextView = root.findViewById(R.id.preferredShapeTextView);

        Player myPlayer = PlayersService.getInstance().getMyPlayer();
        myPlayer.setPreferredShape(shape);
        PlayersService.getInstance().setMyPlayer(myPlayer);

        int drawable = myPlayer.getPreferredShape().getDrawable();
        shapeTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);

        try {
            prefs.edit().putString(SharedPreferencesNames.MY_PLAYER, myPlayer.toJSONString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleUser(FirebaseUser firebaseUser) {
        View root = getView();

        if (root == null) {
            Log.w(TAG, "handleUser: root is null");
            return;
        }

        TextView txtUid = root.findViewById(R.id.uid);
        Button joinButton = root.findViewById(R.id.joinRoomButtom);
        Button createButton = root.findViewById(R.id.createRoomButton);
        Button offlineButton = root.findViewById(R.id.playOfflineButton);

        if (firebaseUser == null) {
            txtUid.setText(null);
            joinButton.setEnabled(false);
            createButton.setEnabled(false);
            offlineButton.setEnabled(false);
        } else {
            txtUid.setText(firebaseUser.getUid());
            joinButton.setEnabled(true);
            createButton.setEnabled(true);
            offlineButton.setEnabled(true);
        }
    }

    private void  setupVersion() {
        View root = getView();

        if (root == null) {
            Log.w(TAG, "setupVersion: root is null!");
            return;
        }

        TextView version = root.findViewById(R.id.version);
        version.setText(BuildConfig.VERSION_NAME);
    }

    private void setupOfflineButton() {
        View root = getView();

        if (root == null) {
            Log.w(TAG, "setupOfflineButton: root is null!");
            return;
        }

        Button offlineButton = root.findViewById(R.id.playOfflineButton);
        offlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
                piskvorkyService.createOfflineGame();

                showGame();
            }
        });
    }

    private void setupCreateButton() {
        View root = getView();

        if (root == null) {
            Log.w(TAG, "setupCreateButton: root is null!");
            return;
        }

        Button createButton = root.findViewById(R.id.createRoomButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
                piskvorkyService.createOnlineGame();

                showGame();
            }
        });
    }

    private void setupJoinButton() {
        View root = getView();

        if (root == null) {
            Log.w(TAG, "setupJoinButton: root is null!");
            return;
        }

        Button joinButton = root.findViewById(R.id.joinRoomButtom);
        final EditText roomIdEditText = root.findViewById(R.id.roomIdExitText);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomId = roomIdEditText.getText().toString();

                if (roomId.isEmpty()) {
                    Log.i(TAG, "setupJoinButton: onClick: ivalid room ID");
                    return;
                }

                PiskvorkyService.getInstance().joinOnlineGame(roomId);
                showGame();
            }
        });
    }

    private void showGame() {
        if (onShowGameListener != null) {
            onShowGameListener.show();
        }
    }

    private OnShowGameListener onShowGameListener;
    public interface OnShowGameListener {
        void show();
    }

    public void setOnShowGameListener(OnShowGameListener onShowGameListener) {
        this.onShowGameListener = onShowGameListener;
    }
}
