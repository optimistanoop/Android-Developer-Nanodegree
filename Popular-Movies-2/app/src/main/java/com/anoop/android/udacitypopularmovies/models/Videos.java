
package com.anoop.android.udacitypopularmovies.models;

import com.google.gson.annotations.Expose;
import org.parceler.Parcel;
import java.util.ArrayList;
import java.util.List;

@Parcel
public class Videos {

    @Expose
    private List<Video> results = new ArrayList<Video>();

    public Videos()
    {

    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }

}
