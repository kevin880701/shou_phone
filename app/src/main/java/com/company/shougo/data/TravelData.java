package com.company.shougo.data;

public class TravelData {

    public TravelData(){
        type = 0;
        lat = 0;
        lng = 0;
        distance = 0;
    }

    private long id;
    private int type;
    private int vendor_id;
    private String vendor_name;
    private String vendor_logo;
    private int coupon_id;
    private String coupon_name;
    private String coupon_logo;
    private double lat;
    private double lng;
    private String address;
    private String custom_address;
    private String parking_address;
    private double distance;
    public int sort;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getVendor_logo() {
        return vendor_logo;
    }

    public void setVendor_logo(String vendor_logo) {
        this.vendor_logo = vendor_logo;
    }

    public int getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(int coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getCoupon_name() {
        return coupon_name;
    }

    public void setCoupon_name(String coupon_name) {
        this.coupon_name = coupon_name;
    }

    public String getCoupon_logo() {
        return coupon_logo;
    }

    public void setCoupon_logo(String coupon_logo) {
        this.coupon_logo = coupon_logo;
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

    public String getCustom_address() {
        return custom_address;
    }

    public void setCustom_address(String custom_address) {
        this.custom_address = custom_address;
    }

    public String getParking_address() {
        return parking_address;
    }

    public void setParking_address(String parking_address) {
        this.parking_address = parking_address;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
