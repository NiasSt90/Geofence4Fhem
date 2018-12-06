package de.a0zero.geofence4fhem.actions;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import de.a0zero.geofence4fhem.app.AppController;
import de.a0zero.geofence4fhem.data.FhemProfile;
import de.a0zero.geofence4fhem.data.FhemProfileRepo;

public class ActionViewModel extends AndroidViewModel {

    FhemProfileRepo fhemProfileRepo;

    private final MutableLiveData<FhemProfile> selected = new MutableLiveData<>();

    public ActionViewModel(@NonNull Application application) {
        super(application);
        fhemProfileRepo = AppController.fhemProfileRepo();
        if (fhemProfileRepo.listAll().isEmpty()) {
            FhemProfile value = new FhemProfile();
            fhemProfileRepo.add(value);
            selected.setValue(value);
        }
        else {
            selected.setValue(fhemProfileRepo.listAll().get(0));
        }
    }

    public void save() {
        fhemProfileRepo.saveAll();
    }

    MutableLiveData<FhemProfile> getSelected() {
        return selected;
    }


}
