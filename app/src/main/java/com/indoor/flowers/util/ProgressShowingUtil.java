package com.indoor.flowers.util;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

import com.indoor.flowers.R;

public class ProgressShowingUtil {

    private ProgressDialog progressDialog;
    private Fragment targetFragment;

    public ProgressShowingUtil(Fragment targetFragment) {
        this.targetFragment = targetFragment;
    }

    public boolean isShown() {
        return progressDialog != null && progressDialog.isShowing();
    }

    public void showProgress() {
        showProgress(true);
    }

    public void showProgress(boolean isCancelable) {
        if (targetFragment == null || !targetFragment.isAdded()
                || targetFragment.getActivity() == null) {
            return;
        }

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(targetFragment.getActivity(), R.style.Dialog_ProgressDialog);
            progressDialog.setIndeterminate(true);
        }

        progressDialog.setCancelable(isCancelable);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
