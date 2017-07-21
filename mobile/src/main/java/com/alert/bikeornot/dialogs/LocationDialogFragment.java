package com.alert.bikeornot.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.alert.bikeornot.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;

public class LocationDialogFragment extends DialogFragment implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private ResultListener mOnResultListener;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private boolean customMarker = false;
    private final float DEFAULT_ZOOM = 12.0f;
    public static final String TITLE_ARG = "title";
    public static final String OLD_LATLNG_ARG = "old_latlng";
    private String dialogTitle = "";
    private LatLng currentLatLng;
    private LatLng customLatLng;
    private LatLng oldLatLng;
    private Marker marker;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_location, null, false);
        ButterKnife.bind(this, view);

        dialogTitle = getArguments().getString(TITLE_ARG);
        oldLatLng = getArguments().getParcelable(OLD_LATLNG_ARG);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        setUpMapIfNeeded();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder
                .setView(view)
                .setTitle(dialogTitle)
                .setPositiveButton("save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mOnResultListener.onNewValue(customMarker ? customLatLng : currentLatLng);
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

        AlertDialog dialog = builder.create();
        if(dialogTitle.equals(getString(R.string.work_return_location))) {

            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Same as start", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mOnResultListener.onNewValue(new LatLng(0,0));
                }
            });
        }
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        removeMapFragment();
        super.onDismiss(dialog);
    }

    private void removeMapFragment() {
        if (mapFragment != null) {
            FragmentManager fm = getFragmentManager();
            if(fm != null) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(mapFragment);
                ft.commit();
            }
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        currentLatLng = latLng;
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(dialogTitle);

        marker = mMap.addMarker(options);
        marker.showInfoWindow();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));

        //Only the first one. We don't need to be super accurate.
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void setOnResultListener(ResultListener resultListener) {
        mOnResultListener = resultListener;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            if(oldLatLng != null){
                mMap.addMarker(new MarkerOptions()
                        .position(oldLatLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title("Current"))
                        .showInfoWindow();
            }
            /*
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location location) {
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if(!customMarker) {
                        if(marker != null)
                            marker.remove();
                        marker = mMap.addMarker(new MarkerOptions().position(currentLatLng));
                    }

                }
            });*/

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    customLatLng = latLng;
                    customMarker = true;
                    if(marker != null)
                        marker.remove();
                    marker = mMap.addMarker(new MarkerOptions().position(latLng)
                                                                .title("New position"));
                    marker.showInfoWindow();
                    float zoom = mMap.getCameraPosition().zoom;
                    //keep the current zoom if less than the default
                    if(zoom <= DEFAULT_ZOOM)
                        zoom = DEFAULT_ZOOM;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
                }
            });

            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if(currentLatLng != null) {
                        if(marker != null)
                            marker.remove();
                        marker = mMap.addMarker(new MarkerOptions().position(currentLatLng));
                        marker.showInfoWindow();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));
                        customMarker = false;
                    }
                    return false;
                }
            });

        }
    }

    public interface ResultListener {
        void onNewValue(LatLng gps);
    }
}
