package de.a0zero.geofence4fhem.data;

import de.a0zero.geofence4fhem.actions.EditFhemNotifyActivity;
import de.a0zero.geofence4fhem.actions.GeofenceAction;
import de.a0zero.geofence4fhem.actions.GeofenceActionInformFhem;

public enum ProfileType {

    FHEM_NOTIFY {
        @Override
        public Class<?> getEditorActionClass() {
            return EditFhemNotifyActivity.class;
        }

        @Override
        public Class<? extends GeofenceAction> getGeofenceActionClass() {
            return GeofenceActionInformFhem.class;
        }
    },

    PHONE_SETTINGS {
        @Override
        public Class<?> getEditorActionClass() {
            return null;
        }

        @Override
        public Class<? extends GeofenceAction> getGeofenceActionClass() {
            return null;
        }
    };

    public abstract Class<?> getEditorActionClass();

    public abstract Class<? extends GeofenceAction> getGeofenceActionClass();

}
