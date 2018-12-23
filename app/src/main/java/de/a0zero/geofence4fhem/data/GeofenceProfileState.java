package de.a0zero.geofence4fhem.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;
import de.a0zero.geofence4fhem.actions.GeofenceAction;

import java.util.Date;


/**
 * Save the location, state-transition, time and result of the execution of an  {@link GeofenceProfiles} action.
 */
@Entity(
		primaryKeys = { "profileId", "geofenceId", "time" },
		foreignKeys = {
				@ForeignKey(
						entity = GeofenceDto.class,
						parentColumns = "id",
						childColumns = "geofenceId",
						onDelete = ForeignKey.CASCADE),
				@ForeignKey(
						entity = Profile.class,
						parentColumns = "ID",
						childColumns = "profileId",
						onDelete = ForeignKey.CASCADE)})
public class GeofenceProfileState {

	@NonNull
	private Date time;

	@NonNull
	private int profileId;

	@NonNull
	private String geofenceId;

	private LatLng location;

	private int transition;

	private boolean success;

	private String message;


	public GeofenceProfileState(@NonNull int profileId, @NonNull String geofenceId, LatLng location, int transition) {
		this.profileId = profileId;
		this.geofenceId = geofenceId;
		this.time = new Date();
		this.location = location;
		this.transition = transition;
	}

	public GeofenceProfileState assignResponse(GeofenceAction.ActionResponse response) {
		this.message = response.message();
		this.success = true;
		return this;
	}

	public GeofenceProfileState assignError(Throwable error) {
		this.message = error.getMessage();
		this.success = false;
		return this;
	}


	@NonNull
	public Date getTime() {
		return time;
	}


	public void setTime(@NonNull Date time) {
		this.time = time;
	}


	@NonNull
	public int getProfileId() {
		return profileId;
	}


	public void setProfileId(@NonNull int profileId) {
		this.profileId = profileId;
	}


	@NonNull
	public String getGeofenceId() {
		return geofenceId;
	}


	public void setGeofenceId(@NonNull String geofenceId) {
		this.geofenceId = geofenceId;
	}


	public LatLng getLocation() {
		return location;
	}


	public void setLocation(LatLng location) {
		this.location = location;
	}


	public int getTransition() {
		return transition;
	}


	/**
	 * Enter or Leave or dwell ....
	 * see {@link Geofence.GEOFENCE_TRANSITION_ENTER}
	 */
	public void setTransition(int transition) {
		this.transition = transition;
	}


	public boolean isSuccess() {
		return success;
	}


	public void setSuccess(boolean success) {
		this.success = success;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
}
