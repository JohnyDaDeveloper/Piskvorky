package cz.johnyapps.piskvorky.ui.main;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

import cz.johnyapps.piskvorky.services.PlayersService;
import cz.johnyapps.piskvorky.ui.start.StartScreenFragment;

public class MainViewModel extends ViewModel {
    private MutableLiveData<Fragment> activeFragment;
    private MutableLiveData<FirebaseUser> firebaseUser;

    public MainViewModel() {
        activeFragment = new MutableLiveData<>();
        activeFragment.setValue(new StartScreenFragment());

        firebaseUser = new MutableLiveData<>();
    }

    public LiveData<Fragment> getActiveFragment() {
        return activeFragment;
    }

    public void setActiveFragment(Fragment activeFragment) {
        this.activeFragment.setValue(activeFragment);
    }

    public LiveData<FirebaseUser> getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            PlayersService.getInstance().setMyUid(firebaseUser.getUid());
        } else {
            PlayersService.getInstance().setMyUid(null);
        }

        this.firebaseUser.setValue(firebaseUser);
    }
}
