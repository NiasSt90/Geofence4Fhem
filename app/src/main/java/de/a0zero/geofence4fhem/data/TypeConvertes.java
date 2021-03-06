package de.a0zero.geofence4fhem.data;

import androidx.room.TypeConverter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Date;


/**
 * room-db type converters
 */
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


	@TypeConverter
	public static Date toDate(Long value) {
		return value == null ? null : new Date(value);
	}


	@TypeConverter
	public static Long toLong(Date value) {
		return value == null ? null : value.getTime();
	}


	@TypeConverter
	public static JsonObject toData(String value) {
		return value == null ? null :
				gson.fromJson(value, JsonObject.class);
	}

	@TypeConverter
	public static String fromData(JsonObject value) {
		return value == null ? null :
				gson.toJson(value);
	}

}
