package de.a0zero.geofence4fhem.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.google.gson.JsonObject;
import de.a0zero.geofence4fhem.profiles.GeofenceAction;

import java.lang.reflect.InvocationTargetException;


/**
 * a profile with some settings, needed for executing on enter/leave of an {@link GeofenceAction} implementation.
 */
@Entity
public class Profile {

	@PrimaryKey(autoGenerate = true)
	int ID;

	String label;

	ProfileType type;

	JsonObject data;


	public Profile(ProfileType type) {
		this.type = type;
		data = new JsonObject();
	}


	public int getID() {
		return ID;
	}


	public void setID(int ID) {
		this.ID = ID;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	@TypeConverters(TypeConvertes.class)
	public void setType(ProfileType type) {
		this.type = type;
	}


	@TypeConverters(TypeConvertes.class)
	public ProfileType getType() {
		return type;
	}


	@TypeConverters(TypeConvertes.class)
	public JsonObject getData() {
		return data;
	}


	@TypeConverters(TypeConvertes.class)
	public void setData(JsonObject data) {
		this.data = data;
	}


	public <T extends ProfileDataMapper> T data(Class<T> tClass) {
		try {
			return tClass.getConstructor(JsonObject.class).newInstance(data);
		}
		catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
