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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jeffwan on 11/19/13.
 */
public class FallDetect extends Activity implements SensorEventListener {

    private static final String TAG = "FallDetect";
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized;
    private final float NOISE = (float) 2.0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falldetect);

        Log.i(TAG, "enter me fall detect");
        mInitialized = false;

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
        // The accelerometer return 3 value , each one for each axis.
        TextView tvX= (TextView)findViewById(R.id.x_axis);
        TextView tvY= (TextView)findViewById(R.id.y_axis);
        TextView tvZ= (TextView)findViewById(R.id.z_axis);
        ImageView iv = (ImageView)findViewById(R.id.image);




        final float alpha = (float) 0.8;


        float x = event.values[0];
        float y = event.values[0];
        float z = event.values[0];

        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            tvX.setText("0.0");
            tvY.setText("0.0");
            tvZ.setText("0.0");
            mInitialized = true;

        } else {

            float deltaX = Math.abs(mLastX - x);
            float deltaY = Math.abs(mLastY - y);
            float deltaZ = Math.abs(mLastZ - z);
            if (deltaX < NOISE) deltaX = (float)0.0;
            if (deltaY < NOISE) deltaY = (float)0.0;
            if (deltaZ < NOISE) deltaZ = (float)0.0;
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            tvX.setText(Float.toString(deltaX));
            tvY.setText(Float.toString(deltaY));
            tvZ.setText(Float.toString(deltaZ));
            iv.setVisibility(View.VISIBLE);
            if (deltaX > deltaY) {
                iv.setImageResource(R.drawable.horizontal);
            } else if (deltaY > deltaX) {
                iv.setImageResource(R.drawable.vertical);
            } else {
                iv.setVisibility(View.INVISIBLE);
            }

        }

    }

}


