package com.mobilesecurity.mobileapp.utils;



import com.mobilesecurity.mobileapp.modules.ServerRequest;
import com.mobilesecurity.mobileapp.modules.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {

    @POST("include/")
    Call<ServerResponse> operation(@Body ServerRequest request);

}