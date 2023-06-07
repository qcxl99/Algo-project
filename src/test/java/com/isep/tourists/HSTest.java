package com.isep.tourists;

import com.isep.tourists.Repository.HSRepository;
import com.isep.tourists.controller.HSController;
import com.isep.tourists.model.HistoricSites;
import com.isep.tourists.service.HSService;
import com.isep.tourists.service.TourService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class HSTest {

    private int csvRowCountFromService;

    @Autowired
    private ResourceLoader resourceLoader;

    private HSService hsService;

    @MockBean
    private HSRepository hsRepository;

    @MockBean
    private TourService tourService;

    @BeforeEach
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        hsService = new HSService(hsRepository, null, tourService); // TODO: Replace null with actual ResourceLoader

        // Read your CSV file here and convert the records to HistoricSites objects
        List<HistoricSites> historicSites = readHistoricSitesDataFromFile("monuments.csv");

        // Then save the data to your mock repository
        Page<HistoricSites> page = new PageImpl<>(historicSites);
        when(hsRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);
    }

    private List<HistoricSites> readHistoricSitesDataFromFile(String filename) throws IOException {
        // Load the file as a resource using the ClassPathResource
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("static/" + filename).getInputStream()));

        // Create the CSVParser
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader());

        List<CSVRecord> csvRecords = csvParser.getRecords();
        for (CSVRecord csvRecord : csvRecords) {
            // Parse CSV fields into HistoricSites fields
            String[] fields = csvRecord.get(0).split(";");

            if (fields.length >= 11) {
                HistoricSites historicSiteData = new HistoricSites();
                historicSiteData.setName(fields[0]);
                historicSiteData.setHistoricalPeriod(fields[1]);
                historicSiteData.setDescription(fields[7]);
                historicSiteData.setLocation(fields[10]);
                historicSiteData.setOpeningHours("9:30");
                historicSiteData.setAddress(fields[9]);

                String pCoordonnees = fields[14];
                String[] coordinates = pCoordonnees.split(",");
                if (coordinates.length == 2) {
                    double latitude = Double.parseDouble(coordinates[0]);
                    double longitude = Double.parseDouble(coordinates[1]);
                    historicSiteData.setLatitude(latitude);
                    historicSiteData.setLongitude(longitude);
                }

                System.out.println(historicSiteData); // Print the HistoricSites object
            }
        }

        return csvRecords.stream()
                .map(csvRecord -> {
                    HistoricSites historicSiteData = new HistoricSites();
                    // Parse CSV fields into HistoricSites fields
                    String[] fields = csvRecord.get(0).split(";");

                    if (fields.length >= 11) {
                        historicSiteData.setName(fields[0]);
                        historicSiteData.setHistoricalPeriod(fields[1]);
                        historicSiteData.setDescription(fields[7]);
                        historicSiteData.setLocation(fields[10]);
                        historicSiteData.setOpeningHours("9:30");
                        historicSiteData.setAddress(fields[9]);

                        String pCoordonnees = fields[14];
                        String[] coordinates = pCoordonnees.split(",");
                        if (coordinates.length == 2) {
                            double latitude = Double.parseDouble(coordinates[0]);
                            double longitude = Double.parseDouble(coordinates[1]);
                            historicSiteData.setLatitude(latitude);
                            historicSiteData.setLongitude(longitude);
                        }
                    }
                    return historicSiteData;
                })
                .collect(Collectors.toList());
    }


    private int getHistoricSiteFieldCount(HistoricSites historicSite) {
        int fieldCount = 0;
        Field[] fields = HistoricSites.class.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                fieldCount++;
            }
        }
        return fieldCount;
    }

    @Test
    public void testGetAllHistoricSites() {
        List<HistoricSites> historicSites = Arrays.asList(
                new HistoricSites(1L, "Site 1", "Description 1", "Location 1", "9:30", new BigDecimal("10.00"), "Period 1", 0.0, 0.0, 0.0, 0, "Address 1"),
                new HistoricSites(2L, "Site 2", "Description 2", "Location 2", "9:30", new BigDecimal("15.00"), "Period 2", 0.0, 0.0, 0.0, 0, "Address 2"),
                new HistoricSites(3L, "Site 3", "Description 3", "Location 3", "9:30", new BigDecimal("20.00"), "Period 3", 0.0, 0.0, 0.0, 0, "Address 3")
        );
        Page<HistoricSites> page = new PageImpl<>(historicSites);

        when(hsRepository.findAll(any(PageRequest.class))).thenReturn(page);

        HSService hsService = new HSService(hsRepository, resourceLoader, tourService);
        Page<HistoricSites> result = hsService.getAllHistoricSites(0, 10);

        verify(hsRepository, times(1)).findAll(any(PageRequest.class));
        assertEquals(page, result);
    }

    @Test
    public void testGetHistoricSiteById() {
        Long siteId = 1L;
        HistoricSites historicSite = new HistoricSites(siteId, "Site 1", "Description 1", "Location 1", "9:30", new BigDecimal("10.00"), "Period 1", 0.0, 0.0, 0.0, 0, "Address 1");

        when(hsRepository.findById(siteId)).thenReturn(Optional.of(historicSite));

        HSService hsService = new HSService(hsRepository, resourceLoader, tourService);
        HistoricSites result = hsService.getHistoricSiteById(siteId);

        verify(hsRepository, times(1)).findById(siteId);
        assertEquals(historicSite, result);
    }

    @Test
    public void testCalculateDistance() {
        double userLatitude = 12.34;
        double userLongitude = 56.78;
        HistoricSites site1 = new HistoricSites();
        site1.setLatitude(12.34);
        site1.setLongitude(56.78);
        HistoricSites site2 = new HistoricSites();
        site2.setLatitude(23.45);
        site2.setLongitude(67.89);
        List<HistoricSites> historicSites = Arrays.asList(site1, site2);

        when(hsRepository.findAll()).thenReturn(historicSites);
        when(tourService.calculateHaversineDistance(userLatitude, userLongitude, site1.getLatitude(), site1.getLongitude()))
                .thenReturn(10.0);
        when(tourService.calculateHaversineDistance(userLatitude, userLongitude, site2.getLatitude(), site2.getLongitude()))
                .thenReturn(20.0);

        HSService hsService = new HSService(hsRepository, resourceLoader, tourService);
        hsService.calculateDistance(userLatitude, userLongitude);

        verify(hsRepository, times(1)).findAll();
        verify(tourService, times(1)).calculateHaversineDistance(userLatitude, userLongitude, site1.getLatitude(), site1.getLongitude());
        verify(tourService, times(1)).calculateHaversineDistance(userLatitude, userLongitude, site2.getLatitude(), site2.getLongitude());
        verify(hsRepository, times(1)).save(site1);
        verify(hsRepository, times(1)).save(site2);

        assertEquals(10.0, site1.getDistance(), 0.01);
        assertEquals(20.0, site2.getDistance(), 0.01);
    }





    @Test
    public void testFindByPeriod() {
        String period = "12e siècle";
        List<HistoricSites> sites = hsRepository.findByPeriod(period);

        for (HistoricSites site : sites) {
            assertEquals(period, site.getHistoricalPeriod());
        }
    }

    @Test
    public void testFindByLocation() {
        String location = "Cuzance";
        List<HistoricSites> sites = hsRepository.findByLocation(location);

        for (HistoricSites site : sites) {
            assertEquals(location, site.getLocation());
        }
    }

    @Test
    public void testFindByLocationAndPeriod() {
        String location = "Cuzance";
        String period = "12e siècle";
        List<HistoricSites> sites = hsRepository.findByLocationAndPeriod(period, location);

        for (HistoricSites site : sites) {
            assertEquals(location, site.getLocation());
            assertEquals(period, site.getHistoricalPeriod());
        }
    }

    @Test
    public void testFindByName() {
        String name = "Eglise de Rignac";
        List<HistoricSites> sites = hsRepository.findByName(name);

        for (HistoricSites site : sites) {
            assertEquals(name, site.getName());
        }
    }

    public int getCsvRowCountFromService() {
        return csvRowCountFromService;
    }
}




