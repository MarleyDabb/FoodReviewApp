package com.example.foodreview;

import com.example.foodreview.models.GoogleMapData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantAPI {
    @GET("place/nearbysearch/json?location=37.421998333333335,-122.084&radius=1500&type=restaurant&type=food&key=AIzaSyAPgsR4B_WgxxvXLCi20ovyQN94dam5OHE")
    Call<GoogleMapData>getRestaurantWithLocation(@Query("lat")double lat, @Query("lon")double lon);
}
