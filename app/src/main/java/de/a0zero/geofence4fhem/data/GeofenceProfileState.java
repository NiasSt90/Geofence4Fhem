package de.a0zero.geofence4fhem.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;
import de.a0zero.geofence4fhem.actions.GeofenceAction;
import de.a0zero.geofence4fhem.app.AppController;

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
						entity = FhemProfile.class,
						parentColumns = "ID",
						childColumns = "profileId",
						onDelete = ForeignKey.CASCADE)},
		indices = @Index("geofenceId")
)
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
		Toast.makeText(AppController.instance(), error.getMessage(), Toast.LENGTH_LONG).show();
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
