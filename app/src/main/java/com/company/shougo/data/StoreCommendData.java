package com.company.shougo.data;

public class StoreCommendData {

    private int vendor_comment_id;
    private int vendor_id;
    private int customer_id;
    private double star;
    private String comments;
    private String date_added;
    private String customer_name;
    private String customer_image;

    public int getVendor_comment_id() {
        return vendor_comment_id;
    }

    public void setVendor_comment_id(int vendor_comment_id) {
        this.vendor_comment_id = vendor_comment_id;
    }

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

    public double getStar() {
        return star;
    }

    public void setStar(double star) {
        this.star = star;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_image() {
        return customer_image;
    }

    public void setCustomer_image(String customer_image) {
        this.customer_image = customer_image;
    }
}
