package de.a0zero.geofence4fhem.profiles.fhem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.data.entities.Profile;
import de.a0zero.geofence4fhem.profiles.ProfilesViewModel;
import de.a0zero.geofence4fhem.profiles.SettingsDataFragment;


public class EditFhemSettingsFragment extends Fragment implements SettingsDataFragment {

	private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 12345;

	@BindView(R.id.fhemUrl)
	EditText fhemUrl;

	@BindView(R.id.fhemUsername)
	EditText fhemUsername;

	@BindView(R.id.fhemPassword)
	EditText fhemPassword;

	@BindView(R.id.fhemDeviceID)
	EditText fhemDeviceID;

	private ProfilesViewModel model;


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_edit_fhem_settings, container, false);
	}


	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		model = new ViewModelProvider(getActivity()).get(ProfilesViewModel.class);
		model.getSelected().observe(getViewLifecycleOwner(), this::loadSelected);
	}


	private void loadSelected(Profile profile) {
		FhemSettings fhemSettings = profile.data(FhemSettings.class);
		fhemUrl.setText(fhemSettings.getFhemUrl());
		fhemUsername.setText(fhemSettings.getUsername());
		fhemPassword.setText(fhemSettings.getPassword());
		fhemDeviceID.setText(fhemSettings.getDeviceUUID());
	}


	@Override
	public void writeBack(Profile profile) {
		FhemSettings fhemSettings = profile.data(FhemSettings.class);
		fhemSettings.setFhemUrl(fhemUrl.getText().toString());
		fhemSettings.setUsername(fhemUsername.getText().toString());
		fhemSettings.setPassword(fhemPassword.getText().toString());
		fhemSettings.setDeviceUUID(fhemDeviceID.getText().toString());
	}


	@OnClick(R.id.deviceUUID)
	public void requestDeviceUUID(View unused) {
		UUID uuid = UUID.randomUUID();
		fhemDeviceID.setText(uuid.toString());
	}

}
