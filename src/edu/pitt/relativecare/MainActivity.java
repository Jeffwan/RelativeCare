package edu.pitt.relativecare;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private Button geoFence, fallDetect, ambient;
//    private Button fallDetect;
//    private Button ambient;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        geoFence = (Button) findViewById(R.id.geofence);
        fallDetect = (Button) findViewById(R.id.falldetect);
        ambient = (Button) findViewById(R.id.ambient);

        geoFence.setOnClickListener(this);
        fallDetect.setOnClickListener(this);
        ambient.setOnClickListener(this);

	}


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.geofence:
                Log.i(TAG,"geofence");
                Intent geofenceIntent = new Intent(this, GeoFence.class);
                startActivity(geofenceIntent);
                break;

            case R.id.falldetect:
                Log.i(TAG,"falldetect");
                Intent fallDetentIntent = new Intent(this, FallDetect.class);
                startActivity(fallDetentIntent);
                break;

            case R.id.ambient:
                Log.i(TAG,"ambient");
                Intent ambientIntent = new Intent(this, Ambient.class);
                startActivity(ambientIntent);
                break;

        }
    }
}
