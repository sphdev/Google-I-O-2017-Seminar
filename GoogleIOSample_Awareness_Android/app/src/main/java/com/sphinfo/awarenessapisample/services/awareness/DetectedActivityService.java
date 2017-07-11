package com.sphinfo.awarenessapisample.services.awareness;

import android.Manifest;
import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.PendingResults;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.sphinfo.awarenessapisample.interfaces.CallbackListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yongkyuncho on 6/14/17.
 */

public class DetectedActivityService {


    private String LOG_TAG = "DetectedActivityService";


    private Activity activity;
    private GoogleApiClient mGoogleApiClient;

    private String[] permissionList;
    public int permissionCode = 10001;

    public DetectedActivityService(Activity activity, GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.activity = activity;

        this.permissionList = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION};
    }

    public void getDetectedActivity(CallbackListener callbackListener){
        PendingResult<DetectedActivityResult> detectedActivity = Awareness.SnapshotApi.getDetectedActivity(this.mGoogleApiClient);

        class DetectedActivityResultCallback implements ResultCallback<DetectedActivityResult>{

            private CallbackListener callbackListener;
            public DetectedActivityResultCallback(CallbackListener callbackListener){
                this.callbackListener = callbackListener;
            }


            @Override
            public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {

                if (!detectedActivityResult.getStatus().isSuccess()) {
                    this.callbackListener.callBack("E00001", null);
                }
                else{

                    ActivityRecognitionResult activityRecognitionResult = detectedActivityResult.getActivityRecognitionResult();
                    DetectedActivity probableActivity = activityRecognitionResult.getMostProbableActivity();
                    int activityType = probableActivity.getType();

                    String activityTypeString = "None";

                    if(activityType == 0){
                        activityTypeString = "IN_VEHICLE";
                    }
                    else if(activityType == 1){
                        activityTypeString = "ON_BICYCLE";
                    }
                    else if(activityType == 2){
                        activityTypeString = "ON_FOOT";
                    }
                    else if(activityType == 8){
                        activityTypeString = "RUNNING";
                    }
                    else if(activityType == 3){
                        activityTypeString = "STILL";
                    }
                    else if(activityType == 5){
                        activityTypeString = "TILTING";
                    }
                    else if(activityType == 4){
                        activityTypeString = "UNKNOWN";
                    }
                    else if(activityType == 7){
                        activityTypeString = "WALKING";
                    }

                    Map<String, Object> result = new HashMap<String, Object>();
                    result.put("confidence", probableActivity.getConfidence());
                    result.put("activityType"  , probableActivity.getType());
                    result.put("activityName"  , activityTypeString);

                    this.callbackListener.callBack("E00001", result);
                }

            }
        }

        DetectedActivityResultCallback detectedActivityResultCallback = new DetectedActivityResultCallback(callbackListener);
        detectedActivity.setResultCallback(detectedActivityResultCallback);
    }
}
