package com.indoor.flowers.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.model.Room;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder> {

    private RoomClickListener listener;
    private List<Room> rooms = new ArrayList<>();

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

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_room, parent, false);
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

    static class RoomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rr_title)
        TextView titleView;
        @BindView(R.id.rr_data)
        TextView dataView;

        private RoomsAdapter adapter;

        public RoomViewHolder(View itemView, RoomsAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        @OnClick(R.id.rr_root)
        void onRootClicked() {
            Room room = adapter.getItemByPosition(getAdapterPosition());
            if (room != null && adapter.listener != null) {
                adapter.listener.onRoomClicked(room);
            }
        }

        private void update(Room room) {
            this.titleView.setText(room != null ? room.getName() : null);
            this.dataView.setText(room != null ? Res.getString(R.string.rr_data_format,
                    room.getTemperature(), room.getBrightness(), room.getHumidity()) : null);
        }
    }

    public interface RoomClickListener {
        void onRoomClicked(Room room);
    }
}
