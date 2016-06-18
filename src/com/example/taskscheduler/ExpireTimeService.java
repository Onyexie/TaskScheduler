package com.example.taskscheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class ExpireTimeService extends Service {
	
	private ArrayList<String> tData=new ArrayList<String>();
	private String str1;
	private String str2;
	private String str3;
	private String str4;
	private String str5;
	private String [][] data;	
		
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate(){
		super.onCreate();
		fetchData();
		//removes previous call backs from the handler
		handler.removeCallbacks(updateTimeTask);
		//executes the run method of the thread after the specified time
		handler.postDelayed(updateTimeTask, 1000);
	}
	
	public void fetchData(){
		Date curr_date = new java.util.Date();
		Calendar cal = Calendar.getInstance();       // get calendar instance
		cal.setTime(curr_date);                      // set cal to date
		cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
		cal.set(Calendar.MINUTE, 0);                 // set minute in hour
		cal.set(Calendar.SECOND, 0);                 // set second in minute
		cal.set(Calendar.MILLISECOND, 0);            // set millis in second 
		curr_date = cal.getTime();             		 // actually computes the new Date
		String columns[] = new String[] {"task", "start", "end", "time" ,"alarm"};
		Uri taskuri = Uri.parse("content://com.taskscheduler.provider.Task/tasks/");
		Cursor c = getApplicationContext().getContentResolver().query(taskuri, columns, "start='"+curr_date.toString()+"'", null, null);
		if(c.moveToFirst()){
			do{
			// Get the field values
			tData.add(c.getString(c.getColumnIndex("task"))+";"+c.getString(c.getColumnIndex("start"))+";"+
			c.getString(c.getColumnIndex("end"))+";"+c.getString(c.getColumnIndex("time"))+";"+
			c.getString(c.getColumnIndex("alarm")));
			Log.i("TDATA",tData.toString());
			}while(c.moveToNext());
		}	
		if (c != null && !c.isClosed()){
            c.close();
        }
		data =new String[tData.size()][5];
		if(!tData.isEmpty()){
			for(int i=0; i<tData.size();i++){
				breakString(tData.get(i));
				data[i][0]=str1;
				data[i][1]=str2;
				data[i][2]=str3;
				data[i][3]=str4+":00";
				data[i][4]=str5;				
			}
		}
	}
	public void stopService(){
		stopSelf();
	}
	//creates a new thread
	private Runnable updateTimeTask = new Runnable() {
		//Runs the Thread
		public void run(){
			try {
				String time = android.text.format.DateFormat.format("k:m:ss", new java.util.Date()).toString();
				for(int i=0; i<tData.size();i++){						
					if(data[i][3].equals(time)){							
						Toast.makeText(ExpireTimeService.this, "Task "+data[i][0]+" expired.", Toast.LENGTH_SHORT).show();						
					}	
				}
				handler.postDelayed(this, 1000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("Error from service", e.toString());
			}					
		}		
	};
	
	private void breakString(String str) {
		// TODO Auto-generated method stub
		str1 = str.substring(0, str.indexOf(";"));
		str = str.substring(str1.length()+1, str.length());
		str2 = str.substring(0, str.indexOf(";"));
		str = str.substring(str2.length()+1, str.length());
		str3 = str.substring(0, str.indexOf(";"));
		str = str.substring(str3.length()+1, str.length());
		str4 = str.substring(0, str.indexOf(";"));
		str5 = str.substring(str4.length()+1, str.length());
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (handler != null)
			handler.removeCallbacks(updateTimeTask);
		handler = null;
	}
/*Creates a handler object, this class provide flexible implementation of threads in Android. Functionally threads created through this method are similar to creating normal threads*/
	private Handler handler = new Handler();
}