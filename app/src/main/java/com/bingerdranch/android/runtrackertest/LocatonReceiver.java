package com.bingerdranch.android.runtrackertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by dsz on 15/7/15.
 */
public class LocatonReceiver extends BroadcastReceiver {
    private static final  String TAG="LocationReceiver";
    @Override
    public void onReceive(Context context,Intent intent){
        Location location=intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
        if(location!=null){
            onLocationReceived(context,location);
        }
        if(intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)){
            boolean enabled=intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED,false);
            onProviderEnabledChanged(enabled);

        }
    }

    protected void onLocationReceived(Context context,Location location){
        Log.i(TAG, location.getProvider()+location.getLongitude()+location.getLatitude());
    }
    protected void onProviderEnabledChanged(boolean enable){
        Log.i(TAG,"provider"+(enable?"enable":"disable"));
    }

}
