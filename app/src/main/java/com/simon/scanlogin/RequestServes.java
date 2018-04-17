package com.simon.scanlogin;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RequestServes {
    @POST("/users/loginByQrCode")
    Call<ResultMsg> loginByQrCode(@Query("username")String username,
                               @Query("token")String token,
                               @Query("sid")String sid);
}
