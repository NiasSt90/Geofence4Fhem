package de.a0zero.geofence4fhem.profiles.fhem;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import de.a0zero.geofence4fhem.data.GeofenceDto;
import de.a0zero.geofence4fhem.data.Profile;
import de.a0zero.geofence4fhem.profiles.GeofenceAction;
import io.reactivex.Observable;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * build the following url and execute a GET request onto it:
 * $FHEM_URL?
 *     id=$DEVICEUUID
 *     &name={@link GeofenceDto#getName()}
 *     &entry=0/1
 *     &date=2018-11-30T15:14:35Z
 *     &latitude=51.7621518
 *     &longitude=14.321822
 *     &device=$DEVICEUUID
 */
public class GeofenceActionInformFhem implements GeofenceAction<Profile> {

    private static final String TAG = GeofenceActionInformFhem.class.getSimpleName();
    
    private final OkHttpClient client;

    public GeofenceActionInformFhem(Context context) {
        client = new OkHttpClient();
    }

    @Override
    public Observable<ActionResponse> enter(GeofenceDto geofenceDto, Profile profile, LatLng currentPosition) {
        return execute(geofenceDto, profile, currentPosition, true);
    }

    @Override
    public Observable<ActionResponse> leave(GeofenceDto geofenceDto, Profile profile, LatLng currentPosition) {
        return execute(geofenceDto, profile, currentPosition, false);
    }

    public static String toISO8601UTC(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        return df.format(date);
    }

    private Observable<ActionResponse> execute(GeofenceDto geofenceDto, Profile profile, LatLng currentPosition, boolean enterZone) {
		 FhemSettings fhemSettings = profile.data(FhemSettings.class);
		 HttpUrl.Builder builder = HttpUrl.parse(fhemSettings.getFhemUrl()).newBuilder();
        HttpUrl httpUrl = builder.addQueryParameter("id", fhemSettings.getDeviceUUID())
                .addQueryParameter("device", fhemSettings.getDeviceUUID())
                .addQueryParameter("entry", enterZone ? "1":"0")
                .addQueryParameter("name", geofenceDto.getName())
                .addQueryParameter("date", toISO8601UTC(new Date()))
                .addQueryParameter("latitude", Double.toString(currentPosition.latitude))
                .addQueryParameter("longitude", Double.toString(currentPosition.longitude))
                .build();
        String credentials = Credentials.basic(
				  fhemSettings.getUsername(), fhemSettings.getPassword());
        Request httpRequest = new Request.Builder()
                .url(httpUrl.url())
                .header("content-type", "application/json")
                .header("Authorization", credentials)
                .build();
        return Observable.fromCallable(() -> client.newCall(httpRequest).execute())
                .doOnNext(response -> {
                    if(!response.isSuccessful()) {
                        Log.d(TAG, "Error on execute:" + httpUrl.toString());
                        throw new RuntimeException("FHEM notify for " + httpUrl.toString() + " = <" + response.message() + ">");
                    }
                })
                .map(response -> response::message);
    }
}
