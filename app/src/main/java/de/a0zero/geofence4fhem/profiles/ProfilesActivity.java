package de.a0zero.geofence4fhem.profiles;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.data.Profile;


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

		model = ViewModelProviders.of(this).get(ProfilesViewModel.class);
		model.getAllProfiles().observe(this, profiles -> profilesAdapter.setData(profiles));
	}


	@Override
	public void onProfileClick(Profile profile) {
		model.setSelectedProfile(profile);
		new ProfileEditorFragment().show(getSupportFragmentManager(), "EDITOR");
	}
}
