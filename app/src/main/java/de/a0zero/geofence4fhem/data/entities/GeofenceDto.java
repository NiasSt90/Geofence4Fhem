package de.a0zero.geofence4fhem.data.entities;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
public class GeofenceDto implements Serializable {

    @PrimaryKey
    @NonNull
    private String id;

    private String title;

    private String name;

    private LatLng position;

    private int radius = 100;

    private boolean useDwell = true;

    public GeofenceDto(LatLng position) {
        this.id = UUID.randomUUID().toString();
        this.position = position;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }


    public boolean isUseDwell() {
        return useDwell;
    }


    public void setUseDwell(boolean useDwell) {
        this.useDwell = useDwell;
    }


    //TODO: move into maps package
    public MarkerOptions createMarkerOptions() {
        return new MarkerOptions().position(position).title(title).snippet(name).draggable(true);
    }

    //TODO: move into maps package
    public CircleOptions createCircleOptions() {
        return new CircleOptions().center(position).radius(radius)
                .strokeColor(Color.RED).fillColor(0x220000FF).strokeWidth(5);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeofenceDto geofence = (GeofenceDto) o;
        return Objects.equals(id, geofence.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
