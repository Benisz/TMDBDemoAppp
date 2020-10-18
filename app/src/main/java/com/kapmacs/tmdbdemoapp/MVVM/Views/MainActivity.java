package com.kapmacs.tmdbdemoapp.MVVM.Views;

import android.content.UriMatcher;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.GsonBuilder;
import com.kapmacs.tmdbdemoapp.R;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.AccountDetails.AccountDetailsResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.CreateSession.CreateSessionResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Details.DetailsResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Search.SearchResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.TokenRequest.NewTokenRequestResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.RetrofitServiceCreator;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Services.MovieDBService;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String TAG = "MainActivity";
    private final MutableLiveData<String> LastToken =new MutableLiveData<>();

    //TODO: CLean up these test TEST DEBUG functions
    //TODO: Connect Login button to the next activity
    //TODO: Leave some explanation to the functions and variables
    /*TODO: Left to make
           1. Create Model to communicate with MovieDB
           2. Create shared ViewModel and connect it to Model
           3. Create adapters and ViewPager
           4. Create Views and fragments
           5. Connect Views with adapters and with shared ViewModel

    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //APIKey= getResources().getString(R.string.API_Key);
        setContentView(R.layout.activity_main);
        WebView view = findViewById(R.id.webview);
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setDomStorageEnabled(true);
        DebugTestResponses();
        LastToken.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                view.setWebViewClient(new WebViewClient() {
                                          @Override
                                          public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                                              Uri uri = request.getUrl();
                                              if (("https://www.themoviedb.org/authenticate/" + s + "/allow").equals(uri.toString())) {
                                                  Log.d(TAG, "shouldOverrideUrlLoading: Allow");
                                                  view.setVisibility(View.INVISIBLE);
                                                  DebugTestCreateSession(s);
                                              } else if (("https://www.themoviedb.org/authenticate/" + s + "/deny").equals(uri.toString())) {
                                                  Log.d(TAG, "shouldOverrideUrlLoading: Deny");
                                              } else {
                                                  Log.d(TAG, "shouldOverrideUrlLoading: Other loading");
                                              }

                                              return super.shouldOverrideUrlLoading(view, request);
                                          }
                                      }
                );
                view.loadUrl("https://www.themoviedb.org/authenticate/" + s);
            }
        });


    }

    private void DebugTestResponses()
    {
        MovieDBService service=new RetrofitServiceCreator().getMovieDBService();
        CompositeDisposable disposable=new CompositeDisposable();

        //Test New Token Request
        disposable.add(service.requestAuthToken(
            getResources().getString(R.string.API_Key))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<NewTokenRequestResponse>() {
                @Override
                public void accept(NewTokenRequestResponse newTokenRequestResponse) throws Exception {
                    Log.d(TAG, "accept: searchResponse result:"+(new GsonBuilder().create().toJson(newTokenRequestResponse)));
                    LastToken.setValue(newTokenRequestResponse.request_token);
                }
            })

        );

        //Test Search
        disposable.add(service.querySearch(
                getResources().getString(R.string.API_Key),"Thor",1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SearchResponse>() {
                    @Override
                    public void accept(SearchResponse searchResponse) throws Exception {
                        Log.d(TAG, "accept: searchResponse result:"+(new GsonBuilder().create().toJson(searchResponse)));
                    }
                })

        );
       disposable.add(service.queryMovieDetails(
                10195,getResources().getString(R.string.API_Key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DetailsResponse>() {
                    @Override
                    public void accept(DetailsResponse detailsResponse) throws Exception {
                        Log.d(TAG, "accept: detailsResponse result:"+(new GsonBuilder().create().toJson(detailsResponse)));
                    }
                })

        );


    }
    private void DebugTestCreateSession(String requestToken)
    {
        MovieDBService service=new RetrofitServiceCreator().getMovieDBService();
        CompositeDisposable disposable=new CompositeDisposable();
        HashMap<String,String> body=new HashMap<>();
        body.put("request_token",requestToken);
        disposable.add(service.createSession(getResources().getString(R.string.API_Key),body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CreateSessionResponse>() {
                    @Override
                    public void accept(CreateSessionResponse createSessionResponse) throws Exception {
                        Log.d(TAG, "accept: createSessionResponse result:"+(new GsonBuilder().create().toJson(createSessionResponse)));
                        DebugTestGetAccountDetails(createSessionResponse.session_id);
                    }
                }));




    }
    private void DebugTestGetAccountDetails(String sessionID)
    {
        MovieDBService service=new RetrofitServiceCreator().getMovieDBService();
        CompositeDisposable disposable=new CompositeDisposable();
        disposable.add(service.getAccountDetails(getResources().getString(R.string.API_Key),sessionID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AccountDetailsResponse>() {
                    @Override
                    public void accept(AccountDetailsResponse accountDetailsResponse) throws Exception {
                        Log.d(TAG, "accept: accountDetailsResponse result:"+(new GsonBuilder().create().toJson(accountDetailsResponse)));
                    }

    }));
    }

}