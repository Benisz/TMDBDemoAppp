package com.kapmacs.tmdbdemoapp.MVVM.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kapmacs.tmdbdemoapp.BuildConfig;
import com.kapmacs.tmdbdemoapp.MVVM.ViewModels.MovieDBViewModel;
import com.kapmacs.tmdbdemoapp.MVVM.Views.Adapters.MovieDBRecyclerAdapter;
import com.kapmacs.tmdbdemoapp.R;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Details.DetailsResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Search.SearchResponse;

import org.jetbrains.annotations.NotNull;


public class MovieDBFragmentSearch extends Fragment implements MovieDBRecyclerAdapter.MovieDBRecyclerCallback {
    /* This is a fragment for MovieDB Activity
    *
    *
    *
    *
    *
    *
    *
    *
    * */





    private static final String TAG = "MovieDBFragment";

    private SearchView searchView;
    private MovieDBViewModel mViewModel;
    private RecyclerView movieRV;
    private LiveData<SearchResponse> searchResult;
    private SearchResponse lastResponse;


    public static MovieDBFragmentSearch newInstance() {
        return new MovieDBFragmentSearch();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(getActivity()).get(MovieDBViewModel.class);

        searchView=getView().findViewById(R.id.searchView);
        movieRV=getView().findViewById(R.id.movies_rv);

    }

    @Override
    public void onStart() {
        super.onStart();
        searchResult=mViewModel.getSearchResponse();

        MovieDBRecyclerAdapter adapter=new MovieDBRecyclerAdapter(this,getResources().getString(R.string.API_Key));


        movieRV.setLayoutManager(new LinearLayoutManager(getContext()));
        movieRV.setAdapter(adapter);


        //This loads in a new page when RecyclerView reaches the bottom
        movieRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1))
                {
                    if(lastResponse.page+1<=lastResponse.total_pages)
                    mViewModel.getSearchResult(searchView.getQuery().toString(),lastResponse.page+1);

                }
            }
        });



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.length()>=1)
                mViewModel.getSearchResult(s,1);
                return false;
            }
        });
        searchResult.observe(this, new Observer<SearchResponse>() {
            @Override
            public void onChanged(SearchResponse searchResponse) {
                lastResponse=searchResponse;
                adapter.updateList(searchResponse);

                if(BuildConfig.DEBUG)
                Log.d(TAG, "onChanged: "+searchResponse.total_results.toString());
            }
        });

    }


        @Override
        public void onItemSelected(DetailsResponse details) {
            Intent intent=new Intent(getActivity(),MovieDBFragmentDetails.class);
            intent.putExtra("Details",new Gson().toJson(details));
            startActivity(intent);
        }

}