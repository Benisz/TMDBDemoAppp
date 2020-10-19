package com.kapmacs.tmdbdemoapp.MVVM.Views.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import com.kapmacs.tmdbdemoapp.BuildConfig;
import com.kapmacs.tmdbdemoapp.R;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Details.DetailsResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Search.SearchResponse;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Search.SearchResult;
import com.kapmacs.tmdbdemoapp.WebRetrofit.RetrofitServiceCreator;
import com.kapmacs.tmdbdemoapp.WebRetrofit.Services.MovieDBService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MovieDBRecyclerAdapter extends RecyclerView.Adapter<MovieDBRecyclerAdapter.ResultViewHolder> {

    private static final String TAG = "MovieDBRecyclerAdapter";
    private final MovieDBRecyclerCallback movieDBRecyclerCallback;
    private final MovieDBService service=new RetrofitServiceCreator().getMovieDBService();
    private final CompositeDisposable disposable=new CompositeDisposable();
    private final String apiID;
    private List<SearchResult> list=new ArrayList<>();


    public MovieDBRecyclerAdapter(MovieDBRecyclerCallback movieDBRecyclerCallback,String apiID) {
        this.movieDBRecyclerCallback = movieDBRecyclerCallback;
        this.apiID=apiID;
    }
    public void  updateList(SearchResponse response)
    {
        if(response.page==1)
        list=response.results;
        else{
            list.addAll(response.results);
        }
        notifyDataSetChanged();
    }
    @NonNull
    @NotNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_d_b_search_card,viewGroup,false);

        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ResultViewHolder resultViewHolder, int i) {

        SearchResult currentMovie=list.get(i);

        resultViewHolder.titleTV.setText(currentMovie.title);

        disposable.add(service.queryMovieDetails(currentMovie.id,apiID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DetailsResponse>() {
                    @Override
                    public void accept(DetailsResponse _detailsResponse) throws Exception {
                        if(_detailsResponse.budget>0) {
                            resultViewHolder.budgetTV.setText(String.format("The budget was %s $ for this movie", _detailsResponse.budget));
                        }
                        else
                        {resultViewHolder.budgetTV.setText(String.format("The budget not available for this movie"));}



                        resultViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                movieDBRecyclerCallback.onItemSelected(  _detailsResponse);

                            }
                        });


                        if(BuildConfig.DEBUG)
                            Log.d(TAG, "accept: detailsResponse result:"+(new GsonBuilder().create().toJson(_detailsResponse)));
                    }
                })

        );


        //Poster link
        String poster = "https://image.tmdb.org/t/p/w500" +currentMovie.poster_path;
        Glide.with(resultViewHolder.itemView).load(poster).into(resultViewHolder.posterIV);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface MovieDBRecyclerCallback{

        void onItemSelected(DetailsResponse details);


    }







    class ResultViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTV;
        public TextView budgetTV;
        public ImageView posterIV;


        public ResultViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            titleTV=itemView.findViewById(R.id.title_tv);
            budgetTV=itemView.findViewById(R.id.budget_tv);
            posterIV=itemView.findViewById(R.id.movie_poster_iv);
        }
    }





}
