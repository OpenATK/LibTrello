package com.openatk.libtrello;

import com.google.gson.Gson;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class TrelloSyncHelper {
	
	Runnable delaySync = null;
	Handler handler = new Handler();
	
	public void syncDelayed(final Context context){
		if(delaySync != null) handler.removeCallbacks(delaySync);
		final Runnable r = new Runnable() {
		    public void run() {
		    	sync(context);
		        delaySync = null;
		    }
		};
		delaySync = r;
        handler.postDelayed(delaySync, 3000);
	}
	
	public void sync(Context context){
		Log.d("TrelloSyncHelper", "Syncing");		
		TrelloContentProvider.Sync(context.getApplicationContext().getPackageName());	
	}
	
	public void autoSyncOn(Context context){
		//Set AutoSync flag, in preferences then trigger sync so it will pick it up
		ContentValues toPass = new ContentValues();
		Gson gson = new Gson();
		TrelloSyncInfo newInfo = new TrelloSyncInfo();
		newInfo.setAutoSync(true);
		String json = gson.toJson(newInfo);
		toPass.put("json", json);
		Uri uri = Uri.parse("content://" + context.getApplicationContext().getPackageName() + ".trello.provider/set_sync_info");
		context.getContentResolver().update(uri, toPass, null, null);  
		
		TrelloContentProvider.Sync(context.getApplicationContext().getPackageName());	
	}
	
	public void autoSyncOff(Context context){
		//Set AutoSync flag
		ContentValues toPass = new ContentValues();
		Gson gson = new Gson();
		TrelloSyncInfo newInfo = new TrelloSyncInfo();
		newInfo.setAutoSync(false);
		String json = gson.toJson(newInfo);
		toPass.put("json", json);
		Uri uri = Uri.parse("content://" + context.getApplicationContext().getPackageName() + ".trello.provider/set_sync_info");
		context.getContentResolver().update(uri, toPass, null, null);  
	}
	
	public void onResume(Context context){
		//Trello app will look to see if autosync is on and sync accordingly
		Log.d("TrelloSyncHelper", "onResume");		
		Bundle bundle = new Bundle();
        bundle.putBoolean("isAutoSyncRequest", true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true); // Performing a sync no matter if it's off
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true); // Performing a sync no matter if it's off
		TrelloContentProvider.Sync(context.getApplicationContext().getPackageName(), bundle);	
	}
}
