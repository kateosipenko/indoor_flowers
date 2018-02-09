package com.indoor.flowers.util;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int mVerticalSpace = 0;
    private int mHorizontalSpace = 0;
    private int columnsCount = 1;
    private RecyclerView.LayoutManager layoutManager;

    public SpaceItemDecoration(int verticalSpace) {
        mVerticalSpace = verticalSpace;
    }

    public SpaceItemDecoration(int verticalSpace, int horizontalSpace) {
        mVerticalSpace = verticalSpace;
        mHorizontalSpace = horizontalSpace;
    }

    public int getVerticalSpace() {
        return mVerticalSpace;
    }

    public int getHorizontalSpace() {
        return mHorizontalSpace;
    }

    public void setVerticalSpace(int mVerticalSpace) {
        this.mVerticalSpace = mVerticalSpace;
    }

    public void setHorizontalSpace(int mHorizontalSpace) {
        this.mHorizontalSpace = mHorizontalSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int currentPosition = parent.getChildAdapterPosition(view);
        int itemsCount = parent.getAdapter().getItemCount();
        if (layoutManager == null) {
            layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                columnsCount = ((GridLayoutManager) layoutManager).getSpanCount();
            }
        }

        if (!isInLastRow(currentPosition, itemsCount, parent.getAdapter())) {
            outRect.bottom = mVerticalSpace;
        }

        if (parent.getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            outRect.right = mHorizontalSpace;
        } else {
            outRect.left = mHorizontalSpace;
        }
    }

    private boolean isInLastRow(int position, int itemsCount, RecyclerView.Adapter adapter) {
        int countOfItemsInLastRow = itemsCount - (itemsCount / columnsCount) * columnsCount;
        if (countOfItemsInLastRow == 0) {
            countOfItemsInLastRow = columnsCount;
        }

        return position >= itemsCount - countOfItemsInLastRow;
    }
}

