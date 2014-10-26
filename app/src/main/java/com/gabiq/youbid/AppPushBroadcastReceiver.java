package com.gabiq.youbid;


import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.gabiq.youbid.activity.HomeActivity;
import com.gabiq.youbid.model.Notification;
import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class AppPushBroadcastReceiver extends ParsePushBroadcastReceiver {

    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        return HomeActivity.class;
    }


    @Override
    protected void onPushOpen(Context context, Intent intent) {

        ParseAnalytics.trackAppOpened(intent);

        String uriString = null;
        try {
            JSONObject pushData = new JSONObject(intent.getStringExtra("com.parse.Data"));
            uriString = pushData.optString("uri");
        } catch (JSONException e) {
            Log.v("com.parse.ParsePushReceiver", "Unexpected JSONException when receiving push data: ", e);
        }
        Class<? extends Activity> cls = getActivity(context, intent);
        Intent activityIntent;
        if (uriString != null && !uriString.isEmpty()) {
            activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        } else {
            activityIntent = new Intent(context, HomeActivity.class);
        }

        Bundle extras = intent.getExtras();
        activityIntent.putExtras(extras);
        if (Build.VERSION.SDK_INT >= 16) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(cls);
            stackBuilder.addNextIntent(activityIntent);
            stackBuilder.startActivities();
        } else {
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(activityIntent);
        }
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        if (intent.hasExtra("ordered")) {
            super.onPushReceive(context, intent);

            if (extras != null) {
                createNotification(extras);
            }

        } else {
            Intent newIntent = new Intent("com.parse.push.intent.RECEIVE");
            newIntent.putExtras(intent.getExtras());
            newIntent.putExtra("ordered", true);
            context.sendOrderedBroadcast(newIntent, null, null, null, Activity.RESULT_OK, null, null);
        }
    }

    private void createNotification(Bundle extra) {
        String jsonString = extra.getString("com.parse.Data");
        if (jsonString != null) {
            try {
                JSONObject json = new JSONObject(jsonString);
                if (json != null) {
                    Notification notification = new Notification();

                    if (json.has("alert")) {
                        String message = json.getString("alert");
                        notification.setMessage(message);
                    }

                    if (json.has("senderId")) {
                        String senderId = json.getString("senderId");
                        notification.setSenderId(senderId);
                    }

                    if (json.has("bidId")) {
                        String bidId = json.getString("bidId");
                        notification.setBidId(bidId);
                    }

                    if (json.has("itemId")) {
                        String itemId = json.getString("itemId");
                        notification.setItemId(itemId);
                    }

                    if (json.has("type")) {
                        String type = json.getString("type");
                        notification.setType(type);
                    }

                    Date createdTime = new Date();
                    notification.setCreatedAt(createdTime);


                    notification.save();
                    notification.loadUserInfo();
                }
            } catch (JSONException e) {
                Log.e("Error", "Error parsing json in push notification " + e.toString());
            }
        }
    }

}
