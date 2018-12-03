package de.a0zero.geofence4fhem.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.a0zero.geofence4fhem.transition.AddingGeofencesService;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, AddingGeofencesService.class);
        context.startService(startServiceIntent);
    }
}
