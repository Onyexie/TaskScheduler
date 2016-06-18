package com.example.taskscheduler;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompleteReciever extends BroadcastReceiver {

	public static final String TAG = "ExpireTimeServiceManager";
	 @Override
	 public void onReceive(Context context, Intent intent) {
	  // just make sure we are getting the right intent (better safe than sorry)
	  if( "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
	   ComponentName comp = new ComponentName(context.getPackageName(),ExpireTimeService.class.getName());
	   ComponentName service = context.startService(new Intent().setComponent(comp));
	   if (null == service){
	    // something really wrong here
	    Log.e(TAG, "Could not start service " + comp.toString());
	   }
	  } else {
	   Log.e(TAG, "Received unexpected intent " + intent.toString());   
	  }
	 }
}