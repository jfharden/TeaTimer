package com.jamescoggan.teatimer.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jamescoggan.teatimer.R;
import com.jamescoggan.teatimer.utils.DataLayer;
import com.jamescoggan.teatimer.utils.RecyclerItemClickListener;
import com.jamescoggan.teatimer.utils.SharedPrefs;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeActivityFragment extends Fragment {

    @Bind(R.id.timer_list)
    RecyclerView mList;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    private CompositeSubscription subscriptions;
    RecyclerView.LayoutManager mLayoutManager;
    TeaTimerAdapter mAdapter;
    ArrayList<Long> itemList;

    public HomeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, rootView);

        itemList = SharedPrefs.getTimers(getContext());

        mLayoutManager = new LinearLayoutManager(getActivity());

        mAdapter = new TeaTimerAdapter(itemList);

        mList.setHasFixedSize(true);
        mList.setAdapter(mAdapter);
        mList.setLayoutManager(mLayoutManager);
        mList.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getBaseContext(), (view, position) -> {
                    Timber.d("Clicked" + String.valueOf(position));
                })
        );


        fab.setOnClickListener(view -> {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int item) {
                long time = mAdapter.getItemTime(viewHolder.getAdapterPosition());
                SharedPrefs.removeTimer(getContext(), time);
                itemList = SharedPrefs.getTimers(getContext());
                mAdapter.setItemList(itemList);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mList);

        return rootView;
    }

    private void addTime(Long time) {
        SharedPrefs.addTimer(getContext(), time);
        itemList = SharedPrefs.getTimers(getContext());
        mAdapter.setItemList(itemList);

        if (this.getView() != null) {
            Snackbar.make(this.getView(), "Time added to list", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        subscriptions = new CompositeSubscription();
        subscriptions.add(
                DataLayer.toObserverable().subscribe(this::addTime));
    }

    @Override
    public void onStop() {
        super.onStop();
        subscriptions.unsubscribe();
    }
}
