package com.bingerdranch.android.runtrackertest;

import android.support.v4.app.Fragment;

public class RunActivity extends FragmentSingleActivity {
    public static final  String EXTRA_RUN_ID="com.bingerdranch.android.runtracker.run_id";
    @Override
    public Fragment createFragment() {
        long id=getIntent().getLongExtra(EXTRA_RUN_ID,-1);
        if(id!=-1){
            return RunFragment.newInsatnce(id);
        }else {
            return new RunFragment();
        }
    }
}
