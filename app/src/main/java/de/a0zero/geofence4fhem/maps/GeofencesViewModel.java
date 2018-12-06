package de.a0zero.geofence4fhem.maps;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import de.a0zero.geofence4fhem.app.AppController;
import de.a0zero.geofence4fhem.data.GeofenceDto;
import de.a0zero.geofence4fhem.data.GeofenceRepo;


class GeofencesViewModel extends AndroidViewModel {

    private final GeofenceRepo geofenceRepo;

    private final MutableLiveData<List<GeofenceDto>> geofences = new MutableLiveData<>();

    private final MutableLiveData<GeofenceDto> selected = new MutableLiveData<>();

    private final MutableLiveData<GeofenceDto> changed = new MutableLiveData<>();

    public GeofencesViewModel(@NonNull Application application) {
        super(application);
        geofenceRepo = AppController.geofenceRepo();
        geofences.setValue(geofenceRepo.listAll());
    }

    void saveGeofences() {
        geofenceRepo.saveAll();
    }

    void create(GeofenceDto geofence) {
        select(geofence);
    }

    void select(GeofenceDto geofence) {
        selected.setValue(geofence);
    }

    void delete(GeofenceDto geofence) {
        if (geofence != null) {
            geofenceRepo.delete(geofence);
        }
    }

    MutableLiveData<GeofenceDto> getSelected() {
        return selected;
    }


    void updated(GeofenceDto geofence) {
        if (!geofenceRepo.exists(geofence)) {
            geofenceRepo.add(geofence);
        }
        changed.setValue(geofence);
    }

    MutableLiveData<GeofenceDto> getChanged() {
        return changed;
    }

    MutableLiveData<List<GeofenceDto>> geofenceLiveData() {
        return geofences;
    }

}
