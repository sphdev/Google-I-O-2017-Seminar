package com.sphinfo.awarenessapisample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceQueryResult;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceStateMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sphinfo.awarenessapisample.control.FCMControl;
import com.sphinfo.awarenessapisample.control.FirebaseControl;
import com.sphinfo.awarenessapisample.control.GoogleServiceControl;
import com.sphinfo.awarenessapisample.interfaces.CallbackListener;
import com.sphinfo.awarenessapisample.services.awareness.DetectedActivityService;
import com.sphinfo.awarenessapisample.services.LocationService;
import com.sphinfo.awarenessapisample.services.awareness.FenceService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MapsActivity extends FragmentActivity {

    private String LOG_TAG = "MapsActivity";

    private GoogleServiceControl googleServiceControl;
    private FirebaseControl firebaseControl;
    private FCMControl fCMControl;

    private GoogleMap mGoogleMap;

    private LocationService locationService;
    private DetectedActivityService detectedActivityService;
    private FenceService fenceService;

    private String PREFIX_DATA_STORAGE_FENCING = "Fencing";
    private String PREFIX_DATA_STORAGE_TRACKING = "Tracking";

    private ArrayList<Map<String, Object>> fenceDataList = null;
    private ArrayList<Circle> fenceCircleDataList = null;

    //Google Play Service Connection Callback 처리
    class PlayServiceConnectCallback implements CallbackListener {

        private MapsActivity activity;

        public PlayServiceConnectCallback(MapsActivity activity) {
            this.activity = activity;
        }


        @Override
        public void callBack(String returnCode, Object result) {

            if (returnCode.equals(GoogleServiceControl.GOOGLESERVICE_RESULT_CONNECTION_SUCCESS)) {
                Log.d(this.activity.LOG_TAG, "Google Service Connect Success : " + this.activity.googleServiceControl.isConnected());

                this.activity.locationService = new LocationService(this.activity, this.activity.googleServiceControl.getmGoogleApiClient());
                this.activity.detectedActivityService = new DetectedActivityService(this.activity, this.activity.googleServiceControl.getmGoogleApiClient());

                if(this.activity.locationService.ckeckPermission()){
                    this.activity.locationService.getLastLocation();
                }
                else{
                    String[] psemissionList = new String[]{android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.ACCESS_COARSE_LOCATION};
                    ActivityCompat.requestPermissions(this.activity, this.activity.locationService.getPermissionList(), this.activity.locationService.permissionCode);
                }

            } else {

                ConnectionResult connectionResult = (ConnectionResult) result;
                Log.d(this.activity.LOG_TAG, "Google Service Connect fail : " + connectionResult.getErrorCode());
            }
        }
    }


    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //지도관련 정보 설정.
        LoadMapUsingMapFragment();

        //Google servie 관리 클래스 생성
        PlayServiceConnectCallback playServiceConnectCallback = new PlayServiceConnectCallback(this);
        this.googleServiceControl = new GoogleServiceControl(this, playServiceConnectCallback);

        ArrayList<Api> apiList = new ArrayList();
        apiList.add(LocationServices.API);
        apiList.add(Awareness.API);

        this.googleServiceControl.setGoogleServiceBuilder(apiList);
        this.googleServiceControl.connectGoogleService();


        this.fCMControl = new FCMControl("news");



        this.firebaseControl = new FirebaseControl();

        this.fenceDataList = new ArrayList<Map<String, Object>>();
        this.fenceCircleDataList = new ArrayList<Circle>();

        this.fenceService = new FenceService(this, this.googleServiceControl.getmGoogleApiClient());

        ArrayList<String> fenceTypeList = new ArrayList<String>();
        fenceTypeList.add(FenceService.LOCATION_ENTERING);
        fenceTypeList.add(FenceService.LOCATION_EXITING);
        fenceTypeList.add(FenceService.LOCATION_IN);

        Map<String, Object> fenceDataSPH = new HashMap<String, Object>();
        fenceDataSPH.put("latitude"  ,   37.5237446);
        fenceDataSPH.put("longitude" ,   126.926865);
        fenceDataSPH.put("radius"    ,   100.0);
        fenceDataSPH.put("duringtime",   10000L);
        this.fenceDataList.add(fenceDataSPH);
        setFenceEvent(fenceService, "SPH", fenceDataSPH, fenceTypeList);

        Map<String, Object> fenceDataYY = new HashMap<String, Object>();
        fenceDataYY.put("latitude"  ,   37.521635);
        fenceDataYY.put("longitude" ,   126.924282);
        fenceDataYY.put("radius"    ,   100.0);
        fenceDataYY.put("duringtime",   10000L);
        this.fenceDataList.add(fenceDataYY);
        setFenceEvent(fenceService, "YY", fenceDataYY, fenceTypeList);

        Map<String, Object> fenceDataKD = new HashMap<String, Object>();
        fenceDataKD.put("latitude"  ,   37.543597);
        fenceDataKD.put("longitude" ,   126.951694);
        fenceDataKD.put("radius"    ,   100.0);
        fenceDataKD.put("duringtime",   10000L);
        this.fenceDataList.add(fenceDataKD);
        setFenceEvent(fenceService, "KD", fenceDataKD, fenceTypeList);

        Map<String, Object> fenceDataJS = new HashMap<String, Object>();
        fenceDataJS.put("latitude"  ,   37.584007);
        fenceDataJS.put("longitude" ,   126.909778);
        fenceDataJS.put("radius"    ,   100.0);
        fenceDataJS.put("duringtime",   10000L);
        this.fenceDataList.add(fenceDataJS);
        setFenceEvent(fenceService, "JS", fenceDataJS, fenceTypeList);



    }


    public void setFenceEvent( FenceService fenceService, String fenceKey,  Map<String, Object> fenceData, ArrayList<String> fenceTypeList){

        class LocationFenceReceiver extends BroadcastReceiver {

            private MapsActivity activity;
            private ArrayList<String> fenceKeyList = null;

            public LocationFenceReceiver(MapsActivity activity, ArrayList<String> fenceKeyList){
                this.fenceKeyList = fenceKeyList;
                this.activity = activity;
            }

            @Override
            public void onReceive(Context context, Intent intent) {

                FenceState fenceState = FenceState.extract(intent);

                String fenceKey = null;
                for(int index = 0 ; index < this.fenceKeyList.size() ; index++){
                    String fenceKeyTemp = this.fenceKeyList.get(index);
                    if (TextUtils.equals(fenceState.getFenceKey(), fenceKeyTemp)){
                        fenceKey = fenceState.getFenceKey();
                        break;
                    }
                }

                if (fenceKey != null) {

                    String referenceName = this.activity.PREFIX_DATA_STORAGE_FENCING+"/" + (String)Long.toString(fenceState.getLastFenceUpdateTimeMillis()) + "/";

                    JSONObject fencing = new JSONObject();
                    JSONObject event = new JSONObject();

                    switch(fenceState.getCurrentState()) {

                        case FenceState.TRUE:
                            Toast.makeText(this.activity, "Fence onReceive : " + fenceKey + " : TRUE", Toast.LENGTH_LONG).show();
                            Log.i(LOG_TAG, "=============================");
                            Log.i(LOG_TAG, "Fence onReceive : " + fenceKey + " : TRUE");
                            Log.i(LOG_TAG, "=============================");

                            try {
                                event.put("fencekey", fenceKey);
                                event.put("currentstate", "TRUE");
                                event.put("previousstate", fenceState.getPreviousState());
                                event.put("time", fenceState.getLastFenceUpdateTimeMillis());

                                fencing.put("event", event);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            String fencingType = "";
                            if(fenceKey.contains("_ENTERING") == true){
                                fencingType = "GeoFence Entering";
                            }
                            else if(fenceKey.contains("_EXITING") == true){
                                fencingType = "GeoFence Exiting";
                            }
                            else if(fenceKey.contains("_IN") == true){
                                fencingType = "GeoFence In";
                            }

                            String fencingArea = "";
                            if(fenceKey.contains("SPH_") == true){
                                fencingArea = "SPH 본사 반경 150m";
                            }
                            else if(fenceKey.contains("YY_") == true){
                                fencingArea = "여의도역 5호, 9호선 환승지역 반경 150m";
                            }
                            else if(fenceKey.contains("KD_") == true){
                                fencingArea = "공덕역 5호선, 6호선 환승지역 반경 150m";
                            }

                            else if(fenceKey.contains("JJ_") == true){
                                fencingArea = "증산역 6호선 지역 반경 150m";
                            }

                            this.activity.fCMControl.sendMessage(fencingType, fencingArea);


                            break;

                        case FenceState.FALSE:
                            Toast.makeText(this.activity, "Fence onReceive : " + fenceKey + " : FALSE", Toast.LENGTH_LONG).show();
                            Log.e(LOG_TAG, "=============================");
                            Log.e(LOG_TAG, "Fence onReceive : " + fenceKey + " : FASE");
                            Log.e(LOG_TAG, "=============================");

                            try {
                                event.put("fencekey", fenceKey);
                                event.put("currentstate", "FALSE");
                                event.put("previousstate", fenceState.getPreviousState());
                                event.put("time", fenceState.getLastFenceUpdateTimeMillis());

                                fencing.put("event", event);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;

                        case FenceState.UNKNOWN:
                            Toast.makeText(this.activity, "Fence onReceive : " + fenceKey + " : UNKNOWN", Toast.LENGTH_LONG).show();
                            Log.e(LOG_TAG, "=============================");
                            Log.e(LOG_TAG, "Fence onReceive : " + fenceKey + " : UNKNOWN");
                            Log.e(LOG_TAG, "=============================");

                            try {
                                event.put("fencekey", fenceKey);
                                event.put("currentstate", "UNKNOWN");
                                event.put("previousstate", fenceState.getPreviousState());
                                event.put("time", fenceState.getLastFenceUpdateTimeMillis());

                                fencing.put("event", event);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                    }

                    this.activity.firebaseControl.writeData(referenceName, fencing.toString());
                }

            }
        };


        ArrayList<String> fenceKeyList = new ArrayList<String>();
        //AwarenessFence locationFenceAwareness = null;
        for(int index = 0  ; index < fenceTypeList.size() ; index++){
            AwarenessFence locationFenceAwareness = this.fenceService.makeLocationFence(fenceTypeList.get(index), fenceData);
            String fenceKeyTemp = fenceKey + "_" + fenceTypeList.get(index);
            this.fenceService.registerFence(fenceKeyTemp, locationFenceAwareness);
            fenceKeyList.add(fenceKeyTemp);

            Log.d(LOG_TAG, "==> RegisterFence Key : " + fenceKeyTemp);
        }

        LocationFenceReceiver locationFenceReceiver = new LocationFenceReceiver(this, fenceKeyList);
        fenceService.registerFenceReceiver(locationFenceReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.googleServiceControl.disconnectGoogleService();
        this.fenceService.unregisterFenceReceiverAll();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if(requestCode == locationService.permissionCode){
            Log.d(this.LOG_TAG, "permissions : " +  permissions);
        }

    }

    /**
     * 2015.08.19 : choyk
     * 구글맵이 로드되었을 콜백리스너를 이용한 처리하는 경우
     */
    public void LoadMapUsingMapFragment(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new MapReadyCallback(this));
    }


    /**
     * 2015.08.19 : choyk
     * 구글맵이 로드되었을 콜백리스너를 이용한 처리하는 경우 : 콜백
     */
    class MapReadyCallback implements OnMapReadyCallback {

        private MapsActivity activity;

        public MapReadyCallback(MapsActivity activity){
            this.activity = activity;
        }

        @Override
        public void onMapReady(GoogleMap mGoogleMap) {
            // TODO Auto-generated method stub
            activity.mGoogleMap = mGoogleMap;
            setPosition(activity.mGoogleMap);
            drawFenceCircle(activity.mGoogleMap, activity.fenceDataList);

        }

    }


    public void drawFenceCircle(GoogleMap mGoogleMap, ArrayList<Map<String, Object>> fenceDataList){

        int fillcolor = Color.argb(60, 67, 116, 217);
        int strokecolor = Color.argb(0, 255, 255, 255);

        for(int index = 0 ; index < fenceDataList.size() ; index++){

            Map<String, Object> fenceData = fenceDataList.get(index);
            Log.d(LOG_TAG, "==> " + index + " : " + fenceData.toString());

            double latitude = (double) fenceData.get("latitude");
            double longitude = (double) fenceData.get("longitude");
            double radius = (double) fenceData.get("radius");

            Circle circle = mGoogleMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(radius)
                    .fillColor(fillcolor)
                    .strokeColor(strokecolor));

            fenceCircleDataList.add(circle);

        }

    }

    /**
     * 2015.08.19 : choyk
     * 지도위에 위치 표시
     */
    public void setPosition(GoogleMap mGoogleMap){
        LatLng sphPosition = new LatLng(37.5237446,126.926865);

        MarkerOptions markerOptions = new MarkerOptions().position(sphPosition).title("Marker in SPH");
        Marker marker = mGoogleMap.addMarker(markerOptions);
        marker.showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder().target((sphPosition)).zoom(16).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    /**
     * 2015.08.19 : choyk
     * 지도위에 위치 표시
     */
    public void drawLastPosition(Location lastlocation){

        LatLng lastLatLng = new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude());

        Date currentTime = new Date();


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("hh:mm:ss");
        String currentTimeString = simpleDateFormat.format(currentTime);

        MarkerOptions markerOptions = new MarkerOptions().position(lastLatLng).title(currentTimeString);
        Marker marker = mGoogleMap.addMarker(markerOptions);

        String dataId = Long.toString(currentTime.getTime());

        Map<String, Object> dataTemp = new HashMap<String, Object>();
        dataTemp.put("dataId",   dataId);
        dataTemp.put("latitude", lastlocation.getLatitude());
        dataTemp.put("longitude", lastlocation.getLongitude());
        dataTemp.put("accuracy", lastlocation.getAccuracy());


        class AwanessDetectedActivityCallback implements CallbackListener{

            private Map<String, Object> data;
            private Marker marker;
            private MapsActivity activity;

            public AwanessDetectedActivityCallback(Map<String, Object> data, Marker marker, MapsActivity activity){
                this.data = data;
                this.marker = marker;
                this.activity = activity;
            }


            @Override
            public void callBack(String returnCode, Object result) {

                String referenceName = this.activity.PREFIX_DATA_STORAGE_TRACKING+"/" + (String)data.get("dataId") + "/";

                JSONObject tracking = new JSONObject();
                JSONObject position = new JSONObject();
                JSONObject detected = new JSONObject();

                if(result != null) {
                    Map<String, Object> activity = (Map<String, Object>)result;
                    marker.setTitle(activity.get("activityName") + " : " + marker.getTitle());

                    String activityName = (String)activity.get("activityName");

                    BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.none);

                    if(activityName.equals("IN_VEHICLE")) {
                        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.in_vehicle);
                    }
                    else if(activityName.equals("ON_BICYCLE")) {
                        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.on_bicycle);
                    }
                    else if(activityName.equals("ON_FOOT")) {
                        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.on_foot);
                    }
                    else if(activityName.equals("RUNNING")) {
                        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.running);
                    }
                    else if(activityName.equals("STILL")) {
                        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.still);
                    }
                    else if(activityName.equals("TILTING")) {
                        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.tilting);
                    }
                    else if(activityName.equals("ON_FOOT")) {
                        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.on_foot);
                    }

                    marker.setIcon(markerIcon);


                    Log.d("DetectedActivity", "Callback : " + activity);

                    try {
                        position.put("latitude", data.get("latitude"));
                        position.put("longitude", data.get("longitude"));
                        position.put("accuracy", data.get("accuracy"));

                        detected.put("confidence",   activity.get("confidence"));
                        detected.put("activityType", activity.get("activityType"));
                        detected.put("activityName", activity.get("activityName"));

                        tracking.put("position", position);
                        tracking.put("detected", detected);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else{


                    try {
                        position.put("latitude", data.get("latitude"));
                        position.put("longitude", data.get("longitude"));
                        position.put("accuracy", data.get("accuracy"));

                        detected.put("confidence",   -1);
                        detected.put("activityType", -1);
                        detected.put("activityName", "NONE");

                        tracking.put("position", position);
                        tracking.put("detected", detected);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    marker.setTitle(marker.getTitle() + "\n" + "Could not get the current activity.");
                    marker.setTitle("None" + " : " + marker.getTitle());
                }


                this.activity.firebaseControl.writeData(referenceName, tracking.toString());

                marker.showInfoWindow();
            }
        }

        AwanessDetectedActivityCallback awanessDetectedActivityCallback = new AwanessDetectedActivityCallback(dataTemp, marker, this);
        this.detectedActivityService.getDetectedActivity(awanessDetectedActivityCallback);

        CameraPosition cameraPosition = new CameraPosition.Builder().target((lastLatLng)).zoom(16).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.lastknownlocation:
                Location lastlocation = this.locationService.getLastLocation();

                if(lastlocation != null) {
                    this.drawLastPosition(lastlocation);
                }
                else{
                    Toast.makeText(this, "Location is null", Toast.LENGTH_LONG);
                }

                break;

            case R.id.requestLocationUpdates:

                class SearchLocationCallback implements CallbackListener{

                    private MapsActivity activity;
                    public SearchLocationCallback(MapsActivity activity){
                        this.activity = activity;
                    }


                    @Override
                    public void callBack(String returnCode, Object result) {

                        if(result == null){
                            Toast.makeText(this.activity, "Location is null", Toast.LENGTH_LONG);
                        }
                        else{
                            this.activity.drawLastPosition((Location)result);
                        }

                    }
                }

                SearchLocationCallback searchLocationCallback = new SearchLocationCallback(this);

                this.locationService.requestLocationUpdates(searchLocationCallback);

                break;

            case R.id.removeLocationUpdates:

                class RemoveLocationCallback implements CallbackListener{

                    private MapsActivity activity;
                    public RemoveLocationCallback(MapsActivity activity){
                        this.activity = activity;
                    }


                    @Override
                    public void callBack(String returnCode, Object result) {

                        if(result == null){
                            Toast.makeText(this.activity, "Location is null", Toast.LENGTH_LONG);
                        }
                        else{
                            this.activity.drawLastPosition((Location)result);
                        }

                    }
                }

                RemoveLocationCallback removeLocationCallback = new RemoveLocationCallback(this);

                this.locationService.removeLocationUpdates(removeLocationCallback);

                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
