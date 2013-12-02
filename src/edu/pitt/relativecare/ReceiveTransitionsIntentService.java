package edu.pitt.relativecare;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import edu.pitt.relativecare.dao.SimpleGeofence;
import edu.pitt.relativecare.email.GMailSender;
import edu.pitt.relativecare.utils.GeofenceUtils;
import edu.pitt.relativecare.utils.LocationServiceErrorMessages;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * This class receives geofence transition events from Location Services, in the
 * form of an Intent containing the transition type and geofence id(s) that triggered
 * the event.
 */
public class ReceiveTransitionsIntentService extends IntentService {

    private static final String TAG = "ReceiveTransitionsIntentService";
    private SimpleGeofenceStore mPrefs;
    private String devicePhoneNumber;

	/**
     * Sets an identifier for this class' background thread
     */
    public ReceiveTransitionsIntentService() {
        super("ReceiveTransitionsIntentService");

    }

    @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
      mPrefs = new SimpleGeofenceStore(this);
      devicePhoneNumber = mPrefs.getDevicePhoneNumber();
	}

	/**
     * Handles incoming intents
     * @param intent The Intent sent by Location Services. This Intent is provided
     * to Location Services (inside a PendingIntent) when you call addGeofences()
     */
    @Override
    protected void onHandleIntent(Intent intent) {
    	
    	// broadcastIntent 主要是用于error handling, 没错误的话直接 sendNotification了
    	
        // Create a local broadcast Intent
        Intent broadcastIntent = new Intent();

        // Give it the category for all intents sent by the Intent Service
        broadcastIntent.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

        // First check for errors
        if (LocationClient.hasError(intent)) {

            int errorCode = LocationClient.getErrorCode(intent);
            String errorMessage = LocationServiceErrorMessages.getErrorString(this, errorCode);

            // Log the error
            Log.e(GeofenceUtils.APPTAG,getString(R.string.geofence_transition_error_detail, errorMessage)
            );

            // Set the action and error message for the broadcast intent
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCE_ERROR) .putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS, errorMessage);
            // Broadcast the error *locally* to other components in this app
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

        // If there's no error, ***get the transition type***  and ***create a notification***
        } else {

            // Get the type of transition (entry or exit)
            int transition = LocationClient.getGeofenceTransition(intent);

            // Test that a valid transition was reported 
            // 这代码有什么用？难道还有 除了enter 和exit 的transition的类型么, 其实没啥用撒
            if (
                    (transition == Geofence.GEOFENCE_TRANSITION_ENTER)
                    ||
                    (transition == Geofence.GEOFENCE_TRANSITION_EXIT)
               ) {

            	// getTriggeringGeofences 就是看触发这个geofences列表，intent - the intent generated for geofence alert
                // Post a notification
                List<Geofence> geofences = LocationClient.getTriggeringGeofences(intent);
                if (geofences == null) {
                	Log.d(TAG, "onHandleIntent end. trigger is null");
                	return;
                }
                
                for (Geofence geofence : geofences ) {
                	SimpleGeofenceStore store = new SimpleGeofenceStore(getApplicationContext());
                	SimpleGeofence simpleGeofence = store.getGeofence(geofence.getRequestId()); //这个requestId什么来头？
                	String transitionType = getTransitionString(transition);
                	
                	// 任何 Enter Exit 后面的行为，都可以在这里添加
                	sendNotification(transitionType, simpleGeofence.getName());

                }
                
                
                
//                String[] geofenceIds = new String[geofences.size()];
//                for (int index = 0; index < geofences.size() ; index++) {
//                    geofenceIds[index] = geofences.get(index).getRequestId();
//                }
                
                // 这里把所有符合条件的geofence join在一起了, 这是sample code思路
                // 小日本-- 循环里面sendNotification，也就是说每一个都单独的send一条，因为一般不会重合设置，所以我觉得小日本的思路较好
                //String ids = TextUtils.join(GeofenceUtils.GEOFENCE_ID_DELIMITER,geofenceIds);
//                String transitionType = getTransitionString(transition);
                
                // success 没问题的话，sendNotification 出去
//                sendNotification(transitionType, ids);

                // Log the transition type and a message, getString 方法不就是R.id.title 一个参数么？但是为啥？
//                Log.d(GeofenceUtils.APPTAG,
//                        getString(
//                                R.string.geofence_transition_notification_title,
//                                transitionType,
//                                ids));
//                Log.d(GeofenceUtils.APPTAG,
//                        getString(R.string.geofence_transition_notification_text));

            // An invalid transition was reported
            } else {
                // Always log as an error
                Log.e(GeofenceUtils.APPTAG,
                        getString(R.string.geofence_transition_invalid_type, transition));
            }
        }
    }

    // Use Gmails Authentication to send Email
    private void sendEmail(String emailBody) {
    	// get contact information
    	String email = mPrefs.getContactEmail();
    	Log.i(TAG,"Email: " + email);
    	
    	try {   
            GMailSender sender = new GMailSender("relativecares@gmail.com", "relative123"); // 账号密码
            sender.sendMail("Email from Relative Care",   
            		devicePhoneNumber + " " + emailBody,   
                    "relativecares@gmail.com",
                    email);   
            
            Log.i(TAG, "send email successfully");
        } catch (Exception e) {   
            Log.e("SendMail", e.getMessage(), e);   
        } 	
	}
    
    private void sendMessage(String smsBody) {
    	String number = mPrefs.getContactNumber();
    	Log.i(TAG, "Number: "+number);
    	// TODO: change API 4.0 version to replace the deprecated ones.
    	SmsManager.getDefault().sendTextMessage(number,null,devicePhoneNumber + " "  + smsBody,null,null);
    	Log.i(TAG, "send message successfully");
    	
    }
    

	/**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the main Activity.
     * @param transitionType The type of transition that occurred.
     *
	 */
    private void sendNotification(String transitionType, String name) {

        // Create an explicit content Intent that starts the main Activity
        Intent notificationIntent = new Intent(getApplicationContext(),GeoFenceActivity.class);

        // Construct a task stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack，这个Noticfication是不是用于返回MainActivity.class的
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Set the notification contents
        builder.setSmallIcon(R.drawable.ic_notification)
               .setContentTitle( getString(R.string.geofence_transition_notification_title,
                               				transitionType, name))
               .setContentText(getString(R.string.geofence_transition_notification_text))
               .setContentIntent(notificationPendingIntent);

        // Get an instance of the Notification manager，Issue the notification
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
        
        // Send email and sms
    	sendEmail(getString(R.string.geofence_transition_notification_title,
   				transitionType, name));
    	sendMessage(getString(R.string.geofence_transition_notification_title,
   				transitionType, name));
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);

            default:
                return getString(R.string.geofence_transition_unknown);
        }
    }
}
