package edu.pitt.relativecare;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by jeffwan on 11/19/13.
 */
public class FallDetectActivity extends Activity implements SensorEventListener, OnCheckedChangeListener {

    private static final String TAG = "FallDetect";
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float x, y, z;
    private TextView tvX, tvY, tvZ;
    private Switch falldownSwitch; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falldetect);
        tvX = (TextView) findViewById(R.id.x_axis);
        tvY = (TextView) findViewById(R.id.y_axis);
        tvZ = (TextView) findViewById(R.id.z_axis);
        falldownSwitch = (Switch) findViewById(R.id.falldown_switch);
        falldownSwitch.setOnCheckedChangeListener(this);
        
        Log.i(TAG, "enter me fall detect");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            List<Sensor> deviceSensor = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

            for (int i=0; i< deviceSensor.size(); i++) {
                mSensor = deviceSensor.get(i);
                Log.i(TAG, mSensor.getName());
            }
        } else {
            //Sorry, There are no accelerometers on your device
        }

        mSensorManager.registerListener(this, mSensor ,SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	// TODO: SharePerefence 的值要改掉，这块跟设置向导是一样的
    	if (isChecked) {
    		Toast.makeText(this, "is on", 1).show();
    	} else {
    		Toast.makeText(this, "is off", 1).show();
    	}
		
	}

    
    
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do something here if sensor accuracy changes.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // If the sensor data is unreliable, return
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
            return;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                //get acceleration vector data
                x = event.values[0];
                y = event.values[0];
                z = event.values[0];

                Log.i(TAG,"x: "+ x);
                Log.i(TAG,"y: "+ y);
                Log.i(TAG,"z: "+ z);

                //
                tvX.setText(Float.toString(x));
                tvY.setText(Float.toString(y));
                tvZ.setText(Float.toString(z));
        }







    }

	

}


