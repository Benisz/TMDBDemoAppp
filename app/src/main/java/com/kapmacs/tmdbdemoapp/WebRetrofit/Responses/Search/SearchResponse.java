package com.kapmacs.tmdbdemoapp.WebRetrofit.Responses.Search;

import java.util.List;

public class SearchResponse {
    public Integer page;
    public List<SearchResult> results;
    public Integer total_results;
    public Integer total_pages;

}
