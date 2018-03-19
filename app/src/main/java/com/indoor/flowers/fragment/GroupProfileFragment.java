package com.indoor.flowers.fragment;


import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.evgeniysharafan.utils.Res;
import com.evgeniysharafan.utils.picasso.CircleTransformation;
import com.indoor.flowers.R;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;
import com.indoor.flowers.model.NotificationType;
import com.indoor.flowers.model.NotificationWithTarget;
import com.indoor.flowers.model.PhotoItem;
import com.indoor.flowers.util.FilesUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;

public class GroupProfileFragment extends FlowerProfileFragment {

    @BindView(R.id.ffp_back_layer_one)
    View layerOneView;
    @BindView(R.id.ffp_back_layer_two)
    View layerTwoView;
    @BindView(R.id.ffp_back_layer_three)
    View layerThreeView;
    @BindView(R.id.ffp_change_icon)
    ImageView changeIconView;

    private Group group;

    public static GroupProfileFragment newInstance(long groupId) {
        Bundle args = new Bundle();
        args.putLong(KEY_ITEM_ID, groupId);
        GroupProfileFragment fragment = new GroupProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long flowerId = getIdFromArgs();
        group = flowersProvider.getGroupById(flowerId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        layerOneView.setBackgroundTintList(ColorStateList.valueOf(Res.getColor(R.color.accent50)));
        layerTwoView.setBackgroundTintList(ColorStateList.valueOf(Res.getColor(R.color.accent200)));
        layerThreeView.setBackgroundTintList(ColorStateList.valueOf(Res.getColor(R.color.accent700)));
        changeIconView.setImageTintList(ColorStateList.valueOf(Res.getColor(R.color.primary_dark)));
        return view;
    }

    @Override
    public void onNameChanged(String name) {
        if (!TextUtils.isEmpty(name) && !name.equals(group.getName())) {
            group.setName(name);
            flowersProvider.updateGroup(group);
        }
    }

    @Override
    public void onPhotoTaken(File photo) {
        group.setImagePath(photo.getPath());
        refreshImage();
        flowersProvider.updateGroup(group);

        PhotoItem photoItem = new PhotoItem();
        String filePath = FilesUtils.copyFileForTarget(photo.getPath(), FilesUtils.DataPart.GROUPS, group.getId());
        photoItem.setImagePath(filePath);

        photoItem.setTargetId(group.getId());
        photoItem.setTargetTable(Flower.TABLE_NAME);
        photoItem.setDate(Calendar.getInstance());
        flowersProvider.addPhoto(photoItem);
        hideProgress();
    }

    @Override
    protected void refreshViewWithData() {
        if (nameView == null) {
            return;
        }

        nameView.setText(group.getName());
        if (group.getId() == DatabaseProvider.DEFAULT_ID) {
            nameView.startEditing();
        }

        refreshImage();
    }

    @Override
    protected void refreshImage() {
        if (imageView == null) {
            return;
        }

        int padding = 0;
        if (TextUtils.isEmpty(group.getImagePath())) {
            padding = Res.getDimensionPixelSize(R.dimen.ff_image_start_padding);
            imageView.setImageResource(R.drawable.ic_photo_camera);
        } else {
            Picasso.with(getActivity())
                    .load(new File(group.getImagePath()))
                    .transform(new CircleTransformation(0, 0))
                    .into(imageView);
            padding = Res.getDimensionPixelSize(R.dimen.ff_image_padding);
        }

        imageView.setPadding(padding, padding, padding, padding);
    }

    @Override
    protected void refreshStatusData() {
        NotificationWithTarget waterNotification = flowersProvider.getLastNotificationAction(
                Group.TABLE_NAME,
                group.getId(),
                NotificationType.WATERING);
        NotificationWithTarget fertilizerNotification = flowersProvider.getLastNotificationAction(
                Group.TABLE_NAME,
                group.getId(),
                NotificationType.FERTILIZER);
        updateStatusViews(waterNotification, fertilizerNotification);
    }
}
