package com.example.finsmart.main_activity.home_page;

public class SumDataPoint {
    private final String date;
    private final double price;

    public SumDataPoint(String date, double price) {
        this.date = date;
        this.price = price;
    }

    public String getDate() { return date; }
    public double gePrice() { return price; }
}