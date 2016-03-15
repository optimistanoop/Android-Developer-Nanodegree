package com.anoop.android.udacitypopularmovies;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Movie  implements Parcelable{

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
        return Constants.POSTER_BASE_LINK+posterLink;
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

    private Movie(Parcel source)
    {
        isAdult = source.readByte() == 1;
        id = source.readString();
        overview = source.readString();
        releaseDate = (Date) source.readSerializable();
        posterLink = source.readString();
        popularity = source.readDouble();
        title = source.readString();
        voteAverage = source.readDouble();
        voteCount = source.readInt();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>()
    {
        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isAdult ? 1:0));
        dest.writeString(id);
        dest.writeString(overview);
        dest.writeSerializable(releaseDate);
        dest.writeString(posterLink);
        dest.writeDouble(popularity);
        dest.writeString(title);
        dest.writeDouble(voteAverage);
        dest.writeInt(voteCount);
    }
}

