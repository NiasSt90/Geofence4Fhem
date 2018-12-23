package de.a0zero.geofence4fhem.actions;

import android.Manifest;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.a0zero.geofence4fhem.BuildConfig;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.data.Profile;

public class EditFhemNotifyActivity extends AppCompatActivity {


    private static final String TAG = EditFhemNotifyActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 12345;


    @BindView(R.id.fhemUrl)
    EditText fhemUrl;

    @BindView(R.id.fhemUsername)
    EditText fhemUsername;

    @BindView(R.id.fhemPassword)
    EditText fhemPassword;

    @BindView(R.id.fhemDeviceID)
    EditText fhemDeviceID;


    private ActionViewModel model;

    private GeofencesAdapter geofencesAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fhem);
        ButterKnife.bind(this);

        geofencesAdapter = new GeofencesAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.geofencesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(geofencesAdapter);

        model = ViewModelProviders.of(this).get(ActionViewModel.class);
        model.getSelected().observe(this, this::loadSelected);
        model.getAllGeofenceProfiles().observe(this, geofences -> geofencesAdapter.setData(geofences));
    }

    private void loadSelected(Profile profile) {
        fhemUrl.setText(profile.getFhemUrl());
        fhemUsername.setText(profile.getUsername());
        fhemPassword.setText(profile.getPassword());
        fhemDeviceID.setText(profile.getDeviceUUID());
    }

    public void save(View view) {
        Profile value = model.getSelected().getValue();
        if (value != null) {
            value.setFhemUrl(fhemUrl.getText().toString());
            value.setUsername(fhemUsername.getText().toString());
            value.setPassword(fhemPassword.getText().toString());
            value.setDeviceUUID(fhemDeviceID.getText().toString());
            model.save(value);
            finish();
        }
    }

    public void requestDeviceUUID(View unused) {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            fhemDeviceID.setText(manager.getDeviceId());
        }
        else {
            boolean showRequestPermUI = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE);
            if (showRequestPermUI) {
                showSnackbar(R.string.phone_state_permission_rationale, android.R.string.ok, view -> startRequestPermission());
            } else {
                startRequestPermission();
            }
        }
    }

    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
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
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");
                requestDeviceUUID(null);
            } else {
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
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
}
