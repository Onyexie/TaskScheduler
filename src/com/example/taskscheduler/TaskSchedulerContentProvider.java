package com.example.taskscheduler;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class TaskSchedulerContentProvider extends ContentProvider {
	
	public static final String _ID = "id";
	public static final String NAME = "task";
	//private static final String TAG = "TaskSchedulerContentProvider";		 
	private static final String DATABASE_NAME = "TaskScheduler.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "Task";	 
	public static final String PROVIDER_NAME = 
	      "com.taskscheduler.provider.Task";
	public static final Uri CONTENT_URI = 
	      Uri.parse("content://"+ PROVIDER_NAME + "/tasks");		 
	private static final int ALLTASK = 1;
	private static final int TASK_ID = 2;
	private SQLiteDatabase taskDB;    
	public static class OpenHelper extends SQLiteOpenHelper {

	      OpenHelper(Context context) {
	         super(context, DATABASE_NAME, null, DATABASE_VERSION);
	      }

	      @Override
	      public void onCreate(SQLiteDatabase db) {
	         db.execSQL("CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY, key TEXT, task TEXT, start TEXT, end TEXT, time TEXT, alarm TEXT)");
	      }
	      
	      @Override
	      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	         Log.w("Example", "Upgrading database, this will drop tables and recreate.");
	         db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	         onCreate(db);
	      }
	   }
	//private OpenHelper dbHelper;

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		int count=0;
	      switch (uriMatcher.match(arg0)){
	         case ALLTASK:
	            count = taskDB.delete(
	               TABLE_NAME,
	               arg1, 
	               arg2);
	            break;
	         case TASK_ID:
	            String id = arg0.getPathSegments().get(1);
	            count = taskDB.delete(
	            		TABLE_NAME,                        
	               _ID + " = " + id + 
	               (!TextUtils.isEmpty(arg1) ? " AND (" + 
	               arg1 + ')' : ""), 
	               arg2);
	            break;
	         default: throw new IllegalArgumentException(
	            "Unknown URI " + arg0);    
	      }       
	      getContext().getContentResolver().notifyChange(arg0, null);
	      return count;	
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		switch (uriMatcher.match(arg0)){
        //---get all reviews---
        case ALLTASK:
           return "vnd.android.cursor.dir/vnd.taskscheduler.tasks";
        //---get a particular review---
        case TASK_ID:                
           return "vnd.android.cursor.item/vnd.taskscheduler.tasks";
        default:
           throw new IllegalArgumentException("Unsupported URI: " + arg0);        
     }
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) { 
		// TODO Auto-generated method stub
		long rowID = taskDB.insert(
		         TABLE_NAME, "", arg1);
		 
		      //---if added successfully---
		      if (rowID>0)
		      {
		         Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
		         getContext().getContentResolver().notifyChange(_uri, null);    
		         return _uri;                
		      }        
		      throw new SQLException("Failed to insert row into " + arg0);
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		 Context context = getContext();
	     OpenHelper dbHelper = new OpenHelper(context);
	     taskDB = dbHelper.getWritableDatabase();
	     return (taskDB == null)? false:true;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
	      sqlBuilder.setTables(TABLE_NAME);
	 
	      if (uriMatcher.match(arg0) == TASK_ID)
	         //---if getting a particular book---
	         sqlBuilder.appendWhere(
	            _ID + " = " + arg0.getPathSegments().get(1));                
	 
	      Cursor c = sqlBuilder.query(
	    		  taskDB, 
	         arg1, 
	         arg2, 
	         arg3, 
	         null, 
	         null, 
	         arg4);
	 
	      //---register to watch a content URI for changes---
	      c.setNotificationUri(getContext().getContentResolver(), arg0);
	      return c;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		int count = 0;
	      switch (uriMatcher.match(arg0)){
	         case ALLTASK:
	            count = taskDB.update(
	            		TABLE_NAME, 
	            		arg1,
	            		arg2, 
	            		arg3);
	            break;
	         case TASK_ID:                
	            count = taskDB.update(
	            		TABLE_NAME, 
	            		arg1,
	               _ID + " = " + arg0.getPathSegments().get(1) + 
	               (!TextUtils.isEmpty(arg2) ? " AND (" + 
	            		   arg2 + ')' : ""), 
	            		   arg3);
	            break;
	         default: throw new IllegalArgumentException(
	            "Unknown URI " + arg0);    
	      }       
	      getContext().getContentResolver().notifyChange(arg0, null);
	      return count;
	}
	private static final UriMatcher uriMatcher;
	static{
	      uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	      uriMatcher.addURI(PROVIDER_NAME, "tasks", ALLTASK);
	      uriMatcher.addURI(PROVIDER_NAME, "tasks/#", TASK_ID);      
	   }   
}
