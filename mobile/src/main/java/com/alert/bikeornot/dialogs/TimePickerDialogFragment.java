package com.alert.bikeornot.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;

import com.alert.bikeornot.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimePickerDialogFragment extends DialogFragment {
    private String dialogTitle;
    private ResultListener mOnResultListener;
    public static final String TITLE_ARG = "title";
    public static final String CURRENT_SET_TIME_ARG = "current_set_time";
    private String currentSetTime;
    @Bind(R.id.timePicker)
    TimePicker timePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_time_picker, null, false);
        ButterKnife.bind(this, view);

        dialogTitle = getArguments().getString(TITLE_ARG);
        currentSetTime = getArguments().getString(CURRENT_SET_TIME_ARG);

        if(!currentSetTime.equals("")) {
            if (Build.VERSION.SDK_INT >= 23 ) {
                timePicker.setHour(Integer.valueOf(currentSetTime.split(":")[0]));
                timePicker.setMinute(Integer.valueOf(currentSetTime.split(":")[1]));
            } else {
                timePicker.setCurrentHour(Integer.valueOf(currentSetTime.split(":")[0]));
                timePicker.setCurrentMinute(Integer.valueOf(currentSetTime.split(":")[1]));
            }

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setView(view)
                .setTitle(dialogTitle)
                .setPositiveButton("save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int chosenHour;
                                int chosenMinute;
                                if (Build.VERSION.SDK_INT >= 23 ) {
                                    chosenHour = timePicker.getHour();
                                    chosenMinute = timePicker.getMinute();
                                } else {
                                    chosenHour = timePicker.getCurrentHour();
                                    chosenMinute = timePicker.getCurrentMinute();
                                }

                                String time = String.valueOf(chosenHour) + ":" + String.valueOf(chosenMinute);
                                mOnResultListener.onNewValue(time);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
        return builder.create();
    }

    public void setOnResultListener(ResultListener resultListener) {
        mOnResultListener = resultListener;
    }

    public interface ResultListener {
        public void onNewValue(String time);
    }
}
