package edu.pitt.relativecare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import edu.pitt.relativecare.engine.GPSInfoProvider;

/**
 * Created by jeffwan on 11/19/13.
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "message comes");
        Object[] objs = (Object[]) intent.getExtras().get("pdus");

        for(Object obj : objs) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
            String sender = smsMessage.getOriginatingAddress();
            String body = smsMessage.getMessageBody();

            if ("#*location*#".equals(body)) {
                String location = GPSInfoProvider.getInstance(context).getLastLocation();
                Log.i(TAG,"return location of the phone"+ location);
                if (TextUtils.isEmpty(location)) {
                    SmsManager.getDefault().sendTextMessage(sender,null,location,null,null);
                }
                abortBroadcast();
            }

        }
    }
}
