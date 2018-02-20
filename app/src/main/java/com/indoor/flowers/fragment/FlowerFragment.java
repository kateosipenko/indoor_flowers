package com.indoor.flowers.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.evgeniysharafan.utils.Fragments;
import com.evgeniysharafan.utils.Toasts;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.EventsAdapter;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Event;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.util.FlowersAlarmsUtils;
import com.indoor.flowers.util.OnItemClickListener;
import com.indoor.flowers.util.PermissionHelper;
import com.indoor.flowers.util.PermissionUtil;
import com.indoor.flowers.util.ProgressShowingUtil;
import com.indoor.flowers.util.TakePhotoUtils;
import com.indoor.flowers.util.TakePhotoUtils.OnPhotoTakenListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

public class FlowerFragment extends Fragment implements OnPhotoTakenListener, OnItemClickListener<Event> {

    private static final String KEY_FLOWER_ID = "key_flower_id";

    @BindView(R.id.faf_flower_image)
    ImageView imageView;
    @BindView(R.id.faf_flower_name)
    EditText nameView;
    @BindView(R.id.faf_snackbar)
    CoordinatorLayout snackbarContainer;
    @BindView(R.id.faf_events_list)
    RecyclerView eventsList;
    @BindView(R.id.faf_add_event)
    Button addEventButton;
    @BindView(R.id.faf_delete)
    Button deleteButton;

    private ProgressShowingUtil progressUtil;
    private PermissionHelper permissionHelper;

    private Flower flower;
    private FlowersProvider provider;

    private EventsAdapter eventsAdapter;

    private Unbinder unbinder;

    public FlowerFragment() {
        permissionHelper = new PermissionHelper(this, 0, PermissionUtil.CAMERA_PERMISSIONS,
                new int[]{R.string.permissions_camera_required, R.string.permissions_storage_required});
    }

    public static FlowerFragment newInstance() {
        return new FlowerFragment();
    }

    public static FlowerFragment newInstance(long flowerId) {
        Bundle args = new Bundle();
        args.putLong(KEY_FLOWER_ID, flowerId);
        FlowerFragment fragment = new FlowerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressUtil = new ProgressShowingUtil(this);
        provider = new FlowersProvider(getActivity());
        long flowerId = getFlowerIdFromArgs();
        if (flowerId != DatabaseProvider.DEFAULT_ID) {
            flower = provider.getFlowerById(flowerId);
        } else {
            flower = new Flower();
            flower.setId(DatabaseProvider.DEFAULT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flower, container, false);
        unbinder = ButterKnife.bind(this, view);
        permissionHelper.setSnackbarContainer(snackbarContainer);
        setupEventsList();
        refreshViewWithFlower();
        reloadEvents();
        return view;
    }

    @Override
    public void onDestroyView() {
        progressUtil.hideProgress();
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        provider.unbind();
        super.onDestroy();
    }

    @OnClick(R.id.faf_choose_image)
    void onChooseImageClicked() {
        if (permissionHelper.hasAllPermissions()) {
            TakePhotoUtils.getInstance().showSystemChooser(this);
        } else {
            permissionHelper.checkPermissions();
        }
    }

    @OnClick(R.id.faf_save)
    void onCreateFlowerClicked() {
        if (TextUtils.isEmpty(flower.getName())) {
            Toasts.showLong(R.string.faf_error_name_empty);
            return;
        }

        provider.createOrUpdateFlower(flower);
        refreshEventsVisibility();
        refreshDeleteButtonVisibility();
    }

    @OnClick(R.id.faf_delete)
    void onDeleteFlowerClicked() {
        FlowersAlarmsUtils.deleteAlarmsForEvents(getActivity(),
                provider.getEventsForTarget(flower.getId(), Flower.TABLE_NAME));
        provider.deleteFlower(flower, true);
        getActivity().onBackPressed();
    }

    @OnClick(R.id.faf_add_event)
    void onAddEventClicked() {
        Fragments.replace(getFragmentManager(), android.R.id.content,
                EventFragment.newInstance(flower.getId(), Flower.TABLE_NAME), null, true);
    }

    @OnTextChanged(R.id.faf_flower_name)
    void onNameTextChanged(CharSequence s, int start, int before, int count) {
        flower.setName(nameView.getText().toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TakePhotoUtils.REQUEST_CODE_CAMERA:
            case TakePhotoUtils.REQUEST_CODE_GALLERY:
            case TakePhotoUtils.REQUEST_CODE_SYSTEM_CHOOSER:
                if (TakePhotoUtils.getInstance().isPhotoRequestOk(requestCode, resultCode)) {
                    progressUtil.showProgress();
                    TakePhotoUtils.getInstance().onActivityResult(requestCode, resultCode, data, this);
                }
                break;
        }
    }

    @Override
    public void onPhotoTaken(File photo) {
        flower.setImagePath(photo.getPath());
        Picasso.with(getActivity()).load(photo).into(imageView);
        progressUtil.hideProgress();
    }

    @Override
    public void onPhotoError() {
        Toasts.showLong(R.string.photo_choose_error);
        progressUtil.hideProgress();
    }

    @Override
    public void onItemClicked(Event item) {
        Fragments.replace(getFragmentManager(), android.R.id.content,
                EventFragment.newInstance(item.getId()), null, true);
    }

    private void reloadEvents() {
        List<Event> events = provider.getEventsForTarget(flower.getId(), Flower.TABLE_NAME);
        eventsAdapter.setItems(events);
    }

    private void setupEventsList() {
        if (eventsAdapter == null) {
            eventsAdapter = new EventsAdapter();
        }

        eventsAdapter.setListener(this);
        eventsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventsList.setAdapter(eventsAdapter);
    }

    private void refreshViewWithFlower() {
        if (nameView == null) {
            return;
        }

        refreshEventsVisibility();
        nameView.setText(flower.getName());
        if (!TextUtils.isEmpty(flower.getImagePath())) {
            Picasso.with(getActivity())
                    .load(new File(flower.getImagePath()))
                    .into(imageView);
        }

        refreshDeleteButtonVisibility();
    }

    private void refreshDeleteButtonVisibility() {
        if (deleteButton == null) {
            return;
        }

        deleteButton.setVisibility(flower.getId() == DatabaseProvider.DEFAULT_ID ? View.GONE : View.VISIBLE);
    }

    private void refreshEventsVisibility() {
        if (flower == null || eventsList == null) {
            return;
        }

        if (flower.getId() == DatabaseProvider.DEFAULT_ID) {
            eventsList.setVisibility(View.INVISIBLE);
            addEventButton.setVisibility(View.INVISIBLE);
        } else {
            eventsList.setVisibility(View.VISIBLE);
            addEventButton.setVisibility(View.VISIBLE);
        }
    }

    private long getFlowerIdFromArgs() {
        return getArguments() != null && getArguments().containsKey(KEY_FLOWER_ID)
                ? getArguments().getLong(KEY_FLOWER_ID, -1) : -1;
    }
}
