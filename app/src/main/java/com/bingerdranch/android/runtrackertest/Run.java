package com.bingerdranch.android.runtrackertest;

import java.util.Date;

/**
 * Created by dsz on 15/7/15.
 */
public class Run {
    private long mId;
    private Date mStartDate;

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public Run (){
        mStartDate=new Date();
    }
    public Date getStartDate(){
        return mStartDate;
    }
    public void setStartDate(Date date){
        mStartDate=date;
    }
    public int getDurationSeconds(long endMills){
        return (int)(endMills-mStartDate.getTime())/1000;
    }
    public static String formatDuration(int durationSeconds){
        int seconds=durationSeconds%60;
        int minutes=((durationSeconds-seconds)/60)%60;
        int hours=(durationSeconds-seconds)/3600;
        return String.format("%1$02d:%2$02d:%3$02d",hours,minutes,seconds);

    }

}
