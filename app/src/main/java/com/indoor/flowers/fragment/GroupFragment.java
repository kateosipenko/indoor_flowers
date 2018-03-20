package com.indoor.flowers.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.evgeniysharafan.utils.Fragments;
import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.NotificationsProvider;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.util.FilesUtils;
import com.indoor.flowers.util.FlowersAlarmsUtils;

public class GroupFragment extends FlowerFragment {

    private static final String KEY_GROUP_ID = "key_group_id";
    private static final String KEY_FLOWER_ID = "key_flower_id";

    private Group group;

    public static GroupFragment newInstance() {
        return new GroupFragment();
    }

    public static GroupFragment newInstance(long groupId) {
        Bundle args = new Bundle();
        args.putLong(KEY_GROUP_ID, groupId);

        GroupFragment fragment = new GroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static GroupFragment newInstanceForFlower(long flowerId) {
        Bundle args = new Bundle();
        args.putLong(KEY_FLOWER_ID, flowerId);

        GroupFragment fragment = new GroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long groupId = getArguments() != null && getArguments().containsKey(KEY_GROUP_ID)
                ? getArguments().getLong(KEY_GROUP_ID, DatabaseProvider.DEFAULT_ID)
                : DatabaseProvider.DEFAULT_ID;
        group = provider.getGroupById(groupId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Window window = getActivity().getWindow();
        window.setStatusBarColor(Res.getColor(R.color.accent_dark));
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.menu_group_bottom);
        bottomNavigationView.setItemIconTintList(ColorStateList.valueOf(Res.getColor(R.color.accent)));
        bottomNavigationView.setItemTextColor(Res.getColorStateList(R.color.sel_bottom_menu_accent_text));
        toolbar.setBackgroundColor(Res.getColor(R.color.accent700));

        if (savedInstanceState == null) {
            navigateToMenuItem(bottomNavigationView.getSelectedItemId());
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        Window window = getActivity().getWindow();
        window.setStatusBarColor(Res.getColor(R.color.primary_dark));
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (group != null && group.getId() != DatabaseProvider.DEFAULT_ID) {
            inflater.inflate(R.menu.menu_group, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mg_delete) {
            NotificationsProvider notificationsProvider = new NotificationsProvider(getActivity());
            FlowersAlarmsUtils.deleteAlarmsForEvents(getActivity(),
                    notificationsProvider.getEventsForTarget(group.getId(), Group.TABLE_NAME));
            FilesUtils.deleteDataForTarget(FilesUtils.DataPart.GROUPS, group.getId());
            provider.deleteGroup(group);
            getActivity().onBackPressed();
        }

        return true;
    }

    @Override
    public void onNameChanged(String name) {
        if (!TextUtils.isEmpty(name) && !name.equals(group.getName())) {
            group.setName(name);
            provider.updateGroup(group);
        }
    }

    @Override
    protected void navigateToMenuItem(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.mgb_status:
                fragment = GroupProfileFragment.newInstance(group.getId());
                break;
            case R.id.mgb_gallery:
                fragment = TargetGalleryFragment.newInstance(group.getId(), Group.TABLE_NAME);
                break;
            case R.id.mgb_notifications:
                fragment = TargetNotificationsFragment.newInstance(group.getId(), Group.TABLE_NAME);
                break;
            case R.id.mgb_flowers:
                fragment = GroupFlowersFragment.newInstance(group.getId());
                break;
        }

        if (fragment != null) {
            Fragments.replace(getChildFragmentManager(), R.id.ff_container, fragment, null);
        }
    }

    @Override
    protected void refreshName() {
        if (nameView == null) {
            return;
        }

        nameView.setText(group.getName());
        if (group.getId() == DatabaseProvider.DEFAULT_ID) {
            nameView.startEditing();
        }
    }
}
