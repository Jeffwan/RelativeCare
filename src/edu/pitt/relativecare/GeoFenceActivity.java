package edu.pitt.relativecare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jeffwan on 11/19/13.
 */
public class GeoFenceActivity extends Activity implements GoogleMap.OnMapLongClickListener {

    private static final String TAG = "GeoFence";
    private GoogleMap mMap;
    private LocationManager locationManager;
    private MapFragment mMapFragment;
    private UiSettings uiSettings;
    public static final LatLng HOME = new LatLng(40.45222, -79.947935);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);

        // 判断连接状态
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if(status!= ConnectionResult.SUCCESS){
            int requestCode=10;
            Dialog dialog=GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }
        else{
            mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setMyLocationEnabled(true);
            mMap.setIndoorEnabled(true);
            mMap.setOnMapLongClickListener(this);

        }


    }

    @Override
    public void onMapLongClick(LatLng point) {
        Log.i(TAG, point.toString());
        Location location = new Location("Test");
        location.setLatitude(point.latitude);
        location.setLongitude(point.longitude);
        location.setTime(new Date().getTime());

        // Get marker address from Latitude and Longtitude
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String markerAddress = "";
        if (Geocoder.isPresent()) {
            try {
                List<Address> addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                Address address = addresses.get(0);
                markerAddress = address.getAddressLine(0) +
                        ", "+ address.getAddressLine(1) + ", " + address.getAddressLine(2);
                Log.i(TAG, markerAddress);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // 这块还是看一下 视频好了，搞定这块，然后就可以进入整合阶段了
        // 1. Show a setting window to determine the radius,name
        // raidus -- radio to select?
        // name -- show the marker's name? or let user give
        showConfigDialog(point, markerAddress);
    }

    // AlertDialog 争取复用, 创建时候出来，marker click listener的时候也要出来
    private AlertDialog dialog;
    private void showConfigDialog(final LatLng point, String markerAddress) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GeoFence Configuaration");

        // get all UI widget
        View view = View.inflate(getApplicationContext(), R.layout.geofence_setting, null);
        final EditText et_fencename = (EditText) view.findViewById(R.id.fencename);
        TextView et_fenceaddress = (TextView) view.findViewById(R.id.fenceaddress);
        final Spinner sp_radius = (Spinner) view.findViewById(R.id.radius);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);

        // Initial values
        et_fenceaddress.setText(markerAddress);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String fenceName = et_fencename.getText().toString().trim();
                int fenceRadius = Integer.parseInt(sp_radius.getSelectedItem().toString().trim());

                Log.i(TAG, "OK Button Clicked.");

                if (TextUtils.isEmpty(fenceName)) {
                    Toast.makeText(getApplicationContext(), "fence name can't be empty", 1).show();
                    return;
                }

                // 1. Add a marker to map with info window

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(point)
                        .title(fenceName)
                        .snippet("Ridus: "+fenceRadius)
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                Marker marker = mMap.addMarker(markerOptions);
                marker.showInfoWindow();


                // 3. Instantiates a new CircleOptions object and defines the center and radius
                CircleOptions circleOptions = new CircleOptions()
                        .strokeColor(Color.RED)
                        .strokeWidth(0)
                        .fillColor(0x40ff0000)
                        .center(point)
                        .radius(fenceRadius); // In meters -- here customize according to the user configuration
                Circle circle = mMap.addCircle(circleOptions);
                dialog.dismiss();
            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                return;

            }
        });

        builder.setView(view);
        dialog = builder.show();

    }

}
