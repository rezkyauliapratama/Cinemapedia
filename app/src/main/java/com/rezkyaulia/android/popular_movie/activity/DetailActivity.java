package com.rezkyaulia.android.popular_movie.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.view.MenuItem;
import android.view.View;

import com.rezkyaulia.android.popular_movie.model.Movie;
import com.rezkyaulia.android.popular_movie.R;
import com.rezkyaulia.android.popular_movie.databinding.ActivityDetailBinding;
import com.rezkyaulia.android.popular_movie.util.ApiClient;
import com.rezkyaulia.android.popular_movie.util.Common;
import com.rezkyaulia.android.popular_movie.util.ImageSize;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import timber.log.Timber;

/**
 * Created by Rezky Aulia Pratama on 7/2/2017.
 */

public class DetailActivity extends BaseActivity {
    public static final String EXTRA1 = "EXTRA1";

    Movie mMovie;

    ActivityDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        setSupportActionBar(binding.contentAppbar.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        mMovie = (Movie) getIntent().getSerializableExtra(EXTRA1);

        loadData();

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

    private void loadData(){
        if (mMovie != null){
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


            Picasso.with(this)
                    .load(ApiClient.getInstance().URL_IMAGE.concat(ImageSize.getInstance().MEDIUM).concat(mMovie.getPosterPath()))
                    .placeholder(R.drawable.ic_movie) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.ic_error_sing)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(binding.contentDetail.imagePoster);
        }
    }
}
