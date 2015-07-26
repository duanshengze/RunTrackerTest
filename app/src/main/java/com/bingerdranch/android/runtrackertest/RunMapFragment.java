package com.bingerdranch.android.runtrackertest;

import android.content.res.Resources;
import android.database.Cursor;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;

/**
 * Created by dsz on 15/7/25.
 */
public class RunMapFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_RUN_ID="RUN_ID";
    private static final int LOAD_LOCATIONS=0;
    private GoogleMap mGoogleMap;
   private RunDatabaseHelper.LocationCursor mLocationCursor;
    public static RunMapFragment newInstance(long runId){
        Bundle args=new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunMapFragment rf=new RunMapFragment();
        rf.setArguments(args);
        return rf;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle args=getArguments();
        if(args!=null){
            long runId=args.getLong(ARG_RUN_ID,-1);
            if(runId!=-1){
                LoaderManager lm=getLoaderManager();
                lm.initLoader(LOAD_LOCATIONS,args,this);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id,Bundle args){
        long runId=args.getLong(ARG_RUN_ID, -1);
        return new LocationListCursorLoader(getActivity(),runId);
    }
    @Override
    public void onLoadFinished(Loader<Cursor>loader,Cursor cursor){
        mLocationCursor=(RunDatabaseHelper.LocationCursor)cursor;
        updateUI();
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mLocationCursor.close();
        mLocationCursor=null;
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
        View v=super.onCreateView(inflater,parent,savedInstanceState);
        mGoogleMap=getMap();
        mGoogleMap.setMyLocationEnabled(true);
        return v;
    }
//    ui更新
    private void updateUI(){
        if(mGoogleMap==null||mLocationCursor==null)return;
//        创建用于地图的线条
        PolylineOptions line=new PolylineOptions();
//       用于在所有的数据都收集完时，创建一个供地图缩放的包围框
        LatLngBounds.Builder latlngBuilder=new LatLngBounds.Builder();
        mLocationCursor.moveToFirst();
        while (!mLocationCursor.isAfterLast()){
            Location loc=mLocationCursor.getLocation();
            LatLng latLng=new LatLng(loc.getLatitude(),loc.getLongitude());
            Resources r=getResources();
            if(mLocationCursor.isFirst()){
                String startDate=new Date(loc.getTime()).toString();
                MarkerOptions startMarkerOptions=new MarkerOptions()
                        .position(latLng)
                        .title(r.getString(R.string.run_start))
                        .snippet(r.getString(R.string.run_started_at_format,startDate));
                mGoogleMap.addMarker(startMarkerOptions);

            }else if(mLocationCursor.isLast()){
                String endDate=new Date(loc.getTime()).toString();
                MarkerOptions finishMarkerOptions=new MarkerOptions()
                        .position(latLng)
                        .title(r.getString(R.string.run_finish))
                        .snippet(r.getString(R.string.run_finished_at_format,endDate));
                mGoogleMap.addMarker(finishMarkerOptions);
            }
            line.add(latLng);
            latlngBuilder.include(latLng);
            mLocationCursor.moveToNext();
        }
        mGoogleMap.addPolyline(line);
        Display display=getActivity().getWindowManager().getDefaultDisplay();
        LatLngBounds latLngBounds=latlngBuilder.build();
        CameraUpdate movement= CameraUpdateFactory.newLatLngBounds(latLngBounds,display.getWidth(),display.getHeight(),15);
        mGoogleMap.moveCamera(movement);

    }



}
