package de.a0zero.geofence4fhem.data;

import de.a0zero.geofence4fhem.actions.EditFhemNotifyActivity;

public enum ProfileType {

    FHEM_NOTIFY {
        @Override
        public Class<?> getEditorActionClass() {
            return EditFhemNotifyActivity.class;
        }

        @Override
        public Class<?> getGeofenceActionClass() {
            return null;
        }
    },

    PHONE_SETTINGS {
        @Override
        public Class<?> getEditorActionClass() {
            return null;
        }

        @Override
        public Class<?> getGeofenceActionClass() {
            return null;
        }
    };

    public abstract Class<?> getEditorActionClass();

    public abstract Class<?> getGeofenceActionClass();

}
