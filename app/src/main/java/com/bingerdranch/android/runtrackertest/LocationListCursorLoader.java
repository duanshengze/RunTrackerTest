package com.bingerdranch.android.runtrackertest;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by dsz on 15/7/25.
 */
public class LocationListCursorLoader extends SQLiteCursorLoader {
    private long mRunId;
    public LocationListCursorLoader(Context context,long runId){
        super(context);
        mRunId=runId;
    }
    @Override
    protected Cursor loadCursor(){
        return RunManager.get(getContext()).queryLocationsForRun(mRunId);
    }

}
