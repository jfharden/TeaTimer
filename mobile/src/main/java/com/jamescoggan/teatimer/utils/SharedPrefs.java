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

import java.io.IOException;
import java.util.ArrayList;

public class SharedPrefs {
    private static final String APP_PREFERENCES = "TeaPrefs";
    private static final String SAVED_TIMES = "saved-times";

    public static void addTimer(Context context, long time) {
        ArrayList<Long> timers = getTimers(context);
        timers.add(time);
        saveTimers(context, timers);
    }

    public static void removeTimer(Context context, long time) {
        ArrayList<Long> timers = getTimers(context);
        timers.remove(time);
        saveTimers(context, timers);
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Long> getTimers(Context context) {
        ArrayList<Long> timers = new ArrayList<>();
        try {
            String defaultData = ObjectSerializer.serialize(timers);
            String savedData = getPrefs(context).getString(SAVED_TIMES, defaultData);
            Object deserializedData = ObjectSerializer.deserialize(savedData);
            timers = (ArrayList<Long>) deserializedData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return timers;
    }

    private static void saveTimers(Context context, ArrayList<Long> timers) {
        SharedPreferences.Editor editor = getEditor(context);
        try {
            editor.putString(SAVED_TIMES, ObjectSerializer.serialize(timers));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getPrefs(context).edit();
    }
}
