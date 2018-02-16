package com.indoor.flowers.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.evgeniysharafan.utils.Res;
import com.indoor.flowers.R;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.util.FlowersAlarmsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FlowerDetailsFragment extends Fragment {

    private static final String KEY_FLOWER_ID = "key_flower_id";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Unbinder unbinder;

    private Flower flower;

    private FlowersProvider provider;

    public static FlowerDetailsFragment newInstance(long flowerId) {
        Bundle args = new Bundle();
        args.putLong(KEY_FLOWER_ID, flowerId);

        FlowerDetailsFragment fragment = new FlowerDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        provider = new FlowersProvider(getActivity());
        long flowerId = getArguments() != null && getArguments().containsKey(KEY_FLOWER_ID)
                ? getArguments().getLong(KEY_FLOWER_ID) : DatabaseProvider.DEFAULT_ID;
        flower = provider.getFlowerById(flowerId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flower_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupToolbar();
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_flower_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mfd_delete:
                FlowersAlarmsUtils.deleteAlarmsForFlower(getActivity(), flower);
                provider.deleteFlower(flower);
                getActivity().onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(flower.getName());
        if (!TextUtils.isEmpty(flower.getImagePath())) {
            Bitmap flowerImage = BitmapFactory.decodeFile(flower.getImagePath());
            toolbar.setLogo(new BitmapDrawable(Res.get(), flowerImage));
        } else {
            toolbar.setLogo(R.drawable.ic_flower);
        }
    }
}
