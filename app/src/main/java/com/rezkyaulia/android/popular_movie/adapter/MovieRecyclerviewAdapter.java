package com.rezkyaulia.android.popular_movie.adapter;

import android.content.Context;
import android.support.v7.view.menu.ListMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;

import com.androidnetworking.error.ANError;
import com.rezkyaulia.android.popular_movie.R;
import com.rezkyaulia.android.popular_movie.databinding.ItemLoadingBinding;
import com.rezkyaulia.android.popular_movie.databinding.ItemMovieBinding;
import com.rezkyaulia.android.popular_movie.databinding.ItemMovieSmallBinding;
import com.rezkyaulia.android.popular_movie.databinding.ListRecyclerviewHorizontalBinding;
import com.rezkyaulia.android.popular_movie.fragment.MovieFragment;
import com.rezkyaulia.android.popular_movie.model.ApiMovieResponse;
import com.rezkyaulia.android.popular_movie.model.Movie;
import com.rezkyaulia.android.popular_movie.model.MovieAbstract;
import com.rezkyaulia.android.popular_movie.util.ApiClient;
import com.rezkyaulia.android.popular_movie.util.Common;
import com.rezkyaulia.android.popular_movie.util.Constant;
import com.rezkyaulia.android.popular_movie.util.ImageSize;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Rezky Aulia Pratama on 7/1/2017.
 */

public class MovieRecyclerviewAdapter extends BaseAdapter{
    private Context mContext;
    private List<MovieAbstract> mItems;
    private MovieFragment.OnRecyclerViewInteraction mListener;


    private int animationCount = 0;
    private int lastPosition = -1;

    public MovieRecyclerviewAdapter(Context context, List<MovieAbstract> items, MovieFragment.OnRecyclerViewInteraction listener ) {
        mContext = context;
        mItems = items;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constant.getInstance().TYPE_MAIN) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_movie, parent, false);
            return new MainViewHolder(view);
        } else if (viewType == Constant.getInstance().TYPE_SECONDARY){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_recyclerview_horizontal, parent, false);
            return new HorizontalViewHolder(view);
        }else if (viewType == Constant.getInstance().TYPE_NULL){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position)!=null){
            if (mItems.get(position) instanceof MovieAbstract){
                if (mItems.get(position).getType() == Constant.getInstance().TYPE_MAIN){
                    return Constant.getInstance().TYPE_MAIN;
                }else if (mItems.get(position).getType() == Constant.getInstance().TYPE_SECONDARY){
                    return Constant.getInstance().TYPE_SECONDARY;
                }
            }

        }else{
            return  Constant.getInstance().TYPE_NULL;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MainViewHolder) {
            onMainBindViewHolder((MainViewHolder) holder, position);
        } else if (holder instanceof HorizontalViewHolder) {
            onSecondaryBindViewHolder((HorizontalViewHolder) holder, position);
        }else if (holder instanceof LoadingViewHolder) {
            onNullBindViewHolder((LoadingViewHolder)holder,position);
        }
    }

    public void onMainBindViewHolder(final MainViewHolder holder, final int position) {
        final Movie mItem = mItems.get(position).getMovie();

        holder.binding.textviewTitle.setText(mItem.getTitle());
        holder.binding.textViewPoint.setText(String.valueOf(mItem.getVoteAverage()));

        String year = String.valueOf(Common.getInstance().parseDate(mItem.getReleaseDate()).get(Calendar.YEAR));
        holder.binding.textviewReleaseDate.setText(year);

        Picasso.with(mContext)
                .load(ApiClient.getInstance().URL_IMAGE.concat(ImageSize.getInstance().NORMAL).concat(mItem.getPosterPath() != null?mItem.getPosterPath():""))
                .placeholder(R.drawable.ic_movie) //this is optional the image to display while the url image is downloading
                .error(R.drawable.ic_error_sing)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                .into(holder.binding.imagePoster);

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnListItemInteraction(mItem);
            }
        });

//        setAnimation(holder.binding.getRoot(), position);

        holder.binding.executePendingBindings();   // update the view now
    }


    public void onSecondaryBindViewHolder(final HorizontalViewHolder holder, final int position) {
        String category = "";
        String query = "";
        if(mItems.get(position).getCategory().equals(Constant.getInstance().QUERY_UPCOMING)){
            category = mContext.getResources().getString(R.string.now_playing);
            query = Constant.getInstance().QUERY_NOW_PLAYING;
        }else {
            category = mContext.getResources().getString(R.string.upcoming);
            query = Constant.getInstance().QUERY_UPCOMING;

        }

        holder.binding.viewTitle.textviewTitle.setText(category);

        ApiClient.getInstance().getListMovie(query,1, new ApiClient.OnFetchDataListener<ApiMovieResponse>() {
            @Override
            public void OnResponse(ApiMovieResponse response) {
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false);
                holder.binding.recyclerViewHorizontal.setLayoutManager(mLayoutManager);
                MovieHorizontalRecyclerviewAdapter adapter = new MovieHorizontalRecyclerviewAdapter(mContext,response.getResults(),mListener);
                holder.binding.recyclerViewHorizontal.setAdapter(adapter);

            }

            @Override
            public void OnError(ANError error) {
                Timber.e("ERROR : "+error.getMessage());
            }
        });
        holder.binding.executePendingBindings();   // update the view now

    }

    public void onNullBindViewHolder(final LoadingViewHolder holder, final int position) {
        holder.binding.progressBar1.setIndeterminate(true);
        holder.binding.executePendingBindings();
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
                    viewToAnimate.getContext(), android.R.anim.slide_in_left);
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
            ((MainViewHolder)holder).binding.getRoot().setVisibility(View.VISIBLE);
            ((MainViewHolder)holder).binding.getRoot().clearAnimation();
        } else if (holder instanceof HorizontalViewHolder) {
            ((HorizontalViewHolder)holder).binding.getRoot().setVisibility(View.VISIBLE);
            ((HorizontalViewHolder)holder).binding.getRoot().clearAnimation();
        } else if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder)holder).binding.getRoot().setVisibility(View.VISIBLE);
            ((LoadingViewHolder)holder).binding.getRoot().clearAnimation();
        }
    }




    private class MainViewHolder extends RecyclerView.ViewHolder {
        private final ItemMovieBinding binding;
        public MainViewHolder(View itemView) {
            super(itemView);
            binding = ItemMovieBinding.bind(itemView);
        }
    }

    private class HorizontalViewHolder extends RecyclerView.ViewHolder{
        private final ListRecyclerviewHorizontalBinding binding;
        public HorizontalViewHolder(View itemView) {
            super(itemView);
            binding = ListRecyclerviewHorizontalBinding.bind(itemView);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ItemLoadingBinding binding;
        public LoadingViewHolder(View view) {
            super(view);
            binding = ItemLoadingBinding.bind(view);
        }
    }
}
