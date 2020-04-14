package cz.johnyapps.piskvorky.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import cz.johnyapps.piskvorky.R;
import cz.johnyapps.piskvorky.SharedPreferencesNames;
import cz.johnyapps.piskvorky.services.PiskvorkyService;
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
