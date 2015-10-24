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

public class WearSharedPrefs {
    private static final String APP_PREFERENCES = "WearTeaPrefs";
    private static final String SAVED_TIME = "wear-saved-time";

    public static void setTime(Context context, long time) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putLong(SAVED_TIME, time);
        editor.commit();
    }

    public static long getTime(Context context) {
        return getPrefs(context).getLong(SAVED_TIME, 10000l);
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getPrefs(context).edit();
    }
}
