package com.david.requests;

import java.util.ArrayList;
import java.util.List;

public class SearchVolumeRequest {
    public String apikey;
    public List<String> keyword;
    public List<String> metrics_location;
    public List<String> metrics_language;
    public String metrics_network = "googlesearchnetwork";
    public String metrics_currency = "USD";
    public String output = "json";

    public SearchVolumeRequest(){
        metrics_location = new ArrayList<String>();
        metrics_language = new ArrayList<String>();
        keyword = new ArrayList<String>();
    }
}
