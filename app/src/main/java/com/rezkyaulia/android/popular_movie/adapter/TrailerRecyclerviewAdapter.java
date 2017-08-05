package com.rezkyaulia.android.popular_movie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.rezkyaulia.android.popular_movie.R;
import com.rezkyaulia.android.popular_movie.databinding.ListDetailItemTrailerBinding;
import com.rezkyaulia.android.popular_movie.model.Trailer;
import com.rezkyaulia.android.popular_movie.util.ApiClient;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Rezky Aulia Pratama on 7/30/2017.
 */

public class TrailerRecyclerviewAdapter extends RecyclerView.Adapter<TrailerRecyclerviewAdapter.ViewHolder> {

    Context mContext;
    List<Trailer> mItems;
    OnRecyclerViewInteraction mListener;

    private int animationCount = 0;
    private int lastPosition = -1;

    public TrailerRecyclerviewAdapter(Context context, List<Trailer> items,OnRecyclerViewInteraction listener) {
        mContext = context;
        mItems = items;
        mListener = listener;
    }


    @Override
    public TrailerRecyclerviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_detail_item_trailer, parent, false);
        return new TrailerRecyclerviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerRecyclerviewAdapter.ViewHolder holder, int position) {
        final Trailer item = mItems.get(position);

        Picasso.with(mContext)
                .load(ApiClient.getInstance().URL_THUMBNAIL.concat(item.getKey()).concat("/0.jpg"))
                .placeholder(R.drawable.ic_movie) //this is optional the image to display while the url image is downloading
                .error(R.drawable.ic_error_sing)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                .into(holder.binding.imageThumbnail);

        holder.binding.textViewTitle.setText(item.getName());
        holder.binding.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnListItemInteraction(item);
            }
        });

        setAnimation(holder.binding.getRoot(), position);

        holder.binding.executePendingBindings();   // update the view now
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
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.binding.getRoot().setVisibility(View.VISIBLE);
        holder.binding.getRoot().clearAnimation();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ListDetailItemTrailerBinding binding;
        public ViewHolder(View itemView) {
            super(itemView);
            binding = ListDetailItemTrailerBinding.bind(itemView);
        }
    }


    public interface OnRecyclerViewInteraction {
        // TODO: Update argument type and name
        void OnListItemInteraction(Trailer trailer);

    }

}
