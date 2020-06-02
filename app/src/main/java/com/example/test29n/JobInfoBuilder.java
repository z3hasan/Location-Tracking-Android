package com.example.test29n;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

public class JobInfoBuilder {

    private static final String TAG = "UTIL";
    public static void scheduleJob(Context context){

        int interval = 30*60*1000;
        int flexInterval = interval/3;

        ComponentName serviceComponent = new ComponentName(context, Jobservice.class);
        JobInfo.Builder builder = new JobInfo.Builder(0,serviceComponent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setPeriodic(interval, flexInterval);
        }
        else
        {
            builder.setPeriodic(interval);
        }
        
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.build();
        
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        if (jobScheduler != null) {
            int resultCode = jobScheduler.schedule(builder.build());
            if (resultCode == JobScheduler.RESULT_SUCCESS){
                  Log.d(TAG, "Job scheduled");
            }
        }

    }
}
