package com.kapmacs.tmdbdemoapp.MVVM.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kapmacs.tmdbdemoapp.MVVM.Models.MovieDBModel;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.AccountDetails.AccountDetailsResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Search.SearchResponse;

public class MovieDBViewModel extends ViewModel  {
   private MovieDBModel model;
   private Context context;




    public void setup(String sessionID, String apiID) {
       model=new MovieDBModel(apiID,sessionID);
    }


    public LiveData<AccountDetailsResponse> getAccountDetailsResponse() {
        return model.getAccountDetailsResponse();
    }
    public void getAccountDetailsResult()
    {
       model.AccountDetailsResponse();
    }
    public void getSearchResult(String searchText, Integer page)
    {
       model.SearchResponse(searchText,page);
    }
    public LiveData<SearchResponse> getSearchResponse()
    {
        return model.getSearchResponse();
    }


    @Override
    protected void onCleared() {
        model.onDestroy();
        super.onCleared();
    }
}