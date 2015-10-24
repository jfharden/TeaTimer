package com.jamescoggan.teatimer.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.jamescoggan.teatimer.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class HomeActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    @Bind(R.id.container)
    FrameLayout container;

    private long currentTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        loadTimerList();
    }

    private void loadTimerList() {
        loadFragment(new HomeActivityFragment());
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitAllowingStateLoss();
    }

    public void loadTimer(long time) {
        Timber.d("Loading timer with " + String.valueOf(time));
        currentTime = time;
        loadFragment(new TimerFragment());
    }

    public long getTime() {
        return currentTime;
    }
}
