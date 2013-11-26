package edu.pitt.relativecare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.pitt.relativecare.GeofenceUtils.REMOVE_TYPE;
import edu.pitt.relativecare.GeofenceUtils.REQUEST_TYPE;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jeffwan on 11/19/13.
 */
public class GeoFenceActivity extends Activity implements GoogleMap.OnMapLongClickListener {

	private static final String TAG = "GeoFence";
    private GoogleMap mMap;
    private MapFragment mMapFragment;

    
    /*
     * Use to set an expiration time for a geofence. After this amount
     * of time Location Services will stop tracking the geofence.
     * Remember to unregister a geofence when you're finished with it.
     * Otherwise, your app will use up battery. To continue monitoring
     * a geofence indefinitely, set the expiration time to
     * Geofence#NEVER_EXPIRE.
     */
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * DateUtils.HOUR_IN_MILLIS;

    // Store the current request
    private REQUEST_TYPE mRequestType;
    
    // Store the current type of removal
    private REMOVE_TYPE mRemoveType;
    
    // Persistent storage for geofences
    private SimpleGeofenceStore mPrefs;
    
    // Store a list of geofences to add
    List<Geofence> mCurrentGeofences;
    
    // Add geofences handler
    private GeofenceRequester mGeofenceRequester;
    
    // Remove geofences handler
    private GeofenceRemover mGeofenceRemover;
    
    /*
     * Internal lightweight geofence objects for geofence 1 and 2
     */
    private SimpleGeofence mUIGeofence1;
    
    private SimpleGeofence geofenceMap;
    
    // decimal formats for latitude, longitude, and radius
    private DecimalFormat mLatLngFormat;
    private DecimalFormat mRadiusFormat;
    
    /*
     * An instance of an inner class that receives broadcasts from listeners and from the
     * IntentService that receives geofence transition events
     */
    private GeofenceSampleReceiver mBroadcastReceiver;

    // An intent filter for the broadcast receiver
    private IntentFilter mIntentFilter;

    // Store the list of geofences to remove
    private List<String> mGeofenceIdsToRemove;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Handle LatLng format
        String latLngPattern = getString(R.string.lat_lng_pattern);
        mLatLngFormat = new DecimalFormat(latLngPattern);
        mLatLngFormat.applyLocalizedPattern(mLatLngFormat.toLocalizedPattern());
        
        // Handle radius  format
        String radiusPattern = getString(R.string.radius_pattern);
        mRadiusFormat = new DecimalFormat(radiusPattern);
        mRadiusFormat.applyLocalizedPattern(mRadiusFormat.toLocalizedPattern());
        
        /**** 我自己其实是还没有理解 Receiver 和IntentFilter 在这里作用的  ****/
        // Create a new broadcast receiver to receive updates from the listeners and service
        mBroadcastReceiver = new GeofenceSampleReceiver();
        
        // Create an intent filter for the broadcast receiver
        mIntentFilter = new IntentFilter();
        
        // Action for broadcast Intents that report successful addition of geofences, 
        // removal of geofences, containing various types of geofencing errors
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);
        
        // All Location Services sample apps use this category
        mIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);
        
        mPrefs = new SimpleGeofenceStore(this);
        
        // Instantiate the current List of geofences --  这个还要往地图里塞的，记住了你可
        mCurrentGeofences = new ArrayList<Geofence>();
        // Instantiate a Geofence requester
        mGeofenceRequester = new GeofenceRequester(this);
        // Instantiate a Geofence remover
        mGeofenceRemover = new GeofenceRemover(this);

        
        geofenceMap = mPrefs.getGeofence("1");
        
        
        setContentView(R.layout.activity_geofence);
       
        // 判断连接状态
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if(status!= ConnectionResult.SUCCESS){
            int requestCode=10;
            Dialog dialog=GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }
        else{
            mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setMyLocationEnabled(true);
            mMap.setIndoorEnabled(true);
            mMap.setOnMapLongClickListener(this);
            
            if (geofenceMap!=null) {
                LatLng point = new LatLng(geofenceMap.getLatitude(), geofenceMap.getLongitude());
                // 1. Add a marker to map with info window
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(point)
                        .title(geofenceMap.getName())
                        .snippet("Ridus: "+geofenceMap.getRadius())
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                Marker marker = mMap.addMarker(markerOptions);
                marker.showInfoWindow();

                // 2. Instantiates a new CircleOptions object and defines the center and radius
                CircleOptions circleOptions = new CircleOptions()
                        .strokeColor(Color.RED)
                        .strokeWidth(0)
                        .fillColor(0x40ff0000)
                        .center(point)
                        .radius(geofenceMap.getRadius()); // In meters -- here customize according to the user configuration
                Circle circle = mMap.addCircle(circleOptions);
            }

        }
    }
    
    /*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * GeofenceRemover and GeofenceRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     * calls
     */
    
    // Seems error handling if error activity starts. 先不用管他们 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // If the request was to add geofences
                        if (GeofenceUtils.REQUEST_TYPE.ADD == mRequestType) {
                            // Toggle the request flag and send a new request
                            mGeofenceRequester.setInProgressFlag(false);
                            // Restart the process of adding the current geofences
                            mGeofenceRequester.addGeofences(mCurrentGeofences);
                        // If the request was to remove geofences
                        } else if (GeofenceUtils.REQUEST_TYPE.REMOVE == mRequestType ){
                            // Toggle the removal flag and send a new removal request
                            mGeofenceRemover.setInProgressFlag(false);
                            // If the removal was by Intent
                            if (GeofenceUtils.REMOVE_TYPE.INTENT == mRemoveType) {
                                // Restart the removal of all geofences for the PendingIntent
                                mGeofenceRemover.removeGeofencesByIntent(
                                    mGeofenceRequester.getRequestPendingIntent());
                            // If the removal was by a List of geofence IDs
                            } else {
                                // Restart the removal of the geofence list
                                mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);
                            }
                        }
                    break;
                    // If any other result was returned by Google Play services
                    default:
                        // Report that Google Play services was unable to resolve the problem.
                        Log.d(GeofenceUtils.APPTAG, getString(R.string.no_resolution));
                }

            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d(GeofenceUtils.APPTAG,
                       getString(R.string.unknown_activity_request_code, requestCode));

               break;
        }
    }

    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// Register the broadcast receiver to receive status updates
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, mIntentFilter);
        /*
         * Get existing geofences from the latitude, longitude, and
         * radius values stored in SharedPreferences. If no values
         * exist, null is returned.
         */
        mUIGeofence1 = mPrefs.getGeofence("1");
        /*
         * If the returned geofences have values, use them to set
         * values in the UI, using the previously-defined number
         * formats.
         */
        /*** 这个地方的作用就是让界面回来以后，这些值还在界面显示，我觉得不用写onResume也么的问题 ****/
        if (mUIGeofence1 != null) {
//            mLatitude1.setText(mLatLngFormat.format(mUIGeofence1.getLatitude()));
//            mLongitude1.setText(mLatLngFormat.format(mUIGeofence1.getLongitude()));
//            mRadius1.setText(mRadiusFormat.format(mUIGeofence1.getRadius()));
        }

	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
    	MenuInflater inflator = getMenuInflater();
    	inflator.inflate(R.menu.menu, menu);
		return true;
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		mPrefs.setGeofence("1", mUIGeofence1);
//      mPrefs.setGeofence("2", mUIGeofence2);
	}

	/**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d(GeofenceUtils.APPTAG, getString(R.string.play_services_available));
            // Continue
            return true;             

        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
//                errorFragment.show(getSupportFragmentManager(), GeofenceUtils.APPTAG);
            }
            return false;
        }
    }
	
    /**
     * Called when the user clicks the "Remove geofences" button
     *
     * @param view The view that triggered this callback
     */
    public void onUnregisterByPendingIntentClicked(View view) {
        /*
         * Remove all geofences set by this app. To do this, get the
         * PendingIntent that was added when the geofences were added
         * and use it as an argument to removeGeofences(). The removal
         * happens asynchronously; Location Services calls
         * onRemoveGeofencesByPendingIntentResult() (implemented in
         * the current Activity) when the removal is done
         */

        /*
         * Record the removal as remove by Intent. If a connection error occurs,
         * the app can automatically restart the removal if Google Play services
         * can fix the error
         */
        // Record the type of removal
        mRemoveType = GeofenceUtils.REMOVE_TYPE.INTENT;

        /*
         * Check for Google Play services. Do this after
         * setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to
         * know what type of request was in progress.
         */
        if (!servicesConnected()) {
            return;
        }

        // Try to make a removal request
        try {
        /*
         * Remove the geofences represented by the currently-active PendingIntent. If the
         * PendingIntent was removed for some reason, re-create it; since it's always
         * created with FLAG_UPDATE_CURRENT, an identical PendingIntent is always created.
         */
        mGeofenceRemover.removeGeofencesByIntent(mGeofenceRequester.getRequestPendingIntent());

        } catch (UnsupportedOperationException e) {
            // Notify user that previous request hasn't finished.
            Toast.makeText(this, R.string.remove_geofences_already_requested_error,
                        Toast.LENGTH_LONG).show();
        }

    }

//    /**
//     * Called when the user clicks the "Remove geofence 1" button
//     * @param view The view that triggered this callback
//     */
//    public void onUnregisterGeofence1Clicked(View view) {
//        /*
//         * Remove the geofence by creating a List of geofences to
//         * remove and sending it to Location Services. The List
//         * contains the id of geofence 1 ("1").
//         * The removal happens asynchronously; Location Services calls
//         * onRemoveGeofencesByPendingIntentResult() (implemented in
//         * the current Activity) when the removal is done.
//         */
//
//        // Create a List of 1 Geofence with the ID "1" and store it in the global list
//        mGeofenceIdsToRemove = Collections.singletonList("1");
//
//        /*
//         * Record the removal as remove by list. If a connection error occurs,
//         * the app can automatically restart the removal if Google Play services
//         * can fix the error
//         */
//        mRemoveType = GeofenceUtils.REMOVE_TYPE.LIST;
//
//        /*
//         * Check for Google Play services. Do this after
//         * setting the request type. If connecting to Google Play services
//         * fails, onActivityResult is eventually called, and it needs to
//         * know what type of request was in progress.
//         */
//        if (!servicesConnected()) {
//
//            return;
//        }
//
//        // Try to remove the geofence
//        try {
//            mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);
//
//        // Catch errors with the provided geofence IDs
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (UnsupportedOperationException e) {
//            // Notify user that previous request hasn't finished.
//            Toast.makeText(this, R.string.remove_geofences_already_requested_error,
//                        Toast.LENGTH_LONG).show();
//        }
//    }

   

    /**
     * Called when the user clicks the "Register geofences" button.
     * Get the geofence parameters for each geofence and add them to
     * a List. Create the PendingIntent containing an Intent that
     * Location Services sends to this app's broadcast receiver when
     * Location Services detects a geofence transition. Send the List
     * and the PendingIntent to Location Services.
     * @param markerAddress 
     * @param fenceName 
     * @param radius 
     */
    public void onRegisterClicked(LatLng point, String name, String address, int radius) {

        mRequestType = GeofenceUtils.REQUEST_TYPE.ADD;
        
        if (!servicesConnected()) {
            return;
        }


        /*
         * Create a version of geofence 1 that is "flattened" into individual fields. This
         * allows it to be stored in SharedPreferences.
         */
        mUIGeofence1 = new SimpleGeofence(
            "1",
            // Get latitude, longitude, and radius from the UI
            name,
            address,
            Double.valueOf(point.latitude),
            Double.valueOf(point.longitude),
            Float.valueOf(radius),
            // Set the expiration time
            GEOFENCE_EXPIRATION_IN_MILLISECONDS,
            // Only detect entry transitions
            Geofence.GEOFENCE_TRANSITION_ENTER);

        // Store this flat version in SharedPreferences
        mPrefs.setGeofence("1", mUIGeofence1);

        /*
         * Add Geofence objects to a List. toGeofence()
         * creates a Location Services Geofence object from a
         * flat object
         */
        mCurrentGeofences.add(mUIGeofence1.toGeofence());

        // Start the request. Fail if there's already a request in progress
        try {
            // Try to add geofences
            mGeofenceRequester.addGeofences(mCurrentGeofences);
        } catch (UnsupportedOperationException e) {
            // Notify user that previous request hasn't finished.
            Toast.makeText(this, R.string.add_geofences_already_requested_error,
                        Toast.LENGTH_LONG).show();
        }
    }
  

	@Override
    public void onMapLongClick(LatLng point) {
        Log.i(TAG, point.toString());
        Location location = new Location("Test");
        location.setLatitude(point.latitude);
        location.setLongitude(point.longitude);
        location.setTime(new Date().getTime());

        // Get marker address from Latitude and Longtitude
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String markerAddress = "";
        if (Geocoder.isPresent()) {
            try {
                List<Address> addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                Address address = addresses.get(0);
                markerAddress = address.getAddressLine(0) +
                        ", "+ address.getAddressLine(1) + ", " + address.getAddressLine(2);
                Log.i(TAG, markerAddress);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // 1. Show a setting window to determine the radius,name
        showConfigDialog(point, markerAddress);
    }

    // AlertDialog 争取复用, 创建时候出来，marker click listener的时候也要出来
    private AlertDialog dialog;
    private void showConfigDialog(final LatLng point, final String markerAddress) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GeoFence Configuaration");

        // get all UI widget
        View view = View.inflate(getApplicationContext(), R.layout.geofence_setting, null);
        final EditText et_fencename = (EditText) view.findViewById(R.id.fencename);
        TextView et_fenceaddress = (TextView) view.findViewById(R.id.fenceaddress);
        final Spinner sp_radius = (Spinner) view.findViewById(R.id.radius);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);

        // Initial values
        et_fenceaddress.setText(markerAddress);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// Get fenceName and fenceRadius from AlertDialog
                String fenceName = et_fencename.getText().toString().trim();
                int fenceRadius = Integer.parseInt(sp_radius.getSelectedItem().toString().trim());
                
                // Check if fenceName is empty
                if (TextUtils.isEmpty(fenceName)) {
                    Toast.makeText(getApplicationContext(), "fence name can't be empty", 1).show();
                    return;
                }

                // 1. Add a marker to map with info window
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(point)
                        .title(fenceName)
                        .snippet("Ridus: "+fenceRadius)
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                Marker marker = mMap.addMarker(markerOptions);
                marker.showInfoWindow();

                // 2. Instantiates a new CircleOptions object and defines the center and radius
                CircleOptions circleOptions = new CircleOptions()
                        .strokeColor(Color.RED)
                        .strokeWidth(0)
                        .fillColor(0x40ff0000)
                        .center(point)
                        .radius(fenceRadius); // In meters -- here customize according to the user configuration
                Circle circle = mMap.addCircle(circleOptions);
                dialog.dismiss();
                 
                // 3. Save Geofence
                onRegisterClicked(point,fenceName, markerAddress, fenceRadius);
                
            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                return;
            }
        });
        
        // Show alert dialog
        builder.setView(view);
        dialog = builder.show();
    }

    /**
     * Define a Broadcast receiver that receives updates from connection listeners and
     * the geofence transition service.
     */
    public class GeofenceSampleReceiver extends BroadcastReceiver {
        /*
         * Define the required method for broadcast receivers
         * This method is invoked when a broadcast Intent triggers the receiver
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            // Check the action code and determine what to do
            String action = intent.getAction();

            // Intent contains information about errors in adding or removing geofences
            if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)) {
                handleGeofenceError(context, intent);
            // Intent contains information about successful addition or removal of geofences
            } else if (
                    TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_ADDED)
                    ||
                    TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED)) {
                handleGeofenceStatus(context, intent);

            // Intent contains information about a geofence transition
            } else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_TRANSITION)) {
                handleGeofenceTransition(context, intent);
            // The Intent contained an invalid action
            } else {
                Log.e(GeofenceUtils.APPTAG, getString(R.string.invalid_action_detail, action));
                Toast.makeText(context, R.string.invalid_action, Toast.LENGTH_LONG).show();
            }
        }

        /**
         * If you want to display a UI message about adding or removing geofences, put it here.
         *
         * @param context A Context for this component
         * @param intent The received broadcast Intent
         */
        private void handleGeofenceStatus(Context context, Intent intent) {

        }

        /**
         * Report geofence transitions to the UI
         *
         * @param context A Context for this component
         * @param intent The Intent containing the transition
         */
        private void handleGeofenceTransition(Context context, Intent intent) {
            /*
             * If you want to change the UI when a transition occurs, put the code
             * here. The current design of the app uses a notification to inform the
             * user that a transition has occurred.
             */
        }

        /**
         * Report addition or removal errors to the UI, using a Toast
         *
         * @param intent A broadcast Intent sent by ReceiveTransitionsIntentService
         */
        private void handleGeofenceError(Context context, Intent intent) {
            String msg = intent.getStringExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS);
            Log.e(GeofenceUtils.APPTAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    
}
