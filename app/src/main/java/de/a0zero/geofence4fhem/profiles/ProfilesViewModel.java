package de.a0zero.geofence4fhem.profiles;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.a0zero.geofence4fhem.App;
import de.a0zero.geofence4fhem.data.dao.GeofenceProfilesDao;
import de.a0zero.geofence4fhem.data.dao.ProfileDAO;
import de.a0zero.geofence4fhem.data.entities.GeofenceDto;
import de.a0zero.geofence4fhem.data.entities.GeofenceProfiles;
import de.a0zero.geofence4fhem.data.entities.Profile;
import de.a0zero.geofence4fhem.data.entities.SelectedGeofence;

import java.util.List;


public class ProfilesViewModel extends AndroidViewModel {

	private final ProfileDAO profileDAO;

	private final GeofenceProfilesDao geofenceProfilesRepo;

	private LiveData<List<Profile>> profiles;

	private MutableLiveData<Profile> selected = new MutableLiveData<>();

	private LiveData<List<SelectedGeofence>> selectedGeofences = new MutableLiveData<>();


	public ProfilesViewModel(@NonNull Application application) {
		super(application);
		profileDAO = App.profileDAO();
		geofenceProfilesRepo = App.geofenceActionRepo();
		this.profiles = profileDAO.liveList();
	}


	public LiveData<List<Profile>> getAllProfiles() {
		return profiles;
	}


	public LiveData<Profile> getSelected() {
		return selected;
	}


	public void setSelectedProfile(Profile profile) {
		selected.setValue(profile);
		selectedGeofences = profileDAO.selectedGeofences(profile.getID());
	}


	public LiveData<List<SelectedGeofence>> getSelectedGeofences() {
		return selectedGeofences;
	}


	public void save(Profile profile) {
		long profileID = profile.getID();
		if (profileDAO.update(profile) == 0) {
			profileID = profileDAO.add(profile);
		}
		for (SelectedGeofence selectedGeofence : selectedGeofences.getValue()) {
			GeofenceDto geofenceDto = selectedGeofence.geofence;
			GeofenceProfiles geofenceProfiles = new GeofenceProfiles((int) profileID, geofenceDto.getId());
			if (selectedGeofence.selected) {
				geofenceProfilesRepo.add(geofenceProfiles);
			}
			else {
				geofenceProfilesRepo.del(geofenceProfiles);
			}
		}
	}


	public void deleteSelected() {
		profileDAO.delete(selected.getValue());
	}
}
