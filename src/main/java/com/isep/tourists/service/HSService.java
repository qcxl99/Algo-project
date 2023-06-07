package com.isep.tourists.service;

import com.isep.tourists.Repository.HSRepository;
import com.isep.tourists.model.HistoricSites;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;


@AllArgsConstructor
@Service
public class HSService implements ApplicationRunner {
    private final HSRepository hsRepository;
    private final ResourceLoader resourceLoader;
    private final TourService tourService;


    public Page<HistoricSites> getAllHistoricSites(int page, int size) {
        return hsRepository.findAll(PageRequest.of(page, size));
    }

    public HistoricSites getHistoricSiteById(Long id) {
        return hsRepository.findById(id).orElse(null);
    }

    public HistoricSites createHistoricSite(HistoricSites historicSites) {
        return hsRepository.save(historicSites);
    }

    public void updateHistoricSite(HistoricSites historicSites) {
        hsRepository.save(historicSites);
    }

    public void deleteHistoricSite(Long id) {
        hsRepository.deleteById(id);
    }
    public List<HistoricSites> filterSitesByPeriod(String period) {
        // Logic to filter historic sites by perio

        return hsRepository.findByPeriod(period);
    }
    public List<HistoricSites> filterSitesByLocation(String location) {

        return hsRepository.findByLocation(location);
    }
    public List<HistoricSites> filterSitesByLocationAndPeriod(String period, String location) {

        return hsRepository.findByLocationAndPeriod(period, location);
    }
    public void calculateDistance(double userLatitude, double userLongitude) {
        List<HistoricSites> historicSites = hsRepository.findAll();
        for (HistoricSites site : historicSites) {
            double siteLatitude = site.getLatitude();
            double siteLongitude = site.getLongitude();
            double distance = tourService.calculateHaversineDistance(userLatitude, userLongitude, siteLatitude, siteLongitude);
            site.setDistance(distance);
            hsRepository.save(site);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
/*        List<HistoricSites> historicSiteDataList = readHistoricSitesDataFromFile("monuments.csv");
        hsRepository.saveAll(historicSiteDataList);*/
    }
    public List<HistoricSites> readHistoricSitesDataFromFile(String filename) {
        List<HistoricSites> historicSiteDataList = new ArrayList<>();

        try {
            // Load the file as a resource using the resource loader
            Resource resource = resourceLoader.getResource("classpath:static/" + filename);
            System.out.println(resource);

            // Read the file using a BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

            // Create the CSVParser
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader());

            List<CSVRecord> csvRecords = csvParser.getRecords();
            int totalRecords = csvRecords.size();
            int maxRecords = Math.min(totalRecords, 1500);

/*            Random random = new Random();
            Set<Integer> randomIndexes = new HashSet<>();
            while (randomIndexes.size() < maxRecords) {
                int randomIndex = random.nextInt(totalRecords);
                randomIndexes.add(randomIndex);
            }*/

            for (int index = 0; index< totalRecords -1; index++) {
                CSVRecord csvRecord = csvRecords.get(index);
                HistoricSites historicSiteData = new HistoricSites();
                String appellationCourante = csvRecord.get("appellation_courante");
                String historique = csvRecord.get("historique");
                String region = csvRecord.get("region");
                String siecle = csvRecord.get("siecle");
                String adresse = csvRecord.get("adresse");
                String pCoordonnees = csvRecord.get("p_coordonnees");
                if (appellationCourante != null && historique != null && region != null && siecle != null && pCoordonnees != null && adresse != null)
                historicSiteData.setName(appellationCourante);
                historicSiteData.setDescription(historique);
                historicSiteData.setLocation(csvRecord.get("departement") +", "+ csvRecord.get("commune"));
                historicSiteData.setOpeningHours("9:30");
                historicSiteData.setHistoricalPeriod(siecle);
                historicSiteData.setAdmissionFee(new BigDecimal(new Random().nextDouble(30)));
                historicSiteData.setAddress(adresse);

                String[] coordinates = pCoordonnees.split(",");
                if (coordinates.length == 2) {
                    double latitude = Double.parseDouble(coordinates[0]);
                    double longitude = Double.parseDouble(coordinates[1]);
                    historicSiteData.setLatitude(latitude);
                    historicSiteData.setLongitude(longitude);
                }
                historicSiteDataList.add(historicSiteData);
            }
        } catch (IOException e) {
            // Handle the exception
        }

        return historicSiteDataList;
    }
    // Add additional methods as per your requirements
}
