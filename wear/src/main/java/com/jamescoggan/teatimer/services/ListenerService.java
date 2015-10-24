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

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.jamescoggan.teatimer.utils.WearSharedPrefs;

public class ListenerService extends WearableListenerService {
    private final String TAG = ListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals("/tea_timer_event")) {
            final String message = new String(messageEvent.getData());
            Log.d(TAG, "Message received on watch is: " + message);
            long time = Long.parseLong(message);
            WearSharedPrefs.setTime(getApplicationContext(), time);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
