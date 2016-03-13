package com.anoop.android.udacitypopularmovies;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by anoop on 12/3/16.
 */
public class Movie {

    private Boolean isAdult;
    private List<Integer> genreIds = new ArrayList<Integer>();
    private String id;
    private String language;
    private String overview;
    private Date releaseDate;
    private String posterLink;
    private Double popularity;
    private String title;
    private Boolean video;
    private Double voteAverage;
    private int voteCount;

    public Movie(JSONObject movie)
            throws JSONException {
        setData(movie);
    }

    private void setData(JSONObject movieJSON)
            throws JSONException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        try {
            releaseDate = dateFormat.parse(movieJSON.getString(Constants.RELEASE_DATE));
        } catch (ParseException e) {
            releaseDate = null;
        }

        overview = movieJSON.getString(Constants.OVERVIEW);
        id = movieJSON.getString(Constants.ID);
        posterLink = movieJSON.getString(Constants.POSTER_LINK);
        popularity = movieJSON.getDouble(Constants.POPULARITY);
        title = movieJSON.getString(Constants.TITLE);
        voteAverage = movieJSON.getDouble(Constants.VOTE_AVERAGE);
        voteCount = movieJSON.getInt(Constants.VOTE_COUNT);
        isAdult = movieJSON.getBoolean(Constants.ADULT);

    }

    public Boolean getIsAdult() {
        return isAdult;
    }

    public void setIsAdult(Boolean isAdult) {
        this.isAdult = isAdult;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getPosterLink() {
        return posterLink;
    }

    public void setPosterLink(String posterLink) {
        this.posterLink = posterLink;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }
}

