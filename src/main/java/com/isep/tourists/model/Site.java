package com.isep.tourists.model;


import java.math.BigDecimal;

public interface Site {

    String getName();
    String getDescription();
    String getLocation();
    BigDecimal getAdmissionFee();
    String getHistoricalPeriod();
    int getDuration();
    double getLatitude();
    double getLongitude();
    double getDistance();
    void setDistance(double distance);

    void setDuration(int duration);
}