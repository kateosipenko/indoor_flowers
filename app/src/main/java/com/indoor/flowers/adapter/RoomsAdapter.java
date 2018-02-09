package com.indoor.flowers.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.indoor.flowers.R;
import com.indoor.flowers.model.Room;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder> {

    private RoomClickListener listener;
    private List<Room> rooms = new ArrayList<>();

    private int selectedPosition = 0;

    public void setListener(RoomClickListener listener) {
        this.listener = listener;
    }

    public void setRooms(List<Room> items) {
        this.rooms.clear();
        if (items != null) {
            this.rooms.addAll(items);
        }

        notifyDataSetChanged();
    }

    public Room getSelectedRoom() {
        return selectedPosition >= 0 && selectedPosition < rooms.size()
                ? rooms.get(selectedPosition) : null;
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_room_collapsed, parent, false);
        return new RoomViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        holder.update(getItemByPosition(position));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    private Room getItemByPosition(int position) {
        return position >= 0 && position < rooms.size() ? rooms.get(position) : null;
    }

    private void setSelectedPosition(int position) {
        int old = selectedPosition;
        this.selectedPosition = position;
        notifyItemChanged(old);
        notifyItemChanged(selectedPosition);
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rrc_icon)
        ImageView iconView;

        private RoomsAdapter adapter;

        public RoomViewHolder(View itemView, RoomsAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        @OnClick(R.id.rrc_root)
        void onRootClicked() {
            Room room = adapter.getItemByPosition(getAdapterPosition());
            adapter.setSelectedPosition(getAdapterPosition());
            if (room != null && adapter.listener != null) {
                adapter.listener.onRoomClicked(room);
            }
        }

        private void update(Room room) {
            if (room.getIconRes() != -1) {
                iconView.setImageResource(room.getIconRes());
            } else {
                Picasso.with(itemView.getContext())
                        .load(new File(room.getImagePath()))
                        .into(iconView);
            }

            if (getAdapterPosition() != adapter.selectedPosition) {
                iconView.setAlpha(0.7f);
            } else {
                iconView.setAlpha(1f);
            }
        }
    }

    public interface RoomClickListener {
        void onRoomClicked(Room room);
    }
}
