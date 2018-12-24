package de.a0zero.geofence4fhem.data;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

public class SelectedGeofence {

	@Embedded
	public GeofenceDto geofence;

	@ColumnInfo(name = "selected")
	public boolean selected;
}
