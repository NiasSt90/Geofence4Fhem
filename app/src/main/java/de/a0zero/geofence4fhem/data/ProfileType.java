package de.a0zero.geofence4fhem.data;

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
		public Fragment getEditorFragment() {
			return new EditFhemSettingsFragment();
		}


		@Override
		public Class<? extends GeofenceAction> getGeofenceActionClass() {
			return GeofenceActionInformFhem.class;
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
		public Fragment getEditorFragment() {
			return new EditRingerSettingsFragment();
		}


		@Override
		public Class<? extends GeofenceAction> getGeofenceActionClass() {
			return GeofenceActionChangeRingerSettings.class;
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


	public abstract Fragment getEditorFragment();

	public abstract Class<? extends GeofenceAction> getGeofenceActionClass();

	public abstract int getLabelRes();

	public abstract int getImageRes();
}
