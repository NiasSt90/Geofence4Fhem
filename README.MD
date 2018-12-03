# GeofenceApp test project 

**This is only a proof of example to show the problems with geofencing for an app on devices >= android 8.0.**

You get only the geofence events, if someone, i.e. another app like google maps or in this sample 
my continues location request (@see de.a0zero.geofence4fhem.app.MainActivity.addLocationUpdateRequests)
requests active an location. Without this you will never get an geofence ENTER/LEAVE intent.

you can try this by yourself like:
Start the app and add some geofences on the map (sorry you need a map-api). 
For editing a geofence -> long-click on the info window of the marker.

Now add the geofences (add geofences button). As long as you are this apps map view or google-maps app you can
trigger the geofence event with the correct fake location events from emulator.
But if you put this app in the background (independent from start of the foreground TrackingService)
you will get no events anymore.
But if you request continuous the location tracking (button from MainActivity) **and** started the 
foreground service (both are needed) it works, even if the main activity goes to background.
The (silly) ForegroundService with a notification allows the app to receive the intent (in GeofenceBroadcastReceiver)


Remark:
most of the location code is based on a discussion on stack overflow and some examples from google.
i've used two different ways of adding the geofences based on the [discussion on stack overflow.]
(https://stackoverflow.com/questions/45422591/geofence-notification-not-triggered-when-app-in-background)

See the if (false) in de.a0zero.geofence4fhem.app.MainActivity.addGeofencesButtonHandler
there is **no difference** in both regarding the receiving of the events. both are working or not, only 
depends on the foreground-Service and continues location requests.

