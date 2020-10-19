package com.kapmacs.tmdbdemoapp.MVVM.Views;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kapmacs.tmdbdemoapp.R;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Details.DetailsResponse;

public class MovieDBFragmentDetails extends AppCompatActivity {
  private   DetailsResponse details;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_dbfragment_details);
        details= new Gson().fromJson( getIntent().getStringExtra("Details"),DetailsResponse.class);


        TextView title=findViewById(R.id.details_title_tv);
        TextView overview=findViewById(R.id.details_overview_tv);
        TextView voteCount=findViewById(R.id.details_vote_count_tv);
        TextView ratings=findViewById(R.id.details_ratings_tv);
        TextView homePage=findViewById(R.id.details_home_page_tv);
        ImageView posterIV=findViewById(R.id.details_poster_iv);



        //Filling TextViews
        title.setText(details.title);
        overview.setText(details.overview);
        voteCount.setText("Vote Count:"+details.vote_count.toString());
        ratings.setText("Ratings:"+details.vote_average.toString());
        homePage.setText(details.homepage);


        //Hyperlink setup
        homePage.setClickable(true);
        homePage.setMovementMethod(LinkMovementMethod.getInstance());
        homePage.setText(Html.fromHtml("<a href='"+details.homepage+"'>Home page</a>"));




        //Poster
        String poster = "https://image.tmdb.org/t/p/w500" +details.poster_path;
        Glide.with(this).load(poster).into(posterIV);









    }
}