package com.simon.scanlogin.interfaces;


import com.simon.scanlogin.domain.ResultMsg;
import com.simon.scanlogin.exception.UserNotLoginException;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by simon on 2017/4/23.
 */

public interface RequestWithToken {
    @GET("/api/users")
    Call<ResultMsg> getUser(@Query("access_token") String access_token)  throws UserNotLoginException;
}
