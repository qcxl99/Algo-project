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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class YourTest {

    @Mock
    private HSRepository hsRepository;

    @Mock
    private MuseumRepository museumRepository;

    @Mock
    private TourService tourService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
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

        List<Site> inputSites = new ArrayList<>();

        List<Site> expectedOutput = new ArrayList<>();

        when(museumRepository.findAll()).thenReturn(new ArrayList<>());
        when(tourService.generateTourRecommendations(Mockito.anyList(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(),
                Mockito.any(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(expectedOutput);

        List<Site> actualOutput = tourService.generateTourRecommendations(inputSites, userLatitude, userLongitude, maxSites, maxBudget, duration, period, location, type, sortBy);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testApplyLocationFilters() {
        String location = "Paris";
        String type = "all";

        Museum museum1 = new Museum();
        museum1.setLocation("Paris");
        Museum museum2 = new Museum();
        museum2.setLocation("London");

        HistoricSites historicSite1 = new HistoricSites();
        historicSite1.setLocation("Paris");
        HistoricSites historicSite2 = new HistoricSites();
        historicSite2.setLocation("Rome");

        List<Museum> museums = new ArrayList<>();
        museums.add(museum1);
        museums.add(museum2);

        List<HistoricSites> historicSites = new ArrayList<>();
        historicSites.add(historicSite1);
        historicSites.add(historicSite2);

        List<Site> expectedFilteredSites = new ArrayList<>();
        expectedFilteredSites.addAll(museums);
        expectedFilteredSites.addAll(historicSites);

        MuseumRepository museumRepository = Mockito.mock(MuseumRepository.class);
        HSRepository hsRepository = Mockito.mock(HSRepository.class);

        Mockito.when(museumRepository.findByLocation(Mockito.anyString())).thenReturn(museums);
        Mockito.when(hsRepository.findByLocation(Mockito.anyString())).thenReturn(historicSites);

        TourService tourService = new TourService(hsRepository, museumRepository);

        List<Site> actualFilteredSites = tourService.applyLocationFilters(new ArrayList<>(), location, type);

        assertEquals(expectedFilteredSites, actualFilteredSites);
    }

    @Test
    public void testFilterSitesByName() {
        String name = "SiteName";

        HistoricSites historicSite = new HistoricSites();
        historicSite.setName(name);

        Museum museum = new Museum();
        museum.setName(name);

        List<Site> sites = new ArrayList<>();
        sites.add(historicSite);
        sites.add(museum);

        HSRepository hsRepository = Mockito.mock(HSRepository.class);
        MuseumRepository museumRepository = Mockito.mock(MuseumRepository.class);
        Mockito.when(hsRepository.findByName(Mockito.anyString())).thenReturn(Collections.singletonList(historicSite));
        Mockito.when(museumRepository.findByName(Mockito.anyString())).thenReturn(Collections.singletonList(museum));

        TourService tourService = new TourService(hsRepository, museumRepository);

        List<Site> filteredSites = tourService.filterSitesByName(sites, name);

        assertEquals(2, filteredSites.size());
        assertTrue(filteredSites.contains(historicSite));
        assertTrue(filteredSites.contains(museum));
    }

    @Test
    public void testFilterSitesByHistoricPeriodAndLocation() {
        String historicPeriod = "Ancient";
        String location = "Paris";
        String type = "all";

        HistoricSites historicSite = new HistoricSites();
        historicSite.setHistoricalPeriod(historicPeriod);
        historicSite.setLocation(location);

        Museum museum = new Museum();
        museum.setLocation(location);

        List<Site> sites = new ArrayList<>();
        sites.add(historicSite);
        sites.add(museum);

        HSRepository hsRepository = Mockito.mock(HSRepository.class);
        MuseumRepository museumRepository = Mockito.mock(MuseumRepository.class);
        Mockito.when(hsRepository.findByLocationAndPeriod(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.singletonList(historicSite));
        Mockito.when(museumRepository.findByLocation(Mockito.anyString())).thenReturn(Collections.singletonList(museum));

        TourService tourService = new TourService(hsRepository, museumRepository);

        List<Site> filteredSites = tourService.filterSitesByHistoricPeriodAndLocation(sites, historicPeriod, location, type);

        assertEquals(2, filteredSites.size());
        assertTrue(filteredSites.contains(historicSite));
        assertTrue(filteredSites.contains(museum));
    }

    @Test
    public void testSortSitesByDistance() {
        double userLatitude = 12.34;
        double userLongitude = 56.78;

        Site site1 = Mockito.mock(Site.class);
        Mockito.when(site1.getLatitude()).thenReturn(12.35);
        Mockito.when(site1.getLongitude()).thenReturn(56.79);

        Site site2 = Mockito.mock(Site.class);
        Mockito.when(site2.getLatitude()).thenReturn(12.36);
        Mockito.when(site2.getLongitude()).thenReturn(56.80);

        List<Site> sites = new ArrayList<>();
        sites.add(site1);
        sites.add(site2);

        TourService tourService = new TourService(hsRepository, museumRepository);

        List<Site> sortedSites = tourService.sortSitesByDistance(sites, userLatitude, userLongitude);

        assertEquals(2, sortedSites.size());
        assertEquals(site1, sortedSites.get(0));
        assertEquals(site2, sortedSites.get(1));
    }

    @Test
    public void testOptimizeTour() {
        int numSitesPerDay = 3;

        Site site1 = Mockito.mock(Site.class);
        Mockito.when(site1.getLatitude()).thenReturn(12.34);
        Mockito.when(site1.getLongitude()).thenReturn(56.78);

        Site site2 = Mockito.mock(Site.class);
        Mockito.when(site2.getLatitude()).thenReturn(12.35);
        Mockito.when(site2.getLongitude()).thenReturn(56.79);

        Site site3 = Mockito.mock(Site.class);
        Mockito.when(site3.getLatitude()).thenReturn(12.36);
        Mockito.when(site3.getLongitude()).thenReturn(56.80);

        Site site4 = Mockito.mock(Site.class);
        Mockito.when(site4.getLatitude()).thenReturn(12.37);
        Mockito.when(site4.getLongitude()).thenReturn(56.81);

        List<Site> sites = new ArrayList<>();
        sites.add(site1);
        sites.add(site2);
        sites.add(site3);
        sites.add(site4);

        TourService tourService = new TourService(hsRepository, museumRepository);

        List<Site> optimizedTour = tourService.optimizeTour(sites, numSitesPerDay);

        assertEquals(4, optimizedTour.size());
        assertEquals(site1, optimizedTour.get(0));
        assertEquals(site2, optimizedTour.get(1));
        assertEquals(site3, optimizedTour.get(2));
        assertEquals(site4, optimizedTour.get(3));

        List<Site> day1 = optimizedTour.subList(0, 3);
        List<Site> day2 = optimizedTour.subList(3, 4);
        assertEquals(3, day1.size());
        assertEquals(1, day2.size());
    }

    @Test
    public void testFilterTourByTotalDuration() {
        int maxTotalDuration = 8; // Maximum total duration in hours
        int maxSitesPerDay = 3; // Maximum number of sites per day

        Site site1 = Mockito.mock(Site.class);
        Mockito.when(site1.getDuration()).thenReturn(2);

        Site site2 = Mockito.mock(Site.class);
        Mockito.when(site2.getDuration()).thenReturn(4);

        Site site3 = Mockito.mock(Site.class);
        Mockito.when(site3.getDuration()).thenReturn(3);

        Site site4 = Mockito.mock(Site.class);
        Mockito.when(site4.getDuration()).thenReturn(1);

        List<Site> tour = new ArrayList<>();
        tour.add(site1);
        tour.add(site2);
        tour.add(site3);
        tour.add(site4);

        TourService tourService = new TourService(hsRepository, museumRepository);

        List<Site> filteredTour = tourService.filterTourByTotalDuration(tour, maxTotalDuration, maxSitesPerDay);

        int totalDuration = calculateTotalDuration(filteredTour);
        int numDays = (int) Math.ceil((double) filteredTour.size() / maxSitesPerDay);

        assertFalse(totalDuration <= maxTotalDuration);
        assertTrue(filteredTour.size() <= maxSitesPerDay * numDays);
    }

    private int calculateTotalDuration(List<Site> sites) {
        int totalDuration = 0;
        for (Site site : sites) {
            totalDuration += site.getDuration();
        }
        return totalDuration;
    }






}
