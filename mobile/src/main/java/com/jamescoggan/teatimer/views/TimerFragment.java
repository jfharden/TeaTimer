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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.jamescoggan.teatimer.R;
import com.jamescoggan.teatimer.services.timerService;
import com.jamescoggan.teatimer.utils.DataLayer;
import com.jamescoggan.teatimer.utils.TimeHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class TimerFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @Bind(R.id.timer_text)
    TextView timerText;

    @Bind(R.id.timer_button)
    Button timerButton;

    private CompositeSubscription subscriptions;
    boolean running = false;
    long currentTime = 0;
    GoogleApiClient googleClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timer, container, false);
        ButterKnife.bind(this, rootView);

        currentTime = ((HomeActivity) getActivity()).getTime();
        timerText.setText(TimeHelper.timeInMillisToMinutesSeconds(currentTime));

        // Build a new GoogleApiClient that includes the Wearable API
        googleClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        return rootView;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.timer_button)
    public void onTimerClick() {
        Intent serviceIntent = new Intent(getActivity(), timerService.class);
        if (running) {
            running = false;
            serviceIntent.setAction("stop");
            getActivity().startService(serviceIntent);
        } else {
            running = true;
            serviceIntent.setAction(String.valueOf((currentTime)));
            getActivity().startService(serviceIntent);
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.timer_send_to_watch)
    public void sendToPhone() {
        new SendToDataLayerThread("/tea_timer_event", String.valueOf(currentTime)).start();
    }

    private void timeReceived(long time) {
        if (time <= 0) {
            running = false;
            time = currentTime;
            timerButton.setText(getString(R.string.button_start));
        } else {
            running = true;
            timerButton.setText(getString(R.string.button_stop));
        }
        timerText.setText(TimeHelper.timeInMillisToMinutesSeconds(time));
    }

    class SendToDataLayerThread extends Thread {
        String path;
        String message;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Timber.d("Message: {" + message + "} sent to: " + node.getDisplayName());
                } else {
                    // Log an error
                    Timber.d("ERROR: failed to send Message");
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    @Override
    public void onStart() {
        super.onStart();
        subscriptions = new CompositeSubscription();
        subscriptions.add(
                DataLayer.timerToObserverable().subscribe(this::timeReceived));
        googleClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        subscriptions.unsubscribe();
    }
}
