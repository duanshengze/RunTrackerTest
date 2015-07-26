package com.bingerdranch.android.runtrackertest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by dsz on 15/7/15.
 */
public class RunManager {
    private static final String TAG = "LocationManager";
    private Context mAppContext;
    private LocationManager mLocationManager;
    private static RunManager sRunManager;
    public static String ACTION_LOCATION = "com.bingerdranch.android.runtrackertest.ACTION_LOCATION";
    private static final String TEST_PROVIDER = "TEST_PROVIDER";
    private static final String PREFS_CURRENT_ID = "prefs_current_id";
    private RunDatabaseHelper mHelper;
    private long mcurrentId;
    private SharedPreferences mSharedPreferences;

    private RunManager(Context context) {
        mAppContext = context;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
        mSharedPreferences = context.getSharedPreferences(PREFS_CURRENT_ID, Context.MODE_PRIVATE);
        mcurrentId = mSharedPreferences.getLong(PREFS_CURRENT_ID, -1);
        mHelper = new RunDatabaseHelper(context);
    }

    public static RunManager get(Context context) {
        if (sRunManager == null) {
//            使用application context避免activity丢失
            return sRunManager = new RunManager(context.getApplicationContext());
        } else {
            return sRunManager;
        }

    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent intent = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, intent, flags);

    }

    //    开始提供GPS信号纪录
    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;
        if (mLocationManager.getProvider(TEST_PROVIDER) != null && mLocationManager.isProviderEnabled(TEST_PROVIDER)) {
            provider = TEST_PROVIDER;
        }
        Location location = mLocationManager.getLastKnownLocation(provider);
        if (location != null) {
            broadcastLoaction(location);
        }
        PendingIntent pi = getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);

    }

    private void broadcastLoaction(Location location) {
        Intent intent = new Intent(ACTION_LOCATION);
        intent.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(intent);
    }

    //    停止提供GPS信号纪录
    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }

    }

    //    检查是否在纪录
    public boolean isTrackingRun() {
        return getLocationPendingIntent(false) != null;
    }

    //    检查当前记录是否在记录
    public boolean isTrackingRun(Run run) {
        return run != null && mcurrentId == run.getId();

    }

    public Run startNewRun() {
        Run run = insertRun();
        startTrackingRun(run);
        return run;
    }

    public void startTrackingRun(Run run){
        mcurrentId = run.getId();
        mSharedPreferences.edit().putLong(PREFS_CURRENT_ID, mcurrentId).commit();
        startLocationUpdates();
    }



    public void stopRun() {
        stopLocationUpdates();
        mcurrentId = -1;
        mSharedPreferences.edit().remove(PREFS_CURRENT_ID);
    }

    private Run insertRun() {
        Run run = new Run();
        run.setId(mHelper.insertRun(run));
        return run;
    }

    public void insertLocation(Location location) {
        if(mcurrentId!=-1){

            mHelper.insertLocation(mcurrentId, location);
        }
    }

    public RunDatabaseHelper.RunCursor querryRuns() {
        return mHelper.querryRuns();
    }


    public Run querryRun(long id) {
        Run run = new Run();
        RunDatabaseHelper.RunCursor runCursor = mHelper.querryRun(id);
        runCursor.moveToFirst();
        if (!runCursor.isAfterLast())

            run = runCursor.getRun();

        runCursor.close();
        return run;
    }

    public Location querryLastLoaction(long id){
        Location location=null;
        RunDatabaseHelper.LocationCursor locationCursor=mHelper.querrylastLocarion(id);
        locationCursor.moveToFirst();
        if(locationCursor.isAfterLast()){
            location=locationCursor.getLocation();
        }
        locationCursor.close();
        return location;
    }
    public RunDatabaseHelper.LocationCursor queryLocationsForRun(long runId){
        return mHelper.queryLocationsForRun(runId);
    }


}
