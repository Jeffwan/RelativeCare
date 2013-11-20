package edu.pitt.relativecare.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.List;

/**
 * Created by jeffwan on 11/19/13.
 */
public class GPSInfoProvider {

    private static GPSInfoProvider mGPSInfoProvider;
    private static LocationManager locationManager;
    private static MyListener listener;
    private static SharedPreferences sp;

    private GPSInfoProvider() {

    }

    public synchronized static GPSInfoProvider getInstance(Context context) {
        if (mGPSInfoProvider == null) {
            mGPSInfoProvider = new GPSInfoProvider();
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            List<String> names = locationManager.getAllProviders();
            for (String name : names) {
                System.out.println(name);
            }


            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);

            String provider = locationManager.getBestProvider(criteria, true);
            System.out.println("Best Provider" + provider);

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
        return sp.getString("lastLocation","");
    }


    private class MyListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double accuracy = location.getAccuracy();

            String locations = "longtitude: "+ longitude + " latitude: "+ latitude;
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("lastLocation", locations);
            editor.commit();

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
