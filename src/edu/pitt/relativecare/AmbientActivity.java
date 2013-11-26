                           package edu.pitt.relativecare;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by jeffwan on 11/19/13.
 */
public class AmbientActivity extends Activity {

    private static final String TAG = "Ambient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambient);
        Log.i(TAG,"enter me Ambient");
    }
}
