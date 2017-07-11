package com.sphinfo.awarenessapisample.control;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by yongkyuncho on 6/20/17.
 */

public class FCMControl {

    private String LOG_TAG = "FCMControl";
    private String subject;
    private String token;

    public FCMControl(String subject){

        this.subject = subject;

        FirebaseMessaging.getInstance().subscribeToTopic(this.subject);
        this.token = FirebaseInstanceId.getInstance().getToken();

        Log.d(LOG_TAG, " Client Token : " + this.token);

    }

    // HTTP POST request
    public void sendMessage(String title, String body) {

        Log.d("FCMControl", "===> sendMessage : Start");

        new AsyncTask<String, Void, Void>(){

            @Override
            protected Void doInBackground(String... message) {

                Log.d("FCMControl", "===> notification title : " + message[0]);
                Log.d("FCMControl", "===> notification body  : " + message[1]);

                String url = "https://fcm.googleapis.com/fcm/send";
                URL obj = null;
                try {
                    obj = new URL(url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Authorization", "key=AIzaSyBxpPbB1kD6sobRJI_iwFmtysJC3fTX_NY");
                    httpURLConnection.connect();

                    JSONObject notificationParam = new JSONObject();
                    JSONObject messageParam = new JSONObject();
                    try {
                        notificationParam.put("title"       , message[0]);
                        notificationParam.put("body"        , message[1]);
                        notificationParam.put("click_action", "https://dummypage.com");

                        messageParam.put("notification", notificationParam);
                        messageParam.put("to"          , "fFWw5rebZfo:APA91bHo5Nb6UiK4vpV3XcgPLvcm_kAObNKGN_RfB2gOiBYGp4jwN7i9w8XSuHvtqnE_Tr6sUEdAXeYs8CM_M7mXyBVrdQ9QO-L-iwtyV2eUSo7YyKea_2ZYd7RE4PUScwV_P_Genjkf");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("FCMControl", "===> notificationParam.toString() : " + messageParam.toString());
                    byte[] outputBytes = new byte[0];
                    try {
                        outputBytes = messageParam.toString().getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                    // Send post request
                    OutputStream outputStream = null;
                    try {
                        outputStream = httpURLConnection.getOutputStream();
                        outputStream.write(outputBytes);
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    int responseCode = 0;
                    try {
                        responseCode = httpURLConnection.getResponseCode();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("FCMControl","Sending 'POST' request to URL : " + url);
                    Log.d("FCMControl","Response Code : " + responseCode);

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = bufferedReader.readLine()) != null) {
                        response.append(inputLine);
                    }
                    bufferedReader.close();

                    //print result
                    Log.d("FCMControl", "==> response.toString() : " + response.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }


//        byte[] outputBytes = "{\"notification\":{\"title\": \"My title\", \"text\": \"My text\", \"sound\": \"default\"}, \"to\": \"fFWw5rebZfo:APA91bHo5Nb6UiK4vpV3XcgPLvcm_kAObNKGN_RfB2gOiBYGp4jwN7i9w8XSuHvtqnE_Tr6sUEdAXeYs8CM_M7mXyBVrdQ9QO-L-iwtyV2eUSo7YyKea_2ZYd7RE4PUScwV_P_Genjkf\"}".getBytes("UTF-8");

//        var param =  {
//                "notification": {
//                     "title": title,
//                    "body": body,
//                    "click_action" : "https://dummypage.com"
//        },
//
//        "to" : token
//			    }



                return null;

            }


        }.execute(title, body);

    }

}
