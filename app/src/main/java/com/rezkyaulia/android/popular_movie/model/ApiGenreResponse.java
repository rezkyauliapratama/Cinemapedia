package com.rezkyaulia.android.popular_movie.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rezky Aulia Pratama on 7/1/2017.
 */

public class ApiGenreResponse {
    @SerializedName("genres")
    private List<Genre> results;

    public List<Genre> getResults() {
        return results;
    }

    public void setResults(List<Genre> results) {
        this.results = results;
    }
}
