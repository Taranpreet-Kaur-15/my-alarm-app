package com.example.myalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mSharedPreferences;

    private static final String ALARM_TIME_KEY = "alarm_time";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ListView and adapter
        ListView listView = findViewById(R.id.alarmlist);
        List<AlarmItem> alarms = new ArrayList<>();
        AlarmAdapter adapter = new AlarmAdapter(this, alarms);
        listView.setAdapter(adapter);

        mSharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedTime = mSharedPreferences.getString(ALARM_TIME_KEY, "");
        if (!savedTime.isEmpty()) {

            String[] savedTimes = savedTime.split(",");
            for (String time : savedTimes) {
                // Create an Intent for the AlarmReceiver
                Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                alarmIntent.putExtra("isAlarmSet", true);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

                // Create an AlarmItem from the saved time and PendingIntent
                AlarmItem savedAlarm = new AlarmItem(time, true, pendingIntent);
                alarms.add(savedAlarm);
            }
        }




        Button addButton = findViewById(R.id.setAlarm);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Save the alarm time in SharedPreferences
                                mSharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                String savedTimesString = mSharedPreferences.getString(ALARM_TIME_KEY, null);
                                List<String> savedTimes = new ArrayList<>();
                                if (savedTimesString != null) {
                                    savedTimes = new ArrayList<>(Arrays.asList(savedTimesString.split(",")));
                                }

                                // Create new Alarm object with selected time and add to list
                                String time = String.format("%02d:%02d", hourOfDay, minute);
                                savedTimes.add(time);


                                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                                editor.putString(ALARM_TIME_KEY, TextUtils.join(",", savedTimes));
                                editor.apply();

                                // Create PendingIntent for alarm
                                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                                intent.putExtra("isAlarmSet", true);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

                                AlarmItem newAlarm = new AlarmItem(time, true, pendingIntent);
                                alarms.add(newAlarm);
                                adapter.notifyDataSetChanged();
                            }
                        }, 12, 0, false);
                timePickerDialog.show();
            }
        });



    }
}