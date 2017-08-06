package com.rezkyaulia.android.popular_movie.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.rezkyaulia.android.popular_movie.database.DbHelper;
import com.rezkyaulia.android.popular_movie.util.Constant;
import com.rezkyaulia.android.popular_movie.model.Movie;
import com.rezkyaulia.android.popular_movie.R;
import com.rezkyaulia.android.popular_movie.databinding.ActivityMainBinding;
import com.rezkyaulia.android.popular_movie.util.EventBus;
import com.rezkyaulia.android.popular_movie.fragment.MovieFragment;

import timber.log.Timber;

/**
 * Created by Rezky Aulia Pratama on 6/30/2017.
 */

public class MainActivity extends BaseActivity implements MovieFragment.OnRecyclerViewInteraction{
    private static final String EXTRA1 = "EXTRA1";
    ActivityMainBinding binding;
    Menu menu;
    private String mCategory;
    Fragment fragment;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.includeAppBar.toolbar);
        getSupportActionBar().setTitle(R.string.movie);

        if(savedInstanceState != null){
            mCategory = savedInstanceState.getString(EXTRA1);
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "movieFragment");
        }else{
            mCategory = Constant.getInstance().QUERY_NOW_PLAYING;
            fragment = MovieFragment.newInstance(mCategory);
        }

        if (fragment != null){
            displayFragment(binding.includeContent.framelayout.getId(),fragment);

        }

        Timber.e("OnCreate ! ");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        Timber.e("onSaveInstanceState ! ");

        getSupportFragmentManager().putFragment(outState, "movieFragment", fragment );
        outState.putString(EXTRA1, mCategory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar rewardTbl clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        //noinspection SimplifiableIfStatement
        if (id == R.id.sort_now_playing) {
            mCategory = Constant.getInstance().QUERY_NOW_PLAYING;
            EventBus.instanceOf().setObservable(mCategory);

            return true;
        }else if(id == R.id.sort_upcoming){
            mCategory = Constant.getInstance().QUERY_UPCOMING;
            EventBus.instanceOf().setObservable(mCategory);

            return true;
        }else if(id == R.id.sort_most_popular){
            mCategory = Constant.getInstance().QUERY_POPULAR;
            EventBus.instanceOf().setObservable(mCategory);

            return true;
        }else if(id == R.id.sort_top_rated){
            mCategory = Constant.getInstance().QUERY_TOP_RATED;
            EventBus.instanceOf().setObservable(mCategory);

            return true;
        }else if(id == R.id.favorite){
            mCategory = Constant.getInstance().QUERY_FAVORITE;
            EventBus.instanceOf().setObservable(mCategory);

            return true;
        }


        return super.onOptionsItemSelected(item);
    }



    @Override
    public void OnListItemInteraction(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA1,movie);
        startActivity(intent);
    }
}
