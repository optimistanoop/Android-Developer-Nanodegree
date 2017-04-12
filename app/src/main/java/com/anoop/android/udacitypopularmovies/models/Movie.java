
package com.anoop.android.udacitypopularmovies.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.format.DateFormat;
import com.anoop.android.udacitypopularmovies.db.MovieContract;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Parcel
public class Movie {

    @Expose
    private Boolean adult;
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    @SerializedName("genre_ids")
    @Expose
    private List<Integer> genreIds = new ArrayList<Integer>();
    @Expose
    private Integer id;
    @SerializedName("original_language")
    @Expose
    private String originalLanguage;
    @SerializedName("original_title")
    @Expose
    private String originalTitle;
    @Expose
    private String overview;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @Expose
    private Double popularity;
    @Expose
    private String title;
    @Expose
    private Boolean video;
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;
    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;
    @Expose
    private Reviews reviews;
    @Expose
    private Videos videos;

    public Movie() {

    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Date getReleaseDateClass()
    {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        try{
            return format.parse(releaseDate);
        } catch (ParseException e)
        {
            return null;
        }
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * 
     * @return
     *     The posterPath
     */
    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
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

    public Reviews getReviews() {
        return reviews;
    }

    public void setReviews(Reviews reviews) {
        this.reviews = reviews;
    }

    public Videos getVideos() {
        return videos;
    }

    public void setVideos(Videos videos) {
        this.videos = videos;
    }

    public ContentValues getContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, this.getId());
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, this.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_ADULT, (this.getAdult() ? 1:0));
        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, this.getBackdropPath());
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, this.getOriginalLanguage());
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, this.getOriginalTitle());
        Date releaseDate = getReleaseDateClass();
        if(releaseDate != null)
        {
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate.getTime());
        }
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, this.getPosterPath());
        values.put(MovieContract.MovieEntry.COLUMN_POPULARITY, this.getPopularity());
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, this.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_VIDEO, (this.getVideo() ? 1:0));
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, this.getVoteAverage());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, this.getVoteCount());

        return values;
    }

    public static final String[] ALL_COLUMN_PROJECTION = {
        MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_ADULT,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_VIDEO,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT
    };

    private static final int COL_ID = 0;
    private static final int COL_MOVIE_ID = 1;
    private static final int COL_OVERVIEW = 2;
    private static final int COL_ADULT = 3;
    private static final int COL_BACKDROP_PATH = 4;
    private static final int COL_ORIGINAL_LANGUAGE = 5;
    private static final int COL_ORIGINAL_TITLE = 6;
    private static final int COL_RELEASE_DATE = 7;
    private static final int COL_POSTER_PATH = 8;
    private static final int COL_POPULARITY = 9;
    private static final int COL_TITLE = 10;
    private static final int COL_VIDEO = 11;
    private static final int COL_VOTE_AVERAGE = 12;
    private static final int COL_VOTE_COUNT = 13;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public Movie(Cursor cursor)
    {
        id = cursor.getInt(COL_MOVIE_ID);
        overview = cursor.getString(COL_OVERVIEW);
        adult = (cursor.getInt(COL_ADULT) == 1);
        backdropPath = cursor.getString(COL_BACKDROP_PATH);
        originalLanguage = cursor.getString(COL_ORIGINAL_LANGUAGE);
        originalTitle = cursor.getString(COL_ORIGINAL_TITLE);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(cursor.getLong(COL_RELEASE_DATE));
        releaseDate = DateFormat.format(DATE_FORMAT, cal).toString();
        posterPath = cursor.getString(COL_POSTER_PATH);
        popularity = cursor.getDouble(COL_POPULARITY);
        title = cursor.getString(COL_TITLE);
        video = (cursor.getInt(COL_VIDEO) == 1);
        voteAverage = cursor.getDouble(COL_VOTE_AVERAGE);
        voteCount = cursor.getInt(COL_VOTE_COUNT);
    }

}
