# simple GeofenceApp for FHEM 

This app was started as a proof of concept to show/handle the problems with background reception of geofence events after the upgrade to android oreo (8.0).

## Current state of the project

You can create/edit/delete some geofences on a google map. 
Then you create some profiles and assign each of them to some of the created fences. These profiles are executed on enter/leave events of the geofence.

Currently there are only two type of profiles:
* [FHEM Geofancy](https://wiki.fhem.de/wiki/GEOFANCY) with Basic Http-Auth
* Ringer settings (silent/vibrate/normal)

Some others will coming soon, like individual http calls (POST/PUT/...), change wlan/bluetooth settings or execute tasker actions.
The ongoing notification (Foreground Service) will show you the last six results of the profile executions.

Currently the register/re-register of the geofences (after create/update, location-provider changes, ...) must be made manually from the main menue of the app.
Later this will happen automatically for you.
