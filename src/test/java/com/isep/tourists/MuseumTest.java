package com.isep.tourists;

import com.isep.tourists.model.Museum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MuseumTest {

    @Test
    public void testMuseum() {
        Museum museum = new Museum();
        museum.setMuseumId(1L);
        museum.setName("Museum 1");
        museum.setDescription("Description 1");
        museum.setLocation("Location 1");
        museum.setOpeningHours("9:30");
        museum.setAdmissionFee(new BigDecimal("10.00"));
        museum.setLatitude(0.0);
        museum.setLongitude(0.0);
        museum.setDistance(0.0);
        museum.setDuration(0);
        museum.setAddress("Address 1");

        assertEquals(1L, museum.getMuseumId());
        assertEquals("Museum 1", museum.getName());
        assertEquals("Description 1", museum.getDescription());
        assertEquals("Location 1", museum.getLocation());
        assertEquals("9:30", museum.getOpeningHours());
        assertEquals(new BigDecimal("10.00"), museum.getAdmissionFee());
        assertEquals(0.0, museum.getLatitude());
        assertEquals(0.0, museum.getLongitude());
        assertEquals(0.0, museum.getDistance());
        assertEquals(0, museum.getDuration());
        assertEquals("Address 1", museum.getAddress());
    }
}
