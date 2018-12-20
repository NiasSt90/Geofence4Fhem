package de.a0zero.geofence4fhem.app;

import android.Manifest;
import android.app.PendingIntent;
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
import android.widget.Toast;
import butterknife.ButterKnife;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import de.a0zero.geofence4fhem.BuildConfig;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.actions.EditFhemNotifyActivity;
import de.a0zero.geofence4fhem.data.GeofenceDto;
import de.a0zero.geofence4fhem.maps.MapsActivity;
import de.a0zero.geofence4fhem.transition.AddingGeofencesService;
import de.a0zero.geofence4fhem.transition.GeofenceBroadcastReceiver;
import de.a0zero.geofence4fhem.transition.GeofenceErrorMessages;
import de.a0zero.geofence4fhem.transition.TrackingService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.getName();

	private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

	private GeofencingClient geofencingClient;


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

		geofencingClient = LocationServices.getGeofencingClient(this);

		findViewById(R.id.gotoMaps)
				.setOnClickListener(e -> startActivity(new Intent(this, MapsActivity.class)));
		findViewById(R.id.gotoActions)
				.setOnClickListener(e -> startActivity(new Intent(this, EditFhemNotifyActivity.class)));

		startService(new Intent(this, TrackingService.class));
	}


	public void addGeofencesButtonHandler(View view) {
		if (!checkPermissions()) {
			requestPermissions();
			return;
		}
		//these are two different ways of inserting the geofences, old (AddingGeofencesService) and new one...
		if (false) {
			Intent startServiceIntent = new Intent(this, AddingGeofencesService.class);
			startService(startServiceIntent);
		}
		else {
			addGeofences();
		}
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
		GeofencingRequest geofencingRequest = getGeofencingRequest();
		if (!geofencingRequest.getGeofences().isEmpty()) {
			geofencingClient.addGeofences(geofencingRequest,
					getGeofencePendingIntent(GeofenceBroadcastReceiver.ACTION_GEOFENCE_UPDATE))
					.addOnCompleteListener(this::onComplete);
		}
		else {
			showSnackbar("No geofences created to add....");
		}
	}


	@SuppressWarnings("MissingPermission")
	private void removeGeofences() {
		if (!checkPermissions()) {
			showSnackbar(getString(R.string.insufficient_permissions));
			return;
		}
		geofencingClient.removeGeofences(
				getGeofencePendingIntent(GeofenceBroadcastReceiver.ACTION_GEOFENCE_UPDATE))
				.addOnCompleteListener(this::onComplete);
	}


	public void onComplete(@NonNull Task<Void> task) {
		if (task.isSuccessful()) {
			//switchButtonsEnabledState();
			Toast.makeText(this, "TaskOnComplete with Success", Toast.LENGTH_SHORT).show();
		}
		else {
			// Get the status code for the error and log it using a user-friendly message.
			String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
			Log.e("ERROR", errorMessage);
			Toast.makeText(this, "TaskOnComplete Error:" + errorMessage, Toast.LENGTH_LONG).show();
		}
	}


	/**
	 * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
	 * Also specifies how the geofence notifications are initially triggered.
	 */
	private GeofencingRequest getGeofencingRequest() {
		List<Geofence> geofenceList = new ArrayList<>();
		for (GeofenceDto geofence : AppController.geofenceRepo().listAll()) {
			geofenceList.add(new Geofence.Builder()
					.setRequestId(geofence.getId())
					.setCircularRegion(geofence.getPosition().latitude, geofence.getPosition().longitude,
							geofence.getRadius())
					.setExpirationDuration(Geofence.NEVER_EXPIRE)
					.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
					.build());
		}

		GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
		// The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
		// GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
		// is already inside that geofence.
		builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
		builder.addGeofences(geofenceList);
		return builder.build();
	}


	private PendingIntent getGeofencePendingIntent(String action) {
		Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
		intent.setAction(action);
		PendingIntent geofencePendingIntent =
				PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return geofencePendingIntent;
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
