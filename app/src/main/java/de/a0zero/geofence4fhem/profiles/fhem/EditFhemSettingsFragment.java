package de.a0zero.geofence4fhem.profiles.fhem;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.material.snackbar.Snackbar;
import de.a0zero.geofence4fhem.BuildConfig;
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
		model = ViewModelProviders.of(getActivity()).get(ProfilesViewModel.class);
		model.getSelected().observe(this, this::loadSelected);
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


	@OnClick(R.id.requestDeviceUUID)
	public void requestDeviceUUID(View unused) {
		TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)
			 == PackageManager.PERMISSION_GRANTED) {
			fhemDeviceID.setText(manager.getDeviceId());
		}
		else {
			boolean showRequestPermUI =
					ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_PHONE_STATE);
			if (showRequestPermUI) {
				showSnackbar(R.string.phone_state_permission_rationale, android.R.string.ok,
						view -> startRequestPermission());
			}
			else {
				startRequestPermission();
			}
		}
	}


	private void startRequestPermission() {
		ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE},
				REQUEST_PERMISSIONS_REQUEST_CODE);
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
			if (grantResults.length <= 0) {
				// If user interaction was interrupted, the permission request is cancelled and you
				// receive empty arrays.
			}
			else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				requestDeviceUUID(null);
			}
			else {
				// Permission denied.
				showSnackbar(R.string.phone_state_permission_denied_explanation, R.string.settings,
						view -> {
							// Build intent that displays the App settings screen.
							Intent intent = new Intent();
							intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
							intent.setData(uri);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						});
			}
		}
	}


	private void showSnackbar(final int mainTextStringId, final int actionStringId, View.OnClickListener listener) {
		Snackbar.make(
				getView().findViewById(android.R.id.content),
				getString(mainTextStringId),
				Snackbar.LENGTH_INDEFINITE)
				.setAction(getString(actionStringId), listener).show();
	}

}
