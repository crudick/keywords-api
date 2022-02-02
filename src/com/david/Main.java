package com.david;

import com.david.constants.ServiceEndpoints;
import com.david.results.SearchVolumeResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        String CSV_FILE_NAME = "";
        var i = 0;
        while(i< args.length) {
            if(args[i].equalsIgnoreCase("-f")){
                i++;
                CSV_FILE_NAME = args[i];
            }
            i++;
        }
        var fh = new FileHelper();
        fh.openWorkbook(CSV_FILE_NAME);
        var service = new KeywordLookupService();
        service.initialize("e6176241edd47ca42ee206250507325ff5aec829");
        var data = fh.readInputsFromExcel();
        for (Map.Entry<Integer,List<String>> entry : data.entrySet()){
            String keyword = entry.getValue().get(0);
            var keywordSuggestionsResult = service.getKeywordSuggestions(keyword, ServiceEndpoints.GoogleSuggestions);

            if(keywordSuggestionsResult.results != null){
                for(Map.Entry<String,List<SearchVolumeResult>> searchResult: keywordSuggestionsResult.results.entrySet()){
                    var newRecords = searchResult.getValue();
                    newRecords.forEach(newRecord->{
                        newRecord.seedword = searchResult.getKey();
                    });
                    fh.records.addAll(newRecords);
                }
            }
            fh.updateInputStatus(entry.getKey(),"complete");

        }
        fh.dedupeRecords();
        fh.writeRecordsToOutputSheet();
        fh.closeWorkbook();
    }

    public static void getKeyWordSuggestions(){
        var fh = new FileHelper();

        var service = new KeywordLookupService();
        service.initialize("e6176241edd47ca42ee206250507325ff5aec829");
        String keywords = "bsci audit";
        String CSV_FILE_NAME = "C:\\Users\\coler\\Downloads\\keywords.csv";

        //var data = new Map<String, List<SearchVolumeResult>>();
        var keywordSuggestionsResult = service.getKeywordSuggestions(keywords, ServiceEndpoints.GoogleSuggestions);

        var records = keywordSuggestionsResult.results.get("");

        fh.records.addAll(records);
        fh.buildCSV(CSV_FILE_NAME);
    }

}
