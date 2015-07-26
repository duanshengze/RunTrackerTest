package com.bingerdranch.android.runtrackertest;

import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2015/7/16.
 */
public class RunListActivity extends  FragmentSingleActivity {
    @Override
    public Fragment createFragment() {
        return new RunListFragment();
    }

}
