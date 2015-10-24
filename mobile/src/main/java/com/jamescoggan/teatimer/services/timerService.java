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
package com.jamescoggan.teatimer.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.jamescoggan.teatimer.R;
import com.jamescoggan.teatimer.utils.DataLayer;
import com.jamescoggan.teatimer.utils.TimeHelper;

import timber.log.Timber;

public class timerService extends Service {


    private Handler mHandler;
    private long currentTime = 0;
    private boolean running = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String intentAction = intent.getAction();
            if (intentAction.equals("stop")) {
                running = false;
                this.stopSelf();
            } else {
                running = true;
                Timber.d("Received timer event: " + intentAction);
                currentTime = Long.parseLong(intentAction);
                mHandler = new Handler();
                mHandler.postDelayed(mRunnable, 1000L);
            }
        }
        return START_STICKY;
    }


    private final Runnable mRunnable = () -> {
        currentTime = currentTime - 1000;
        Timber.d("Count down: " + String.valueOf(currentTime));
        if (currentTime > 0 && running) {
            DataLayer.sendTimer(currentTime);
            mHandler.postDelayed(this.mRunnable, 1000L);
            notifyUser(TimeHelper.timeInMillisToMinutesSeconds(currentTime), false);
        } else {
            DataLayer.sendTimer(0l);
            notifyUser(getString(R.string.timer_finished), true);
            running = false;
            this.stopSelf();
        }
    };

    private void notifyUser(String text, boolean vibrate) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(text);
        if (vibrate)
            mBuilder.setVibrate(new long[]{0, 200, 500, 200, 500, 200, 500, 200, 500, 200});
        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
