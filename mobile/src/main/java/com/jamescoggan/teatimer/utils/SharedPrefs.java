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
package com.jamescoggan.teatimer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

public class SharedPrefs {
    private static final String APP_PREFERENCES = "TeaPrefs";
    private static final String SAVED_TIMES = "saved-times";

    public static void addTimer(Context context, long time) {
        Timber.d("Saving time to prefs:" + String.valueOf(time));
        ArrayList<Long> timers = getTimers(context);
        timers.add(time);
        saveTimers(context, timers);
    }

    public static void removeTimer(Context context, long time) {
        Timber.d("Removing time to prefs:" + String.valueOf(time));
        ArrayList<Long> timers = getTimers(context);
        timers.remove(time);
        saveTimers(context, timers);
    }

    public static ArrayList<Long> getTimers(Context context) {
        ArrayList<Long> timers = new ArrayList<>();
        Set<String> saved = getPrefs(context).getStringSet(SAVED_TIMES, new HashSet<>());

        for (String aSaved : saved) {
            timers.add(Long.valueOf(aSaved));
        }
        Timber.d("Got " + String.valueOf(timers.size()) + " Timers");
        return timers;
    }

    private static void saveTimers(Context context, ArrayList<Long> timers) {
        Set<String> data = new HashSet<>();
        for (Long time : timers) {
            data.add(String.valueOf(time));
        }

        SharedPreferences.Editor editor = getEditor(context);
        editor.putStringSet(SAVED_TIMES, data);
        editor.commit();
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getPrefs(context).edit();
    }
}
