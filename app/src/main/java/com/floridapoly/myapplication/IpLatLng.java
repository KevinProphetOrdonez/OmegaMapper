package com.floridapoly.myapplication;

public class IpLatLng {
    private String ip;
    private double lat, lng;

    IpLatLng(String newIP, double newLat, double newLng){
        this.ip = newIP;
        this.lat = newLat;
        this.lng = newLng;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
}
