package com.bingerdranch.android.runtrackertest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.util.Date;

/**
 * Created by dsz on 15/7/15.
 */
public class RunDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "run.sqlite";
    private static final String TABLE_RUN = "run";
    private static final String TABLE_LOCATION = "location";
    private static final String COLUMN_RUN_START_TIME = "start_time";
    private static final String COLUMN_RUN_ID = "_id";
    private static int VERSION = 1;
    private static final String COlUMN_LOCATION_LATITUDE = "latitude";
    private static final String COLUMN_LOCATION_LONGITUDE = "longitude";
    private static final String COLUMN_LOCATION_ALTITUDE = "altitude";
    private static final String COLUMN_LOCATION_RUN_ID = "run_id";
    private static final String COLUMN_LOCATION_PROVIDER = "provider";
    private static final String COLUMN_LOCATION_TIMESTEMP = "timestamp";


    public RunDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table run(_id integer primary key autoincrement,start_time integer)");
        db.execSQL("create table location(timestamp integer,longitude real,latitude real,"
                + "altitude real," +
                "provider varchar(100),run_id integer references run(_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertRun(Run run) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RUN_START_TIME, run.getStartDate().getTime());
        return getWritableDatabase().insert(TABLE_RUN, null, cv);
    }

    public long insertLocation(long runId, Location location) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LOCATION_TIMESTEMP, location.getTime());
        cv.put(COLUMN_LOCATION_LONGITUDE, location.getLongitude());
        cv.put(COlUMN_LOCATION_LATITUDE, location.getLatitude());
        cv.put(COLUMN_LOCATION_ALTITUDE, location.getAltitude());

        cv.put(COLUMN_LOCATION_PROVIDER, location.getProvider());

        cv.put(COLUMN_LOCATION_RUN_ID, runId);
        return getWritableDatabase().insert(TABLE_LOCATION, null, cv);
    }

    public RunCursor querryRuns() {
        Cursor cursor = getReadableDatabase().query(TABLE_RUN, null, null, null, null, null, COLUMN_RUN_START_TIME + " asc");
        return new RunCursor(cursor);
    }

    public RunCursor querryRun(long id) {
        Cursor cursor = getReadableDatabase().query(TABLE_RUN, null, COLUMN_RUN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, "1");
        return new RunCursor(cursor);
    }


    public class RunCursor extends CursorWrapper {
        public RunCursor(Cursor cursor) {
            super(cursor);
        }

        public Run getRun() {
            if (isAfterLast() || isBeforeFirst()) return null;
            Run run = new Run();
            long runId = getLong(getColumnIndex(COLUMN_RUN_ID));
            long startTime = getLong(getColumnIndex(COLUMN_RUN_START_TIME));
            run.setId(runId);
            run.setStartDate(new Date(startTime));
            return run;
        }

    }
    public LocationCursor querrylastLocarion(long id){
            Cursor cursor=getReadableDatabase().query(TABLE_LOCATION,null,COLUMN_LOCATION_RUN_ID+"=?",null,null,null,COLUMN_LOCATION_TIMESTEMP+" desc","1");
            return  new LocationCursor(cursor);
        }
    public class LocationCursor extends CursorWrapper{
        public LocationCursor(Cursor cursor){
            super(cursor);
        }
        public Location getLocation(){
            if(isAfterLast()||isBeforeFirst()) return null ;
            String provider=getString(getColumnIndex(COLUMN_LOCATION_PROVIDER));
            Location location=new Location(provider);
            location.setAltitude(getDouble(getColumnIndex(COLUMN_LOCATION_ALTITUDE)));
            location.setLatitude(getDouble(getColumnIndex(COlUMN_LOCATION_LATITUDE)));
            location.setLongitude(getDouble(getColumnIndex(COLUMN_LOCATION_LONGITUDE)));
            location.setTime(getLong(getColumnIndex(COLUMN_LOCATION_TIMESTEMP)));
            return location;

        }
    }
    public LocationCursor queryLocationsForRun(long runId){
        Cursor wrapped=getReadableDatabase().query(
                TABLE_LOCATION,
                null,
                COLUMN_LOCATION_RUN_ID+"=?",
                new String[]{String.valueOf(runId)},
                null,
                null,
                COLUMN_LOCATION_TIMESTEMP+" asc");
        return new LocationCursor(wrapped);

    }
}
