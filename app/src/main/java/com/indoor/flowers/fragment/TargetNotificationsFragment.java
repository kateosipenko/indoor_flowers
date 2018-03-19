package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Fragments;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.EventsAdapter;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.NotificationsProvider;
import com.indoor.flowers.model.Notification;
import com.indoor.flowers.util.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TargetNotificationsFragment extends Fragment implements OnItemClickListener<Notification> {

    private static final String KEY_TARGET_ID = "key_target_id";
    private static final String KEY_TARGET_TABLE = "key_target_table";

    @BindView(R.id.fl_list)
    RecyclerView listView;

    private Unbinder unbinder;
    private EventsAdapter eventsAdapter;

    private NotificationsProvider notificationsProvider;

    public static TargetNotificationsFragment newInstance(long targetId, String targetTable) {
        Bundle args = new Bundle();
        args.putLong(KEY_TARGET_ID, targetId);
        args.putString(KEY_TARGET_TABLE, targetTable);

        TargetNotificationsFragment fragment = new TargetNotificationsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationsProvider = new NotificationsProvider(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (eventsAdapter == null) {
            eventsAdapter = new EventsAdapter();
        }

        eventsAdapter.setListener(this);
        listView.setAdapter(eventsAdapter);
        reloadEvents();
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onItemClicked(Notification item) {
        Fragments.replace(getNavigationFragmentManager(), android.R.id.content,
                NotificationFragment.newInstance(item.getId()), null, true);
    }

    @OnClick(R.id.fl_action_add)
    void onAddEventClicked() {
        Fragments.replace(getNavigationFragmentManager(), android.R.id.content,
                NotificationFragment.newInstance(getTargetId(), getTargetTable()),
                null, true);
    }

    private FragmentManager getNavigationFragmentManager() {
        return getParentFragment() != null ? getParentFragment().getFragmentManager()
                : getFragmentManager();
    }

    private void reloadEvents() {
        List<Notification> events = notificationsProvider.getEventsForTarget(getTargetId(),
                getTargetTable());
        eventsAdapter.setItems(events);
    }

    private long getTargetId() {
        Bundle args = getArguments();
        return args != null && args.containsKey(KEY_TARGET_ID)
                ? args.getLong(KEY_TARGET_ID, DatabaseProvider.DEFAULT_ID)
                : DatabaseProvider.DEFAULT_ID;
    }

    private String getTargetTable() {
        Bundle args = getArguments();
        return args != null && args.containsKey(KEY_TARGET_TABLE)
                ? args.getString(KEY_TARGET_TABLE)
                : null;
    }
}
