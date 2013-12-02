package edu.pitt.relativecare;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ImageButton geoFence, fallDetect, ambient, settings;
//    private Button fallDetect;
//    private Button ambient;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        geoFence = (ImageButton) findViewById(R.id.geofence);
        fallDetect = (ImageButton) findViewById(R.id.falldetect);	
        ambient = (ImageButton) findViewById(R.id.ambient);
        settings = (ImageButton) findViewById(R.id.settings);

        geoFence.setOnClickListener(this);
        fallDetect.setOnClickListener(this);
        ambient.setOnClickListener(this);
        settings.setOnClickListener(this);

	}


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.geofence:
                Log.i(TAG,"geofence");
                Intent geofenceIntent = new Intent(this, GeoFenceActivity.class);
                startActivity(geofenceIntent);
                break;

            case R.id.falldetect:
                Log.i(TAG,"falldetect");
                Intent fallDetentIntent = new Intent(this, FallDetectActivity.class);
                startActivity(fallDetentIntent);
                break;

            case R.id.ambient:
                Log.i(TAG,"ambient");
                Intent ambientIntent = new Intent(this, AmbientActivity.class);
                startActivity(ambientIntent);
                break;
                
            case R.id.settings:
            	Log.i(TAG, "settings");
            	Intent settingsIntent = new Intent(this, SettingsActivity.class);
            	startActivity(settingsIntent);
            	break;
        }
    }
}
