package com.example.taskscheduler;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class TaskSchedulerActivity extends Activity {
	/** Called when the activity is first created. */

	private DatePicker sDate;
	private DatePicker dDate;
	private TimePicker sTime;
	private EditText mNotes;
	private CheckBox mAlarm;
	boolean FLAG = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		sDate = (DatePicker) findViewById(R.id.mdatePicker1);
		dDate = (DatePicker) findViewById(R.id.mdatePicker2);
		sTime = (TimePicker) findViewById(R.id.mtimePicker1);
		mNotes = (EditText) findViewById(R.id.meditText1);
		mAlarm = (CheckBox) findViewById(R.id.mcheckBox1);		
	}

	public void myClickHandler(View view) {
		try {
			switch (view.getId()) {
			case R.id.mbutton1: {
				save();
				break;
			}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("Error", e.toString());
		}
	}

	public void save() {
		
		int st_day = sDate.getDayOfMonth();
		int st_mon = sDate.getMonth();
		int st_year = sDate.getYear();
		
		Date st_date = new Date (st_year-1900, st_mon, st_day);
		
		//mNotes.setText ("Start date: " + st_date.toString());
		
		int due_day = dDate.getDayOfMonth();
		int due_mon = dDate.getMonth();
		int due_year = dDate.getYear();

		Date due_date = new Date (due_year-1900, due_mon, due_day);
		//mNotes.setText (mNotes.getText() + "\nDue date: " + due_date.toString());
		
		int st_hour = sTime.getCurrentHour();
		int st_min = sTime.getCurrentMinute();
		
		String task = String.valueOf(mNotes.getText());
		Boolean alarm = mAlarm.isChecked();
		
		Date curr_date = new java.util.Date();
		int curr_hour = curr_date.getHours();
		int curr_min = curr_date.getMinutes();
		
		Calendar cal = Calendar.getInstance();       // get calendar instance 
		cal.setTime(curr_date);                           // set cal to date 
		cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight 
		cal.set(Calendar.MINUTE, 0);                 // set minute in hour 
		cal.set(Calendar.SECOND, 0);                 // set second in minute 
		cal.set(Calendar.MILLISECOND, 0);            // set millis in second 
		curr_date = cal.getTime();             		// actually computes the new Date 
		
		
		//mNotes.setText (mNotes.getText() + "\nCurr date: " + curr_date.toString());

		if (st_date.compareTo(curr_date)<0)
		{
			Toast.makeText(TaskSchedulerActivity.this,"Start date/time cannot be less than the current date/time.", Toast.LENGTH_SHORT).show();
			return;
		}
		else if (st_date.compareTo(curr_date)== 0)
		{
			if (st_hour < curr_hour)
			{
				Toast.makeText(TaskSchedulerActivity.this,"Start date/time cannot be less than the current date/time.", Toast.LENGTH_SHORT).show();
				return;
			}
			else if ((st_hour == curr_hour) && (st_min < curr_min))
			{
				Toast.makeText(TaskSchedulerActivity.this,"Start date/time cannot be less than the current date/time.", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		if (due_date.compareTo(st_date) < 0)
			Toast.makeText(TaskSchedulerActivity.this,"Due date cannot be less than start date.", Toast.LENGTH_SHORT).show();
		else if (task.equalsIgnoreCase(""))
			Toast.makeText(TaskSchedulerActivity.this,"The Task field cannot be left blank.", Toast.LENGTH_SHORT).show();
		else{
			ContentValues values = new ContentValues();
			values.put("key", task+""+st_date.toString()+""+String.valueOf(st_hour)+":"+String.valueOf(st_min));
			values.put("task", task);
			values.put("start", st_date.toString());
			values.put("end", due_date.toString());
			values.put("time", String.valueOf(st_hour)+":"+String.valueOf(st_min));
			values.put("alarm", alarm.toString());
			getContentResolver().insert(Uri.parse("content://com.taskscheduler.provider.Task/tasks"), values);
			Intent intent = new Intent(this, ExpireTimeService.class); 
	        stopService(intent);
	    	startService(intent);
			finish();
		}
	}
}