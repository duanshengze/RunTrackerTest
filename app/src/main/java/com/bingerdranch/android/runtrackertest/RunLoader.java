package com.bingerdranch.android.runtrackertest;

import android.content.Context;

/**
 * Created by dsz on 15/7/24.
 */
public class RunLoader extends DataLoader<Run> {
    private long mRunId;
    public  RunLoader(Context context,long runId){
        super(context);
        mRunId=runId;
    }
    @Override
    public  Run loadInBackground(){
        return RunManager.get(getContext()).querryRun(mRunId);
    }
}
