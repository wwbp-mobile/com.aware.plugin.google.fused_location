AWARE Plugin: Ambient Noise
===========================

Google's Locations API provider. This plugin provides the user's current location in an energy efficient way.

[ ![Download](https://api.bintray.com/packages/denzilferreira/com.awareframework/com.aware.plugin.ambient_noise/images/download.svg) ](https://bintray.com/denzilferreira/com.awareframework/com.aware.plugin.ambient_noise/_latestVersion)

# Settings
* **status_google_fused_location**: (boolean) activate/deactivate plugin
* **frequency_google_fused_location**: (integer) How frequently to fetch user's location (in seconds)
* **max_frequency_google_fused_location**: (integer) How fast are you willing to get the latest location (in seconds). Set it smaller/same as the previous frequency.
* **accuracy_google_fused_location**: (integer) One of the following numbers: 100 (High accuracy); 102 (balanced); 104 (low power); 105 (no power, listens to others location requests)

# Broadcasts
**ACTION_AWARE_LOCATIONS**
Broadcasted when we acquired a new location, with the following extras:
- **data**: (Location) latest location information
    
# Providers
##  Locations Data
> content://com.aware.provider.locations/locations

Field | Type | Description
----- | ---- | -----------
_id | INTEGER | primary key auto-incremented
timestamp | REAL | unix timestamp in milliseconds of sample
device_id | TEXT | AWARE device ID
double_latitude | REAL | the location’s latitude, in degrees
double_longitude	| REAL | the location’s longitude, in degrees
double_bearing | REAL |	the location’s bearing, in degrees
double_speed |	REAL | the speed if available, in meters/second over ground
double_altitude | REAL | the altitude if available, in meters above sea level
provider | TEXT | gps, network, fused
accuracy | INTEGER | the estimated location accuracy
label | TEXT | Customizable label. Useful for data calibration and traceability