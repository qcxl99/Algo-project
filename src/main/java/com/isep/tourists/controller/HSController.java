package com.isep.tourists.controller;

import com.isep.tourists.model.HistoricSites;
import com.isep.tourists.model.Site;
import com.isep.tourists.service.HSService;
import com.isep.tourists.service.TourService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/HistoricSites")
public class HSController {
    private final HSService hsService;
    private final TourService tourService;

    @GetMapping
    public Page<HistoricSites> getAllHistoricSites(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return hsService.getAllHistoricSites(page, size);
    }

    @GetMapping("/{id}")
    public HistoricSites getHistoricSitesById(@PathVariable Long id) {
        return hsService.getHistoricSiteById(id);
    }

    @PostMapping
    public HistoricSites createHistoricSites(@RequestBody HistoricSites historicSites) {
        return hsService.createHistoricSite(historicSites);
    }

    @PutMapping("/{id}")
    public void updateHistoricSite(@PathVariable Long id, @RequestBody HistoricSites historicSites) {
        historicSites.setId(id);
        hsService.updateHistoricSite(historicSites);
    }

    @DeleteMapping("/{id}")
    public void deleteMonument(@PathVariable Long id) {
        hsService.deleteHistoricSite(id);
    }

    @GetMapping("/filter")
    public String filterSitesByPeriod(@RequestParam(required = false) String latitude,
                                      @RequestParam(required = false) String longitude,
                                      @RequestParam(required = false) String period,
                                      @RequestParam(required = false) String location, Model model) {
        List<HistoricSites> filteredSites = new ArrayList<>();
        if(period != null && location != null){
            filteredSites = hsService.filterSitesByLocationAndPeriod(period, location);
        }
        else if(period != null){
            filteredSites = hsService.filterSitesByPeriod(period);
        }
        else if(location != null){
            filteredSites = hsService.filterSitesByLocation(location);
        }
        else {
            filteredSites = hsService.getAllHistoricSites(0,15 ).getContent();
        }
        Collections.sort(filteredSites, Comparator.comparingDouble(Site::getDistance));
        model.addAttribute("filteredSites", filteredSites);
        return "HSperiod";
    }


}

