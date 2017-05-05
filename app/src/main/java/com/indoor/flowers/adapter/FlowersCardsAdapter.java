package com.indoor.flowers.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.indoor.flowers.R;
import com.indoor.flowers.util.CursorRecyclerAdapter;

import butterknife.ButterKnife;

public class FlowersCardsAdapter extends CursorRecyclerAdapter<FlowersCardsAdapter.ViewHolder> {

    @Override
    public void onBindViewHolderCursor(ViewHolder holder, Cursor cursor) {
        holder.update(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_flower_card, parent, false);
        return new ViewHolder(view);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void update(Cursor cursor) {

        }
    }
}
