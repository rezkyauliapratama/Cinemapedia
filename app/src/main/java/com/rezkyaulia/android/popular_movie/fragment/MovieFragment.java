package com.rezkyaulia.android.popular_movie.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.error.ANError;
import com.rezkyaulia.android.popular_movie.util.ApiClient;
import com.rezkyaulia.android.popular_movie.model.ApiResponse;
import com.rezkyaulia.android.popular_movie.util.Constant;
import com.rezkyaulia.android.popular_movie.model.Movie;
import com.rezkyaulia.android.popular_movie.adapter.MovieRecyclerviewAdapter;
import com.rezkyaulia.android.popular_movie.R;
import com.rezkyaulia.android.popular_movie.databinding.FragmentRecyclerviewBinding;
import com.rezkyaulia.android.popular_movie.util.EventBus;

import java.util.List;

import rx.Observer;
import timber.log.Timber;

/**
 * Created by Rezky Aulia Pratama on 7/1/2017.
 */

public class MovieFragment extends BaseFragment {

    OnRecyclerViewInteraction mListener;
    FragmentRecyclerviewBinding binding;
    private String mCategory;

    public static MovieFragment newInstance() {
        MovieFragment fragment = new MovieFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = Constant.getInstance().QUERY_POPULAR;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_recyclerview,container,false); // LayoutInflater.from(context).inflate(R.layout.content_progressbar,view,false);
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

                if (binding.swipeRefreshLayout != null) {
                    binding.swipeRefreshLayout.setRefreshing(true);
                }
                loadData();
            }


        });

        loadData();

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
    }

    private void loadData(){
        String category = "";

        if(mCategory.equals(Constant.getInstance().QUERY_POPULAR)){
            category = getContext().getResources().getString(R.string.most_popular);
        }else if(mCategory.equals(Constant.getInstance().QUERY_TOP_RATED)){
            category = getContext().getResources().getString(R.string.top_rated);
        }

        binding.category.setText(category);

        ApiClient.getInstance().getList(mCategory,new ApiClient.OnFetchDataListener() {
            @Override
            public void OnResponse(ApiResponse response) {
                if (response != null){
                    List<Movie> movies;
                    movies = response.getResults();

                    if (movies.size()>0){
                        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                        binding.recyclerView.setAdapter(new MovieRecyclerviewAdapter(getContext(),movies,mListener));

                        if (binding.swipeRefreshLayout != null) {
                            binding.swipeRefreshLayout.setRefreshing(false);
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


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecyclerViewInteraction) {
            mListener = (OnRecyclerViewInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
