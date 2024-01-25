package com.example.myalarm;

import static java.security.AccessController.getContext;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class AlarmAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
    private Context context;
    private List<AlarmItem> items;

    public AlarmAdapter(Context context, List<AlarmItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.listitems, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.timeTextView = view.findViewById(R.id.timeText);
            holder.deleteButton = view.findViewById(R.id.deleteButton);
            holder.enableSwitch = view.findViewById(R.id.alarmSwitch);

            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        AlarmItem item = items.get(position);

        // Set the time text
        holder.timeTextView.setText(item.getTime());

        //  switch button
        holder.enableSwitch.setTag(position);
        holder.enableSwitch.setOnCheckedChangeListener(null);
        holder.enableSwitch.setChecked(item.isEnabled());
        holder.enableSwitch.setOnCheckedChangeListener(this);


        holder.deleteButton.setOnClickListener(v -> {
            items.remove(position);
            notifyDataSetChanged();
        });

        return view;
    }

        @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = (Integer) buttonView.getTag();
        AlarmItem alarm = (AlarmItem) getItem(position);
        alarm.setEnabled(isChecked);
        if (isChecked) {

            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("isAlarmSet", true);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            // Save the PendingIntent to the AlarmItem object
            alarm.setPendingIntent(pendingIntent);

            // Set the alarm to go off at the specified time
            String[] parts = alarm.getTime().split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                // If the alarm time has already passed today, set it for tomorrow
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
            Toast.makeText(context, "Alarm is set for "+alarm.getTime(), Toast.LENGTH_SHORT).show();
        } else {

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = alarm.getPendingIntent();
            alarmManager.cancel(pendingIntent);
            Toast.makeText(context, "Alarm cancelled", Toast.LENGTH_SHORT).show();
        }
    }



    private static class ViewHolder {
        TextView timeTextView;
        Button deleteButton;
        Switch enableSwitch;
    }
}

