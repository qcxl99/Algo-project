package com.isep.tourists.service;

import com.isep.tourists.Repository.MuseumRepository;
import com.isep.tourists.model.Museum;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@AllArgsConstructor
@Service
public class MuseumService implements ApplicationRunner {

    private final MuseumRepository museumRepository;
    private final ResourceLoader resourceLoader;
    private final TourService tourService;


    public Page<Museum> getAllMuseums(Pageable pageable) {
        return museumRepository.findAll(pageable);
    }
    public Museum getMuseumById(Long id) {
        return museumRepository.findById(id).orElse(null);
    }

    public Museum createMuseum(Museum museum) {
        return museumRepository.save(museum);
    }

    public void updateMuseum(Museum museum) {
        museumRepository.save(museum);
    }

    public void deleteMuseum(Long id) {
        museumRepository.deleteById(id);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
/*        // Read the data from a file or any other source
        List<Museum> museumDataList = readMuseumDataFromFile("museums.xlsx");
        museumRepository.saveAll(museumDataList);*/
    }
    private List<Museum> readMuseumDataFromFile(String filename) {
        List<Museum> museumDataList = new ArrayList<>();

        try {
            // Load the file as a resource using the resource loader
            Resource resource = resourceLoader.getResource("classpath:static/" + filename);

            // Create the FileInputStream from the resource
            FileInputStream fileInputStream = new FileInputStream(resource.getFile());

            // Create the XSSFWorkbook to read the XLSX file
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

            // Get the first sheet of the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            // Iterate over the rows of the sheet
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                Museum museumData = new Museum();

                if(row.getCell(4) != null){
                    museumData.setName(row.getCell(4).getStringCellValue());
                }

                if(row.getCell(9) !=null){
                    museumData.setDescription(row.getCell(9).getStringCellValue());
                }
                if (row.getCell(1) != null && row.getCell(2) != null &&row.getCell(7) != null) {
                    String location = "";
                    if (row.getCell(1).getCellType() == CellType.STRING) {
                        location += row.getCell(1).getStringCellValue();
                    }
                    if (row.getCell(2).getCellType() == CellType.STRING) {
                        location += ", " + row.getCell(2).getStringCellValue();
                    }
                    if (row.getCell(7).getCellType() == CellType.NUMERIC) {
                        location += ", " + row.getCell(7).getNumericCellValue();
                    }
                    else if(row.getCell(7).getCellType() == CellType.STRING){
                        location += ", " +  row.getCell(7).getStringCellValue();
                    }
                    museumData.setLocation(location);
                }

                museumData.setOpeningHours("9:30");
                museumData.setAdmissionFee(BigDecimal.valueOf(new Random().nextDouble() * 30));

                if (row.getCell(10) != null) {
                    if (row.getCell(10).getCellType() == CellType.NUMERIC) {
                        museumData.setLatitude(row.getCell(10).getNumericCellValue());
                    } else if(row.getCell(10).getCellType() == CellType.STRING) {
                        museumData.setLatitude(Double.parseDouble(row.getCell(10).getStringCellValue()));
                    }
                }

                if (row.getCell(11) != null) {
                    if(row.getCell(11).getCellType() == CellType.NUMERIC){
                        museumData.setLongitude(row.getCell(11).getNumericCellValue());
                    }
                    else if(row.getCell(11).getCellType() == CellType.STRING){
                        museumData.setLongitude(Double.parseDouble(row.getCell(11).getStringCellValue()));
                    }

                }

                if (row.getCell(5) != null) {
                    museumData.setAddress(row.getCell(5).getStringCellValue());
                }

                museumDataList.add(museumData);
            }

            // Close the workbook and file input stream
            workbook.close();
            fileInputStream.close();
        } catch (IOException e) {
            // Handle the exception
        }
        return museumDataList;
    }
}
