package com.aware.plugin.google.fused_location;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.SeekBar;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.providers.Locations_Provider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

/**
 * Created by denzil on 08/06/16.
 */
public class GeofenceMap extends FragmentActivity implements OnMapReadyCallback {
    public static String EXTRA_LABEL = "label";

    private static GoogleMap mMap;
    private static EditText label;
    private static SeekBar radius;
    private static FloatingActionButton save_label;
    private static Circle geofence;

    private static String loadedLabel = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().getStringExtra(EXTRA_LABEL) != null && getIntent().getStringExtra(EXTRA_LABEL).length() > 0) {
            loadedLabel = getIntent().getStringExtra(EXTRA_LABEL);
        }

        //Make the dialog without title and transparent
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));

        setContentView(R.layout.dialog_geolabel);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        label = (EditText) findViewById(R.id.location_label);
        if (loadedLabel.length() > 0) label.setText(loadedLabel);

        radius = (SeekBar) findViewById(R.id.location_radius);
        if (loadedLabel.length() > 0) {
            radius.setProgress(GeofenceUtils.getLabelLocationRadius(this, loadedLabel));
        }
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                geofence.setRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        save_label = (FloatingActionButton) findViewById(R.id.save_label);

        save_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (label.getText().toString().length() > 0) {
                    Location geoLocal = new Location("Fused Location");
                    geoLocal.setLatitude(geofence.getCenter().latitude);
                    geoLocal.setLongitude(geofence.getCenter().longitude);
                    GeofenceUtils.saveLabel(
                            getApplicationContext(),
                            label.getText().toString(),
                            geoLocal,
                            radius.getProgress());

                    finish();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Location user_location = null;
        Cursor last_location = getContentResolver().query(Locations_Provider.Locations_Data.CONTENT_URI, null, null, null, Locations_Provider.Locations_Data.TIMESTAMP + " DESC LIMIT 1");
        if (last_location != null && last_location.moveToFirst()) {
            double lat = last_location.getDouble(last_location.getColumnIndex(Locations_Provider.Locations_Data.LATITUDE));
            double lon = last_location.getDouble(last_location.getColumnIndex(Locations_Provider.Locations_Data.LONGITUDE));

            user_location = new Location("Fused Location");
            user_location.setLatitude(lat);
            user_location.setLongitude(lon);
            user_location.setAccuracy(last_location.getFloat(last_location.getColumnIndex(Locations_Provider.Locations_Data.ACCURACY)));
        }
        if (last_location != null && !last_location.isClosed()) last_location.close();
        if (loadedLabel.length() > 0) {
            user_location = GeofenceUtils.getLabelLocation(this, loadedLabel);
        }

        if (user_location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(user_location.getLatitude(), user_location.getLongitude()), 15));
            geofence = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(user_location.getLatitude(), user_location.getLongitude()))
                    .radius(radius.getProgress())
                    .fillColor(0x7F33B5E5)
                    .strokeColor(Color.parseColor("#ffffff"))
                    .strokeWidth(1)
            );
        }
    }
}
