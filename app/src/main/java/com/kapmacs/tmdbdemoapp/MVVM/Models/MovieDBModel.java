package com.kapmacs.tmdbdemoapp.MVVM.Models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.GsonBuilder;
import com.kapmacs.tmdbdemoapp.BuildConfig;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.AccountDetails.AccountDetailsResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.CreateSession.CreateSessionResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Details.DetailsResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Error.ErrorResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Search.SearchResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.RetrofitServiceCreator;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Services.MovieDBService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MovieDBModel {
    //TODO: Make it singleton
    //TODO: Handle error cases


    private static final String TAG = "MovieDBModel";
    private final MovieDBService service=new RetrofitServiceCreator().getMovieDBService();
    private final MutableLiveData<ErrorResponse> errorResponse=new MutableLiveData<>();
    private final MutableLiveData<CreateSessionResponse> sessionResponse=new MutableLiveData<>();
    private final MutableLiveData<DetailsResponse> detailsResponse=new MutableLiveData<>();
    private final MutableLiveData<SearchResponse> searchResponse=new MutableLiveData<>();
    private final MutableLiveData<AccountDetailsResponse> accountDetailsResponse=new MutableLiveData<>();
    private final CompositeDisposable disposable=new CompositeDisposable();
    private final String apiID;
    private final String sessionID;

    public MovieDBModel(String apiID, String sessionID) {
        this.apiID = apiID;
        this.sessionID = sessionID;
    }

    public void onDestroy()
    {
        disposable.clear();
    }

    public void ErrorResponse() {

    }
    public void DetailsResponse(Integer movieID) {
        disposable.add(service.queryMovieDetails(movieID
                ,apiID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DetailsResponse>() {
                    @Override
                    public void accept(DetailsResponse _detailsResponse) throws Exception {

                        detailsResponse.setValue(_detailsResponse);
                        if(BuildConfig.DEBUG)
                        Log.d(TAG, "accept: detailsResponse result:"+(new GsonBuilder().create().toJson(_detailsResponse)));
                    }
                })

        );
    }

    public void SearchResponse(String searchText,Integer pageNumber ) {


        disposable.add(service.querySearch(
                apiID,searchText,pageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SearchResponse>() {
                    @Override
                    public void accept(SearchResponse _searchResponse) throws Exception {

                        searchResponse.setValue(_searchResponse);
                        if(BuildConfig.DEBUG)
                        Log.d(TAG, "accept: searchResponse result:"+(new GsonBuilder().create().toJson(_searchResponse)));
                    }
                })

        );
    }

    public void AccountDetailsResponse() {
        disposable.add(service.getAccountDetails(apiID,sessionID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AccountDetailsResponse>() {
                    @Override
                    public void accept(AccountDetailsResponse _accountDetailsResponse) throws Exception {
                        accountDetailsResponse.setValue(_accountDetailsResponse);
                        if(BuildConfig.DEBUG)
                        Log.d(TAG, "accept: accountDetailsResponse result:"+(new GsonBuilder().create().toJson(_accountDetailsResponse)));
                    }

                }));
    }


    public LiveData<ErrorResponse> getErrorResponse() {
        return errorResponse;
    }

    public LiveData<CreateSessionResponse> getSessionResponse() {
        return sessionResponse;
    }

    public LiveData<DetailsResponse> getDetailsResponse() {
        return detailsResponse;
    }

    public LiveData<SearchResponse> getSearchResponse() {
        return searchResponse;
    }

    public LiveData<AccountDetailsResponse> getAccountDetailsResponse() {
        return accountDetailsResponse;
    }
}
