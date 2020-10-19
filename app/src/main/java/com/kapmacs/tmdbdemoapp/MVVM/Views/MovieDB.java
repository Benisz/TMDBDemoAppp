package com.kapmacs.tmdbdemoapp.MVVM.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.kapmacs.tmdbdemoapp.MVVM.ViewModels.MovieDBViewModel;
import com.kapmacs.tmdbdemoapp.R;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.AccountDetails.AccountDetailsResponse;

public class MovieDB extends AppCompatActivity {

  private MovieDBViewModel movieDBViewModel;
  private TextView userNameTV;
  private LiveData<AccountDetailsResponse> accountDetailsResponseLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_d_b_activity);

        userNameTV=findViewById(R.id.user_name_tv);

        movieDBViewModel =new ViewModelProvider(this).get(MovieDBViewModel.class);
        Intent intent=getIntent();
        movieDBViewModel.setup(intent.getStringExtra("SessionID"),getResources().getString(R.string.API_Key));
        movieDBViewModel.getAccountDetailsResult();
        accountDetailsResponseLiveData= movieDBViewModel.getAccountDetailsResponse();
        accountDetailsResponseLiveData.observe(this, new Observer<AccountDetailsResponse>() {
            @Override
            public void onChanged(AccountDetailsResponse accountDetailsResponse) {
                userNameTV.setText(String.format("Hi %s what are you looking for today?",accountDetailsResponse.username));
            }
        });

        //TODO: replace with ViewPager and modify this activity to to handle MovieDBFragmentSearch and MovieDBFragmentDetails as child views
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MovieDBFragmentSearch.newInstance())
                    .commitNow();
        }
    }
}