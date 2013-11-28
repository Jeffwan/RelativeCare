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
            	// 这边可能需要根据最新的Location 改动一些，还要就是拼接字符串，写个单独的函数，
                String location = GPSInfoProvider.getInstance(context).getLastLocation();
                Log.i(TAG,"return location of the phone"+ location);
                if (TextUtils.isEmpty(location)) {
                    SmsManager.getDefault().sendTextMessage(sender,null,lntlngToLink(location),null,null);
                }
                abortBroadcast();
            }

        }
    }

    // 拼接一下，要考 location 的存储类型
	private String lntlngToLink(String location) {
		// TODO Auto-generated method stub
		return null;
	}
    
    
    
}
