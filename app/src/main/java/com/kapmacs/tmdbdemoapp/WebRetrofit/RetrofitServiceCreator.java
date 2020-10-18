package com.kapmacs.tmdbdemoapp.WebRetrofit;

import com.kapmacs.tmdbdemoapp.WebRetrofit.Services.MovieDBService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServiceCreator {
    public static final String baseURL="https://api.themoviedb.org/3/";

    public MovieDBService getMovieDBService()
    {
        final Retrofit retrofit=createRetrofit();
        return retrofit.create(MovieDBService.class);
    }

    private Retrofit createRetrofit()
    {
        return  new Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
    }

}
