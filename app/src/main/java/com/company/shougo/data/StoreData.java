package com.company.shougo.data;

import java.util.List;

public class StoreData {

    public StoreData(){
        favorites = false;
    }

    private boolean favorites;

    private int vendor_id;
    private int customer_id;
    private String description;
    private String store_owner;
    private String store_name;
    private String address;
    private String email;
    private String telephone;
    private String fax;
    private String parking;
    private String city;
    private String logo;
    private String facebook;
    private String instagram;
    private String youtube;
    private String twitter;
    private String pinterest;
    private int status;
    private int approved;
    private String date_added;
    private int type;
    private double star;
    private double lat;
    private double lng;
    private double p_lat;
    private double p_lng;
    private double distance;
    private int category_id;
    private List<Desc> desc;

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStore_owner() {
        return store_owner;
    }

    public void setStore_owner(String store_owner) {
        this.store_owner = store_owner;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getPinterest() {
        return pinterest;
    }

    public void setPinterest(String pinterest) {
        this.pinterest = pinterest;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getStar() {
        return star;
    }

    public void setStar(double star) {
        this.star = star;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public boolean isFavorites() {
        return favorites;
    }

    public void setFavorites(boolean favorite) {
        this.favorites = favorite;
    }

    public double getP_lat() {
        return p_lat;
    }

    public void setP_lat(double p_lat) {
        this.p_lat = p_lat;
    }

    public double getP_lng() {
        return p_lng;
    }

    public void setP_lng(double p_lng) {
        this.p_lng = p_lng;
    }

    public List<Desc> getDesc() {
        return desc;
    }

    public void setDesc(List<Desc> desc) {
        this.desc = desc;
    }

    public static class Desc {
        private int vendor_d_id;
        private int vendor_id;
        private String description;
        private String youtube;
        private String image;
        private int sort;

        public int getVendor_d_id() {
            return vendor_d_id;
        }

        public void setVendor_d_id(int vendor_d_id) {
            this.vendor_d_id = vendor_d_id;
        }

        public int getVendor_id() {
            return vendor_id;
        }

        public void setVendor_id(int vendor_id) {
            this.vendor_id = vendor_id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getYoutube() {
            return youtube;
        }

        public void setYoutube(String youtube) {
            this.youtube = youtube;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }
    }
}
