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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TourTest {

    @Mock
    private HSRepository hsRepository;

    @Mock
    private MuseumRepository museumRepository;

    private TourService tourService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        tourService = new TourService(hsRepository, museumRepository);
    }

    @Test
    public void testGetAllTour() {
        HistoricSites site1 = new HistoricSites();
        HistoricSites site2 = new HistoricSites();
        List<HistoricSites> historicSites = Arrays.asList(site1, site2);
        when(hsRepository.findAll()).thenReturn(historicSites);

        Museum museum1 = new Museum();
        Museum museum2 = new Museum();
        List<Museum> museums = Arrays.asList(museum1, museum2);
        when(museumRepository.findAll()).thenReturn(museums);

        List<Site> allTour = tourService.getAllTour();

        List<Site> expectedSites = new ArrayList<>();
        expectedSites.addAll(historicSites);
        expectedSites.addAll(museums);

        assertEquals(expectedSites, allTour);
    }

    @Test
    public void testGenerateTourRecommendations_emptySites() {
        List<Site> sites = new ArrayList<>();
        double userLatitude = 12.34;
        double userLongitude = 56.78;
        int maxSites = 3;
        BigDecimal maxBudget = BigDecimal.valueOf(100);
        int duration = 2;
        String period = "Ancient";
        String location = "Paris";
        String type = "all";
        String sortBy = "distance";

        List<Site> expectedOutput = new ArrayList<>();

        List<Site> actualOutput = tourService.generateTourRecommendations(sites, userLatitude, userLongitude, maxSites, maxBudget, duration, period, location, type, sortBy);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testGenerateTourRecommendations() {
        double userLatitude = 12.34;
        double userLongitude = 56.78;
        int maxSites = 3;
        BigDecimal maxBudget = BigDecimal.valueOf(100);
        int duration = 2;
        String period = "Ancient";
        String location = "Paris";
        String type = "all";
        String sortBy = "distance";

        Site site1 = Mockito.mock(Site.class);
        Mockito.when(site1.getName()).thenReturn("Site 1");
        Mockito.when(site1.getDescription()).thenReturn("Description of Site 1");
        Mockito.when(site1.getLocation()).thenReturn("Location 1");
        Mockito.when(site1.getAdmissionFee()).thenReturn(BigDecimal.valueOf(10));
        Mockito.when(site1.getHistoricalPeriod()).thenReturn("Ancient");
        Mockito.when(site1.getDuration()).thenReturn(2);
        Mockito.when(site1.getLatitude()).thenReturn(12.34);
        Mockito.when(site1.getLongitude()).thenReturn(56.78);
        Mockito.when(site1.getDistance()).thenReturn(0.0);

        Site site2 = Mockito.mock(Site.class);
        Mockito.when(site2.getName()).thenReturn("Site 2");
        Mockito.when(site2.getDescription()).thenReturn("Description of Site 2");
        Mockito.when(site2.getLocation()).thenReturn("Location 2");
        Mockito.when(site2.getAdmissionFee()).thenReturn(BigDecimal.valueOf(20));
        Mockito.when(site2.getHistoricalPeriod()).thenReturn("Modern");
        Mockito.when(site2.getDuration()).thenReturn(3);
        Mockito.when(site2.getLatitude()).thenReturn(12.34);
        Mockito.when(site2.getLongitude()).thenReturn(56.78);
        Mockito.when(site2.getDistance()).thenReturn(0.0);

        List<Site> inputSites = new ArrayList<>();
        inputSites.add(site1);
        inputSites.add(site2);

        List<Site> expectedOutput = new ArrayList<>();
        expectedOutput.add(site1);
        expectedOutput.add(site2);

        TourService tourService = new TourService(hsRepository, museumRepository);

        List<Site> actualOutput = tourService.generateTourRecommendations(inputSites, userLatitude, userLongitude, maxSites, maxBudget, duration, period, location, type, sortBy);

        assertEquals(expectedOutput, actualOutput);
    }







    // 其他测试方法...
}
