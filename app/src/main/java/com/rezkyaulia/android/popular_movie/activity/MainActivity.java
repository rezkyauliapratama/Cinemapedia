package com.rezkyaulia.android.popular_movie.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.app.infideap.stylishwidget.view.Stylish;
import com.rezkyaulia.android.popular_movie.database.DbHelper;
import com.rezkyaulia.android.popular_movie.util.Common;
import com.rezkyaulia.android.popular_movie.util.Constant;
import com.rezkyaulia.android.popular_movie.model.Movie;
import com.rezkyaulia.android.popular_movie.R;
import com.rezkyaulia.android.popular_movie.databinding.ActivityMainBinding;
import com.rezkyaulia.android.popular_movie.util.EventBus;
import com.rezkyaulia.android.popular_movie.fragment.MovieFragment;
import com.rezkyaulia.android.popular_movie.util.PreferencesManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        checkVersion();
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


    private void checkVersion() {
        final int versionCode = Common.getInstance().getVersionCode(this);

        if (PreferencesManager.getInstance().getCurrentVersion() != versionCode) {
            final View view = getLayoutInflater().inflate(R.layout.layout_whatsnew, null);
            AndroidNetworking.get("https://raw.githubusercontent.com/rezkyauliapratama/Cinemapedia/dev_rezky/app/src/main/play/en-GB/whatsnew")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsString(new StringRequestListener() {
                                     @Override
                                     public void onResponse(String response) {
                                         TextView textView = (TextView) view.findViewById(R.id.textView_whatsnew_desc);

                                         Spannable spannable = style(response);
                                         textView.setText(spannable);
                                         new AlertDialog.Builder(MainActivity.this)
                                                 .setView(view)
                                                 .setPositiveButton(R.string.gotit, new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         PreferencesManager.getInstance().setCurrentVersion(versionCode);
                                                     }
                                                 })
                                                 .create().show();
                                     }

                                     @Override
                                     public void onError(ANError anError) {
                                         Timber.e("ERROR : "+anError.getMessage());
                                     }
                                 }
                    );
            /*Atom.with(this)
                    .load("https://raw.githubusercontent.com/truevoxasia/Recruiter-App/alpha/app/src/main/play/en-GB/whatsnew?token=AEIfcGQfFRs0rUCPD4lNXh6NvtBKAawlks5ZivmLwA%3D%3D")
                    .asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String result) {
                    if (e != null || result == null)
                        return;
                    TextView textView = (TextView) view.findViewById(R.id.textView_whatsnew_desc);

                    Spannable spannable = style(result);
                    textView.setText(spannable);
                    new AlertDialog.Builder(MainActivity.this)
                            .setView(view)
                            .setPositiveButton(R.string.gotit, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PreferencesManager.getInstance().setCurrentVersion(versionCode);
                                }
                            })
                            .create().show();
                }
            });*/
        }
    }
    private Spannable style(String result) {
        result = result.replaceAll("\n\\.", "\n\n").replaceAll("- ", "◆ ");
//                .replaceAll("=", "—");
        SpannableStringBuilder builder = new SpannableStringBuilder(result);
        Pattern pattern = Pattern.compile("Version [\\d\\.]+");
        Matcher matcher = pattern.matcher(result);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            builder.setSpan(new ForegroundColorSpan(
                    ContextCompat.getColor(this, R.color.colorPrimary)
            ), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        pattern = Pattern.compile("================");
        matcher = pattern.matcher(result);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            builder.setSpan(new ForegroundColorSpan(
                    ContextCompat.getColor(this, R.color.colorPrimary)
            ), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.setSpan(new StrikethroughSpan(
            ), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        }
        return builder;
    }

    @Override
    public void OnListItemInteraction(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA1,movie);
        startActivity(intent);
    }
}
