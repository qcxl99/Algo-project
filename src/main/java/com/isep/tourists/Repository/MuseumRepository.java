package com.isep.tourists.Repository;

import com.isep.tourists.model.Museum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MuseumRepository extends JpaRepository<Museum, Long> {

    @Query("SELECT m from Museum m where m.location LIKE %:location%")
    List<Museum> findByLocation(String location);

    @Query("SELECT m from Museum m where m.name LIKE %:name%")
    List<Museum> findByName(String name);


}
