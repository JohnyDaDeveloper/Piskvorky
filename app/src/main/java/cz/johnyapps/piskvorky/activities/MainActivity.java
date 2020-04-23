package cz.johnyapps.piskvorky.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;

import cz.johnyapps.piskvorky.R;
import cz.johnyapps.piskvorky.SharedPreferencesNames;
import cz.johnyapps.piskvorky.entities.Player;
import cz.johnyapps.piskvorky.services.PiskvorkyService;
import cz.johnyapps.piskvorky.services.PlayersService;
import cz.johnyapps.piskvorky.shapes.Shapes;
import cz.johnyapps.piskvorky.ui.main.MainViewModel;
import cz.johnyapps.piskvorky.ui.piskvorky.PiskvorkyFragment;
import cz.johnyapps.piskvorky.ui.start.StartScreenFragment;

public class MainActivity extends AppCompatActivity implements Shapes {
    private static final String TAG = "MainActivity";

    private MainViewModel viewModel;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(SharedPreferencesNames.NAME, Context.MODE_PRIVATE);

        loadCachedData();
        setContentView(R.layout.activity_main);
        setupViewModel();

        loadUserData(null);
        anonymousLogin();
    }

    private void loadUserData(String uid) {
        String playerJson = prefs.getString(SharedPreferencesNames.MY_PLAYER, null);

        if (playerJson != null) {
            try {
                Player player = Player.fromJSONString(playerJson, uid);
                PlayersService.getInstance().setMyPlayer(player);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Player player = new Player(uid, Shapes.CROSS);
            PlayersService.getInstance().setMyPlayer(player);

            try {
                prefs.edit().putString(SharedPreferencesNames.MY_PLAYER, player.toJSONString()).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void anonymousLogin() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            firebaseAuth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "anonymousLogin: success");
                                handleUser(FirebaseAuth.getInstance().getCurrentUser());
                            } else {
                                Log.w(TAG, "anonymousLogin: failure");
                                handleUser(null);
                            }
                        }
                    });
        } else {
            handleUser(user);
        }
    }

    private void handleUser(FirebaseUser user) {
        Log.v(TAG, "handleUser: " + (user == null ? "null" : user.getUid()));
        viewModel.setFirebaseUser(user);

        if (user != null) {
            loadUserData(user.getUid());
        }
    }

    private void loadCachedData() {
        PiskvorkyService.getInstance().setHighlightLastMove(prefs.getBoolean(SharedPreferencesNames.HIGHLIGHT_LAST_MOVE, true));
    }

    private void setupViewModel() {
        ViewModelProvider provider = new ViewModelProvider(this);
        viewModel = provider.get(MainViewModel.class);

        viewModel.getActiveFragment().observe(this, new Observer<Fragment>() {
            @Override
            public void onChanged(Fragment fragment) {
                switchFragment(fragment);
            }
        });
    }

    private void switchFragment(Fragment fragment) {
        Log.d(TAG, "switchFragment: " + fragment);

        if (fragment instanceof StartScreenFragment) {
            loadStartFragment((StartScreenFragment) fragment);
        } else if (fragment instanceof PiskvorkyFragment) {
            loadPiskvorkyFragment((PiskvorkyFragment) fragment);
        } else {
            viewModel.setActiveFragment(new StartScreenFragment());

            if (fragment != null) {
                Log.e(TAG, "switchFragment: unknown fragment, switching to default");
            }
        }
    }

    private void loadStartFragment(StartScreenFragment startScreenFragment) {
        startScreenFragment.setOnShowGameListener(new StartScreenFragment.OnShowGameListener() {
            @Override
            public void show() {
                loadPiskvorkyFragment(new PiskvorkyFragment());
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, startScreenFragment);
        transaction.commit();
    }

    private void loadPiskvorkyFragment(PiskvorkyFragment piskvorkyFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, piskvorkyFragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
}
