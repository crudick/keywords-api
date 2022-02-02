package com.david;

import com.david.results.SearchVolumeResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileHelper {

    public String workingDirectory;
    public String workbookName;
    public List<SearchVolumeResult> records;
    Workbook workbook;
    Sheet outputSheet;

    public FileHelper(){
        records = new ArrayList<>();
    }

    public void openWorkbook(String fileName){
        FileInputStream file = null;
        workbookName = fileName;
        try {
            file = new FileInputStream(new File(fileName));
            workbook = new XSSFWorkbook(file);

            outputSheet = workbook.getSheet("Output");
            if(outputSheet != null){
                int i = 0;
                for (Row row : outputSheet) {
                    if(i==0 || row.getCell(0) == null){
                        //Skip the header
                        i++;
                        continue;
                    }
                    var keyword = row.getCell(0).getStringCellValue();
                    var volume = row.getCell(1).getNumericCellValue();
                    if((keyword != null && keyword.length() > 0) ||
                            volume != 0){
                        var searchVolumeResult = new SearchVolumeResult();
                        searchVolumeResult.string = keyword;
                        searchVolumeResult.volume =  (long)volume;
                        searchVolumeResult.cpc = (float)row.getCell(2).getNumericCellValue();
                        searchVolumeResult.cmp = (float)row.getCell(3).getNumericCellValue();
                        searchVolumeResult.seedword = row.getCell(4).getStringCellValue();

                        records.add(searchVolumeResult);
                    }
                    i++;
                }
                workbook.removeSheetAt(1);
            }
            outputSheet = workbook.createSheet("Output");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, List<String>> readInputsFromExcel(){
        Map<Integer, List<String>> data = new HashMap<>();

            // Read data from first sheet of workbook
            Sheet sheet = workbook.getSheetAt(0);

            int i = 0;
            for (Row row : sheet) {
                var keyword = row.getCell(0).getStringCellValue();;
                if(keyword==null || keyword.length() == 0){
                    i++;
                    continue;
                }
                var status = "incomplete";
                if(row.getCell(1) != null){
                    status = row.getCell(1).getStringCellValue();
                }

                if(!keyword.equalsIgnoreCase("Seedwords") &&
                        !status.equalsIgnoreCase("completed")){
                    if(keyword != null && keyword.length() > 0){
                        data.put(i, new ArrayList<String>());
                        for (Cell cell : row) {
                            switch (cell.getCellType()) {
                                case STRING:
                                    data.get(i).add(cell.getRichStringCellValue().getString());
                                    break;
                                case NUMERIC:
                                    data.get(i).add(cell.getNumericCellValue() + "");
                                    break;
                                default: data.get(i).add(" ");
                            }
                        }
                    }
                }
                i++;
            }

        return data;
    }

    public void dedupeRecords(){
        var newList = new ArrayList<SearchVolumeResult>();
        records.forEach(record ->{
            var exists = false;
            for (int i = 0; i < newList.size(); i++) {
                if(newList.get(i).string.equalsIgnoreCase(record.string)){
                    exists = true;
                }
            }
            if(!exists){
                newList.add(record);
            }
        });
        records = newList;
    }

    public void writeRecordsToOutputSheet(){

        Row header = outputSheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setBold(true);
        headerStyle.setFont(font);


        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Keyword");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("SearchVolume");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("CPC (USD)");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Competition");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Query Source");
        headerCell.setCellStyle(headerStyle);

        var i = 1;

        for (SearchVolumeResult record : records) {
            var row = outputSheet.createRow(i);

            var cell = row.createCell(0);
            cell.setCellValue(record.string);

            cell = row.createCell(1);
            cell.setCellValue(record.volume);

            cell = row.createCell(2);
            cell.setCellValue(record.cpc);

            cell = row.createCell(3);
            cell.setCellValue(record.cmp);

            cell = row.createCell(4);
            cell.setCellValue(record.seedword);

            i++;
        }

    }

    public void updateInputStatus(int row, String status){
        Sheet sheet = workbook.getSheetAt(0);
        var sheetRow = sheet.getRow(row);
        Cell cell = sheetRow.createCell(1);
        cell.setCellValue(status);
    }

    public void closeWorkbook(){
        try {

            LocalDateTime dateObj = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String datetime = dateObj.format(formatter);

            workbookName = workbookName.substring(0,workbookName.length()-5) +"_"+datetime+".xlsx";
            FileOutputStream outputStream = new FileOutputStream(workbookName);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void buildCSV(String fileName){
        List<String[]> dataLines = new ArrayList<>();
        dataLines.add(new String[]{ "Keyword", "Volume", "cpc", "Competition" });

        records.forEach(record -> {
            dataLines.add(new String[]{record.string,
                    String.valueOf(record.volume),
                    String.valueOf(record.cpc),
                    String.valueOf(record.cmp)});
        });
        File csvOutputFile = new File(fileName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
