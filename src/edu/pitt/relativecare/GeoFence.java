package edu.pitt.relativecare;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by jeffwan on 11/19/13.
 */
public class GeoFence extends Activity {

    private static final String TAG = "GeoFence";
    private LocationManager mLocationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Log.i(TAG,"GeoFence");

    }
}
