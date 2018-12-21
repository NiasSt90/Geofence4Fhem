package de.a0zero.geofence4fhem.actions;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.a0zero.geofence4fhem.R;

public class GeofencesAdapter extends RecyclerView.Adapter<GeofencesAdapter.GeofencesViewHolder> {


    private Context context;
    private List<ProfileWithGeofences> data;
    private LayoutInflater layoutInflater;

    public GeofencesAdapter(Context context) {
        this.data = new ArrayList<>();
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public GeofencesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.layout_geofence_item, parent, false);
        return new GeofencesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GeofencesViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<ProfileWithGeofences> newData) {
        if (data != null) {
            data.clear();
            data.addAll(newData);
        } else {
            data = newData;
        }
        notifyDataSetChanged();
    }


    class GeofencesViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private Switch toggleButton;

        GeofencesViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.geofenceName);
            toggleButton = itemView.findViewById(R.id.geofenceEnabled);
        }

        void bind(ProfileWithGeofences item) {
            if (item != null) {
                title.setText(item.geofenceDto.getTitle());
                toggleButton.setTag(item);
                toggleButton.setChecked(item.active);
                toggleButton.setOnCheckedChangeListener(
                        (button, isChecked) -> ((ProfileWithGeofences)button.getTag()).active = isChecked);
            }
        }
    }
}
