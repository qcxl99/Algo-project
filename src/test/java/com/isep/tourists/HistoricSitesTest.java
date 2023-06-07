package com.isep.tourists;

import com.isep.tourists.model.HistoricSites;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoricSitesTest {

    @Test
    public void testHistoricSites() {
        HistoricSites historicSite = new HistoricSites();
        historicSite.setId(1L);
        historicSite.setName("Site 1");
        historicSite.setDescription("Description 1");
        historicSite.setLocation("Location 1");
        historicSite.setOpeningHours("9:30");
        historicSite.setAdmissionFee(new BigDecimal("10.00"));
        historicSite.setHistoricalPeriod("Period 1");
        historicSite.setLatitude(0.0);
        historicSite.setLongitude(0.0);
        historicSite.setDistance(0.0);
        historicSite.setDuration(0);
        historicSite.setAddress("Address 1");

        assertEquals(1L, historicSite.getId());
        assertEquals("Site 1", historicSite.getName());
        assertEquals("Description 1", historicSite.getDescription());
        assertEquals("Location 1", historicSite.getLocation());
        assertEquals("9:30", historicSite.getOpeningHours());
        assertEquals(new BigDecimal("10.00"), historicSite.getAdmissionFee());
        assertEquals("Period 1", historicSite.getHistoricalPeriod());
        assertEquals(0.0, historicSite.getLatitude());
        assertEquals(0.0, historicSite.getLongitude());
        assertEquals(0.0, historicSite.getDistance());
        assertEquals(0, historicSite.getDuration());
        assertEquals("Address 1", historicSite.getAddress());
    }
}


