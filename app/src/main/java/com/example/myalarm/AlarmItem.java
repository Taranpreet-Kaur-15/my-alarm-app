package com.example.myalarm;

import android.app.PendingIntent;

public class AlarmItem {
    private String time;
    private boolean isEnabled;
    private PendingIntent pendingIntent;

    public AlarmItem(String time, boolean isEnabled, PendingIntent pendingIntent) {
        this.time = time;
        this.isEnabled = isEnabled;
        this.pendingIntent = pendingIntent;
    }

    public PendingIntent getPendingIntent(){
        return pendingIntent;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPendingIntent(PendingIntent pendingIntent){
        this.pendingIntent = pendingIntent;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }
}
