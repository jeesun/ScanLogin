package com.simon.scanlogin.interfaces;

import com.simon.scanlogin.domain.ResultMsg;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RequestServes {
    //app扫描网页二维码登录
    @POST("/api/qrCodes/loginByQrCode")
    Call<ResultMsg> loginByQrCode(@Query("username")String username,
                                  @Query("access_token")String access_token,
                                  @Query("sid")String sid);

    //账号密码登录
    @GET("/api/oauthUsers/{phone}/{password}")
    Call<ResultMsg> login(@Path("phone") String phone,
                          @Path("password") String password);

    //使用refresh_token刷新access_token
    @POST("/api/oauthUsers/refreshToken")
    Call<ResultMsg> refreshToken(@Query("refresh_token")String refresh_token);
}
