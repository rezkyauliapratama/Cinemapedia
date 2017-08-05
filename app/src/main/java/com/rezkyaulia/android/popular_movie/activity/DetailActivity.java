package com.rezkyaulia.android.popular_movie.activity;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.rezkyaulia.android.popular_movie.adapter.DetailMovieRecyclerviewAdapter;
import com.rezkyaulia.android.popular_movie.adapter.MovieRecyclerviewAdapter;
import com.rezkyaulia.android.popular_movie.adapter.ReviewRecyclerviewAdapter;
import com.rezkyaulia.android.popular_movie.adapter.TrailerRecyclerviewAdapter;
import com.rezkyaulia.android.popular_movie.data.DetailModel;
import com.rezkyaulia.android.popular_movie.database.DbHelper;
import com.rezkyaulia.android.popular_movie.model.ApiReviewResponse;
import com.rezkyaulia.android.popular_movie.model.ApiTrailerResponse;
import com.rezkyaulia.android.popular_movie.model.Movie;
import com.rezkyaulia.android.popular_movie.R;
import com.rezkyaulia.android.popular_movie.databinding.ActivityDetailBinding;
import com.rezkyaulia.android.popular_movie.model.Review;
import com.rezkyaulia.android.popular_movie.model.Trailer;
import com.rezkyaulia.android.popular_movie.util.ApiClient;
import com.rezkyaulia.android.popular_movie.util.Common;
import com.rezkyaulia.android.popular_movie.util.Constant;
import com.rezkyaulia.android.popular_movie.util.ImageSize;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import timber.log.Timber;


/**
 * Created by Rezky Aulia Pratama on 7/2/2017.
 */

public class DetailActivity extends BaseActivity implements TrailerRecyclerviewAdapter.OnRecyclerViewInteraction{
    public static final String EXTRA1 = "EXTRA1";

    Movie mMovie;
    List<Object> mitems;
    ActivityDetailBinding binding;
    LinearLayoutManager layoutManager;
    DetailMovieRecyclerviewAdapter adapter;
    private boolean isLandscape = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        setSupportActionBar(binding.contentAppbar.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        mMovie = (Movie) getIntent().getParcelableExtra(EXTRA1);

        mitems = new ArrayList<>();
        loadData();
        initRecyclerview();

        binding.contentAppbar.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >= -20)
                {
                    // Collapsed
                    binding.contentAppbar.textViewTitleBar.setVisibility(View.GONE);
                }
                else
                {
                    // Not collapsed
                    binding.contentAppbar.textViewTitleBar.setVisibility(View.VISIBLE);

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar rewardTbl clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume() {
        super.onResume();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;

        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        float smallestWidth = Math.min(widthDp, heightDp);

        if (smallestWidth > 600) {
            //Device is a 7" tablet
            isLandscape = true;
        }else{
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                isLandscape = true;
            }else{
                isLandscape = false;
            }
        }

    }

    private void loadData(){
        if (mMovie != null){
            AndroidNetworking.cancelAll();
            binding.contentAppbar.textViewTitle.setText(mMovie.getTitle());
            binding.contentAppbar.textViewTitleBar.setText(mMovie.getTitle());
            Picasso.with(this)
                    .load(ApiClient.getInstance().URL_IMAGE.concat(ImageSize.getInstance().ORI).concat(mMovie.getBackdropPath()))
                    .placeholder(R.drawable.ic_movie) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.ic_error_sing)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(binding.contentAppbar.imageBackdrop);

            mitems.add(0,mMovie);
            mitems.add(1,new DetailModel(Constant.getInstance().TYPE_TRAILER,mMovie.getId()));
            mitems.add(2,new DetailModel(Constant.getInstance().TYPE_REVIEW,mMovie.getId()));

            if (adapter != null)
                adapter.notifyDataSetChanged();


        }
    }

    private void initRecyclerview(){
        layoutManager = new LinearLayoutManager(this);
        adapter = new DetailMovieRecyclerviewAdapter(this,mitems,DetailActivity.this);
        binding.contentDetail.recyclerView.setLayoutManager(layoutManager);
        binding.contentDetail.recyclerView.setHasFixedSize(true);
        binding.contentDetail.recyclerView.setAdapter(adapter);




    }

    private void checkFavorite(){
        String stringId = Integer.toString(mMovie.getId());
        Uri uri = DbHelper.getInstance(this).getFavoriteContract().CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        Cursor cursor = getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);

        Timber.e("cursor getcount : "+cursor.getCount());
        /*if (cursor.getCount() > 0){
            binding.contentDetail.imageViewFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
        }else{
            binding.contentDetail.imageViewFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_unfavorite));

        }*/
    }


    public  void watchYoutubeVideo(String id) {
        Intent applicationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.getInstance().YOUTUBE + id));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(ApiClient.getInstance().URL_YOUTUBE + id));
        try {
            startActivity(applicationIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(browserIntent);
        }
    }

    @Override
    public void OnListItemInteraction(Trailer trailer) {
        watchYoutubeVideo(trailer.getKey());
    }
}
