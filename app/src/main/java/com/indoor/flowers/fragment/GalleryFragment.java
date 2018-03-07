package com.indoor.flowers.fragment;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.evgeniysharafan.utils.Res;
import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.GalleryPagerAdapter;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.PhotoItem;
import com.indoor.flowers.util.FilesUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GalleryFragment extends Fragment implements OnPageChangeListener {

    private static final String KEY_TARGET_TABLE = "key_target_table";
    private static final String KEY_TARGET_ID = "key_target_id";
    private static final String KEY_SELECTED_ID = "key_selected_id";

    @BindView(R.id.fg_toolbar)
    Toolbar toolbar;
    @BindView(R.id.fg_data_pager)
    ViewPager dataPager;
    @BindView(R.id.fg_date_text)
    TextView dateTextView;

    private FlowersProvider provider;
    private Unbinder unbinder;

    private GalleryPagerAdapter pagerAdapter;

    public static GalleryFragment newInstance(long targetId, String targetTable,
                                              long selectedId) {
        Bundle args = new Bundle();
        args.putLong(KEY_TARGET_ID, targetId);
        args.putLong(KEY_SELECTED_ID, selectedId);
        args.putString(KEY_TARGET_TABLE, targetTable);

        GalleryFragment fragment = new GalleryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = new FlowersProvider(getActivity());
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        unbinder = ButterKnife.bind(this, view);

        setupActionBar();
        refreshStatusBarColor(R.color.material_black);
        setupViewPager();
        reloadData();
        if (savedInstanceState == null) {
            scrollToSelectedPosition();
        }

        onPageSelected(dataPager.getCurrentItem());
        return view;
    }

    @Override
    public void onDestroyView() {
        refreshStatusBarColor(R.color.primary_dark);
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        provider.unbind();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_gallery, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mg_delete) {
            PhotoItem photoItem = pagerAdapter.getItemByPosition(dataPager.getCurrentItem());
            if (photoItem != null) {
                FilesUtils.deleteFile(photoItem.getImagePath());
                provider.deletePhoto(photoItem);
                reloadData();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        PhotoItem currentItem = pagerAdapter.getItemByPosition(position);
        if (currentItem != null) {
            dateTextView.setText(Res.getString(R.string.full_date_format, currentItem.getDate()));
        } else {
            dateTextView.setText(null);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setupViewPager() {
        if (pagerAdapter == null) {
            pagerAdapter = new GalleryPagerAdapter();
        }

        dataPager.setAdapter(pagerAdapter);
        dataPager.addOnPageChangeListener(this);
    }

    private void reloadData() {
        long targetId = getTargetIdFromArgs();
        String targetTable = getTargetTableFromArgs();
        List<PhotoItem> photos = provider.getPhotosForTarget(targetId, targetTable);
        pagerAdapter.setPhotos(photos);
    }

    private void scrollToSelectedPosition() {
        Long selectedId = getSelectedIdFromArgs();
        if (selectedId != null) {
            int selectedPosition = pagerAdapter.getPositionById(selectedId);
            if (selectedPosition >= 0) {
                dataPager.setCurrentItem(selectedPosition);
            }
        }
    }

    private void setupActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                long targetId = getTargetIdFromArgs();
                String targetTable = getTargetTableFromArgs();
                String targetName = provider.getTargetName(targetId, targetTable);
                if (!TextUtils.isEmpty(targetName)) {
                    actionBar.setTitle(Res.getString(R.string.fg_title_format, targetName));
                }
            }
        }
    }

    private void refreshStatusBarColor(@ColorRes int color) {
        if (Utils.hasLollipop()) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Res.getColor(color));
        }
    }

    // region ARGS

    private Long getTargetIdFromArgs() {
        return getArguments() != null && getArguments().containsKey(KEY_TARGET_ID)
                ? getArguments().getLong(KEY_TARGET_ID) : null;
    }

    private Long getSelectedIdFromArgs() {
        return getArguments() != null && getArguments().containsKey(KEY_SELECTED_ID)
                ? getArguments().getLong(KEY_SELECTED_ID) : null;
    }

    private String getTargetTableFromArgs() {
        return getArguments() != null && getArguments().containsKey(KEY_TARGET_TABLE)
                ? getArguments().getString(KEY_TARGET_TABLE) : null;
    }

    // endregion ARGS
}
