package edu.pitt.relativecare;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by jeffwan on 11/19/13.
 */
@SuppressLint("CutPasteId")
public class AmbientActivity extends Activity implements SensorEventListener {

    private static final String TAG = "AmbientActivity";
    private SensorManager mSensorManager;
    private TextView tv_temperature_text;
    private TextView tv_humidity_text;
    private TextView tv_pressure_text;
 
    private TextView tv_temperature_value;
    private TextView tv_humidity_value;
    private TextView tv_pressure_value;
    
    private TextView tv_temperature_hint;
    private TextView tv_humidity_hint;
    private TextView tv_pressure_hint;
   
    
    

    
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambient);
        tv_temperature_text = (TextView) findViewById(R.id.temperature_text);
        tv_humidity_text = (TextView) findViewById(R.id.humidity_text);
        tv_pressure_text = (TextView) findViewById(R.id.pressure_text);
        
        tv_temperature_value = (TextView) findViewById(R.id.temperature_value);
        tv_humidity_value = (TextView) findViewById(R.id.humidity_value);
        tv_pressure_value = (TextView) findViewById(R.id.pressure_value);
        
        tv_temperature_hint = (TextView) findViewById(R.id.temperature_hint);
        tv_humidity_hint = (TextView) findViewById(R.id.humidity_hint);
        tv_pressure_hint = (TextView) findViewById(R.id.pressure_hint);
        
        
        
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
        
//        mSensorManager.registerListener(this, 
//        		mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
//        
        mSensorManager.registerListener(this, 
        		mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), SensorManager.SENSOR_DELAY_NORMAL);

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY), SensorManager.SENSOR_DELAY_NORMAL);
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
			float temperature  = event.values[0];
			Log.i(TAG, String.valueOf(temperature));
			tv_temperature_value.setText(String.valueOf(temperature));
			
			break;
			
		case Sensor.TYPE_PRESSURE:
			float pressure = event.values[0];
			Log.i(TAG, String.valueOf(pressure));
			// Do something with the 
			tv_pressure_value.setText(String.valueOf(pressure));
			break;
			
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			float humidity = event.values[0];
			Log.i(TAG, String.valueOf(humidity));
			tv_humidity_value.setText(String.valueOf(humidity));
			break;
			
//		case Sensor.TYPE_ACCELEROMETER:
//			float accelerometer = event.values[0];
//			Log.i(TAG, String.valueOf(accelerometer));
//			break;
			
		default:
			break;
		}
		
	}
	
	 @Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			mSensorManager.unregisterListener(this);
		}

		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			mSensorManager.registerListener(this, 
	        		mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE),SensorManager.SENSOR_DELAY_NORMAL);

			mSensorManager.registerListener(this,
					mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),SensorManager.SENSOR_DELAY_NORMAL);

			mSensorManager.registerListener(this,
					mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY),SensorManager.SENSOR_DELAY_NORMAL);
		}

    
    
}
