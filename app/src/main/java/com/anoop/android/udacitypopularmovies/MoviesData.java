package com.anoop.android.udacitypopularmovies;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anoop on 12/3/16.
 */
public class MoviesData {

    private Integer page;
    private Integer totalPages;
    private Integer totalResults;
    private List<Movie> results = new ArrayList<Movie>();

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
