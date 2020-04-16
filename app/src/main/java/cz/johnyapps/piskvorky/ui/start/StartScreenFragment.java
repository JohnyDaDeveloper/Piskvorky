package cz.johnyapps.piskvorky.ui.start;

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

import cz.johnyapps.piskvorky.BuildConfig;
import cz.johnyapps.piskvorky.GameModes;
import cz.johnyapps.piskvorky.R;
import cz.johnyapps.piskvorky.internet.PiskvorkyImporter;
import cz.johnyapps.piskvorky.services.PiskvorkyService;
import cz.johnyapps.piskvorky.shapes.Shapes;
import cz.johnyapps.piskvorky.ui.main.MainViewModel;

public class StartScreenFragment extends Fragment {
    private static final String TAG = "StartScreenFragment";

    private MainViewModel viewModel;

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

                PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
                piskvorkyService.setGameMode(GameModes.ONLINE);

                PiskvorkyImporter importer = new PiskvorkyImporter();
                importer.importGame(roomId);

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
