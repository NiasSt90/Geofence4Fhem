package de.a0zero.geofence4fhem.maps;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.data.GeofenceDto;

/**
 * create/edit/delete Geofences on a map....<br/>
 * maps-api key needed. see build file where to put it.
 * <p>
 * finally the created geofences are save to (shared-prefs) {@link de.a0zero.geofence4fhem.data.GeofenceRepo}
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private GoogleMap googleMap;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @BindView(R.id.fab_saveAll)
    FloatingActionButton fabSaveAll;

    @BindView(R.id.delMarker)
    ImageButton markerDelButton;

    @BindView(R.id.editMarker)
    ImageButton markerEditButton;

    private GeofencesViewModel model;

    Map<String, Marker> markerMap = new HashMap<>();
    Map<String, Circle> circleMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocationPermission();
        model = ViewModelProviders.of(this).get(GeofencesViewModel.class);
        fabSaveAll.setOnClickListener(event -> model.saveGeofences());
        markerEditButton.setOnClickListener(this::onClickEdiutGeofenceButton);
        markerDelButton.setOnClickListener(this::onClickDeleteGeofenceButton);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMapClickListener(this::onMapClickListener);
        this.googleMap.setOnMapLongClickListener(this::onMapLongClickListener);
        this.googleMap.setOnMarkerClickListener(this::onMarkerClickListener);
        this.googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d("DEBUG", "onMarkerDragEnd:" + marker.getId());
                GeofenceDto geofence = (GeofenceDto) marker.getTag();
                if (geofence != null) {
                    geofence.setPosition(marker.getPosition());
                    Circle circle = circleMap.get(geofence.getId());
                    circle.setCenter(marker.getPosition());
                }
            }
        });
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        markerMap = new WeakHashMap<>();
        circleMap = new WeakHashMap<>();
        model.geofenceLiveData().observe(this, this::createMarkers);
        model.getChanged().observe(this, this::createOrUpdateMarker);

        updateLocationUI();
        moveCameraToCurrentLocation();
    }

    private void createOrUpdateMarker(GeofenceDto geofence) {
        Marker marker = markerMap.get(geofence.getId());
        if (marker != null) {
            marker.setTitle(geofence.getTitle());
            marker.setSnippet(geofence.getName());
            marker.setPosition(geofence.getPosition());
            marker.showInfoWindow();
        } else {
            marker = googleMap.addMarker(geofence.createMarkerOptions());
            marker.setTag(geofence);
            markerMap.put(geofence.getId(), marker);
        }

        Circle circle = circleMap.get(geofence.getId());
        if (circle != null) {
            circle.setRadius(geofence.getRadius());
        } else {
            circle = googleMap.addCircle(geofence.createCircleOptions());
            circleMap.put(geofence.getId(), circle);
        }
    }

    private void onMapClickListener(LatLng position) {
        markerEditButton.setVisibility(View.INVISIBLE);
        markerDelButton.setVisibility(View.INVISIBLE);
    }

    private void onMapLongClickListener(LatLng position) {
        Log.d("DEBUG", "Clicked on location:" + position.latitude + "/" + position.longitude);
        markerEditButton.setVisibility(View.INVISIBLE);
        markerDelButton.setVisibility(View.INVISIBLE);
        model.create(new GeofenceDto(position));
        new CreateZoneDialog().show(getSupportFragmentManager(), "CreateZone");
    }

    private boolean onMarkerClickListener(Marker marker) {
        fabSaveAll.setEnabled(true);
        marker.showInfoWindow();
        model.select((GeofenceDto) marker.getTag());
        markerEditButton.setTag(marker.getTag());
        markerEditButton.setVisibility(View.VISIBLE);
        markerDelButton.setTag(marker.getTag());
        markerDelButton.setVisibility(View.VISIBLE);
        return true;
    }

    private void onClickEdiutGeofenceButton(View view) {
        new CreateZoneDialog().show(getSupportFragmentManager(), "UpdateZone");
    }

    private void onClickDeleteGeofenceButton(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Geofence Löschen")
                .setMessage("Möchten sie den Geofence löschen?")
                .setPositiveButton("delete", (dialog, id) -> deleteSelectedGeofence())
                .setNegativeButton("cancel", (dialog, id) -> Log.d(TAG, "delete aborted"));
        builder.show();
    }

    private void deleteSelectedGeofence() {
        GeofenceDto selectedGeofence = model.getSelected().getValue();
        if (selectedGeofence != null) {
            Marker marker = markerMap.get(selectedGeofence.getId());
            if (marker != null) marker.remove();
            Circle circle = circleMap.get(selectedGeofence.getId());
            if (circle != null) circle.remove();
            model.delete(selectedGeofence);
        }
    }

    private void createMarkers(List<GeofenceDto> geofences) {
        for (GeofenceDto geofence : geofences) {
            createOrUpdateMarker(geofence);
        }
    }

    private void moveCameraToCurrentLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 16));
                        }
                    } else {
                        Log.e("Maps", "Exception: %s", task.getException());
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}
