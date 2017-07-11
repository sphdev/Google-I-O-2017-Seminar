package com.sphinfo.awarenessapisample.control;

/**
 * Created by yongkyuncho on 6/14/17.
 */

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.sphinfo.awarenessapisample.interfaces.CallbackListener;


public class GoogleServiceControl implements ConnectionCallbacks, OnConnectionFailedListener {

    final public static String GOOGLESERVICE_RESULT_CONNECTION_SUCCESS = "GR90001";
    final public static String GOOGLESERVICE_RESULT_CONNECTION_FAIL = "GR90002";
    final public static String GOOGLESERVICE_RESULT_CONNECTION_SUSPEND = "GR90003";

    final public static int CODE_RESULT_SUCCESS = 90001;
    final public static int CODE_RESULT_FAIL = 90002;

    private GoogleApiClient.Builder mGoogleApiClientBuilder;
    private GoogleApiClient mGoogleApiClient;

    private Activity activity;

    private CallbackListener callback;

    private boolean isConnected;


    public GoogleServiceControl(Activity activity, CallbackListener callback) {
        this.activity = activity;
        this.callback = callback;
        this.isConnected = false;
    }


    /**
     * 2015.08.19 : choyk
     * 구글 플레이 서비스 연결관련 getter setter
     */
    public GoogleApiClient.Builder getmGoogleApiClientBuilder() {
        return mGoogleApiClientBuilder;
    }

    public void setmGoogleApiClientBuilder(GoogleApiClient.Builder mGoogleApiClientBuilder) {
        this.mGoogleApiClientBuilder = mGoogleApiClientBuilder;
    }


    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }

    public boolean isConnected() {

        if (this.mGoogleApiClient != null) {
            return this.mGoogleApiClient.isConnected();
        }
        return false;
    }

//    public void setConnected(boolean isConnected) {
//        this.isConnected = isConnected;
//    }


    /**
     * 2015.08.19 : choyk :
     * Google API Clinet Builder 설정
     */
    public GoogleApiClient.Builder setGoogleServiceBuilder(ArrayList<Api> apiList) {
        GoogleApiClient.Builder mGoogleApiClientBuilder = new GoogleApiClient.Builder(this.activity);

        // Google API Client를 통해 사용할 Google Service를 요청할 수 있
      //  mGoogleApiClientBuilder.addApi(LocationServices.API);

        for(int index = 0 ; index < apiList.size() ; index++){
            mGoogleApiClientBuilder.addApi(apiList.get(index));
        }

        mGoogleApiClientBuilder.addConnectionCallbacks(this);
        mGoogleApiClientBuilder.addOnConnectionFailedListener(this);

        this.setmGoogleApiClientBuilder(mGoogleApiClientBuilder);

        return mGoogleApiClientBuilder;

    }


    /**
     * 2015.08.19 : choyk :
     * Google Service 연결
     * Google Service와 연결이 된 후 결과는 ConnectionCallbacks, OnConnectionFailedListener를 통해 Callback으로 반환
     */
    public void connectGoogleService() {

        setmGoogleApiClient(this.mGoogleApiClientBuilder.build());
        getmGoogleApiClient().connect();
    }

    /**
     * 2015.08.19 : choyk :
     * Google Service 연결 종료
     * 해당 클래스가 Activity가 생성될때 (onCreate) Google Service와 연결을 시도하였다면
     * Activity가 종료되는 시점에 (onDestroy) 연결을 종료합니다.
     */
    public void disconnectGoogleService() {

        if (getmGoogleApiClient() != null) {
            getmGoogleApiClient().disconnect();
        }

    }


    /**
     * 2015.08.19 : choyk :
     * Google Service 연결(connect 함수 호출) 후 결과 응답
     */
    private String LOG_CALLBACK = "GoogleServiceControl";


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // TODO Auto-generated method stub
        Log.d(LOG_CALLBACK, "GoogleService onConnectionSuspended");
        Log.d(LOG_CALLBACK, "Connected Failed : " + result.getErrorCode());

        setmGoogleApiClientBuilder(null);
        setmGoogleApiClient(null);
        callback.callBack(GoogleServiceControl.GOOGLESERVICE_RESULT_CONNECTION_FAIL, result);
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        // TODO Auto-generated method stub
        Log.d(LOG_CALLBACK, "GoogleService onConnected");
        Log.d(LOG_CALLBACK, "Connected Success");

        callback.callBack(GoogleServiceControl.GOOGLESERVICE_RESULT_CONNECTION_SUCCESS, connectionHint);
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // TODO Auto-generated method stub
        Log.d(LOG_CALLBACK, "GoogleService onConnectionSuspended");
        Log.d(LOG_CALLBACK, "Suspended cause : " + cause);
        callback.callBack(GoogleServiceControl.GOOGLESERVICE_RESULT_CONNECTION_SUSPEND, cause);
    }


}
