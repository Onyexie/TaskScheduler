package com.example.taskscheduler;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TaskListActivity extends ListActivity {
	
	private ListView tList;
	private List<String> tData = new ArrayList<String>();
				
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasklist);
    	tList = (ListView)findViewById(android.R.id.list);
        String columns[] = new String[] {"task"};
		Uri movie = Uri.parse("content://com.taskscheduler.provider.Task/tasks/");
		ContentResolver cr = getContentResolver();

		Cursor c = cr.query(movie, columns, null, null, null );

		//Cursor c = managedQuery(movie, columns, null, null, null);
		if (c.moveToFirst()) {
			do {
				// Get the field values
				tData.add(c.getString(c
						.getColumnIndex("task")));				
			} while (c.moveToNext());
		}
		if (c != null && !c.isClosed()) {
            c.close();}
		ArrayAdapter<String> taskList = new ArrayAdapter<String>(this,R.layout.list, tData);
        setListAdapter(taskList);
        if(tData.isEmpty()){
        	setContentView(R.layout.notask);
			tList = (ListView)findViewById(android.R.id.list);
			tList.setVisibility(0);               
        }
	}
	public void refershList(){
		setContentView(R.layout.tasklist);
		tData.clear();		
		String columns[] = new String[] {"task"};
		Uri task = Uri.parse("content://com.taskscheduler.provider.Task/tasks/");
		ContentResolver cr = getContentResolver();

		Cursor c = cr.query(task, columns, null, null, null );

		//Cursor c = managedQuery(task, columns, null, null, null);
		if(c.moveToFirst()){
			do {
				// Get the field values
				tData.add(c.getString(c
						.getColumnIndex("task")));				
			}while (c.moveToNext());
		}
		if (c != null && !c.isClosed()){
            c.close();
        }
		ArrayAdapter<String> taskList = new ArrayAdapter<String>(this,R.layout.list, tData);
        setListAdapter(taskList);
		if(tData.isEmpty()){
        	setContentView(R.layout.notask);
			tList = (ListView)findViewById(android.R.id.list);
			tList.setVisibility(0);               
        }
	}
	public void myClickHandler(View view){
		Intent myIntent2 = new Intent(getApplicationContext(),TaskSchedulerActivity.class);
		switch(view.getId()){
		case R.id.notaskbutton1:
				startActivity(myIntent2);
			break;
		case R.id.tasklistbutton1:
				startActivity(myIntent2);
			break;
		}
	}
	public void onResume(){
		super.onResume();
		refershList();    
	}
		
	public void onPause(){
		super.onPause();
		//this.dh.openHelper.close();
	}
	protected void onListItemClick(ListView l, View v, int position, long id) {	
		String str1=null;
		String str2=null;
		String str3=null;
		String str4=null;
		String columns[] = new String[] {"task", "start", "end", "time"};
		Uri taskuri = Uri.parse("content://com.taskscheduler.provider.Task/tasks/"+(position+1));
		ContentResolver cr = getContentResolver();

		Cursor c = cr.query(taskuri, columns, null, null, null );

		//Cursor c = managedQuery(taskuri, columns, null, null, null);		
		if(c.moveToFirst()){
			// Get the field values
			str1 = c.getString(c.getColumnIndex("task"));
			str2 = c.getString(c.getColumnIndex("start"));
			str3 = c.getString(c.getColumnIndex("end"));
			str4 = c.getString(c.getColumnIndex("time"));			
			
		}		
		str2 = str2.replace("00:00:00 GMT+05:30 ", "");
		str3 = str3.replace("00:00:00 GMT+05:30 ", "");
		Toast.makeText(TaskListActivity.this, "Task: "+str1+"\nStart date: "+str2+"\nStart time: "+str4+"\nDue date: "+str3+"\n", Toast.LENGTH_LONG).show();
		if (c != null && !c.isClosed()){
            c.close();
        }					
	}
}
