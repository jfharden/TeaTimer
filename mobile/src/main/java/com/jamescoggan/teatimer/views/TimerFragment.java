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
package com.jamescoggan.teatimer.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jamescoggan.teatimer.R;
import com.jamescoggan.teatimer.services.timerService;
import com.jamescoggan.teatimer.utils.DataLayer;
import com.jamescoggan.teatimer.utils.TimeHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

public class TimerFragment extends Fragment {

    @Bind(R.id.timer_text)
    TextView timerText;

    @Bind(R.id.timer_button)
    Button timerButton;

    private CompositeSubscription subscriptions;
    boolean running = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timer, container, false);
        ButterKnife.bind(this, rootView);

        timerText.setText(TimeHelper.timeInMillisToMinutesSeconds(((HomeActivity) getActivity()).getTime()));

        return rootView;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.timer_button)
    public void onTimerClick() {
        Intent serviceIntent = new Intent(getActivity(), timerService.class);
        if (running) {
            running = false;
            getActivity().stopService(serviceIntent);
        } else {
            running = true;
            serviceIntent.setAction(String.valueOf(((HomeActivity) getActivity()).getTime()));
            getActivity().startService(serviceIntent);
        }
    }

    private void timeReceived(long time) {
        if (time <= 0) {
            running = false;
            time = 0;
            timerButton.setText(getString(R.string.button_start));
        } else {
            running = true;
            timerButton.setText(getString(R.string.button_stop));
        }
        timerText.setText(TimeHelper.timeInMillisToMinutesSeconds(time));
    }

    @Override
    public void onStart() {
        super.onStart();
        subscriptions = new CompositeSubscription();
        subscriptions.add(
                DataLayer.timerToObserverable().subscribe(this::timeReceived));
    }

    @Override
    public void onStop() {
        super.onStop();
        subscriptions.unsubscribe();
    }
}
