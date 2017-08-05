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
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.rezkyaulia.android.popular_movie.adapter.MovieRecyclerviewAdapter;
import com.rezkyaulia.android.popular_movie.adapter.ReviewRecyclerviewAdapter;
import com.rezkyaulia.android.popular_movie.adapter.TrailerRecyclerviewAdapter;
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

import java.util.Calendar;
import java.util.List;

import timber.log.Timber;


/**
 * Created by Rezky Aulia Pratama on 7/2/2017.
 */

public class DetailActivity extends BaseActivity implements TrailerRecyclerviewAdapter.OnRecyclerViewInteraction{
    public static final String EXTRA1 = "EXTRA1";

    Movie mMovie;

    ActivityDetailBinding binding;
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


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

        loadData();
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

            String year = String.valueOf(Common.getInstance().parseDate(mMovie.getReleaseDate()).get(Calendar.YEAR));

            binding.contentDetail.textViewYear.setText(year);
            binding.contentDetail.textViewRate.setText(String.valueOf(mMovie.getVoteAverage()).concat(getString(R.string.per_rated)));
            binding.contentDetail.textViewOverview.setText(mMovie.getOverview());

            Cursor cursor = getContentResolver().query(
                    DbHelper.getInstance(this).getMovieGenreContract().CONTENT_URI,
                    null,
                    String.valueOf(mMovie.getId()),
                    null,
                    null);

            if (cursor.moveToFirst())
                do {
                    binding.contentDetail.textViewGenre.append(cursor.getString(cursor.getColumnIndex(DbHelper.getInstance(this).getGenreContract().NAME)).concat("; "));
                } while (cursor.moveToNext());
            cursor.close();

            Picasso.with(this)
                    .load(ApiClient.getInstance().URL_IMAGE.concat(ImageSize.getInstance().MEDIUM).concat(mMovie.getPosterPath()))
                    .placeholder(R.drawable.ic_movie) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.ic_error_sing)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(binding.contentDetail.imagePoster);

            checkFavorite();
            getTrailer();
            getReview();
        }
        setOnClickFavorite();
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
        if (cursor.getCount() > 0){
            binding.contentDetail.imageViewFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
        }else{
            binding.contentDetail.imageViewFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_unfavorite));

        }
    }

    private void getTrailer(){
        ApiClient.getInstance().getListTrailer(mMovie.getId(), new ApiClient.OnFetchDataListener<ApiTrailerResponse>() {
            @Override
            public void OnResponse(ApiTrailerResponse response) {
                Timber.e(new Gson().toJson(response));
                List<Trailer> trailers = response.getResults();

                if (trailers.size() > 0){
                    if (isLandscape){
                        binding.contentDetail.recyclerViewTrailer.setLayoutManager(new GridLayoutManager(DetailActivity.this,3));
                    }else{
                        binding.contentDetail.recyclerViewTrailer.setLayoutManager(new GridLayoutManager(DetailActivity.this,2));
                    }
                    binding.contentDetail.recyclerViewTrailer.setAdapter(new TrailerRecyclerviewAdapter(DetailActivity.this,trailers,DetailActivity.this));
                }

            }

            @Override
            public void OnError(ANError error) {
                Timber.e("ERROR : "+error.getMessage());
            }
        });
    }

    private void setOnClickFavorite(){

        binding.contentDetail.imageViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues value = DbHelper.getInstance(DetailActivity.this)
                        .getFavoriteContract().contentValue(mMovie);

                try{
                    getContentResolver().insert(DbHelper.getInstance(DetailActivity.this).getFavoriteContract().CONTENT_URI, value);
                }catch (SQLException e){
                    String stringId = Integer.toString(mMovie.getId());
                    Uri uri = DbHelper.getInstance(DetailActivity.this).getFavoriteContract().CONTENT_URI;
                    uri = uri.buildUpon().appendPath(stringId).build();

                    getContentResolver().delete(uri, null, null);
                }

                checkFavorite();

            }
        });


    }

    private void getReview(){
        ApiClient.getInstance().getListReview(mMovie.getId(), new ApiClient.OnFetchDataListener<ApiReviewResponse>() {
            @Override
            public void OnResponse(ApiReviewResponse response) {
                Timber.e(new Gson().toJson(response));
                List<Review> reviews = response.getResults();

                if (reviews.size() > 0){

                    binding.contentDetail.recyclerViewReview.setLayoutManager(new LinearLayoutManager(DetailActivity.this));

                    binding.contentDetail.recyclerViewReview.setAdapter(new ReviewRecyclerviewAdapter(DetailActivity.this,reviews));
                }

            }

            @Override
            public void OnError(ANError error) {
                Timber.e("ERROR : "+error.getMessage());
            }
        });
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
