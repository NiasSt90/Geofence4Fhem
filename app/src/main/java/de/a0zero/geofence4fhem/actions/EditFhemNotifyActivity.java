package de.a0zero.geofence4fhem.actions;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.data.FhemProfile;

public class EditFhemNotifyActivity extends AppCompatActivity {

    private ActionViewModel model;

    @BindView(R.id.fhemUrl)
    EditText fhemUrl;

    @BindView(R.id.fhemUsername)
    EditText fhemUsername;

    @BindView(R.id.fhemPassword)
    EditText fhemPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fhem);
        ButterKnife.bind(this);

        model = ViewModelProviders.of(this).get(ActionViewModel.class);
        model.getSelected().observe(this, this::loadSelected);
    }

    private void loadSelected(FhemProfile profile) {
        fhemUrl.setText(profile.getFhemUrl());
        fhemUsername.setText(profile.getUsername());
        fhemPassword.setText(profile.getPassword());
    }

    public void save(View view) {
        FhemProfile value = model.getSelected().getValue();
        if (value != null) {
            value.setFhemUrl(fhemUrl.getText().toString());
            value.setUsername(fhemUsername.getText().toString());
            value.setPassword(fhemPassword.getText().toString());
            model.save();
            finish();
        }
    }
}
