package com.isep.tourists.Repository;

import com.isep.tourists.model.HistoricSites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HSRepository extends JpaRepository<HistoricSites, Long> {
    // Define custom methods if needed

    @Query("SELECT H from HistoricSites H where H.historicalPeriod LIKE %:historicalPeriod%")
    List<HistoricSites> findByPeriod(String historicalPeriod);

    @Query("SELECT H from HistoricSites H where H.location LIKE %:location%")
    List<HistoricSites> findByLocation(String location);

    @Query("SELECT H from HistoricSites H where H.location LIKE %:location% AND H.historicalPeriod LIKE %:historicalPeriod%")
    List<HistoricSites> findByLocationAndPeriod(String historicalPeriod, String location);

    @Query("SELECT H from HistoricSites H where H.name LIKE %:name%")
    List<HistoricSites> findByName(String name);
}
