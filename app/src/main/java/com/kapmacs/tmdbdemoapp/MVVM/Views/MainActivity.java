package com.kapmacs.tmdbdemoapp.MVVM.Views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.kapmacs.tmdbdemoapp.R;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.CreateSession.CreateSessionResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.TokenRequest.NewTokenRequestResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.RetrofitServiceCreator;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Services.MovieDBService;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private WebView webView;
    private Button loginBT;
    private Context context;

    //TODO: Connect Login button to the next activity
    //TODO: Leave some explanation to the functions and variables
    /*TODO: Left to make
           1. Create Model to communicate with MovieDB (done)
           2. Create shared ViewModel and connect it to Model (done)
           3. Create adapters and ViewPager
           4. Create Views and fragments
           5. Connect Views with adapters and with shared ViewModel 

    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;

        webView = findViewById(R.id.webview);
        loginBT = findViewById(R.id.login_bt);

        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTokenRequest();
            }
        });

    }

    private void newTokenRequest()
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
                   authorizeToken(newTokenRequestResponse);

                }
            })

        );

    }

    public void authorizeToken(NewTokenRequestResponse newTokenRequestResponse) {
        webView.setVisibility(View.VISIBLE);
        loginBT.setVisibility(View.GONE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
                                  @Override
                                  public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                                      Uri uri = request.getUrl();
                                      if (("https://www.themoviedb.org/authenticate/" + newTokenRequestResponse.request_token + "/allow").equals(uri.toString())) {
                                          Log.d(TAG, "shouldOverrideUrlLoading: Allow");
                                          view.setVisibility(View.INVISIBLE);
                                          CreateSession(newTokenRequestResponse.request_token);
                                      } else if (("https://www.themoviedb.org/authenticate/" + newTokenRequestResponse.request_token + "/deny").equals(uri.toString())) {
                                          Log.d(TAG, "shouldOverrideUrlLoading: Deny");
                                          webView.setVisibility(View.GONE);
                                          loginBT.setVisibility(View.VISIBLE);
                                      } else {
                                          Log.d(TAG, "shouldOverrideUrlLoading: Other loading");
                                      }

                                      return super.shouldOverrideUrlLoading(view, request);
                                  }
                              }
        );
        webView.loadUrl("https://www.themoviedb.org/authenticate/" + newTokenRequestResponse.request_token);
    }

    private void CreateSession(String requestToken)
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
                        loginBT.setVisibility(View.VISIBLE);
                      if(createSessionResponse.success) {
                          Intent intent = new Intent(context,MovieDB.class);
                          intent.putExtra("SessionID", createSessionResponse.session_id);
                          startActivity(intent);
                      }
                    }
                }));
    }

}