package com.indoor.flowers.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.evgeniysharafan.utils.Fragments;
import com.evgeniysharafan.utils.Res;
import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.R;
import com.indoor.flowers.database.provider.DatabaseProvider;
import com.indoor.flowers.database.provider.FlowersProvider;
import com.indoor.flowers.fragment.FlowerFragment;
import com.indoor.flowers.fragment.GroupFragment;
import com.indoor.flowers.model.Flower;
import com.indoor.flowers.model.Group;

public final class DialogUtils {

    public static void showCreateFlowerDialog(final Context context,
                                              final FragmentManager fragmentManager) {
        final EditText editText = new EditText(context);
        editText.setTextColor(Res.getColor(R.color.black_primary_text));
        editText.setSingleLine();
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        new AlertDialog.Builder(context, R.style.Dialog)
                .setTitle(R.string.create_flower)
                .setView(editText)
                .setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!Utils.isEmpty(editText)) {
                            FlowersProvider flowersProvider = new FlowersProvider(context);
                            Flower flower = new Flower();
                            flower.setId(DatabaseProvider.DEFAULT_ID);
                            flower.setName(editText.getText().toString().trim());
                            flowersProvider.createFlower(flower);

                            Fragments.replace(fragmentManager, android.R.id.content,
                                    FlowerFragment.newInstance(flower.getId()), null, true);

                            flowersProvider.unbind();
                        }
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .create().show();
    }

    public static void showCreateGroupDialog(final Context context,
                                             final FragmentManager fragmentManager) {
        final EditText editText = new EditText(context);
        editText.setTextColor(Res.getColor(R.color.black_primary_text));
        editText.setSingleLine();
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        new AlertDialog.Builder(context, R.style.Dialog)
                .setTitle(R.string.create_group)
                .setView(editText)
                .setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!Utils.isEmpty(editText)) {
                            FlowersProvider flowersProvider = new FlowersProvider(context);
                            Group group = new Group();
                            group.setId(DatabaseProvider.DEFAULT_ID);
                            group.setName(editText.getText().toString().trim());
                            flowersProvider.createGroup(group);

                            Fragments.replace(fragmentManager, android.R.id.content,
                                    GroupFragment.newInstance(group.getId()), null, true);

                            flowersProvider.unbind();
                        }
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .create().show();
    }
}
