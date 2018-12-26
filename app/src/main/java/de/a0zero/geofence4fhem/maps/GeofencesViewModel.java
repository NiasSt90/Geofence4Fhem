package de.a0zero.geofence4fhem.maps;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import de.a0zero.geofence4fhem.app.App;
import de.a0zero.geofence4fhem.data.dao.GeofenceDao;
import de.a0zero.geofence4fhem.data.entities.GeofenceDto;

import java.util.List;


class GeofencesViewModel extends AndroidViewModel {

    private final GeofenceDao geofenceRepo;

    private final MutableLiveData<List<GeofenceDto>> geofences = new MutableLiveData<>();

    private final MutableLiveData<GeofenceDto> selected = new MutableLiveData<>();

    private final MutableLiveData<GeofenceDto> changed = new MutableLiveData<>();

    public GeofencesViewModel(@NonNull Application application) {
        super(application);
        geofenceRepo = App.geofenceRepo();
        geofences.setValue(geofenceRepo.listAll());
    }

    void saveGeofences() {
        geofenceRepo.updateAll(geofences.getValue());
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
            geofences.getValue().remove(geofence);
        }
    }

    MutableLiveData<GeofenceDto> getSelected() {
        return selected;
    }


    void updated(GeofenceDto geofence) {
        int affectedRows = geofenceRepo.update(geofence);
        if (affectedRows == 0) {
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
