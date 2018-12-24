package de.a0zero.geofence4fhem.profiles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.data.Profile;

import java.util.ArrayList;
import java.util.List;


public class ProfilesAdapter extends RecyclerView.Adapter<ProfilesAdapter.ProfilesViewHolder> {

	private Context context;

	private final ProfileOnClickListener onClickListener;

	private List<Profile> data;

	private LayoutInflater layoutInflater;


	public ProfilesAdapter(Context context, ProfileOnClickListener onClickListener) {
		this.data = new ArrayList<>();
		this.context = context;
		this.onClickListener = onClickListener;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	@NonNull
	@Override
	public ProfilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = layoutInflater.inflate(R.layout.layout_profile_item, parent, false);
		return new ProfilesViewHolder(itemView);
	}


	@Override
	public void onBindViewHolder(@NonNull ProfilesViewHolder holder, int position) {
		holder.bind(data.get(position));
	}


	@Override
	public int getItemCount() {
		return data.size();
	}


	public void setData(List<Profile> newData) {
		if (data != null) {
			data.clear();
			data.addAll(newData);
		}
		else {
			data = newData;
		}
		notifyDataSetChanged();
	}


	class ProfilesViewHolder extends RecyclerView.ViewHolder {

		private TextView title;


		ProfilesViewHolder(View itemView) {
			super(itemView);
			title = itemView.findViewById(R.id.profileName);
		}


		void bind(Profile item) {
			if (item != null) {
				title.setTag(item);
				title.setOnClickListener(l -> onClickListener.onProfileClick((Profile) l.getTag()));
				title.setText(item.getType().name() + ":" + item.getLabel());
			}
		}
	}
}
