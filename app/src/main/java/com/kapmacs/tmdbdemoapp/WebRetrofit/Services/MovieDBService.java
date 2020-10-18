package com.kapmacs.tmdbdemoapp.WebRetrofit.Services;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.AccountDetails.AccountDetailsResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.CreateSession.CreateSessionResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Details.DetailsResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Search.SearchResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.TokenRequest.NewTokenRequestResponse;

import java.util.HashMap;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDBService {



        @GET("authentication/token/new")
        Single<NewTokenRequestResponse>requestAuthToken(@Query("api_key") String apiKey);

        @POST("authentication/session/new")
        Single<CreateSessionResponse> createSession(@Query("api_key") String apiKey, @Body HashMap<String,String> body);

        @GET("account")
        Single<AccountDetailsResponse> getAccountDetails(@Query("api_key") String apiKey,@Query("session_id") String sessionID);


        @GET("search/movie")
        Single<SearchResponse> querySearch(@Query("api_key") String apiKey, @Query("query") String input, @Query("page") int page);

        @GET("movie/{movie_id}")
        Single<DetailsResponse> queryMovieDetails(@Path("movie_id")int movieID, @Query("api_key") String apiKey);




}
