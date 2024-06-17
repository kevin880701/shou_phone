package com.company.shougo.data;

public class GPSData {

    private double lat,lng;

    private String address;

    public GPSData(){
        lat = 0.0;
        lng = 0.0;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
