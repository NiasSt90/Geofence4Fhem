package de.a0zero.geofence4fhem.data;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class TypeConvertes {

    private static Gson gson = new Gson();

    @TypeConverter
    public static String fromType(ProfileType type) {
        return type.name();
    }

    @TypeConverter
    public static ProfileType toType(String value) {
        return ProfileType.valueOf(value);
    }


    @TypeConverter
    public static String fromLatLngType(LatLng value) {
        return gson.toJson(value);
    }

    @TypeConverter
    public static LatLng toLatLngType(String value) {
        return gson.fromJson(value, LatLng.class);
    }

}
