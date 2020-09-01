package de.a0zero.geofence4fhem.maps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.data.entities.GeofenceDto;

public class CreateZoneDialog extends AppCompatDialogFragment {

    @BindView(R.id.zoneKoordinaten)
    TextView zoneKoordinaten;

    @BindView(R.id.zoneName)
    EditText zoneNameEditView;

    @BindView(R.id.zoneTitle)
    EditText zoneTitleEditView;

    @BindView(R.id.zoneRadiusText)
    EditText zoneRadiusEditView;

    private GeofencesViewModel model;

    private GeofenceDto geofence;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.zone_create, null);
        ButterKnife.bind(this, dialogView);

        model = new ViewModelProvider(getActivity()).get(GeofencesViewModel.class);
        model.getSelected().observe(this, this::initDialogWithData);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setPositiveButton("ok", (dialog, id) -> createZone())
                .setNegativeButton("cancel", (dialog, id) -> CreateZoneDialog.this.getDialog().cancel());
        return builder.create();
    }

    private void initDialogWithData(GeofenceDto geofence) {
        if (geofence != null) {
            zoneKoordinaten.setText(geofence.getPosition().toString());
            zoneTitleEditView.setText(geofence.getTitle());
            zoneNameEditView.setText(geofence.getName());
            zoneRadiusEditView.setText(String.valueOf(geofence.getRadius()));
            this.geofence = geofence;
        }
    }

    private void createZone() {
        geofence.setTitle(zoneTitleEditView.getText().toString());
        geofence.setName(zoneNameEditView.getText().toString());
        geofence.setRadius(Integer.valueOf(zoneRadiusEditView.getText().toString()));
        model.updated(geofence);
    }

}
