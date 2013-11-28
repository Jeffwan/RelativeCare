package edu.pitt.relativecare;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by jeffwan on 11/19/13.
 */
public class AmbientActivity extends Activity implements SensorEventListener {

    private static final String TAG = "AmbientActivity";
    private SensorManager mSensorManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambient);
        Log.i(TAG,"enter me Ambient");
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        /**
         * Used for check hardware
         */
//        List<Sensor> allSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
//        Log.i(TAG, "Sensor List: " + allSensors.size());
//        for (Sensor s: allSensors) {
//        	String tempString = "\n" + "  设备名称：" + s.getName() + "\n" + "  设备版本：" + s.getVersion() + "\n" + "  供应商："
//        			                    + s.getVendor() + "\n";
//        	switch (s.getType()) {
//			case Sensor.TYPE_ACCELEROMETER:
//				Log.i(TAG, "accelerometer" + tempString);
//				break;
//			case Sensor.TYPE_AMBIENT_TEMPERATURE:
//				Log.i(TAG, "ambient_temp" + tempString);
//				break;
//			case Sensor.TYPE_GRAVITY:
//				Log.i(TAG, "gravity" + tempString);
//				break;
//			case Sensor.TYPE_GYROSCOPE:
//				Log.i(TAG, "Gyroscope" + tempString);
//				break;
//			case Sensor.TYPE_LIGHT:
//				Log.i(TAG, "light" + tempString);
//				break;
//			case Sensor.TYPE_LINEAR_ACCELERATION:
//				Log.i(TAG, "linear" + tempString);
//				break;
//			case Sensor.TYPE_MAGNETIC_FIELD:
//				Log.i(TAG, "magnetic" + tempString);
//				break;
//			case Sensor.TYPE_PROXIMITY:
//				Log.i(TAG, "proximity" + tempString);
//				break;
//			case Sensor.TYPE_RELATIVE_HUMIDITY:
//				Log.i(TAG, "Humidity" + tempString);
//				break;
//			case Sensor.TYPE_ORIENTATION:
//				Log.i(TAG, "temperature" + tempString);
//				break;
//			case Sensor.TYPE_PRESSURE:
//				Log.i(TAG, "temperature" + tempString);
//				break;
//			case Sensor.TYPE_TEMPERATURE:
//				Log.i(TAG, "temperature" + tempString);
//				break;
//			case Sensor.TYPE_ROTATION_VECTOR:
//				Log.i(TAG, "temperature" + tempString);
//				break;
//
//			default:
//				break;
//			}
//		}
        
        mSensorManager.registerListener(this, 
        		mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE),SensorManager.SENSOR_DELAY_NORMAL);

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),SensorManager.SENSOR_DELAY_NORMAL);

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY),SensorManager.SENSOR_DELAY_NORMAL);
        
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
		switch (sensor.getType()) {
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			
			
			
			break;
		case Sensor.TYPE_PRESSURE:
			
			break;
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			
			break;

		default:
			break;
		}
		
	}
    
    
}
