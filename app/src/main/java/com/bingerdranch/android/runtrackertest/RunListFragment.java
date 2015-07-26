package com.bingerdranch.android.runtrackertest;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2015/7/16.
 */
public class RunListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final  int REQUEST_NEW_RUN=0;
    RunDatabaseHelper.RunCursor mRunCursor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(0,null,this);
    }


    private class RunCursorAdapter extends CursorAdapter{


        public RunCursorAdapter(Context context,RunDatabaseHelper.RunCursor cursor){
            super(context,cursor,0);
        }
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(android.R.layout.simple_list_item_1,viewGroup,false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            RunDatabaseHelper.RunCursor runCursor=(RunDatabaseHelper.RunCursor)cursor;
            Run run=runCursor.getRun();
            TextView startTimeTextView=(TextView) view;
            startTimeTextView.setText(String.format("Run at %1$s",run.getStartDate().toString()));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_run,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_new_run:
                Intent intent=new Intent(getActivity(),RunActivity.class);
                startActivityForResult(intent,REQUEST_NEW_RUN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if (REQUEST_NEW_RUN==requestCode){
          getLoaderManager().restartLoader(0,null,this);
        }
    }
    @Override
    public void onListItemClick(ListView listView,View v,int position,long id){
        Intent intent=new Intent(getActivity(),RunActivity.class);
        intent.putExtra(RunActivity.EXTRA_RUN_ID,id);
        startActivity(intent);
    }
private static class RunListCursorLoader extends SQLiteCursorLoader{
    public RunListCursorLoader(Context context){
        super(context);
    }
    @Override
    protected Cursor loadCursor(){
        return RunManager.get(getContext()).querryRuns();
    }

}
    @Override
    public Loader<Cursor> onCreateLoader(int id ,Bundle args){
       return new RunListCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor>loader,Cursor cursor){
        RunCursorAdapter adapter=new RunCursorAdapter(getActivity(),(RunDatabaseHelper.RunCursor)cursor);
        setListAdapter(adapter);
    }
    @Override
    public void onLoaderReset(Loader<Cursor>loader){
        setListAdapter(null);
    }



}
