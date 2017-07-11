package com.sphinfo.awarenessapisample.services.awareness;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.BeaconFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceQueryRequest;
import com.google.android.gms.awareness.fence.FenceQueryResult;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceStateMap;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by yongkyuncho on 6/16/17.
 */

public class FenceService {

    public static final int DETECTED_ACTIVITY_WALKING = DetectedActivityFence.WALKING;
    public static final int DETECTED_ACTIVITY_IN_VEHICLE = DetectedActivityFence.IN_VEHICLE;
    public static final int DETECTED_ACTIVITY_ON_BICYCLE = DetectedActivityFence.ON_BICYCLE;
    public static final int DETECTED_ACTIVITY_ON_FOOT = DetectedActivityFence.ON_FOOT;
    public static final int DETECTED_ACTIVITY_RUNNING = DetectedActivityFence.RUNNING;
    public static final int DETECTED_ACTIVITY_STILL = DetectedActivityFence.STILL;
    public static final int DETECTED_ACTIVITY_UNKNOWN = DetectedActivityFence.UNKNOWN;

    public static final String FENCE_RECEIVER_ACTION = "FENCE_RECEIVER_ACTION";

    public static final String LOCATION_ENTERING = "LOCATION_ENTERING";
    public static final String LOCATION_EXITING = "LOCATION_EXITING";
    public static final String LOCATION_IN = "LOCATION_IN";


    private String LOG_TAG = "FenceService";

    private Activity activity;
    private GoogleApiClient mGoogleApiClient;

    private String[] permissionList;
    public int permissionCode = 10001;


    private ArrayList<BroadcastReceiver> receiverList = null;
    public FenceService(Activity activity, GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.activity = activity;

        this.receiverList = new ArrayList<BroadcastReceiver>();

        this.permissionList = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION};

    }

    public AwarenessFence makeDetectedActivityFence(int detectedActivityFenceType) {
        AwarenessFence detectedActivityFence = DetectedActivityFence.during(detectedActivityFenceType);
        return detectedActivityFence;

    }

    public AwarenessFence makeLocationFence(String fenceType, Map<String, Object> options) {

        if (ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

        double latitude = (double) options.get("latitude");
        double longitude = (double) options.get("longitude");
        double radius = (double) options.get("radius");
        long duringtime = (long) options.get("duringtime");

        AwarenessFence locationFence = null;

        if (fenceType.equals(FenceService.LOCATION_ENTERING)) {
            locationFence = LocationFence.entering(latitude, longitude, radius);
        }
        else if(fenceType.equals(FenceService.LOCATION_EXITING)){
            locationFence = LocationFence.exiting(latitude, longitude, radius);
        }
        else if(fenceType.equals(FenceService.LOCATION_IN)){
            locationFence = LocationFence.in(latitude, longitude, radius, duringtime);
        }

        return locationFence;

    }



    public void registerFence(String fenceKey, AwarenessFence fence) {

        Intent intent = new Intent(this.FENCE_RECEIVER_ACTION);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this.activity, 0, intent, 0);


        FenceUpdateRequest.Builder fenceUpdateRequestBuilder= new FenceUpdateRequest.Builder().addFence(fenceKey, fence, mPendingIntent);


        PendingResult<Status> registerFencePendingResult = Awareness.FenceApi.updateFences(mGoogleApiClient, fenceUpdateRequestBuilder.build());
        registerFencePendingResult.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess()) {
                    Log.i(LOG_TAG, "Fence was successfully registered.");
                    Toast.makeText(activity, "Fence was successfully registered.", Toast.LENGTH_LONG);
                    //queryFence(fenceKey);
                } else {
                    Log.e(LOG_TAG, "Fence could not be registered: " + status);
                    Toast.makeText(activity, "Fence could not be registered: " + status, Toast.LENGTH_LONG);
                }
            }
        });

    }

    public void unregisterFence(final String fenceKey) {

        Intent intent = new Intent(this.FENCE_RECEIVER_ACTION);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this.activity, 0, intent, 0);


        FenceUpdateRequest.Builder fenceUpdateRequestBuilder= new FenceUpdateRequest.Builder().removeFence(fenceKey);


        PendingResult<Status> registerFencePendingResult = Awareness.FenceApi.updateFences(mGoogleApiClient, fenceUpdateRequestBuilder.build());
        registerFencePendingResult.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess()) {
                    Log.i(LOG_TAG, "Fence " + fenceKey + " successfully removed.");
                    Toast.makeText(activity, fenceKey + " successfully removed.", Toast.LENGTH_LONG);
                    //queryFence(fenceKey);
                } else {
                    Log.e(LOG_TAG, "Fence " + fenceKey + " could NOT be removed.");
                    Toast.makeText(activity, "Fence " + fenceKey + " could NOT be removed." + status, Toast.LENGTH_LONG);
                }
            }
        });

    }

    public void registerFenceReceiver(BroadcastReceiver fenceReceiver){

        this.activity.registerReceiver(fenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
        this.receiverList.add(fenceReceiver);

    }



    public void unregisterFenceReceiverAll(){

        for(int index = 0 ; index < this.receiverList.size() ; index++){
            BroadcastReceiver fenceReceiverTemp = receiverList.get(index);
            this.activity.unregisterReceiver(fenceReceiverTemp);
        }

    }


    public void queryFence(final String fenceKey) {
        Awareness.FenceApi.queryFences(mGoogleApiClient,
                FenceQueryRequest.forFences(Arrays.asList(fenceKey)))
                .setResultCallback(new ResultCallback<FenceQueryResult>() {
                    @Override
                    public void onResult(@NonNull FenceQueryResult fenceQueryResult) {
                        if (!fenceQueryResult.getStatus().isSuccess()) {
                            Log.e(LOG_TAG, "Could not query fence: " + fenceKey);
                            return;
                        }
                        FenceStateMap map = fenceQueryResult.getFenceStateMap();
                        for (String fenceKey : map.getFenceKeys()) {
                            FenceState fenceState = map.getFenceState(fenceKey);
                            Log.i(LOG_TAG, "Fence " + fenceKey + ": "
                                    + fenceState.getCurrentState()
                                    + ", was="
                                    + fenceState.getPreviousState()
                                    + ", lastUpdateTime="
                                    + fenceState.getLastFenceUpdateTimeMillis());
                        }
                    }
                });
    }

}
