package com.bingerdranch.android.runtrackertest;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

/**
 * Created by Administrator on 2015/7/16.
 */
public class TrakingLoactionReceiver extends LocatonReceiver {
    @Override
    protected void onLocationReceived(Context context, Location location) {
        RunManager.get(context).insertLocation(location);
    }
}
