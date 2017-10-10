package com.alert.bikeornot.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;

import com.alert.bikeornot.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetupNotificationTimeFragment extends Fragment {

    @BindView(R.id.chkDisableNotification)
    Switch chkDisableNotification;

    @BindView(R.id.timePicker)
    TimePicker timePicker;

    private OnFragmentInteractionListener mListener;

    public SetupNotificationTimeFragment() {
        // Required empty public constructor
    }

    public static SetupNotificationTimeFragment newInstance() {
        SetupNotificationTimeFragment fragment = new SetupNotificationTimeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_setup_notification_time, container, false);
        ButterKnife.bind(this, view);
        chkDisableNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    timePicker.setAlpha(0.4f);
                    timePicker.setEnabled(false);
                    timePicker.setOnTouchListener(null);
                } else {
                    timePicker.setAlpha(1f);
                    timePicker.setEnabled(true);
                    timePicker.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                }
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
