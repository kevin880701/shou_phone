package com.company.shougo.data;

public class FavoriteTravelData {
    private int customer_id;
    private int waypoint_id;
    private String name;
    private String date_added;

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getWaypoint_id() {
        return waypoint_id;
    }

    public void setWaypoint_id(int waypoint_id) {
        this.waypoint_id = waypoint_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }
}
