package com.isep.tourists;

import com.isep.tourists.Repository.HSRepository;
import com.isep.tourists.model.HistoricSites;
import com.isep.tourists.service.HSService;
import com.isep.tourists.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class HSServiceTest {

    @MockBean
    private HSRepository hsRepository;

    @MockBean
    private ResourceLoader resourceLoader;

    @MockBean
    private TourService tourService;

    @Autowired
    private HSService hsService;

    @BeforeEach
    public void setup() {
        HistoricSites site1 = new HistoricSites();
        site1.setId(1L);
        site1.setName("Site 1");
        site1.setLocation("Location 1");

        HistoricSites site2 = new HistoricSites();
        site2.setId(2L);
        site2.setName("Site 2");
        site2.setLocation("Location 2");

        List<HistoricSites> sites = Arrays.asList(site1, site2);
        Page<HistoricSites> page = new PageImpl<>(sites);
        when(hsRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);
    }

    @Test
    public void testGetAllHistoricSites() {
        Page<HistoricSites> result = hsService.getAllHistoricSites(0, 10);
        List<HistoricSites> sites = result.getContent();

        assertEquals(2, sites.size());
        assertEquals("Site 1", sites.get(0).getName());
        assertEquals("Location 1", sites.get(0).getLocation());
        assertEquals("Site 2", sites.get(1).getName());
        assertEquals("Location 2", sites.get(1).getLocation());
    }

}
