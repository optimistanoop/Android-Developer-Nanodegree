package com.anoop.android.udacitypopularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.anoop.android.udacitypopularmovies.R;
import com.anoop.android.udacitypopularmovies.api.ApiConstants;
import com.anoop.android.udacitypopularmovies.models.Movie;
import com.anoop.android.udacitypopularmovies.models.Review;
import com.anoop.android.udacitypopularmovies.models.Video;
import com.squareup.picasso.Picasso;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import timber.log.Timber;

public class MovieDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Movie mMovie;
    private ArrayList<Video> mVideos;
    private ArrayList<Review> mReviews;
    private Context mContext;

    private static final int BASIC_INFO = 0;
    private static final int REVIEW = 1;
    private static final int VIDEO = 2;

    public MovieDetailAdapter(Context context)
    {
        Timber.tag(MovieDetailAdapter.class.getSimpleName());
        mContext = context;
    }

    public void setVideos(ArrayList<Video> videos)
    {
        mVideos = videos;
        notifyDataSetChanged();
    }

    public void setReviews(ArrayList<Review> reviews)
    {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    public void setMovie(Movie movie)
    {
        mMovie = movie;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType)
        {
            case BASIC_INFO:
                View basicView = inflater.inflate(R.layout.movie_detail, parent, false);
                viewHolder = new BasicHolder(basicView);
                break;
            case VIDEO:
                View videoView = inflater.inflate(R.layout.video, parent, false);
                viewHolder = new VideoHolder(videoView);
                break;
            case REVIEW:
                View reviewHold = inflater.inflate(R.layout.review, parent, false);
                viewHolder = new ReviewHolder(reviewHold);
                break;
            default:
                throw new InvalidParameterException("Invalid view type: " + viewType);
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {

        int size = 1;
        if(position == 0)
        {
            return BASIC_INFO;
        }

        if(mVideos != null)
        {
            size += mVideos.size();
            if(position < size)
            {
                return VIDEO;
            }
        }

        if(mReviews != null)
        {
            size += mReviews.size();
            if(position < size)
            {
                return REVIEW;
            }
        }

        return -1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType())
        {
            case BASIC_INFO:
                bindBasicInformation((BasicHolder) holder);
                break;

            case VIDEO:
                bindVideo((VideoHolder) holder, position);
                break;

            case REVIEW:
                bindReview((ReviewHolder) holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(mMovie != null)
        {
            count += 1;
        }
        if(mVideos != null)
        {
            count += mVideos.size();
        }
        if(mReviews != null)
        {
            count += mReviews.size();
        }
        return count;
    }

    private void bindReview(ReviewHolder holder, int position)
    {
        position -= 1;
        if(mVideos != null)
        {
            position -= mVideos.size();
        }
        Review review = mReviews.get(position);
        holder.content.setText(review.getContent());
        holder.author.setText(review.getAuthor());
    }

    private void bindVideo(VideoHolder holder, int position)
    {
        Video video = mVideos.get(position - 1);
        holder.videoType.setText(video.getType());
        holder.videoName.setText(video.getName());
    }

    private void bindBasicInformation(BasicHolder holder)
    {
        if(mMovie == null)
        {
            return;
        }
        holder.overview.setText(mMovie.getOverview());

        String path = mMovie.getPosterPath();
        if(path != null && !path.isEmpty())
        {
            Picasso.with(mContext)
                    .load(ApiConstants.POSTER_BASE_URL + path)
                    .into(holder.movieHeader) ; //, new ImageLoadedCallback());
        }


        holder.rating.setText(mMovie.getVoteAverage() + "/10");


        Date releaseDate = mMovie.getReleaseDateClass();

        String dateText = "Unknown";
        if(releaseDate != null)
        {
            dateText = SimpleDateFormat.getDateInstance().format(releaseDate);
        }
        holder.releaseDate.setText(dateText);
    }

    public class BasicHolder extends RecyclerView.ViewHolder
    {
        public final ImageView movieHeader;
        public final TextView releaseDate;
        public final TextView rating;
        public final TextView overview;

        public BasicHolder(View view)
        {
            super(view);
            movieHeader = (ImageView) view.findViewById(R.id.movieHeader);
            releaseDate = (TextView) view.findViewById(R.id.releaseDate);
            rating = (TextView) view.findViewById(R.id.rating);
            overview = (TextView) view.findViewById(R.id.overview);
        }
    }

    public class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public final TextView videoName;
        public final TextView videoType;

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition() - 1;
            Video video = mVideos.get(position);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(video.getVideoURI());
            mContext.startActivity(intent);
        }

        public VideoHolder(View view)
        {
            super(view);
            videoName = (TextView) view.findViewById(R.id.videoName);
            videoType = (TextView) view.findViewById(R.id.videoType);
            view.setOnClickListener(this);
        }
    }

    public class ReviewHolder extends RecyclerView.ViewHolder
    {
        public final TextView author;
        public final TextView content;

        public ReviewHolder(View view)
        {
            super(view);
            author = (TextView) view.findViewById(R.id.tvReviewerName);
            content = (TextView) view.findViewById(R.id.tvReviewContent);
        }
    }
}
