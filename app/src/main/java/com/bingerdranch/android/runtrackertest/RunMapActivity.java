package com.bingerdranch.android.runtrackertest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RunMapActivity extends FragmentSingleActivity {
    public static final String EXTRA_RUN_ID="com.bingerfanch.android.runtracker.run_id";
    @Override
    public Fragment createFragment(){
        long runId=getIntent().getLongExtra(EXTRA_RUN_ID,-1);
        if (runId!=-1){
            return RunMapFragment.newInstance(runId);
        }else {
            return new RunMapFragment();
        }

    }


}
