package com.isep.tourists;

import com.isep.tourists.Repository.HSRepository;
import com.isep.tourists.Repository.MuseumRepository;
import com.isep.tourists.model.HistoricSites;
import com.isep.tourists.model.Museum;
import com.isep.tourists.model.Site;
import com.isep.tourists.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TourServiceTest {

    private TourService tourService;

    @Mock
    private Site mockSite;


    @Mock
    private HSRepository hsRepository;

    @Mock
    private MuseumRepository museumRepository;

    @BeforeEach
    public void setup() {
        initMocks(this);
        tourService = new TourService(hsRepository, museumRepository);
    }

    @Test
    public void testCalculateHaversineDistance() {
        double userLatitude = 12.34;
        double userLongitude = 56.78;
        double siteLatitude = 23.45;
        double siteLongitude = 67.89;
        double expectedDistance = 1703.5563903926998;

        double actualDistance = tourService.calculateHaversineDistance(userLatitude, userLongitude, siteLatitude, siteLongitude);

        assertEquals(expectedDistance, actualDistance, 0.01);
    }

    @Test
    public void testPredictVisitingDurationForMuseum() {
        Museum museum = new Museum();
        int expectedDuration = 6;

        int actualDuration = tourService.predictVisitingDuration(museum);

        assertEquals(expectedDuration, actualDuration);
    }

    @Test
    public void testPredictVisitingDurationForHistoricSite() {
        HistoricSites historicSite = new HistoricSites();
        int expectedDuration = 4;

        int actualDuration = tourService.predictVisitingDuration(historicSite);

        assertEquals(expectedDuration, actualDuration);
    }



}
