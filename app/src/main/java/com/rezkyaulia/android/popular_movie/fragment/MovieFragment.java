package com.rezkyaulia.android.popular_movie.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.rezkyaulia.android.popular_movie.database.DbHelper;
import com.rezkyaulia.android.popular_movie.model.ApiGenreResponse;
import com.rezkyaulia.android.popular_movie.model.Genre;
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
    OnRecyclerViewInteraction mListener;
    FragmentRecyclerviewBinding binding;
    private String mCategory;
    GridLayoutManager mLayoutManager;

    private boolean isLandscape = false;

    public final static String LIST_STATE_KEY = "recycler_list_state";
    Parcelable listState;
    MovieRecyclerviewAdapter adapter;
    List<Movie> movies;


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
        }else{
            mCategory = Constant.getInstance().QUERY_POPULAR;
            movies = new ArrayList<>();

        }

        Timber.e("CATEGORY : "+mCategory);
        return binding.getRoot();
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
                mCategory = category;
                Timber.e("On Next Category : "+category);
                if (binding.swipeRefreshLayout != null) {
                    binding.swipeRefreshLayout.setRefreshing(true);
                }
                loadData();
            }


        });

        mLayoutManager = new GridLayoutManager(getContext(),2);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        adapter = new MovieRecyclerviewAdapter(getContext(),movies,mListener);
        binding.recyclerView.setAdapter(adapter);

        if(savedInstanceState == null){
            Timber.e("START WITH SAVEINSTANCE == NULL");
            loadData();
        }

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
        outState.putParcelableArrayList(EXTRA2, new ArrayList<Movie>(movies));
        super.onSaveInstanceState(outState);
    }



    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            mCategory = savedInstanceState.getString(EXTRA1);
            Timber.e("ONACTIVITYCREATED : "+mCategory);

        }

    }
*/
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
    }


    private void loadData(){
        final DbHelper dbHelper = DbHelper.getInstance(getContext());
        String category = "";

        AndroidNetworking.cancelAll();
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


            if(mCategory.equals(Constant.getInstance().QUERY_POPULAR)){
                category = getContext().getResources().getString(R.string.most_popular);
            }else if(mCategory.equals(Constant.getInstance().QUERY_TOP_RATED)){
                category = getContext().getResources().getString(R.string.top_rated);
            }else{
                category = getContext().getResources().getString(R.string.favorite);
            }

            binding.category.setText(category);

        if (category.equals(getString(R.string.most_popular)) || category.equals(getString(R.string.top_rated))){
            ApiClient.getInstance().getListMovie(mCategory,new ApiClient.OnFetchDataListener<ApiMovieResponse>() {
                @Override
                public void OnResponse(ApiMovieResponse response) {
                    if (response != null){

                        if (response.getResults().size() > 0){
                            movies.clear();
                            movies.addAll(response.getResults());
                            saveGenreData(movies);
                            ContentValues [] values = dbHelper.getMovieContract().contentValues(movies);

                            int rowInserted = getContext().getContentResolver().bulkInsert(
                                    dbHelper.getMovieContract().CONTENT_URI,
                                    values
                            );

                            if (rowInserted>0){
                                Timber.e("SIZE MOVIES  : "+rowInserted);
                                adapter.notifyDataSetChanged();

                                if (binding.swipeRefreshLayout != null) {
                                    binding.swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }

                    }
                }

                @Override
                public void OnError(ANError error) {
                    Timber.e("ERROR :".concat(error.getMessage()));

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
                        movies.add(DbHelper.getInstance(getContext()).getFavoriteContract().assign(cursor));
                    } while (cursor.moveToNext());
                cursor.close();

                adapter.notifyDataSetChanged();

                if (binding.swipeRefreshLayout != null) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }

    }

    private void saveGenreData(List<Movie> movies){

        if (movies.size() > 0){
            Uri uri = DbHelper.getInstance(getContext()).getMovieGenreContract().CONTENT_URI;
            uri = uri.buildUpon().build();

            getContext().getContentResolver().delete(uri, null, null);

            for (Movie movie : movies){
                for (int id : movie.getGenreIds()){
                    ContentValues value = DbHelper.getInstance(getContext())
                            .getMovieGenreContract().contentValue(movie.getId(),id);

                    getContext().getContentResolver().insert(DbHelper.getInstance(getContext()).getMovieGenreContract().CONTENT_URI, value);

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


    public interface OnRecyclerViewInteraction {
        // TODO: Update argument type and name
        void OnListItemInteraction(Movie movie);

    }


}
