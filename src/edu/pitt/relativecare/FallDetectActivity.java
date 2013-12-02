package edu.pitt.relativecare;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by jeffwan on 11/19/13.
 */
public class FallDetectActivity extends Activity implements SensorEventListener, OnCheckedChangeListener {

    private static final String TAG = "FallDetect";
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SimpleGeofenceStore mPrefs;

    private float x, y, z;
    private double v;
    private TextView tvX, tvY, tvZ, tvV;
    private Switch falldownSwitch;

    boolean min,max,falled,notification;
    long mintime=0,maxtime=0;
    int i;
    MediaPlayer player = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = new SimpleGeofenceStore(this);

        min=false;
        max=false;
        notification=false;
        long mintime=System.currentTimeMillis();
        i=0;


        setContentView(R.layout.activity_falldetect);
        tvX = (TextView) findViewById(R.id.x_axis);
        tvY = (TextView) findViewById(R.id.y_axis);
        tvZ = (TextView) findViewById(R.id.z_axis);
        tvV = (TextView) findViewById(R.id.v_axis);
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
                v = Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2));

                //
                tvX.setText(Float.toString(x));
                tvY.setText(Float.toString(y));
                tvZ.setText(Float.toString(z));
                tvV.setText(Double.toString(v));

                falled = fallDetection(v);


                if(falled&&!notification){
                    performAlarm();
                }
        }

    }

    boolean fallDetection(double v)
    {

        boolean falled0=false;
        if(v<=6.0){
            min=true;
            mintime= System.currentTimeMillis();
        }

        if(min==true)
        {
            i++;
            if(v>=12){
                max=true;
                maxtime=System.currentTimeMillis();

            }

        }
        //  if(min==true &&max==true)
        if(min==true &&max==true&&((maxtime-mintime)>500))
        {
            falled0=true;
            i=0;
            min=false;
            max=false;

        }
        return falled0;
    }

    void performAlarm(){
        //   Toast.makeText(this,"Fall Detected!",Toast.LENGTH_LONG).show();
        Toast.makeText(this,"Fall Detected! Time:"+(maxtime-mintime),Toast.LENGTH_LONG).show();

        playSound();
        sendSMS();

        // callEmergency();
        notification = true;

    }

    void playSound(){
        MediaPlayer mplayer = MediaPlayer.create(this, R.raw.ring);
        mplayer.setLooping(false);
        mplayer.setVolume(1.0f, 1.0f);
        mplayer.start();
    }

    public void stopAlarm(){
        if(player.isPlaying()==true)
        {
            player.stop();
        }
    }

    public void sendSMS(){
        try{
            SimpleDateFormat sDateFormat =  new SimpleDateFormat("yyyy-MM-dd   hh:mm:ss");
            String time  = sDateFormat.format(new java.util.Date());

            String smsNumber = mPrefs.getContactNumber();
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(smsNumber,null,smsNumber + "your relative may fall down ! -- "+time,null,null);

        }catch (Exception e){
            Toast.makeText(this,"SMS failed, please try again later",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public static void callEmergency(){

        //  startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:8122721964")), 1);
    }

}


