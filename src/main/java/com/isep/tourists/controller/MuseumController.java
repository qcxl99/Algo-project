package com.isep.tourists.controller;

import com.isep.tourists.model.Museum;
import com.isep.tourists.service.MuseumService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/museums")
public class MuseumController {
    private final MuseumService museumService;

    public MuseumController(MuseumService museumService) {
        this.museumService = museumService;
    }

    @GetMapping
    public Page<Museum> getAllMuseums(Pageable pageable) {
        return museumService.getAllMuseums(pageable);
    }
    @GetMapping("/{id}")
    public Museum getMuseumById(@PathVariable Long id) {
        return museumService.getMuseumById(id);
    }

    @PostMapping
    public Museum createMuseum(@RequestBody Museum museum) {
        return museumService.createMuseum(museum);
    }

    @PutMapping("/{id}")
    public void updateMuseum(@PathVariable Long id, @RequestBody Museum museumt) {
        museumt.setMuseumId(id);
       museumService.updateMuseum(museumt);
    }

    @DeleteMapping("/{id}")
    public void deleteMuseum(@PathVariable Long id) {
        museumService.deleteMuseum(id);
    }


}
