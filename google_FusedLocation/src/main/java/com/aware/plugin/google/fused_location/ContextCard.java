package com.aware.plugin.google.fused_location;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aware.providers.Locations_Provider;
import com.aware.utils.IContextCard;

import java.io.IOException;
import java.util.List;

public class ContextCard implements IContextCard {

    private static Location user_location;

    public ContextCard() {
    }

    @Override
    public View getContextCard(final Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View card = inflater.inflate(R.layout.card, null);
        TextView address = (TextView) card.findViewById(R.id.address);
        TextView last_update = (TextView) card.findViewById(R.id.last_updated);
        Button geofencer = (Button) card.findViewById(R.id.geofencer);

        geofencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locationGeofencer = new Intent(context, GeofenceMap.class);
                locationGeofencer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (!GeofenceUtils.getLabel(context, user_location).equalsIgnoreCase("Somewhere")) {
                    locationGeofencer.putExtra(GeofenceMap.EXTRA_LABEL, GeofenceUtils.getLabel(context, user_location).equalsIgnoreCase("Somewhere"));
                }
                context.startActivity(locationGeofencer);
            }
        });

        Uri locationURI = Uri.parse("content://" + context.getPackageName() + ".provider.locations/locations");
        Cursor last_location = context.getContentResolver().query(locationURI, null, null, null, Locations_Provider.Locations_Data.TIMESTAMP + " DESC LIMIT 1");
        if (last_location != null && last_location.moveToFirst()) {
            double lat = last_location.getDouble(last_location.getColumnIndex(Locations_Provider.Locations_Data.LATITUDE));
            double lon = last_location.getDouble(last_location.getColumnIndex(Locations_Provider.Locations_Data.LONGITUDE));
            long timestamp = last_location.getLong(last_location.getColumnIndex(Locations_Provider.Locations_Data.TIMESTAMP));

            user_location = new Location("Fused Location");
            user_location.setLatitude(lat);
            user_location.setLongitude(lon);
            user_location.setAccuracy(last_location.getFloat(last_location.getColumnIndex(Locations_Provider.Locations_Data.ACCURACY)));

            last_update.setText(String.format("%s", DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString()));

            try {
                Geocoder geo = new Geocoder(context);
                String geo_text = "";
                List<Address> addressList = geo.getFromLocation(lat, lon, 1);
                for (int i = 0; i < addressList.size(); i++) {
                    Address address1 = addressList.get(i);
                    for (int j = 0; j < address1.getMaxAddressLineIndex(); j++) {
                        if (address1.getAddressLine(j).length() > 0) {
                            geo_text += address1.getAddressLine(j) + "\n";
                        }
                    }
                    geo_text += address1.getCountryName();
                }
                if ( ! GeofenceUtils.getLabel(context, user_location).equalsIgnoreCase("Somewhere")) {
                    geo_text+="\nGeofence: "+GeofenceUtils.getLabel(context, user_location);
                }
                address.setText(geo_text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (last_location != null && !last_location.isClosed()) last_location.close();

        return card;
    }
}
