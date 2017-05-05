package com.indoor.flowers.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evgeniysharafan.utils.Fragments;
import com.indoor.flowers.R;
import com.indoor.flowers.adapter.FlowersCardsAdapter;
import com.indoor.flowers.database.DbOpenHelper;
import com.indoor.flowers.database.table.FlowerTable;
import com.indoor.flowers.util.EmptyDataUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FlowersFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final int LOADER_FLOWERS = 3201;
    private static final Uri URI_FLOWERS = DbOpenHelper.findTable(FlowerTable.class).getContentUri();

    @Bind(R.id.ff_flowers_list)
    RecyclerView flowersList;
    @Bind(R.id.ff_empty_text)
    TextView emptyTextView;

    private FlowersCardsAdapter adapter;

    public static FlowersFragment newInstance() {
        return new FlowersFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flowers, container, false);
        ButterKnife.bind(this, view);
        initList();
        restartLoader(LOADER_FLOWERS);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), URI_FLOWERS, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @OnClick(R.id.ff_add_flower)
    public void onAddFlowerClicked() {
        Fragments.replace(getFragmentManager(), android.R.id.content, AddFlowerFragment.newInstance(), null, true);
    }

    private void initList() {
        if (adapter == null) {
            adapter = new FlowersCardsAdapter();
        }

        flowersList.setAdapter(adapter);
        flowersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        EmptyDataUtil emptyDataUtil = new EmptyDataUtil();
        emptyDataUtil.attachToList(flowersList);
        emptyDataUtil.setEmptyTextLayout(emptyTextView);
    }

    private void restartLoader(int loaderId) {
        LoaderManager loaderManager = getLoaderManager();
        if (isAdded() && loaderManager != null) {
            if (loaderManager.getLoader(loaderId) == null) {
                loaderManager.initLoader(loaderId, null, this);
            } else {
                loaderManager.restartLoader(loaderId, null, this);
            }
        }
    }
}
