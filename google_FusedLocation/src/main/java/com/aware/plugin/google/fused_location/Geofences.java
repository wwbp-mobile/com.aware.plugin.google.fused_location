package com.aware.plugin.google.fused_location;

import android.net.Uri;

import com.aware.utils.Aware_Plugin;

/**
 * Created by denzil on 08/06/16.
 */
public class Geofences extends Aware_Plugin {

    public static final String ACTION_AWARE_PLUGIN_FUSED_GEOFENCE = "ACTION_AWARE_PLUGIN_FUSED_GEOFENCE";
    public static final String EXTRA_DATA = "data";

    @Override
    public void onCreate() {
        super.onCreate();

        DATABASE_TABLES = Provider.DATABASE_TABLES;
        TABLES_FIELDS = Provider.TABLES_FIELDS;
        CONTEXT_URIS = new Uri[]{Provider.Geofences.CONTENT_URI};
    }
}
