package com.example.covidtrackertwo.data;

public class History {
    String locationName;
    String date;

    public String getLocationName() {
        return locationName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public History(String locationName, String date) {
        this.locationName = locationName;
        this.date = date;
    }




}
