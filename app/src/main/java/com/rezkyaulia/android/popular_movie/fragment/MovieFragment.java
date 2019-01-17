package com.rezkyaulia.android.popular_movie.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.error.ANError;
import com.rezkyaulia.android.popular_movie.database.DbHelper;
import com.rezkyaulia.android.popular_movie.listener.OnLoadMoreListener;
import com.rezkyaulia.android.popular_movie.model.ApiGenreResponse;
import com.rezkyaulia.android.popular_movie.model.Genre;
import com.rezkyaulia.android.popular_movie.model.MovieAbstract;
import com.rezkyaulia.android.popular_movie.util.ApiClient;
import com.rezkyaulia.android.popular_movie.model.ApiMovieResponse;
import com.rezkyaulia.android.popular_movie.util.Constant;
import com.rezkyaulia.android.popular_movie.model.Movie;
import com.rezkyaulia.android.popular_movie.adapter.MovieRecyclerviewAdapter;
import com.rezkyaulia.android.popular_movie.R;
import com.rezkyaulia.android.popular_movie.databinding.FragmentRecyclerviewBinding;
import com.rezkyaulia.android.popular_movie.util.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import timber.log.Timber;

/**
 * Created by Rezky Aulia Pratama on 7/1/2017.
 */

public class MovieFragment extends BaseFragment {
    public static final String EXTRA1 = "EXTRA1";
    public static final String EXTRA2 = "EXTRA2";
    public static final String EXTRA3 = "EXTRA3";
    OnRecyclerViewInteraction mListener;
    FragmentRecyclerviewBinding binding;
    private String mCategory;
    GridLayoutManager mLayoutManager;
    int mPage;


    private boolean isLandscape = false;

    public final static String LIST_STATE_KEY = "recycler_list_state";
    Parcelable listState;
    MovieRecyclerviewAdapter adapter;
    List<MovieAbstract> movies;


    public static MovieFragment newInstance(String category) {
        MovieFragment fragment = new MovieFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA1, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mCategory = getArguments().getString(EXTRA1);
            movies = new ArrayList<>();
        }
        Timber.e("mCategory : "+mCategory);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_recyclerview,container,false); // LayoutInflater.from(context).inflate(R.layout.content_progressbar,view,false);

        if(savedInstanceState != null){
            Timber.e("SAVEDINSTACESTATE");
            mCategory = savedInstanceState.getString(EXTRA1);
            movies = savedInstanceState.getParcelableArrayList(EXTRA2);
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
            mPage = savedInstanceState.getInt(EXTRA3);
        }

        Timber.e("CATEGORY : "+mCategory);
        return binding.getRoot();
    }

    private void setLayoutManager(){
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (!isLandscape){
                    if (movies.get(position) instanceof MovieAbstract){
                        if (movies.get(position).getType() == Constant.getInstance().TYPE_MAIN){
                            return 1;
                        }else if (movies.get(position).getType() == Constant.getInstance().TYPE_SECONDARY){
                                return 2;
                        }else if (movies.get(position) == null){
                            Timber.e("SpanSize for null potrait");
                            return 2;
                        }
                    }
                }else{
                    if (movies.get(position).getType() == Constant.getInstance().TYPE_MAIN){
                        return 1;
                    }else if (movies.get(position).getType() == Constant.getInstance().TYPE_SECONDARY){
                        return 3;
                    }else if (movies.get(position) == null){
                        Timber.e("SpanSize for null landscape");

                        return 3;
                    }
                }
                return -1;

            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        EventBus.instanceOf().getObservable().subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String category) {
                mPage = 1;
                mCategory = category;
                Timber.e("On Next Category : "+category);
                loadData();
            }


        });

        mLayoutManager = new GridLayoutManager(getContext(),2);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        adapter = new MovieRecyclerviewAdapter(getContext(),movies,mListener);
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Timber.e("NEXT PAGE");
                mPage++;
                loadData();

            }
        });

        if(savedInstanceState == null){
            Timber.e("START WITH SAVEINSTANCE == NULL");
            mPage = 1;
            ApiClient.getInstance().getListGenre(new ApiClient.OnFetchDataListener<ApiGenreResponse>() {
                @Override
                public void OnResponse(ApiGenreResponse response) {
                    if (response != null){
                        List<Genre> genres;
                        genres = response.getResults();

                        ContentValues [] values = DbHelper.getInstance(getContext()).getGenreContract().contentValues(genres);

                        getContext().getContentResolver().bulkInsert(
                                DbHelper.getInstance(getContext()).getGenreContract().CONTENT_URI,
                                values
                        );
                    }
                }

                @Override
                public void OnError(ANError error) {
                    Timber.e("ERROR :".concat(error.getMessage()));
                }
            });
            loadData();
        }

        setTitle();
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Timber.e("SWIPEREFRESHLAYOUT");
                mPage = 1;
                loadData();
            }
        });


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        listState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, listState);

        Timber.e("category : "+mCategory);
        outState.putString(EXTRA1, mCategory);
        outState.putParcelableArrayList(EXTRA2, new ArrayList<MovieAbstract>(movies));
        outState.putInt(EXTRA3, mPage);
        super.onSaveInstanceState(outState);
    }

    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);



    }

    @Override
    public void onResume() {
        super.onResume();

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

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


        if (listState != null) {
            mLayoutManager.onRestoreInstanceState(listState);
            Timber.e("list state != null");

        }

        if (isLandscape){
            mLayoutManager.setSpanCount(3);
        }else{
            mLayoutManager.setSpanCount(2);
        }

        setLayoutManager();

    }


    private void setTitle(){
        String category = "";

        if(mCategory.equals(Constant.getInstance().QUERY_NOW_PLAYING)){
            category = getContext().getResources().getString(R.string.now_playing);
        }else if(mCategory.equals(Constant.getInstance().QUERY_UPCOMING)){
            category = getContext().getResources().getString(R.string.upcoming);
        }else if(mCategory.equals(Constant.getInstance().QUERY_POPULAR)){
            category = getContext().getResources().getString(R.string.most_popular);
        }else if(mCategory.equals(Constant.getInstance().QUERY_TOP_RATED)){
            category = getContext().getResources().getString(R.string.top_rated);
        }else{
            category = getContext().getResources().getString(R.string.favorite);
        }

        binding.category.setText(category);
    }
        private void loadData(){
            if (binding.swipeRefreshLayout != null) {
                binding.swipeRefreshLayout.setRefreshing(true);
            }

            if (!mCategory.equals(Constant.getInstance().QUERY_FAVORITE)){

                ApiClient.getInstance().getListMovie(mCategory,mPage,new ApiClient.OnFetchDataListener<ApiMovieResponse>() {
                    @Override
                    public void OnResponse(ApiMovieResponse response) {
                        new DownloadTask().execute(response);

                    }

                    @Override
                    public void OnError(ANError error) {
                        Timber.e("ERROR :".concat(error.getMessage()));
                        mPage--;
                        if (binding.swipeRefreshLayout != null) {
                            binding.swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
            }else{

                Cursor cursor = getContext().getContentResolver().query(
                        DbHelper.getInstance(getContext()).getFavoriteContract().CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

                movies.clear();
                if (cursor.moveToFirst())
                    do {
                        movies.add(new MovieAbstract(Constant.getInstance().TYPE_MAIN,DbHelper.getInstance(getContext()).getFavoriteContract().assign(cursor)));
                    } while (cursor.moveToNext());
                cursor.close();
                setTitle();
                adapter.notifyDataSetChanged();
                adapter.setLoaded();
                if (binding.swipeRefreshLayout != null) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        }

    private void saveGenreData(List<MovieAbstract> movies){

        if (movies.size() > 0){
            Uri uri = DbHelper.getInstance(getContext()).getMovieGenreContract().CONTENT_URI;
            uri = uri.buildUpon().build();

            getContext().getContentResolver().delete(uri, null, null);

            for (MovieAbstract movie : movies){
                if (movie.getMovie() != null){
                    for (int id : movie.getMovie().getGenreIds()){
                        ContentValues value = DbHelper.getInstance(getContext())
                                .getMovieGenreContract().contentValue(movie.getMovie().getId(),id);

                        getContext().getContentResolver().insert(DbHelper.getInstance(getContext()).getMovieGenreContract().CONTENT_URI, value);

                    }
                }

            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecyclerViewInteraction) {
            mListener = (OnRecyclerViewInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecyclerViewInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private class DownloadTask extends AsyncTask<ApiMovieResponse, Void, Void> {

        @Override
        protected Void doInBackground(ApiMovieResponse... params) {
            final DbHelper dbHelper = DbHelper.getInstance(getContext());
            if (params[0] != null){
                if (params[0].getResults().size() > 0){
                    if (mPage == 1) {
                        movies.clear();
                    }
                    int i=0;
                    for (Movie movie : params[0].getResults()){
                        if (isLandscape){
                            if (i==6 && mPage == 1){
                                movies.add(new MovieAbstract(Constant.getInstance().TYPE_SECONDARY,mCategory));
                            }
                            movies.add(new MovieAbstract(Constant.getInstance().TYPE_MAIN,movie));
                        }else{
                            if (i==4 && mPage == 1){
                                movies.add(new MovieAbstract(Constant.getInstance().TYPE_SECONDARY,mCategory));
                            }
                            movies.add(new MovieAbstract(Constant.getInstance().TYPE_MAIN,movie));
                        }

                        i++;
                    }
                    saveGenreData(movies);


                    ContentValues [] values = dbHelper.getMovieContract().contentValues(params[0].getResults());
                    int rowInserted = getContext().getContentResolver().bulkInsert(
                            dbHelper.getMovieContract().CONTENT_URI,
                            values
                    );

                    if (rowInserted>0){
                        Timber.e("SIZE MOVIES  : "+rowInserted);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Timber.e("onPreExecute");
            if (binding.swipeRefreshLayout != null) {
                binding.swipeRefreshLayout.setRefreshing(true);
            }
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setTitle();
            adapter.notifyDataSetChanged();
            adapter.setLoaded();
            if (binding.swipeRefreshLayout != null) {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    public interface OnRecyclerViewInteraction {
        // TODO: Update argument type and name
        void OnListItemInteraction(Movie movie);

    }

}
