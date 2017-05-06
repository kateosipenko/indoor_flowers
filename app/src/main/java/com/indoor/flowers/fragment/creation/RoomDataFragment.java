package com.indoor.flowers.fragment.creation;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.evgeniysharafan.utils.Toasts;
import com.indoor.flowers.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoomDataFragment extends Fragment {

    public static RoomDataFragment newInstance() {
        return new RoomDataFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_data, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.frd_clarification)
    void onClarificationClick() {
        new AlertDialog.Builder(getActivity(), R.style.Dialog)
                .setTitle(R.string.faf_clarification_content_info)
                .setMessage(R.string.faf_clarification)
                .setPositiveButton(R.string.action_ok, null)
                .create()
                .show();
    }

    @OnClick(R.id.frd_save)
    void onSaveRoomClick() {
        final EditText editText = new EditText(getActivity());
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        new AlertDialog.Builder(getActivity(), R.style.Dialog)
                .setTitle(R.string.frd_save_room)
                .setView(editText)
                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            Toasts.showLong(R.string.frd_name_room_error);
                            onSaveRoomClick();
                        } else {
                            Toasts.showFuture();
                        }
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .create()
                .show();
    }

    @OnClick(R.id.frd_choose)
    void onChooseRoomClick() {
        Toasts.showFuture();
    }
}
