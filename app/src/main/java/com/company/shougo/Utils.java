package com.company.shougo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import com.company.shougo.mamager.DialogManager;

import java.util.Objects;

public class Utils {
    public static void showGPSDialog(Activity activity) {
        DialogManager.showBaseDialog(
                activity
                , activity.getResources().getString(R.string.please_open_gps)
                , activity.getResources().getString(R.string.confirm)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(gpsIntent);
                    }
                }
        );
    }

    static public boolean isGPSEnable(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
