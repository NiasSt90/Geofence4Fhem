package de.a0zero.geofence4fhem.data;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.profiles.GeofenceAction;
import de.a0zero.geofence4fhem.profiles.fhem.EditFhemSettingsFragment;
import de.a0zero.geofence4fhem.profiles.fhem.GeofenceActionInformFhem;
import de.a0zero.geofence4fhem.profiles.ringer.EditRingerSettingsFragment;
import de.a0zero.geofence4fhem.profiles.ringer.GeofenceActionChangeRingerSettings;


public enum ProfileType {

	FHEM_NOTIFY {
		@Override
		public Fragment editor() {
			return new EditFhemSettingsFragment();
		}


		@NonNull
		@Override
		public GeofenceAction action() {
			return new GeofenceActionInformFhem();
		}


		@Override
		public int getLabelRes() {
			return R.string.profile_type_fhem;
		}


		@Override
		public int getImageRes() {
			return R.drawable.ic_profile_type_fhem_black_48dp;
		}
	},

	RINGER_SETTINGS {
		@Override
		public Fragment editor() {
			return new EditRingerSettingsFragment();
		}


		@NonNull
		@Override
		public GeofenceAction action() {
			return new GeofenceActionChangeRingerSettings();
		}


		@Override
		public int getLabelRes() {
			return R.string.profile_type_ringer;
		}


		@Override
		public int getImageRes() {
			return R.drawable.ic_profile_type_ringer_black_48dp;
		}
	};


	public abstract Fragment editor();

	@NonNull
	public abstract GeofenceAction action();

	public abstract int getLabelRes();

	public abstract int getImageRes();
}
