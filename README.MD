# GeofenceApp test project 

**This is only a proof of example to show the problems with geofencing for an app on devices >= android 8.0.**

You get only the geofence events, if someone, i.e. another app like google maps or in this sample 
my continues location request (see de.a0zero.geofence4fhem.app.MainActivity.addLocationUpdateRequests)
requests active an location.<br/> 
Without this you will never get an geofence ENTER/LEAVE intent.<br/>
<p/>
you can try this by yourself like:<br/>
Start the app and add some geofences on the map (sorry you need a map-api key).<br/> 
Hint: for editing a geofence, do a long-click on the info window of the marker.<br/>
<p/>
Now add the geofences (add geofences button).<br/>
As long as you are this apps map view or google-maps-app you can
trigger the geofence event with the correct fake location events from emulator.<br/>
But if you put this app in the background (independent from start of the foreground TrackingService)
you will get no events anymore.<br/>
<br/>
But if you request continuous the location tracking (button from MainActivity) **and** started the 
foreground service (both are needed) <br/>
it works, even if the main activity goes to background.<br/>
<br/>
The (simple) ForegroundService with a notification allows the app to receive the intent (in GeofenceBroadcastReceiver)
<p/>
<p/>
Remark:<br/>
most of the location code is based on a discussion on stack overflow and some examples from google.<br/>
i've used two different ways of adding the geofences based on the [discussion on stack overflow.]
(https://stackoverflow.com/questions/45422591/geofence-notification-not-triggered-when-app-in-background)
<p/>
See the if (false) in de.a0zero.geofence4fhem.app.MainActivity.addGeofencesButtonHandler
there is **no difference** in both regarding the receiving of the events. both are working or not, only 
depends on the foreground-Service and continues location requests.

