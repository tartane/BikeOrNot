package com.alert.bikeornot.utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.alert.bikeornot.R;

public class DialogUtils {
    public static Dialog showBusyDialog(Context context, String message) {
        Dialog busyDialog = new Dialog(context, R.style.lightbox_dialog);
        busyDialog.setContentView(R.layout.lightbox_dialog);
        ((TextView) busyDialog.findViewById(R.id.dialogText)).setText(message);
        try {
            busyDialog.show();
        } catch (Exception e) {
        }//leaked error
        return busyDialog;
    }

    public static void dismissBusyDialog(Dialog busyDialog) {
        try {
            if (busyDialog != null)
                busyDialog.dismiss();
        } catch (Exception e) {
        }//leaked error
        busyDialog = null;
    }

    public static void showErrorDialog(Context context, int messageStringId) {
        new AlertDialog
                .Builder(context)
                .setTitle(R.string.error)
                .setMessage(messageStringId)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}