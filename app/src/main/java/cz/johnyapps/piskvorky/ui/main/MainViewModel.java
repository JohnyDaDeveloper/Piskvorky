package cz.johnyapps.piskvorky.ui.main;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cz.johnyapps.piskvorky.ui.start.StartScreenFragment;

public class MainViewModel extends ViewModel {
    private MutableLiveData<Fragment> activeFragment;

    public MainViewModel() {
        activeFragment = new MutableLiveData<>();
        activeFragment.setValue(new StartScreenFragment());
    }

    public LiveData<Fragment> getActiveFragment() {
        return activeFragment;
    }

    public void setActiveFragment(Fragment activeFragment) {
        this.activeFragment.setValue(activeFragment);
    }
}
