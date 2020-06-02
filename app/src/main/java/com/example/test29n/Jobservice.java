package com.example.test29n;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Jobservice extends JobService {
    private static final String TAG = "Jobservice";
    private boolean jobCancelled = false;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        BackgroundWork(params);
        return true;
    }

    private void BackgroundWork(final JobParameters params){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (checkPermissions()){
                    gettingLastLocation();
                }
                Log.d(TAG, "JOB FINISHED");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job stopped");
        jobCancelled = true;
        return true;
    }
    private void gettingLastLocation(){
        FusedLocationProviderClient fusedLocationClient;
        URL url = new URL();
        final String website_URL = url.index;
        final String location_URL = url.location_URL;
        final String error_URL = url.error_URL;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null){
                                Location LastLocation = task.getResult();
                                JSONObject location = new JSONObject();
                                location = json("latitude",Double.toString(LastLocation.getLatitude()),location);
                                location = json("longitude",Double.toString(LastLocation.getLongitude()),location);
                                sendRequest(location, location_URL);
                            }
                            else{
                                JSONObject error = json("error", "4");
                                sendRequest(error, error_URL);
                            }
                        }
                        else {
                            JSONObject error = json("error", "5");
                            sendRequest(error, error_URL);
                        }
                    }
                });
    }

    private void sendRequest(JSONObject jsonBody, String url){

        final String mRequestBody = jsonBody.toString();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            Log.d("LOG_RESPONSE", response);
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY_ERROR", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type","application/json");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError{
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                }catch (UnsupportedEncodingException uee){
                    return null;
                }

            }

        };

        queue.add(stringRequest);
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private JSONObject json(String name, String value){
        JSONObject JSON_Object = new JSONObject();
        try {
            JSON_Object.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return JSON_Object;
    }

    private JSONObject json(String name, String value, JSONObject JSON_Object){
        try {
            JSON_Object.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return JSON_Object;
    }

}
