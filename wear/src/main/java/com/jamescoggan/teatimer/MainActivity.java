/**
 * Created by James Coggan on 24/10/2015.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jamescoggan.teatimer;

import android.app.NotificationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jamescoggan.teatimer.utils.WearSharedPrefs;
import com.jamescoggan.teatimer.utils.WearTimeHelper;

public class MainActivity extends WearableActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private BoxInsetLayout mContainerView;
    private Button mClockView;

    private Handler mHandler;
    private long currentTime = 0;
    private boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        currentTime = WearSharedPrefs.getTime(getApplicationContext());
        setClock();
        mClockView = (Button) findViewById(R.id.clock);
        mClockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });
    }

    private void setClock() {
        if (mClockView != null) {
            mClockView.setText(WearTimeHelper.timeInMillisToMinutesSeconds(currentTime));
        }
    }

    private void startTimer() {
        if (!isAmbient()) {
            if (running) {
                running = false;
            } else {
                running = true;
                currentTime = WearSharedPrefs.getTime(getApplicationContext());
                mHandler = new Handler();
                mHandler.postDelayed(mRunnable, 1000L);
            }
        }
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            currentTime = currentTime - 1000;
            Log.d(TAG, "Count down: " + String.valueOf(currentTime));
            if (currentTime > 0 && running) {
                setClock();
                mHandler.postDelayed(mRunnable, 1000L);
            } else {
                running = false;
                notifyUser(getString(R.string.timerFinished));
                currentTime = WearSharedPrefs.getTime(getApplicationContext());
                setClock();
            }
        }
    };

    private void notifyUser(String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(text);
        int mNotificationId = 002;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{0, 200, 500, 200, 500, 200, 500, 200, 500, 200}, -1);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mClockView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mClockView.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            mClockView.setBackgroundColor(getResources().getColor(android.R.color.white));
            mClockView.setTextColor(getResources().getColor(android.R.color.black));
        }
    }
}
