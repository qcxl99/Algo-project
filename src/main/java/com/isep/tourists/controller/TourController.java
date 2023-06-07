package com.isep.tourists.controller;

import com.isep.tourists.Repository.HSRepository;
import com.isep.tourists.Repository.MuseumRepository;
import com.isep.tourists.model.HistoricSites;
import com.isep.tourists.model.Museum;
import com.isep.tourists.model.Site;
import com.isep.tourists.service.TourService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

@Controller
@AllArgsConstructor
public class TourController {

    private TourService tourService;
    private HSRepository hsRepository;
    private MuseumRepository museumRepository;

    @GetMapping("/tour-recommendations")
    public String showTourRecommendations(@RequestParam(value ="userLatitude", required = false) Double userLatitude,
                                          @RequestParam(value ="userLongitude", required = false) Double userLongitude,
                                          @RequestParam(value ="name",required = false) String name,
                                          @RequestParam(value = "maxSites", required = false) Integer maxSites,
                                          @RequestParam(value ="maxBudget", required = false) BigDecimal maxBudget,
                                          @RequestParam(value ="duration", required = false) Integer duration,
                                          @RequestParam(value ="period",required = false) String period,
                                          @RequestParam(value ="location",required = false) String location,
                                          @RequestParam(value ="type", required = false) String type,
                                          @RequestParam(value ="sortBy",required = false) String sortBy,Model model) {

        if(userLatitude == null || userLongitude == null){
            return "recommendations";
        }
        // Retrieve the tour recommendations and populate the model
        if(maxSites == null){
            maxSites = 10;
        }
        if(maxBudget == null){
            maxBudget = new BigDecimal(200);
        }
        if(duration == null){
            duration = 7;
        }
        // Retrieve the sites or museums based on the user's preferences
        List<Site> sites = new ArrayList<>();
        // Retrieve historic sites
        List<HistoricSites> historicSites = hsRepository.findAll();
        // Retrieve museums
        List<Museum> museums = museumRepository.findAll();
        //Select type of place
        if(type != null && type.equalsIgnoreCase("historicSites")){
            sites.addAll(historicSites);
            if(period != null && location != null){
                sites = tourService.filterSitesByHistoricPeriodAndLocation(sites, period, location, "historicSites");
            }
            else if(period != null && location == null){
                sites = tourService.filterSitesByHistoricPeriod(sites, period, "historicSites");
            }
            else if(location != null && period == null){
                sites = tourService.applyLocationFilters(sites, location, "historicSites");
            }
        }
        else if(type != null && type.equalsIgnoreCase("museums")){
            sites.addAll(museums);
            if(period != null && location != null){
                sites = tourService.filterSitesByHistoricPeriodAndLocation(sites, period, location, "museums");
            }
            else if(period != null && location == null){
                sites = tourService.filterSitesByHistoricPeriod(sites, period, "museums");
            }
            else if(location != null && period == null){
                sites = tourService.applyLocationFilters(sites, location, "museums");
            }
        }
        else {
            sites.addAll(historicSites);
            sites.addAll(museums);
            if(period != null && location != null){
                sites = tourService.filterSitesByHistoricPeriodAndLocation(sites, period, location, "all");
            }
            else if(period != null && location == null){
                sites = tourService.filterSitesByHistoricPeriod(sites, period, "all");
            }
            else if(location != null && period == null){
                sites = tourService.applyLocationFilters(sites, location, "all");
            }
        }
        if(name != null){
            sites = tourService.filterSitesByName(sites,name);
        }

        List<Site> tourRecommendations = tourService.generateTourRecommendations(sites,
                userLatitude, userLongitude,
                maxSites,
                maxBudget,
                duration,
                location,
                period,
                type,
                sortBy);

        model.addAttribute("tourRecommendations", tourRecommendations);

        // Return the Thymeleaf template to display the tour recommendations
        return "recommendations";
    }
    @GetMapping("/filter")
    public String filterSitesByPeriod(@RequestParam(value ="latitude", required = false) Double latitude,
                                      @RequestParam(value ="longitude", required = false) Double longitude,
                                      @RequestParam(value ="period",required = false) String period,
                                      @RequestParam(value ="location",required = false) String location,
                                      @RequestParam(value ="sortBy",required = false) String sortBy, Model model) {
        List<Site> filteredSites = tourService.getAllTour();
        if(latitude == null || longitude == null){
            latitude =0d;
            longitude=0d;
        }
        if(period != null && location != null){
            filteredSites = tourService.filterSitesByHistoricPeriodAndLocation(filteredSites,period, location,"all");
        }
        else if(period != null){
            filteredSites = tourService.filterSitesByHistoricPeriod(filteredSites,period,"all");
        }
        else if(location != null){
            filteredSites = tourService.applyLocationFilters(filteredSites, location,"all");
        }
        else {
            filteredSites = tourService.getAllTour();
        }
        filteredSites = tourService.initial(filteredSites, latitude, longitude);
        if(sortBy != null && sortBy.equalsIgnoreCase("price")){
            filteredSites = tourService.sortSitesByBudget(filteredSites);
        }
        else {
            filteredSites = tourService.sortSitesByDistance(filteredSites, latitude, longitude);
        }
        model.addAttribute("filteredSites", filteredSites);
        return "Location_and_period";
    }
}

