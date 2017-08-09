package com.rezkyaulia.android.popular_movie.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.rezkyaulia.android.popular_movie.R;
import com.rezkyaulia.android.popular_movie.activity.DetailActivity;
import com.rezkyaulia.android.popular_movie.database.DbHelper;
import com.rezkyaulia.android.popular_movie.databinding.ListDetailContainerMainBinding;
import com.rezkyaulia.android.popular_movie.databinding.ListDetailContainerReviewBinding;
import com.rezkyaulia.android.popular_movie.databinding.ListDetailContainerTrailerBinding;
import com.rezkyaulia.android.popular_movie.model.ApiReviewResponse;
import com.rezkyaulia.android.popular_movie.model.ApiTrailerResponse;
import com.rezkyaulia.android.popular_movie.model.DetailAbstract;
import com.rezkyaulia.android.popular_movie.model.Movie;
import com.rezkyaulia.android.popular_movie.model.Review;
import com.rezkyaulia.android.popular_movie.model.Trailer;
import com.rezkyaulia.android.popular_movie.util.ApiClient;
import com.rezkyaulia.android.popular_movie.util.Common;
import com.rezkyaulia.android.popular_movie.util.Constant;
import com.rezkyaulia.android.popular_movie.util.ImageSize;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Rezky Aulia Pratama on 8/6/2017.
 */

public class DetailMovieRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    List<DetailAbstract> mItems;
    TrailerRecyclerviewAdapter.OnRecyclerViewInteraction mListener;

    private int animationCount = 0;
    private int lastPosition = -1;

    public DetailMovieRecyclerviewAdapter(Context mContext, List<DetailAbstract> mItems,TrailerRecyclerviewAdapter.OnRecyclerViewInteraction listener) {
        this.mContext = mContext;
        this.mItems = mItems;
        this.mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constant.getInstance().TYPE_MAIN){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_detail_container_main, parent, false);
            return new MainViewHolder(view);
        }else if (viewType == Constant.getInstance().TYPE_SECONDARY){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_detail_container_trailer, parent, false);
            return new TrailerViewHolder(view);
        }else if (viewType == Constant.getInstance().TYPE_THIRD){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_detail_container_review, parent, false);
            return new ReviewViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MainViewHolder) {
            onMainBindViewHolder((MainViewHolder) holder, position);
        } else if(holder instanceof TrailerViewHolder){
            onTrailerBindViewHolder((TrailerViewHolder) holder, position);
        } else if(holder instanceof ReviewViewHolder){
            onReviewBindViewHolder((ReviewViewHolder) holder, position);
        }
    }

    private void onMainBindViewHolder(final MainViewHolder holder,final int position){
        Movie movie = mItems.get(position).getMovie();
        String year = String.valueOf(Common.getInstance().parseDate(movie.getReleaseDate()).get(Calendar.YEAR));

        holder.binding.textViewYear.setText(year);
        holder.binding.textViewRate.setText(String.valueOf(movie.getVoteAverage()).concat(mContext.getString(R.string.per_rated)));
        holder.binding.textViewOverview.setText(movie.getOverview());

        Cursor cursor = mContext.getContentResolver().query(
                DbHelper.getInstance(mContext).getMovieGenreContract().CONTENT_URI,
                null,
                String.valueOf(movie.getId()),
                null,
                null);

        if (cursor.moveToFirst())
            do {
                holder.binding.textViewGenre.setText("");
                holder.binding.textViewGenre.append(cursor.getString(cursor.getColumnIndex(DbHelper.getInstance(mContext).getGenreContract().NAME)).concat("; "));
            } while (cursor.moveToNext());
        cursor.close();

        Picasso.with(mContext)
                .load(ApiClient.getInstance().URL_IMAGE.concat(ImageSize.getInstance().MEDIUM).concat(movie.getPosterPath() != null ? movie.getPosterPath() : ""))
                .placeholder(R.drawable.ic_movie) //this is optional the image to display while the url image is downloading
                .error(R.drawable.ic_error_sing)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                .into(holder.binding.imagePoster);

        setAnimation(holder.binding.getRoot(), position);
//        holder.binding.executePendingBindings();   // update the view now
    }

    private void onTrailerBindViewHolder(final TrailerViewHolder holder, final int position){
        Movie movie = mItems.get(position).getMovie();
        holder.binding.viewTitle.textviewTitle.setText(mContext.getString(R.string.trailer));
        ApiClient.getInstance().getListTrailer(movie.getId(), new ApiClient.OnFetchDataListener<ApiTrailerResponse>() {
            @Override
            public void OnResponse(ApiTrailerResponse response) {
                List<Trailer> trailers = response.getResults();

                if (trailers.size() > 0){
                    if (mItems.get(position).isLandscape()){
                        holder.binding.recyclerViewTrailer.setLayoutManager(new GridLayoutManager(mContext,3));
                    }else{
                        holder.binding.recyclerViewTrailer.setLayoutManager(new GridLayoutManager(mContext,2));
                    }
                    holder.binding.recyclerViewTrailer.setAdapter(new TrailerRecyclerviewAdapter(mContext,trailers,mListener));
                }
            }

            @Override
            public void OnError(ANError error) {
                Timber.e("ERROR : "+error.getMessage());
            }
        });
        setAnimation(holder.binding.getRoot(), position);
//        holder.binding.executePendingBindings();   // update the view now
    }

    private void onReviewBindViewHolder(final ReviewViewHolder holder, final int position){
        Movie movie = mItems.get(position).getMovie();
        holder.binding.viewTitle.textviewTitle.setText(mContext.getString(R.string.review));
        ApiClient.getInstance().getListReview(movie.getId(), new ApiClient.OnFetchDataListener<ApiReviewResponse>() {
            @Override
            public void OnResponse(ApiReviewResponse response) {
                List<Review> reviews = response.getResults();

                if (reviews.size() > 0){
                    holder.binding.recyclerViewReview.setLayoutManager(new LinearLayoutManager(mContext));
                    holder.binding.recyclerViewReview.setAdapter(new ReviewRecyclerviewAdapter(mContext,reviews));
                }
            }

            @Override
            public void OnError(ANError error) {
                Timber.e("ERROR : "+error.getMessage());
            }
        });

        setAnimation(holder.binding.getRoot(), position);
//        holder.binding.executePendingBindings();   // update the view now
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(final View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            final Animation animation = AnimationUtils.loadAnimation(
                    viewToAnimate.getContext(), R.anim.zoom_in);
            animationCount++;
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animationCount--;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            animation.setDuration(300);
            viewToAnimate.setVisibility(View.GONE);
            viewToAnimate.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewToAnimate.setVisibility(View.VISIBLE);
                    viewToAnimate.startAnimation(animation);

                }
            }, animationCount * 100);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof MainViewHolder) {
            ((MainViewHolder) holder).binding.getRoot().setVisibility(View.VISIBLE);
            ((MainViewHolder) holder).binding.getRoot().clearAnimation();
        } else if (holder instanceof TrailerViewHolder) {
            ((TrailerViewHolder) holder).binding.getRoot().setVisibility(View.VISIBLE);
            ((TrailerViewHolder) holder).binding.getRoot().clearAnimation();
        }else if (holder instanceof ReviewViewHolder) {
            ((ReviewViewHolder) holder).binding.getRoot().setVisibility(View.VISIBLE);
            ((ReviewViewHolder) holder).binding.getRoot().clearAnimation();
        }
    }


    private class MainViewHolder extends RecyclerView.ViewHolder{
        private final ListDetailContainerMainBinding binding;
        public MainViewHolder(View itemView) {
            super(itemView);
            binding = ListDetailContainerMainBinding.bind(itemView);
        }
    }

    private class TrailerViewHolder extends RecyclerView.ViewHolder{
        private final ListDetailContainerTrailerBinding binding;
        public TrailerViewHolder(View itemView) {
            super(itemView);
            binding = ListDetailContainerTrailerBinding.bind(itemView);
        }
    }

    private class ReviewViewHolder extends RecyclerView.ViewHolder{
        private final ListDetailContainerReviewBinding binding;
        public ReviewViewHolder(View itemView) {
            super(itemView);
            binding = ListDetailContainerReviewBinding.bind(itemView);
        }
    }

}
