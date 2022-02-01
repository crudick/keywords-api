package com.david;

import com.david.constants.ServiceEndpoints;
import com.david.requests.SearchVolumeRequest;
import com.david.requests.SuggestedKeywordsRequest;
import com.david.results.KeywordResults;
import com.david.results.SearchVolumeResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeywordLookupService {

    private HttpClient httpClient;
    private String apiKey;

    public void initialize(String apiKey){
        httpClient = HttpClient.newHttpClient();
        this.apiKey = apiKey;
    }

    public void callSearchVolumeEndpoint(String keyword, String endpoint){

        var searchVolumeRequest = new SearchVolumeRequest();
        searchVolumeRequest.apikey = apiKey;
        searchVolumeRequest.keyword.addAll(List.of(keyword.split(",")));
        searchVolumeRequest.metrics_language.add("en");
        searchVolumeRequest.metrics_location.add("2840");

        Gson gson = new Gson();
        String jsonStr = gson.toJson(searchVolumeRequest);

        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonStr))
                .build();

        // use the client to send the request
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public KeywordResults getKeywordSuggestions(String keyword, String endpoint){

        var keywordRequest = new SuggestedKeywordsRequest();
        keywordRequest.apikey = apiKey;
        keywordRequest.keyword = keyword;
        keywordRequest.metrics_language.add("en");
        keywordRequest.metrics_location.add("2840");

        Gson gson = new Gson();
        String jsonStr = gson.toJson(keywordRequest);

        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonStr))
                .build();

        // use the client to send the request
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            KeywordResults results = new Gson().fromJson(response.body(), new TypeToken<KeywordResults>() {}.getType());
            return results;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
