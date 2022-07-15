package com.example.ssscb_android;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Api {

    String BASE_URL = "https://datapostapi.conveyor.cloud";
    @GET("/api/values/data/{id}")
    Call<Results> getValues(@Path("id") String id);

    @GET("/api/values/data1/{id}")
    Call<Results> GetFromToken(@Path("id") String id);

    @GET("/api/values/data2/{id}")
    Call<IDModel> GetIdFromToken(@Path("id") String id);

    @PUT("/api/values/{id}")
    Call<PutModel> putResults(@Path("id") String userName,@Body String deviceToken);
    @POST("/api/Login")
    Call<LoginResponse> createUser(@Body LoginResponse login);
}