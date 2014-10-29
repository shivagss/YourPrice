package com.gabiq.youbid.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;

import com.gabiq.youbid.fragment.AlertDialog;

import java.util.Date;

public class Utils {

    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
    public static final long WEEK_IN_MILLIS = DAY_IN_MILLIS * 7;

    @SuppressWarnings("deprecation")
    public static void showAlertDialog(FragmentManager manager, String title, String message, Boolean status) {
        com.gabiq.youbid.fragment.AlertDialog dialog = new AlertDialog();
        dialog.initialize(title,message);
        dialog.show(manager, "AlertDialog");

    }
    //Deprecated. Always use the above styled Alert with required title and message
    public static void showAlertDialog(Context context, String title, String message, Boolean status) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();
        // Setting Dialog Title
        alertDialog.setTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(message);
         // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
        }
        });
           // Showing Alert Message
            alertDialog.show();
     }


    public static String getRelativeTimeAgo(Date date) {
            return getRelativeTimeSpanString(date.getTime(),
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS).toString();

    }

    /**
     * Only handles past time in twitter format.
     * 2s
     * 2m
     * 2h
     * 3d
     *
     * @param time
     * @param now
     * @param minResolution
     * @return
     */
    public static CharSequence getRelativeTimeSpanString(long time,
                                                                long now,
                                                                long minResolution) {
        StringBuilder result = new StringBuilder();

        Resources r = Resources.getSystem();
        boolean past = (now >= time);
        long duration = Math.abs(now - time);

        long count;
        if (duration < MINUTE_IN_MILLIS && minResolution < MINUTE_IN_MILLIS) {
            count = duration / SECOND_IN_MILLIS;
            if (past) {
                result.append(count);
                result.append("s");
            } else {
                result.append("-");
                result.append(count);
                result.append("s");
            }
        } else if (duration < HOUR_IN_MILLIS && minResolution < HOUR_IN_MILLIS) {
            count = duration / MINUTE_IN_MILLIS;
            if (past) {
                result.append(count);
                result.append("m");
            } else {
                result.append("-");
                result.append(count);
                result.append("m");
            }
        } else if (duration < DAY_IN_MILLIS && minResolution < DAY_IN_MILLIS) {
            count = duration / HOUR_IN_MILLIS;
            if (past) {
                result.append(count);
                result.append("h");
            } else {
                result.append("-");
                result.append(count);
                result.append("h");
            }
        } else if (duration < WEEK_IN_MILLIS && minResolution < WEEK_IN_MILLIS) {
            result.append( DateUtils.getRelativeTimeSpanString(time, now, minResolution) );
        } else {

            result.append( DateUtils.formatDateRange(null, time, time, 0) );
        }

        return result.toString();
    }

    // Play a ring tone when a notification is displayed
    public static void tryPlayRingtone(Context context) {
        try {
            Uri notification = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(
                    context.getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
        }
    }


}
