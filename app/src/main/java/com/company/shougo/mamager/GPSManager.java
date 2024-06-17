package com.company.shougo.mamager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.company.shougo.R;
import com.company.shougo.data.GPSData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

public class GPSManager {

    private static GPSManager gpsManager;

    private final static String TAG = "GPSManager";

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 15000; // 30 seconds
    private static final float LOCATION_DISTANCE = 0.0f; // 1 meters
    public Location mLastLocation = null;

    private boolean isSearch = false;

    private GPSData searchData;

    private LocationListener[] mLocationListeners = new LocationListener[]{
            new mLocationListener(LocationManager.GPS_PROVIDER),
            new mLocationListener(LocationManager.NETWORK_PROVIDER)
    };

    public GPSManager(Context context) {
        initializeLocationManager(context);
    }

    public static GPSManager getInstance(Context context){
        if (gpsManager==null){
            gpsManager = new GPSManager(context);
        }

        return gpsManager;
    }

    @SuppressLint("MissingPermission")
    public void initializeLocationManager(Context context) {
        try {
            Log.e(TAG, "initializeLocationManager");

            // 创建 LocationRequest，配置位置更新参数
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(1000);  // 间隔时间为 10 秒
            locationRequest.setFastestInterval(100);  // 最快间隔时间为 5 秒

            FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(context);
            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        // Update UI with location data

                        mLastLocation = location;
                    }
                }
            };
            locationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
//
//
//
//
//
//
//
//
//
//
//            if (mLocationManager == null) {
//                mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//            }
//
//            if (mLocationManager != null) {
//                try {
//                    mLocationManager.requestLocationUpdates(
//                            LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                            mLocationListeners[1]);
////                    mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                } catch (java.lang.SecurityException ex) {
//                    Log.e(TAG, "fail to request location update, ignore" + ex);
//                } catch (IllegalArgumentException ex) {
//                    Log.e(TAG, "network provider does not exist, " + ex.getMessage());
//                }
//                try {
//                    mLocationManager.requestLocationUpdates(
//                            LocationManager.GPS_PROVIDER, 25000, LOCATION_DISTANCE,
//                            mLocationListeners[0]);
////                    mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                } catch (java.lang.SecurityException ex) {
//                    Log.e(TAG, "fail to request location update, ignore" + ex);
//                } catch (IllegalArgumentException ex) {
//                    Log.e(TAG, "gps provider does not exist " + ex.getMessage());
//                }
//            }
        }catch (Exception e){
            Log.e(TAG, "initializeLocationManager : error : " + e.toString());
        }

    }
    Random random = new Random();

    public double getLat(){
        double lat = 0.0;
        if (mLastLocation!=null){
            lat = mLastLocation.getLatitude();
        }

        if (isSearch){
            lat = searchData.getLat();
        }

        return lat;
    }

    public double getLng(){
        double lng = 0.0;
        if (mLastLocation!=null){
            lng = mLastLocation.getLongitude();
        }

        if (isSearch){
            lng = searchData.getLng();
        }

        return lng;
    }

    public double getMyLat(){
        double lat = 0.0;
        if (mLastLocation!=null){
        }

        return lat;
    }

    public double getMyLng(){
        double lng = 0.0;
        if (mLastLocation!=null){
            lng = mLastLocation.getLongitude();
        }

        return lng;
    }

    public boolean isAddress(String address){
        if (
                address==null
                || address.length()<=0
                || !(
                        address.contains("縣")
                        || address.contains("市")
                    )
        ){
            return false;
        }

        return true;
    }

    public void setSearch(boolean isSearch, GPSData gpsData){
        this.isSearch = isSearch;
        this.searchData = gpsData;
    }

    public GPSData getAddress(Context context){

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        GPSData gpsData = new GPSData();

        if (isSearch){
            gpsData = searchData;
        }else {
            try {
                if(mLastLocation != null){
                    addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 20); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    List<String> addressLines = new ArrayList<>();
                    for (Address address2 : addresses) {
                        for (int i = 0; i <= address2.getMaxAddressLineIndex(); i++) {
                            addressLines.add(address2.getAddressLine(i));
                        }
                    }

                    AddressLineComparator comparator = new AddressLineComparator();
                    Collections.sort(addressLines, comparator);

                    gpsData.setLat(mLastLocation.getLatitude());
                    gpsData.setLng(mLastLocation.getLongitude());
                    gpsData.setAddress(removeAfterFirstChineseCharacter(addressLines.get(0)));
                }
            } catch (Exception e) {
                Log.e(TAG, "getAddress : " + e.toString());
                gpsData.setAddress(context.getResources().getString(R.string.no_gps));
            }
        }
        return gpsData;
    }


    /**
     * 地址有時會出現兩個號，所以要刪除第一個號後多餘的文字
     * @param address address地址
     */
    public String removeAfterFirstChineseCharacter(String address) {
        int index = address.indexOf('號');
        if (index != -1) {
            return address.substring(0, index + 1); // 包括 '號' 字元
        } else {
            return address; // 字元串中沒有 '號'
        }
    }

    public GPSData getLocationFromAddress(Context context,String strAddress){

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        GPSData gpsData = new GPSData();
        gpsData.setAddress(strAddress);

        try {
            address = coder.getFromLocationName(strAddress,1);
            if (address==null) {
                return null;
            }
            Address location = address.get(0);

            gpsData.setLat(location.getLatitude());
            gpsData.setLng(location.getLongitude());
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return gpsData;
    }

    public float getDistance(double myLat, double myLng, double endLat,double endLng){
        float[] results=new float[1];
        Location.distanceBetween(myLat,myLng,endLat,endLng,results);

        return results[0];
    }

    private class mLocationListener implements LocationListener {

        public mLocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
//            if (mLastLocation!=null){
//                Log.e(TAG , "LocationListener lat : " + mLastLocation.getLatitude() + " | lau : " + mLastLocation.getLongitude());
//            }
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            if (location!=null) {
                mLastLocation = location;
            }

//            if (mLastLocation!=null){
//                Log.e(TAG , "onLocationChanged lat : " + mLastLocation.getLatitude() + " | lau : " + mLastLocation.getLongitude());
//            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    public class AddressLineComparator implements Comparator<String> {
        @Override
        public int compare(String line1, String line2) {
            // 定義排序規則
            if (line1.contains("市") && !line2.contains("市")) {
                return -1;
            } else if (line2.contains("市") && !line1.contains("市")) {
                return 1;
            } else if (line1.contains("縣") && !line2.contains("縣")) {
                return -1;
            } else if (line2.contains("縣") && !line1.contains("縣")) {
                return 1;
            } else if (line1.contains("區") && !line2.contains("區")) {
                return -1;
            } else if (line2.contains("區") && !line1.contains("區")) {
                return 1;
            } else if (line1.contains("路") && !line2.contains("路")) {
                return -1;
            } else if (line2.contains("路") && !line1.contains("路")) {
                return 1;
            } else if (line1.contains("號") && !line2.contains("號")) {
                return -1;
            } else if (line2.contains("號") && !line1.contains("號")) {
                return 1;
            } else {
                return line1.compareTo(line2);
            }
        }
    }


}
