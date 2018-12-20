package de.a0zero.geofence4fhem.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import butterknife.ButterKnife;
import de.a0zero.geofence4fhem.BuildConfig;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.actions.EditFhemNotifyActivity;
import de.a0zero.geofence4fhem.maps.MapsActivity;
import de.a0zero.geofence4fhem.transition.GeofencesManager;
import de.a0zero.geofence4fhem.transition.TrackingService;


public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.getName();

	private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

	private GeofencesManager geofencesManager;


	@Override
	protected void onStart() {
		super.onStart();
		if (!checkPermissions()) {
			requestPermissions();
		}
	}


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		findViewById(R.id.gotoMaps)
				.setOnClickListener(e -> startActivity(new Intent(this, MapsActivity.class)));
		findViewById(R.id.gotoActions)
				.setOnClickListener(e -> startActivity(new Intent(this, EditFhemNotifyActivity.class)));

		startService(new Intent(this, TrackingService.class));
		geofencesManager = new GeofencesManager(this);
	}


	public void addGeofencesButtonHandler(View view) {
		if (!checkPermissions()) {
			requestPermissions();
			return;
		}
		addGeofences();
	}

	public void removeGeofencesButtonHandler(View view) {
		if (!checkPermissions()) {
			requestPermissions();
			return;
		}
		removeGeofences();
	}


	public void startTrackingService(View view) {
		startService(new Intent(this, TrackingService.class));
	}


	public void stopTrackingService(View view) {
		stopService(new Intent(this, TrackingService.class));
	}


	@SuppressWarnings("MissingPermission")
	private void addGeofences() {
		if (!checkPermissions()) {
			showSnackbar(getString(R.string.insufficient_permissions));
			return;
		}
		if (!geofencesManager.addGeofences()) {
			showSnackbar("No geofences created to add???");
		}
	}


	@SuppressWarnings("MissingPermission")
	private void removeGeofences() {
		if (!checkPermissions()) {
			showSnackbar(getString(R.string.insufficient_permissions));
			return;
		}
		geofencesManager.removeGeofences();
	}

	/**
	 * Return the current state of the permissions needed.
	 */
	private boolean checkPermissions() {
		int permissionState = ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION);
		return permissionState == PackageManager.PERMISSION_GRANTED;
	}


	private void requestPermissions() {
		boolean showRequestPermUI = ActivityCompat.shouldShowRequestPermissionRationale(this,
				Manifest.permission.ACCESS_FINE_LOCATION);
		if (showRequestPermUI) {
			showSnackbar(R.string.permission_rationale, android.R.string.ok, view -> startRequestPermission());
		}
		else {
			startRequestPermission();
		}
	}


	private void startRequestPermission() {
		ActivityCompat.requestPermissions(MainActivity.this,
				new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
				REQUEST_PERMISSIONS_REQUEST_CODE);
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		Log.i(TAG, "onRequestPermissionResult");
		if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
			if (grantResults.length <= 0) {
				// If user interaction was interrupted, the permission request is cancelled and you
				// receive empty arrays.
				Log.i(TAG, "User interaction was cancelled.");
			}
			else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.i(TAG, "Permission granted.");
				//performPendingGeofenceTask();
			}
			else {
				// Permission denied.

				// Notify the user via a SnackBar that they have rejected a core permission for the
				// app, which makes the Activity useless. In a real app, core permissions would
				// typically be best requested during a welcome-screen flow.

				// Additionally, it is important to remember that a permission might have been
				// rejected without asking the user for permission (device policy or "Never ask
				// again" prompts). Therefore, a user interface affordance is typically implemented
				// when permissions are denied. Otherwise, your app could appear unresponsive to
				// touches or interactions which have required permissions.
				showSnackbar(R.string.permission_denied_explanation, R.string.settings,
						view -> {
							// Build intent that displays the App settings screen.
							Intent intent = new Intent();
							intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
							intent.setData(uri);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						});
				//mPendingGeofenceTask = PendingGeofenceTask.NONE;
			}
		}
	}


	/**
	 * Shows a {@link Snackbar}.
	 *
	 * @param mainTextStringId The id for the string resource for the Snackbar text.
	 * @param actionStringId   The text of the action item.
	 * @param listener         The listener associated with the Snackbar action.
	 */
	private void showSnackbar(final int mainTextStringId, final int actionStringId,
			View.OnClickListener listener) {
		Snackbar.make(
				findViewById(android.R.id.content),
				getString(mainTextStringId),
				Snackbar.LENGTH_INDEFINITE)
				.setAction(getString(actionStringId), listener).show();
	}


	private void showSnackbar(final String text) {
		View container = findViewById(android.R.id.content);
		if (container != null) {
			Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
		}
	}

}
