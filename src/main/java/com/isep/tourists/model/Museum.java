package com.isep.tourists.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Museums")
public class Museum implements Site{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long museumId;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    @Column(name = "opening_hours")
    private String openingHours;

    @Column(name = "admission_fee")
    private BigDecimal admissionFee;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;
    private double distance;
    private int duration;
    private String address;


    @Override
    public String getHistoricalPeriod() {
        return null;
    }

}

