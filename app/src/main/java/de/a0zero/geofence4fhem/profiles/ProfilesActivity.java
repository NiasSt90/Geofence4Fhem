package de.a0zero.geofence4fhem.profiles;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.data.ProfileType;
import de.a0zero.geofence4fhem.data.entities.Profile;


public class ProfilesActivity extends AppCompatActivity implements ProfileOnClickListener {

	private static final String TAG = ProfilesActivity.class.getSimpleName();

	private ProfilesAdapter profilesAdapter;

	private ProfilesViewModel model;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_profiles);
		ButterKnife.bind(this);

		profilesAdapter = new ProfilesAdapter(this, this);
		RecyclerView recyclerView = findViewById(R.id.profilesList);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setHasFixedSize(true);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setAdapter(profilesAdapter);

		model = new ViewModelProvider(this).get(ProfilesViewModel.class);
		model.getAllProfiles().observe(this, profiles -> profilesAdapter.setData(profiles));
	}


	@OnClick(R.id.fab_add)
	public void onCreateProfile(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose Profile type");
		CharSequence[] profileLabels = new CharSequence[ProfileType.values().length];
		for (int i = 0; i < ProfileType.values().length; i++) {
			profileLabels[i] = getString(ProfileType.values()[i].getLabelRes());
		}
		builder.setItems(profileLabels, (dialog, which) -> createProfile(ProfileType.values()[which]))
				.show();
	}


	private void createProfile(ProfileType type) {
		model.setSelectedProfile(new Profile(type));
		new ProfileEditorFragment().show(getSupportFragmentManager(), "EDITOR");
	}


	@Override
	public void onProfileClick(Profile profile) {
		model.setSelectedProfile(profile);
		new ProfileEditorFragment().show(getSupportFragmentManager(), "EDITOR");
	}
}
