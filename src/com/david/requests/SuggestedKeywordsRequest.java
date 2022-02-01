package com.david.requests;

import java.util.ArrayList;
import java.util.List;

public class SuggestedKeywordsRequest {
    public String apikey;
    public String keyword;
    public String category = "web";
    public String country = "us";
    public String language = "en";
    public String type = "suggestions";
    public boolean metrics = true;
    public List<String> metrics_location;
    public List<String> metrics_language;
    public String metrics_network = "googlesearchnetwork";
    public String metrics_currency = "USD";
    public String output = "json";

    public SuggestedKeywordsRequest(){
        metrics_location = new ArrayList<String>();
        metrics_language = new ArrayList<String>();
    }
}
