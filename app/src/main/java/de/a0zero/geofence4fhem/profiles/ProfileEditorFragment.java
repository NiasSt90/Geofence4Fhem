package de.a0zero.geofence4fhem.profiles;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.data.entities.Profile;


public class ProfileEditorFragment extends DialogFragment {

	@BindView(R.id.profileName)
	EditText profileName;

	private ProfilesViewModel model;

	private SelectedGeofenceAdapter selectedGeofenceAdapter;


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_profile_editor, container);
	}


	@Override
	public void onStart() {
		super.onStart();
		Dialog dialog = getDialog();
		if (dialog != null) {
			int width = ViewGroup.LayoutParams.MATCH_PARENT;
			int height = ViewGroup.LayoutParams.MATCH_PARENT;
			dialog.getWindow().setLayout(width, height);
		}
	}


	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);

		selectedGeofenceAdapter = new SelectedGeofenceAdapter(getActivity());
		RecyclerView recyclerView = view.findViewById(R.id.geofencesList);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setHasFixedSize(true);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setAdapter(selectedGeofenceAdapter);

		model = new ViewModelProvider(getActivity()).get(ProfilesViewModel.class);
		model.getSelected().observe(this, this::loadSelected);
	}

	private void loadSelected(Profile profile) {
		model.getSelectedGeofences().observe(this, geofences -> selectedGeofenceAdapter.setData(geofences));
		profileName.setText(profile.getLabel());
		getChildFragmentManager()
				.beginTransaction()
				.add(R.id.profileData, profile.getType().editor())
				.commit();
	}


	@OnClick(R.id.save)
	public void save(View view) {
		Profile profile = model.getSelected().getValue();
		for (Fragment fragment : getChildFragmentManager().getFragments()) {
			if (fragment instanceof SettingsDataFragment) {
				((SettingsDataFragment) fragment).writeBack(profile);
			}
		}
		profile.setLabel(profileName.getText().toString());
		model.save(profile);
		dismiss();
	}

	@OnClick(R.id.delete)
	public void deleteProfile(View view) {
		model.deleteSelected();
		dismiss();
	}
}
