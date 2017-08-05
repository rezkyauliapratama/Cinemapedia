package com.rezkyaulia.android.popular_movie.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.rezkyaulia.android.popular_movie.R;
import com.rezkyaulia.android.popular_movie.activity.DetailActivity;
import com.rezkyaulia.android.popular_movie.activity.MainActivity;
import com.rezkyaulia.android.popular_movie.data.DetailModel;
import com.rezkyaulia.android.popular_movie.database.DbHelper;
import com.rezkyaulia.android.popular_movie.databinding.ContainerDetailItemMainBinding;
import com.rezkyaulia.android.popular_movie.databinding.ContainerDetailItemReviewBinding;
import com.rezkyaulia.android.popular_movie.databinding.ContainerDetailItemTrailerBinding;
import com.rezkyaulia.android.popular_movie.model.ApiReviewResponse;
import com.rezkyaulia.android.popular_movie.model.ApiTrailerResponse;
import com.rezkyaulia.android.popular_movie.model.Movie;
import com.rezkyaulia.android.popular_movie.model.Review;
import com.rezkyaulia.android.popular_movie.model.Trailer;
import com.rezkyaulia.android.popular_movie.util.ApiClient;
import com.rezkyaulia.android.popular_movie.util.Common;
import com.rezkyaulia.android.popular_movie.util.Constant;
import com.rezkyaulia.android.popular_movie.util.ImageSize;
import com.squareup.picasso.Picasso;
import com.rezkyaulia.android.popular_movie.adapter.TrailerRecyclerviewAdapter.OnRecyclerViewInteraction;

import java.util.Calendar;
import java.util.List;

import rx.Completable;
import timber.log.Timber;

/**
 * Created by Rezky Aulia Pratama on 8/5/2017.
 */

public class DetailMovieRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<Object> mItems;
    private OnRecyclerViewInteraction mListener;

    public DetailMovieRecyclerviewAdapter(Context mContext, List<Object> mItems, OnRecyclerViewInteraction listener) {
        this.mContext = mContext;
        this.mItems = mItems;
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constant.getInstance().TYPE_MAIN) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.container_detail_item_main, parent, false);
            return new MainViewHolder(view);
        }else if (viewType == Constant.getInstance().TYPE_REVIEW) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.container_detail_item_review, parent, false);
            return new ReviewViewHolder(view);
        }else if (viewType == Constant.getInstance().TYPE_TRAILER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.container_detail_item_trailer, parent, false);
            return new TrailerViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MainViewHolder) {
            onMainBindViewHolder((MainViewHolder) holder, position);
        } else if (holder instanceof TrailerViewHolder) {
            onTrailerBindViewHolder((TrailerViewHolder) holder, position);
        }else if (holder instanceof ReviewViewHolder) {
            onReviewBindViewHolder((ReviewViewHolder) holder, position);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof Movie) {
            return Constant.getInstance().TYPE_MAIN;
        } else {
            if (mItems.get(position) instanceof  DetailModel){
                if (((DetailModel)mItems.get(position)).getType() == Constant.getInstance().TYPE_REVIEW){
                    return Constant.getInstance().TYPE_REVIEW;
                }else if (((DetailModel)mItems.get(position)).getType() == Constant.getInstance().TYPE_TRAILER){
                    return Constant.getInstance().TYPE_TRAILER;
                }
            };
        }
        return 0;
    }

    private void onMainBindViewHolder(final MainViewHolder holder, final int position) {

        Movie item = (Movie) mItems.get(position);
        String year = String.valueOf(Common.getInstance().parseDate(item.getReleaseDate()).get(Calendar.YEAR));
        holder.binding.textViewYear.setText(year);
        holder.binding.textViewRate.setText(String.valueOf(item.getVoteAverage()).concat(mContext.getString(R.string.per_rated)));
        holder.binding.textViewOverview.setText(item.getOverview());

        Picasso.with(mContext)
                .load(ApiClient.getInstance().URL_IMAGE.concat(ImageSize.getInstance().MEDIUM).concat(item.getPosterPath()))
                .placeholder(R.drawable.ic_movie) //this is optional the image to display while the url image is downloading
                .error(R.drawable.ic_error_sing)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                .into(holder.binding.imagePoster);

        Cursor cursor = mContext.getContentResolver().query(
                DbHelper.getInstance(mContext).getMovieGenreContract().CONTENT_URI,
                null,
                String.valueOf(item.getId()),
                null,
                null);

        if (cursor.moveToFirst())
            do {
                holder.binding.textViewGenre.append(cursor.getString(cursor.getColumnIndex(DbHelper.getInstance(mContext).getGenreContract().NAME)).concat("; "));
            } while (cursor.moveToNext());
        cursor.close();


    }


    private void onTrailerBindViewHolder(final TrailerViewHolder holder, final int position){
        DetailModel item = (DetailModel) mItems.get(position);
        holder.binding.layoutTitle.textviewTitle.setText(mContext.getString(R.string.trailer));
        ApiClient.getInstance().getListTrailer(item.getId(), new ApiClient.OnFetchDataListener<ApiTrailerResponse>() {
            @Override
            public void OnResponse(ApiTrailerResponse response) {
                Timber.e(new Gson().toJson(response));
                List<Trailer> trailers = response.getResults();

                if (trailers.size() > 0){
                    holder.binding.recyclerViewTrailer.setLayoutManager(new GridLayoutManager(mContext,2));
                    holder.binding.recyclerViewTrailer.setAdapter(new TrailerRecyclerviewAdapter(mContext,trailers,mListener));
                }
            }

            @Override
            public void OnError(ANError error) {
                Timber.e("ERROR : "+error.getMessage());
            }
        });
    }

    private void onReviewBindViewHolder(final ReviewViewHolder holder, final int position){
        DetailModel item = (DetailModel) mItems.get(position);
        holder.binding.layoutTitle.textviewTitle.setText(mContext.getString(R.string.review));

        ApiClient.getInstance().getListReview(item.getId(), new ApiClient.OnFetchDataListener<ApiReviewResponse>() {
            @Override
            public void OnResponse(ApiReviewResponse response) {
                Timber.e(new Gson().toJson(response));
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
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    private class MainViewHolder extends RecyclerView.ViewHolder{
        private final ContainerDetailItemMainBinding binding;
        public MainViewHolder(View itemView) {
            super(itemView);
            binding = ContainerDetailItemMainBinding.bind(itemView);
        }
    }


    private class TrailerViewHolder extends  RecyclerView.ViewHolder{
        private final ContainerDetailItemTrailerBinding binding;
        public TrailerViewHolder(View itemView) {
            super(itemView);
            binding = ContainerDetailItemTrailerBinding.bind(itemView);
        }
    }

    private class ReviewViewHolder extends RecyclerView.ViewHolder{
        private final ContainerDetailItemReviewBinding binding;
        public ReviewViewHolder(View itemView) {
            super(itemView);
            binding = ContainerDetailItemReviewBinding.bind(itemView);
        }
    }


}
