package de.a0zero.geofence4fhem.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class FhemProfileRepo {
    public static final String DB_KEY_PROFILES = "profiles";

    private List<FhemProfile> profileList;

    private final SharedPreferences sharedPreferences;

    private final Gson gson;

    public FhemProfileRepo(Context context) {
        gson = new Gson();
        sharedPreferences = context.getSharedPreferences(GeofenceRepo.class.getName(), Context.MODE_PRIVATE);
        profileList = gson.fromJson(sharedPreferences.getString(DB_KEY_PROFILES, "[]"),
                new TypeToken<List<FhemProfile>>(){}.getType());
    }

    public List<FhemProfile> listAll() {
        return profileList;
    }

    public void add(FhemProfile profile) {
        this.profileList.add(profile);
    }

    public void delete(FhemProfile profile) {
        this.profileList.remove(profile);
    }

    public void saveAll() {
        String json = gson.toJson(profileList);
        sharedPreferences.edit().putString(DB_KEY_PROFILES, json).apply();
    }
}
