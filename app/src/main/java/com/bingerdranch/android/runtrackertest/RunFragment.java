package com.bingerdranch.android.runtrackertest;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by dsz on 15/7/15.
 */
public class RunFragment extends Fragment {
    private static final String TAG="RunFragment";
    private static final String ARG_RUN_ID="RUN_ID";
    private static final int LOAD_RUN=0;
    private static final int LOAD_LOCATION=1;
    private TextView mStartedTextView,mLatitudeTextView,mLongitudeTextView,mAltitudeTextView,mDurationTime;
    private Button mStartButton,mStopButton,mMapButton;
    private RunManager mRunManager;
    private Location mLocation;
    private Run mRun;
    private LocatonReceiver mLocatonReceiver=new LocatonReceiver(){
        @Override
        protected void onLocationReceived(Context context, Location location) {

            if(!mRunManager.isTrackingRun(mRun)) return;

            mLocation=location;
            if(isVisible()){
                updateUI();
            }

        }

        @Override
        protected void onProviderEnabledChanged(boolean enable) {
            Toast.makeText(getActivity(),(enable?"gps开启":"gps关闭"),Toast.LENGTH_SHORT).show();
        }
    };

    public static RunFragment newInsatnce(long id){
        Bundle args=new Bundle();
        args.putLong(ARG_RUN_ID,id);
        RunFragment fragment=new RunFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRunManager=RunManager.get(getActivity());
        getActivity().registerReceiver(mLocatonReceiver, new IntentFilter(RunManager.ACTION_LOCATION));
        Bundle args=getArguments();
        if(args!=null){
            long runId=args.getLong(ARG_RUN_ID,-1);
            if(runId!=-1){
                mRun=mRunManager.querryRun(runId);
                LoaderManager lm=getLoaderManager();
                lm.initLoader(LOAD_RUN,args,new RunLoaderCallbacks());
                lm.initLoader(LOAD_LOCATION,args,new LocationLoaderCallbacks());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_run,container,false);
        mStartedTextView=(TextView)view.findViewById(R.id.fragment_run_startedTexView);
        mAltitudeTextView=(TextView)view.findViewById(R.id.fragment_run_altitudeTexView);
        mLatitudeTextView=(TextView)view.findViewById(R.id.fragment_run_latitudeTexView);
        mLongitudeTextView=(TextView)view.findViewById(R.id.fragment_run_longitudeTexView);
        mStartButton=(Button)view.findViewById(R.id.fragment_run_startButton);
        mDurationTime=(TextView)view.findViewById(R.id.fragment_run_elapsedTimeTextView);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(mRun!=null){
                        mRunManager.startTrackingRun(mRun);
                    }else{
                        mRun=mRunManager.startNewRun();
                    }
                updateUI();
            }
        });
        mStopButton=(Button)view.findViewById(R.id.fragment_run_stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRunManager.stopRun();
                updateUI();
            }
        });
        mMapButton=(Button)view.findViewById(R.id.fragment_run_mapButton);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),RunMapActivity.class);
                intent.putExtra(RunMapActivity.EXTRA_RUN_ID,mRun.getId());
                startActivity(intent);
            }
        });


        updateUI();
        return view;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mLocatonReceiver);
        super.onDestroy();

    }

    private void updateUI(){
        boolean isStart=mRunManager.isTrackingRun();


        if(mRun!=null){
            mStartedTextView.setText(mRun.getStartDate().toString());
        }
        int durationTime=0;
        if(mLocation!=null&&mRun!=null){
            durationTime=mRun.getDurationSeconds(mLocation.getTime());

            mAltitudeTextView.setText(Double.toString( mLocation.getAltitude()));
            mLatitudeTextView.setText(Double.toString(mLocation.getLatitude()));
            mLongitudeTextView.setText(Double.toString(mLocation.getLongitude()));
            mMapButton.setEnabled(true);
        }else {
            mMapButton.setEnabled(false);
        }
        mDurationTime.setText(Run.formatDuration(durationTime));
        mStartButton.setEnabled(!isStart);
        mStopButton.setEnabled(isStart&&mRunManager.isTrackingRun(mRun));
    }


    private class RunLoaderCallbacks implements LoaderManager.LoaderCallbacks<Run>{
        @Override
        public Loader<Run> onCreateLoader(int id,Bundle args){
            return new RunLoader(getActivity(),args.getLong(ARG_RUN_ID));
        }
        @Override
        public void onLoadFinished(Loader<Run>loader,Run run){
            mRun=run;
            updateUI();
        }
        @Override
        public void onLoaderReset(Loader<Run>loader){

        }

    }
    private class LocationLoaderCallbacks implements LoaderManager.LoaderCallbacks<Location>{
        @Override
        public Loader<Location>onCreateLoader(int id,Bundle args){
            return new LastLocationLoader(getActivity(),args.getLong(ARG_RUN_ID));
        }
        @Override
        public void onLoadFinished(Loader<Location>loader,Location location){
            mLocation=location;
            updateUI();
        }
        @Override
        public void onLoaderReset(Loader<Location> loader){

        }
    }
}
