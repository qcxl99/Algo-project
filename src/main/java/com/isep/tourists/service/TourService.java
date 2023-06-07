package com.isep.tourists.service;


import com.isep.tourists.Repository.HSRepository;
import com.isep.tourists.Repository.MuseumRepository;
import com.isep.tourists.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@AllArgsConstructor
@Service
public class TourService {
    private final HSRepository hsRepository;
    private final MuseumRepository museumRepository;

    public List<Site> getAllTour(){
        List<Site> sites = new ArrayList<>();
        // Retrieve historic sites
        List<HistoricSites> historicSites = hsRepository.findAll();
        // Retrieve museums
        List<Museum> museums = museumRepository.findAll();
        sites.addAll(historicSites);
        sites.addAll(museums);
        return sites;
    }
    public List<Site> generateTourRecommendations(List<Site> sites, double userLatitude, double userLongitude, int maxSites, BigDecimal maxBudget, int duration,String period, String location, String type, String sortBy) {
        if (sites.isEmpty()) {
            return sites;
        }
        sites = initial(sites, userLatitude, userLongitude);

        List<Site> filteredSites = applyBudgetaryFilters(sites, maxSites, maxBudget);

        // Check if the filtered sites list is empty
        if (filteredSites.isEmpty()) {
            return filteredSites;
        }

        // Divide the available time into smaller intervals or days
        int numDays = duration;
        int numSitesPerDay = (int) Math.ceil((double) filteredSites.size() / numDays);
        //filter by duration
        List<Site> filteredbyDuration = filterTourByTotalDuration(filteredSites,duration,numSitesPerDay);
        // Check if the filtered sites list is empty
        if (filteredbyDuration.isEmpty()) {
            return filteredSites;
        }

        // Optimize the order of visiting the sites or museums
        List<Site> optimizedTour = optimizeTour(filteredbyDuration, numSitesPerDay);
        // Sort the sites or museums by distance
        List<Site> sortedSites = new ArrayList<>(optimizedTour);
        if(sortBy.equalsIgnoreCase("price")){
            sortedSites = sortSitesByBudget(sortedSites);
        }
        else {
            sortedSites = sortSitesByDistance(sortedSites, userLatitude, userLongitude);
        }
        // Return the optimized tour recommendations
        return sortedSites;
    }

    public List<Site> initial(List<Site> sites, double userLatitude, double userLongitude) {
        for (Site site : sites){
            site.setDistance(Math.round(calculateHaversineDistance(userLatitude, userLongitude, site.getLatitude(), site.getLongitude())));
            site.setDuration(predictVisitingDuration(site));
        }
        return sites;
    }

    public List<Site> applyBudgetaryFilters(List<Site> sites, int maxSites, BigDecimal maxBudget) {
        List<Site> filteredSites = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Site site : sites) {
            // Check if the admission fee is within the user's budget
            if (site.getAdmissionFee().compareTo(maxBudget) <= 0) {
                // Add the site to the filtered list
                filteredSites.add(site);

                // Update the total cost
                totalCost = totalCost.add(site.getAdmissionFee());
            }

            // Check if the desired number of sites has been reached
            if (filteredSites.size() >= maxSites) {
                break;
            }
        }

        // Return the filtered sites or museums
        return filteredSites;
    }
    public List<Site> applyLocationFilters(List<Site> sites, String location, String type){
        List<Site> filteredSites = new ArrayList<>();
        List<Museum> museums = museumRepository.findByLocation(location);
        List<HistoricSites> historicSites = hsRepository.findByLocation(location);
        if(type.equalsIgnoreCase("museums")){
            filteredSites.addAll(museums);
        }
        else if(type.equalsIgnoreCase("historicSites")){
            filteredSites.addAll(historicSites);
        }
        else {
            filteredSites.addAll(museums);
            filteredSites.addAll(historicSites);
        }
        return filteredSites;
    }
    public List<Site> filterSitesByHistoricPeriod(List<Site> sites, String historicPeriod, String type) {
        List<Site> filteredSites = new ArrayList<>();
        List<HistoricSites> historicSites = hsRepository.findByPeriod(historicPeriod);
        List<Museum> museums = museumRepository.findAll();
        if(type.equalsIgnoreCase("museums")){
            filteredSites.addAll(museums);
        }
        else if(type.equalsIgnoreCase("historicSites")){
            filteredSites.addAll(historicSites);
        }
        else {
            filteredSites.addAll(historicSites);
            filteredSites.addAll(museums);
        }
        return filteredSites;
    }
    public List<Site> filterSitesByName(List<Site> sites, String name) {
        List<Site> filteredSites = new ArrayList<>();
        List<HistoricSites> historicSites = hsRepository.findByName(name);
        List<Museum> museums = museumRepository.findByName(name);
        for(Site site: sites){
            if(historicSites.contains(site) || museums.contains(site)){
                filteredSites.add(site);
            }
        }
        return filteredSites;
    }
    public List<Site> filterSitesByHistoricPeriodAndLocation(List<Site> sites, String historicPeriod, String location, String type) {
        List<Site> filteredSites = new ArrayList<>();
        List<HistoricSites> historicSites = hsRepository.findByLocationAndPeriod(historicPeriod,location);
        List<Museum> museums = museumRepository.findByLocation(location);
        if(type.equalsIgnoreCase("museums")){
            filteredSites.addAll(museums);
        }
        else if(type.equalsIgnoreCase("historicSites")){
            filteredSites.addAll(historicSites);
        }
        else {
            filteredSites.addAll(historicSites);
            filteredSites.addAll(museums);
        }
        return filteredSites;
    }
    public List<Site> sortSitesByDistance(List<Site> sites, double userLatitude, double userLongitude) {

        List<Site> sortedSites = new ArrayList<>(sites);

        sortedSites.sort(Comparator.comparingDouble(site -> calculateHaversineDistance(userLatitude, userLongitude, site.getLatitude(), site.getLongitude())));

        return sortedSites;
    }

    public List<Site> optimizeTour(List<Site> sites, int numSitesPerDay) {
        List<Site> optimizedTour = new ArrayList<>();
        Set<Site> unvisitedSites = new HashSet<>(sites);

        // Start with the first site as the initial site
        Site currentSite = sites.get(0);
        optimizedTour.add(currentSite);
        unvisitedSites.remove(currentSite);

        // Iterate until all sites are visited
        while (!unvisitedSites.isEmpty()) {
            double shortestDistance = Double.MAX_VALUE;
            Site nearestSite = null;

            // Find the nearest unvisited site to the current site
            for (Site site : unvisitedSites) {
                double distance = calculateHaversineDistance(currentSite.getLatitude(), currentSite.getLongitude(), site.getLatitude(), site.getLongitude());
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    nearestSite = site;
                }
            }

            // Add the nearest site to the optimized tour
            optimizedTour.add(nearestSite);
            unvisitedSites.remove(nearestSite);

            // Update the current site
            currentSite = nearestSite;
        }

        // Divide the optimized tour into smaller intervals or days
        List<Site> dividedTour = new ArrayList<>();
        int numDays = (int) Math.ceil((double) optimizedTour.size() / numSitesPerDay);
        int startIndex = 0;

        for (int i = 0; i < numDays; i++) {
            int endIndex = Math.min(startIndex + numSitesPerDay, optimizedTour.size());
            List<Site> dayTour = optimizedTour.subList(startIndex, endIndex);
            dividedTour.addAll(dayTour);
            startIndex += numSitesPerDay;
        }

        return dividedTour;
    }
    public List<Site> filterTourByTotalDuration(List<Site> tour, int maxTotalDuration, int maxSitesPerDay) {
        maxTotalDuration *= 24;
        int totalDuration = calculateTotalDuration(tour);
        int eatingDuration = 30; // Eating duration in minutes
        int sleepingDuration = 480; // Sleeping duration in minutes (8 hours)

        int numSites = tour.size();
        if (totalDuration <= maxTotalDuration) {
            return tour; // No need to filter, the tour fits within the maximum total duration
        }

        // Calculate the total time spent on eating, sleeping, and relaxing
        int totalLeisureTime = eatingDuration + sleepingDuration;

        // Calculate the reduction factor based on the total leisure time and the desired leisure time
        double reductionFactor = (double) totalLeisureTime / totalDuration;

        // Adjust the durations of the sites to reduce the average leisure time
        for (Site site : tour) {
            int adjustedDuration = (int) Math.ceil(site.getDuration() * reductionFactor);
            site.setDuration(adjustedDuration);
        }

        // Sort the tour again based on the adjusted durations
        tour.sort(Comparator.comparingInt(Site::getDuration));

        // Recalculate the total duration
        totalDuration = calculateTotalDuration(tour);

        // Remove sites from the tour until the total duration fits within the maximum total duration
        while (totalDuration > maxTotalDuration && numSites > 0) {
            Site siteToRemove = tour.get(numSites - 1);
            tour.remove(siteToRemove);
            totalDuration -= siteToRemove.getDuration();
            numSites--;
        }

        // Adjust the durations of eating and sleeping for each day
        int numDays = (int) Math.ceil((double) tour.size() / maxSitesPerDay);
        int remainingDuration = maxTotalDuration - calculateTotalDuration(tour);

        if (numDays > 1 && remainingDuration > 0) {
            int dailyLeisureTime = remainingDuration / numDays;

            // Adjust the durations of eating and sleeping for each day
            for (int i = 0; i < numDays; i++) {
                int startIndex = i * maxSitesPerDay;
                int endIndex = Math.min(startIndex + maxSitesPerDay, tour.size());

                for (int j = startIndex; j < endIndex; j++) {
                    Site site = tour.get(j);
                    site.setDuration(site.getDuration() + dailyLeisureTime);
                }
            }
        }

        return tour;
    }
    public List<Site> sortSitesByBudget(List<Site> sites) {
        List<Site> sortedSites = new ArrayList<>(sites);
        sortedSites.sort(Comparator.comparing(Site::getAdmissionFee));
        return sortedSites;
    }

    public double calculateHaversineDistance(double userLatitude, double userLongitude, double siteLatitude, double siteLongitude) {
        double earthRadius = 6371; // Radius of the Earth in kilometers

        // Calculate differences
        double latDiff = Math.toRadians(siteLatitude - userLatitude);
        double lonDiff = Math.toRadians(siteLongitude - userLongitude);

        // Haversine formula
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(Math.toRadians(userLatitude)) * Math.cos(Math.toRadians(siteLatitude))
                * Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate distance
        double distance = earthRadius * c;

        return distance;
    }

    public int predictVisitingDuration(Site site) {
        int baseDuration = 2; // Default base duration in hours
        int exhibitCount = 5; // Default number of exhibits
        int averageVisitorTime = 60; // Default average visitor time in minutes

        // Adjust the base duration based on the type of place
        if (site instanceof Museum) {
            exhibitCount = 10; // Simulate the number of exhibits for museums
            averageVisitorTime = 60; // Simulate the average visitor time for museums
        } else if (site instanceof HistoricSites) {
            exhibitCount = 5; // Simulate the number of exhibits for historic sites
            averageVisitorTime = 45; // Simulate the average visitor time for historic sites
        }

        // Adjust the base duration based on the size or number of exhibits
        baseDuration += exhibitCount * 0.3; // Assume 20 minutes per exhibit

        // Adjust the base duration based on average visitor time
        baseDuration += Math.ceil((double) averageVisitorTime / 60); // Convert minutes to hours, rounded up

        return baseDuration;
    }
    public int calculateTotalDuration(List<Site> tour) {
        int totalDuration = 0;
        for (Site site : tour) {
            site.setDuration(predictVisitingDuration(site));
            totalDuration += site.getDuration();
        }
        return totalDuration;
    }


}

