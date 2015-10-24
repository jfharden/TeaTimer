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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jamescoggan.teatimer.R;

import java.util.ArrayList;

public class TeaTimerAdapter extends RecyclerView.Adapter<TeaTimerAdapter.ViewHolder> {

    private ArrayList<Long> itemList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView time;

        public ViewHolder(View itemView, int ViewType) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.list_item_time);
        }
    }

    // Constructor
    public TeaTimerAdapter(ArrayList<Long> itemList) {
        this.itemList = itemList;
    }

    public void setItemList(ArrayList<Long> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @Override
    public TeaTimerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.teatimer_list_item, parent, false);
        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(final TeaTimerAdapter.ViewHolder holder, int position) {
        holder.time.setText(String.valueOf(itemList.get(position)));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public long getItemTime(int position) {
        return itemList.get(position);
    }
}
