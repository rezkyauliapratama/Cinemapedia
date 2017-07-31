package com.rezkyaulia.android.popular_movie.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rezky Aulia Pratama on 7/30/2017.
 */

public class ApiTrailerResponse {
    @SerializedName("results")
    private List<Trailer> results;

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }
}
