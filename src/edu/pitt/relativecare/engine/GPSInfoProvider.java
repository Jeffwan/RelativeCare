package edu.pitt.relativecare.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import edu.pitt.relativecare.MainActivity;
import edu.pitt.relativecare.SimpleGeofenceStore;

/**
 * Created by jeffwan on 11/19/13.
 */
public class GPSInfoProvider {

    private static GPSInfoProvider mGPSInfoProvider;
    private static LocationManager locationManager;
    private static MyListener listener;
    private static SimpleGeofenceStore mPrefs;
    private static final String SHARED_PREFERENCE_NAME =
            MainActivity.class.getSimpleName();
	private static final String TAG = "GPSInfoProvider";
    
    private static double mLatitude;
    private static double mLongitude ;
    	
    private GPSInfoProvider() {

    }

    public synchronized static GPSInfoProvider getInstance(Context context) {
        if (mGPSInfoProvider == null) {
            mGPSInfoProvider = new GPSInfoProvider();
            
            mPrefs = new SimpleGeofenceStore(context);
           
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            	Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);  
            	Log.i(TAG, location.toString());
                if(location != null){  
                	mLatitude = location.getLatitude();  
                	mLongitude = location.getLongitude();
                	mPrefs.setLastLocation(mLatitude, mLongitude);
                 }  
			}
            
            List<String> names = locationManager.getAllProviders();
            for (String name : names) {
                System.out.println(name);
            }

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);

            String provider = locationManager.getBestProvider(criteria, true);
            Log.i(TAG, "Best Provider: "+provider);

            if(!TextUtils.isEmpty(provider)) {
                listener = mGPSInfoProvider.new MyListener();
                locationManager.requestLocationUpdates(provider,0,0,listener);

            } else {
                Toast.makeText(context.getApplicationContext(), "No location provider available",1).show();
            }
        }

        return mGPSInfoProvider;
    }

    public String getLastLocation() {
        return mPrefs.getLastLocation();
    }

    private class MyListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double accuracy = location.getAccuracy();
            
            Log.i(TAG, String.valueOf(latitude));
            Log.i(TAG, String.valueOf(longitude));

            mPrefs.setLastLocation(latitude, longitude);    
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
