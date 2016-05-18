package com.aware.plugin.google.fused_location;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.aware.providers.Locations_Provider;
import com.aware.utils.IContextCard;

import java.io.IOException;
import java.util.List;

public class ContextCard implements IContextCard {

    public ContextCard() {
    }

    @Override
    public View getContextCard(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View card = inflater.inflate(R.layout.card, null);
        TextView address = (TextView) card.findViewById(R.id.address);
        TextView last_update = (TextView) card.findViewById(R.id.last_updated);

        Uri locationURI = Uri.parse("content://" + context.getPackageName() + ".provider.locations/locations");
        Cursor last_location = context.getContentResolver().query(locationURI, null, null, null, Locations_Provider.Locations_Data.TIMESTAMP + " DESC LIMIT 1");
        if (last_location != null && last_location.moveToFirst()) {
            double lat = last_location.getDouble(last_location.getColumnIndex(Locations_Provider.Locations_Data.LATITUDE));
            double lon = last_location.getDouble(last_location.getColumnIndex(Locations_Provider.Locations_Data.LONGITUDE));
            long timestamp = last_location.getLong(last_location.getColumnIndex(Locations_Provider.Locations_Data.TIMESTAMP));

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
                address.setText(geo_text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (last_location != null && !last_location.isClosed()) last_location.close();

        return card;
    }
}
