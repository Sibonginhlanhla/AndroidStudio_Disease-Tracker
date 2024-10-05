package com.example.covidtrackertwo.data;

public class CheckIn {
    private String location_name;
    private Integer positive_count;
    private Integer negative_count;
    private String proportion_percentage;

    public CheckIn(String location_name, Integer positive_count, Integer negative_count, String proportion_percentage) {
        this.location_name = location_name;
        this.positive_count = positive_count;
        this.negative_count = negative_count;
        this.proportion_percentage = proportion_percentage;
    }
}
