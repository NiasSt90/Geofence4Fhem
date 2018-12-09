package de.a0zero.geofence4fhem.actions;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.a0zero.geofence4fhem.app.AppController;
import de.a0zero.geofence4fhem.data.FhemProfile;
import de.a0zero.geofence4fhem.data.GeofenceDto;
import de.a0zero.geofence4fhem.data.GeofenceProfiles;
import de.a0zero.geofence4fhem.data.GeofenceProfilesRepo;

public class ActionViewModel extends AndroidViewModel {

    private static final String TAG = ActionViewModel.class.getSimpleName();

    private final MutableLiveData<FhemProfile> selected = new MutableLiveData<>();

    private final MutableLiveData<List<ProfileWithGeofences>> geofences = new MutableLiveData<>();

    public ActionViewModel(@NonNull Application application) {
        super(application);
        List<FhemProfile> profiles = AppController.profileDAO().listAll();
        int profileID = -1;
        if (profiles.isEmpty()) {
            selected.setValue(new FhemProfile());
        }
        else {
            FhemProfile profile = profiles.get(0);
            selected.setValue(profile);
            profileID = profile.getID();
        }
        GeofenceProfilesRepo geofenceProfilesRepo = AppController.geofenceActionRepo();
        List<ProfileWithGeofences> activatableProfiles = new ArrayList<>();
        List<GeofenceDto> geofencesForProfile = geofenceProfilesRepo.getGeofencesForProfile(profileID);
        for (GeofenceDto geofenceDto : geofencesForProfile) {
            activatableProfiles.add(new ProfileWithGeofences(geofenceDto, true));
        }
        List<GeofenceDto> assignableGeofencesForProfile = geofenceProfilesRepo.getAssignableGeofencesForProfile(profileID);
        for (GeofenceDto geofenceDto : assignableGeofencesForProfile) {
            activatableProfiles.add(new ProfileWithGeofences(geofenceDto, false));
        }
        Collections.sort(activatableProfiles,
                (c1, c2) -> c1.geofenceDto.getTitle().toLowerCase().compareTo(c2.geofenceDto.getTitle().toLowerCase()));
        geofences.setValue(activatableProfiles);
    }

    MutableLiveData<FhemProfile> getSelected() {
        return selected;
    }

    public void save(FhemProfile profile) {
        long profileID = profile.getID();
        if (AppController.profileDAO().update(profile) == 0) {
            profileID = AppController.profileDAO().add(profile);
        }

        for (ProfileWithGeofences profileWithGeofences : geofences.getValue()) {
            GeofenceDto geofenceDto = profileWithGeofences.geofenceDto;
            GeofenceProfiles geofenceProfiles = new GeofenceProfiles((int) profileID, geofenceDto.getId());
            if (profileWithGeofences.active) {
                AppController.geofenceActionRepo().add(geofenceProfiles);
            }
            else {
                AppController.geofenceActionRepo().del(geofenceProfiles);
            }
        }
    }

    LiveData<List<ProfileWithGeofences>> getAllGeofenceProfiles() {
        return geofences;
    }
}
