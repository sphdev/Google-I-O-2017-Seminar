package com.sphinfo.awarenessapisample.services;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.sphinfo.awarenessapisample.interfaces.CallbackListener;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by yongkyuncho on 6/14/17.
 */

public class LocationService {

    private String LOG_TAG = "Play.Location";


    private Activity activity;
    private GoogleApiClient mGoogleApiClient;

    private String[] permissionList;
    public int permissionCode = 10001;

    public LocationService(Activity activity, GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.activity = activity;

        this.permissionList = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION};


//        LocationRequest locationRequestHighAccuracy = new LocationRequest();
//        locationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        LocationRequest locationRequestLowPower = new LocationRequest();
//        locationRequestLowPower.setPriority(LocationRequest.PRIORITY_LOW_POWER);
//
//        LocationSettingsRequest.Builder locationSettingsbuilder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequestHighAccuracy)
//                .addLocationRequest(locationRequestLowPower);
//
//
//        locationSettingsbuilder.setNeedBle(true);
//
//        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(this.mGoogleApiClient, locationSettingsbuilder.build());
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult result) {
//                final Status status = result.getStatus();
//                final LocationSettingsStates = result.getLocationSettingsStates();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        // All location settings are satisfied. The client can initialize location
//                        // requests here.
//                 //...
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        // Location settings are not satisfied. But could be fixed by showing the user
//                        // a dialog.
//                        try {
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            //status.startResolutionForResult(OuterClass.this, REQUEST_CHECK_SETTINGS);
//                        } catch (IntentSender.SendIntentException e) {
//                            // Ignore the error.
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        // Location settings are not satisfied. However, we have no way to fix the
//                        // settings so we won't show the dialog.
//                 //...
//                        break;
//                }
//            }
//        });

    }

    public String[] getPermissionList() {
        return permissionList;
    }

    public boolean ckeckPermission() {

        if (ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    ;

    public Location getLastLocation() {

        if (!this.ckeckPermission()) {
            return null;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(this.mGoogleApiClient);

        Log.d(LOG_TAG, "getLastLocation : location : " + location);

        if (location != null) {

            Log.d(LOG_TAG, "getLastLocation : Latitude  : " + location.getLatitude());
            Log.d(LOG_TAG, "getLastLocation : Longitude : " + location.getLongitude());
        } else {
            new AlertDialog.Builder(this.activity)
                    .setTitle("Please activate location")
                    .setMessage("Click ok to goto settings else exit.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .show();
        }

        return location;
    }


    class RequestLocationUpdatesCallback implements LocationListener {

        private CallbackListener callbackListener;

        public RequestLocationUpdatesCallback(CallbackListener callbackListener) {
            this.callbackListener = callbackListener;
        }


        @Override
        public void onLocationChanged(Location location) {
            callbackListener.callBack("E00001", location);

        }
    }

    private RequestLocationUpdatesCallback requestLocationUpdatesCallback = null;
    public void requestLocationUpdates(CallbackListener callbackListener) {

        if (!this.ckeckPermission()) {
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000 * 5);
        locationRequest.setFastestInterval(1000 * 5);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        this.requestLocationUpdatesCallback = new RequestLocationUpdatesCallback(callbackListener);

        if (ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, locationRequest, this.requestLocationUpdatesCallback);


    }

    public void removeLocationUpdates(CallbackListener callbackListener) {

        if(this.requestLocationUpdatesCallback != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.mGoogleApiClient, this.requestLocationUpdatesCallback);
        }


    }
}
