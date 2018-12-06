package de.a0zero.geofence4fhem.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class GeofenceRepo {

    public static final String DB_KEY_GEOFENCES = "geofences";
    private final Context context;

    private List<GeofenceDto> geofences;
    private final Gson gson;
    private final SharedPreferences sharedPreferences;

    public GeofenceRepo(Context context) {
        this.context = context;
        gson = new Gson();
        sharedPreferences = context.getSharedPreferences(GeofenceRepo.class.getName(), Context.MODE_PRIVATE);
        geofences = gson.fromJson(sharedPreferences.getString(DB_KEY_GEOFENCES, "[]"),
                new TypeToken<List<GeofenceDto>>(){}.getType());
    }

    public List<GeofenceDto> listAll() {
        return geofences;
    }

    public void add(GeofenceDto geofence) {
        geofences.add(geofence);
    }

    public void delete(GeofenceDto geofenceDto) {
        geofences.remove(geofenceDto);
    }

    public boolean exists(GeofenceDto geofence) {
        return geofences.contains(geofence);
    }

    public void saveAll() {
        String json = gson.toJson(geofences);
        sharedPreferences.edit().putString(DB_KEY_GEOFENCES, json).apply();
    }

    public GeofenceDto findByID(String requestId) {
        for (GeofenceDto geofenceDto : geofences) {
            if (geofenceDto.getId().equals(requestId)) return geofenceDto;
        }
        return null;
    }
}
