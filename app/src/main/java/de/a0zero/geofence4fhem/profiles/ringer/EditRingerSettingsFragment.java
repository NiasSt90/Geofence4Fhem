package de.a0zero.geofence4fhem.profiles.ringer;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.maps.model.LatLng;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.data.GeofenceDto;
import de.a0zero.geofence4fhem.data.Profile;
import de.a0zero.geofence4fhem.profiles.ProfilesViewModel;
import de.a0zero.geofence4fhem.profiles.SettingsDataFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class EditRingerSettingsFragment extends Fragment implements SettingsDataFragment {

	private ProfilesViewModel model;

	private CompositeDisposable compositeDisposable = new CompositeDisposable();

	@BindView(R.id.ringerEnter)
	RadioGroup ringerEnter;

	@BindView(R.id.ringerLeave)
	RadioGroup ringerLeave;


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_edit_ringer_settings, container, false);
	}


	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		model = ViewModelProviders.of(getActivity()).get(ProfilesViewModel.class);
		model.getSelected().observe(this, this::loadSelected);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		compositeDisposable.clear();
	}


	private void loadSelected(Profile profile) {
		Integer ringerModeEnter = profile.data(RingerSettings.class).getRingerModeEnter();
		if (ringerModeEnter != null) {
			switch (ringerModeEnter) {
				case AudioManager.RINGER_MODE_SILENT:
					ringerEnter.check(R.id.ringerEnterSilent);
					break;
				case AudioManager.RINGER_MODE_VIBRATE:
					ringerEnter.check(R.id.ringerEnterVibrate);
					break;
				case AudioManager.RINGER_MODE_NORMAL:
					ringerEnter.check(R.id.ringerEnterNormal);
					break;
			}
		}
		else {
			ringerEnter.clearCheck();
		}
		Integer ringerModeLeave = profile.data(RingerSettings.class).getRingerModeLeave();
		if (ringerModeLeave != null) {
			switch (ringerModeLeave) {
				case AudioManager.RINGER_MODE_SILENT:
					ringerLeave.check(R.id.ringerLeaveSilent);
					break;
				case AudioManager.RINGER_MODE_VIBRATE:
					ringerLeave.check(R.id.ringerLeaveVibrate);
					break;
				case AudioManager.RINGER_MODE_NORMAL:
					ringerLeave.check(R.id.ringerLeaveNormal);
					break;
			}
		}
		else {
			ringerLeave.clearCheck();
		}
	}


	@Override
	public void writeBack(Profile profile) {
		RingerSettings ringerSettings = profile.data(RingerSettings.class);
		Integer val = null;
		switch (ringerEnter.getCheckedRadioButtonId()) {
			case R.id.ringerEnterSilent:
				val = AudioManager.RINGER_MODE_SILENT;
				break;
			case R.id.ringerEnterVibrate:
				val = AudioManager.RINGER_MODE_VIBRATE;
				break;
			case R.id.ringerEnterNormal:
				val = AudioManager.RINGER_MODE_NORMAL;
				break;
		}
		ringerSettings.setRingerModeEnter(val);
		val = null;
		switch (ringerLeave.getCheckedRadioButtonId()) {
			case R.id.ringerLeaveSilent:
				val = AudioManager.RINGER_MODE_SILENT;
				break;
			case R.id.ringerLeaveVibrate:
				val = AudioManager.RINGER_MODE_VIBRATE;
				break;
			case R.id.ringerLeaveNormal:
				val = AudioManager.RINGER_MODE_NORMAL;
				break;
		}
		ringerSettings.setRingerModeLeave(val);
	}

	@OnClick(R.id.ringerEnterTest)
	public void testRingerEnter(View view) {
		GeofenceActionChangeRingerSettings testAction = new GeofenceActionChangeRingerSettings();
		LatLng position = new LatLng(0, 0);

		compositeDisposable.add(
		testAction.enter(new GeofenceDto(position), model.getSelected().getValue(), position)
				.observeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						res -> Toast.makeText(getActivity(), "Success:" + res.message(), Toast.LENGTH_LONG).show(),
						err -> Toast.makeText(getActivity(), "Error:" + err.getMessage(), Toast.LENGTH_LONG).show()
				)
		);
	}

	@OnClick(R.id.ringerLeaveTest)
	public void testRingerLeave(View view) {
		GeofenceActionChangeRingerSettings testAction = new GeofenceActionChangeRingerSettings();
		LatLng position = new LatLng(0, 0);

		compositeDisposable.add(
				testAction.leave(new GeofenceDto(position), model.getSelected().getValue(), position)
						.observeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(
								res -> Toast.makeText(getActivity(), "Success:" + res.message(), Toast.LENGTH_LONG).show(),
								err -> Toast.makeText(getActivity(), "Error:" + err.getMessage(), Toast.LENGTH_LONG).show()
						)
		);
	}
}
