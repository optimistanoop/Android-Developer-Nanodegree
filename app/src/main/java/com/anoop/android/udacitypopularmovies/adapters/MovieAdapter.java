package com.anoop.android.udacitypopularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.anoop.android.udacitypopularmovies.R;
import com.anoop.android.udacitypopularmovies.api.ApiConstants;
import com.anoop.android.udacitypopularmovies.landingScreen.MovieGridFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends CursorAdapter {

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.titleView.setVisibility(View.VISIBLE);
        viewHolder.titleView.setText(cursor.getString(MovieGridFragment.COL_TITLE));
        String posterPath = cursor.getString(MovieGridFragment.COL_POSTER_PATH);
        if(posterPath != null && !posterPath.isEmpty())
        {
            Picasso.with(context)
                    .load(ApiConstants.POSTER_BASE_URL + posterPath)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.posterView, viewHolder);
        }
    }

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder implements Callback {
        public final ImageView posterView;
        public final TextView titleView;

        @Override
        public void onSuccess() {
            titleView.setVisibility(View.GONE);
        }

        @Override
        public void onError() {

        }

        public ViewHolder(View view)
        {
            posterView = (ImageView) view.findViewById(R.id.moviePoster);
            titleView = (TextView) view.findViewById(R.id.placeholderTittle);
        }
    }

}
