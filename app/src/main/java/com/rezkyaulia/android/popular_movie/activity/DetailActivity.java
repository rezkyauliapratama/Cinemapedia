package com.rezkyaulia.android.popular_movie.activity;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.rezkyaulia.android.popular_movie.adapter.DetailMovieRecyclerviewAdapter;
import com.rezkyaulia.android.popular_movie.adapter.TrailerRecyclerviewAdapter;
import com.rezkyaulia.android.popular_movie.database.DbHelper;
import com.rezkyaulia.android.popular_movie.model.DetailAbstract;
import com.rezkyaulia.android.popular_movie.model.Movie;
import com.rezkyaulia.android.popular_movie.R;
import com.rezkyaulia.android.popular_movie.databinding.ActivityDetailBinding;
import com.rezkyaulia.android.popular_movie.model.Trailer;
import com.rezkyaulia.android.popular_movie.util.ApiClient;
import com.rezkyaulia.android.popular_movie.util.Constant;
import com.rezkyaulia.android.popular_movie.util.ImageSize;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


/**
 * Created by Rezky Aulia Pratama on 7/2/2017.
 */

public class DetailActivity extends BaseActivity implements TrailerRecyclerviewAdapter.OnRecyclerViewInteraction{
    public static final String EXTRA1 = "EXTRA1";

    Movie mMovie;

    List<DetailAbstract> mItems;
    Parcelable listState;
    public final static String LIST_STATE_KEY = "recycler_list_state";

    ActivityDetailBinding binding;
    private boolean isLandscape = false;
    DetailMovieRecyclerviewAdapter adapter;

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
        mItems = new ArrayList<>();

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


        initRecyclerview();
        setOnClickFavorite();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        listState = binding.contentDetail.recyclerViewDetail.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, listState);
        outState.putParcelable(EXTRA1, mMovie);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null){
            mMovie = savedInstanceState.getParcelable(EXTRA1);
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        }

    }



    private void initRecyclerview(){
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(DetailActivity.this);
        binding.contentDetail.recyclerViewDetail.setLayoutManager(mLayoutManager);
        adapter = new DetailMovieRecyclerviewAdapter(DetailActivity.this,mItems,DetailActivity.this);
        binding.contentDetail.recyclerViewDetail.setAdapter(adapter);

        binding.contentDetail.recyclerViewDetail.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && binding.fab.isShown()) {
                    binding.fab.hide();
                }else{
                    binding.fab.show();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                /*if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    binding.fab.hide();
                }*/
                super.onScrollStateChanged(recyclerView, newState);
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

        loadData();
        checkFavorite();

        if (listState != null) {
            binding.contentDetail.recyclerViewDetail.getLayoutManager().onRestoreInstanceState(listState);
            Timber.e("list state != null");

        }
    }

    private void loadData(){
        if (mMovie != null){
            AndroidNetworking.cancelAll();
            binding.contentAppbar.textViewTitle.setText(mMovie.getTitle());
            binding.contentAppbar.textViewTitleBar.setText(mMovie.getTitle());
            Picasso.with(this)
                    .load(ApiClient.getInstance().URL_IMAGE.concat(ImageSize.getInstance().ORI).concat(mMovie.getBackdropPath() != null ? mMovie.getBackdropPath() : ""))
                    .placeholder(R.drawable.ic_movie) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.ic_error_sing)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(binding.contentAppbar.imageBackdrop);


            mItems.clear();
            mItems.add(0,new DetailAbstract(Constant.getInstance().TYPE_MAIN,mMovie,isLandscape));
            mItems.add(1,new DetailAbstract(Constant.getInstance().TYPE_SECONDARY,mMovie,isLandscape));
            mItems.add(2,new DetailAbstract(Constant.getInstance().TYPE_THIRD,mMovie,isLandscape));

            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
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
            binding.fab.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPink_A700)));
        }else{
            binding.fab.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey_400)));
        }
    }

   /* private void getTrailer(){
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
    }*/

    private void setOnClickFavorite(){

        binding.fab.setOnClickListener(new View.OnClickListener() {
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

   /* private void getReview(){
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
    }*/
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
