
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
@Table(name = "HistoricSites")
public class HistoricSites implements Site{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(columnDefinition = "TEXT")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    @Column(name = "opening_hours")
    private String openingHours;

    @Column(name = "admission_fee")
    private BigDecimal admissionFee;

    @Column(name = "historical_period")
    private String historicalPeriod;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;
    private double distance;
    private int duration;
    @Column(columnDefinition = "TEXT")
    private String address;


}


