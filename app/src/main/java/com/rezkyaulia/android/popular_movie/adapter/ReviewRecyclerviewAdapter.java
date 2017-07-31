package com.rezkyaulia.android.popular_movie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.rezkyaulia.android.popular_movie.R;
import com.rezkyaulia.android.popular_movie.databinding.ListItemReviewBinding;
import com.rezkyaulia.android.popular_movie.model.Review;

import java.util.List;

/**
 * Created by Rezky Aulia Pratama on 7/30/2017.
 */

public class ReviewRecyclerviewAdapter extends RecyclerView.Adapter<ReviewRecyclerviewAdapter.ViewHolder>  {


    Context mContext;
    List<Review> mItems;

    private int animationCount = 0;
    private int lastPosition = -1;

    public ReviewRecyclerviewAdapter(Context context, List<Review> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public ReviewRecyclerviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_review, parent, false);
        return new ReviewRecyclerviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewRecyclerviewAdapter.ViewHolder holder, int position) {
        Review item = mItems.get(position);

        holder.binding.textViewName.setText(item.getAuthor());
        holder.binding.textViewContent.setText(item.getContent());
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
    public int getItemCount() {
        return mItems.size();
    }


    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.binding.getRoot().setVisibility(View.VISIBLE);
        holder.binding.getRoot().clearAnimation();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ListItemReviewBinding binding;
        public ViewHolder(View itemView) {
            super(itemView);
            binding = ListItemReviewBinding.bind(itemView);
        }
    }


}
