package com.simon.scanlogin.interfaces;

import com.simon.scanlogin.domain.AccessToken;
import com.simon.scanlogin.domain.ResultMsg;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface RequestServes {
    //app扫描网页二维码登录
    @POST("/api/qrCodes/loginByQrCode")
    Observable<ResultMsg> loginByQrCode(@Query("username")String username,
                                        @Query("access_token")String access_token,
                                        @Query("sid")String sid);

    //账号密码登录
    @GET("/api/oauthUsers/{phone}/{password}")
    Observable<ResultMsg> login(@Path("phone") String phone,
                          @Path("password") String password);

    //使用refresh_token刷新access_token
    @POST("/api/oauthUsers/refreshToken")
    Observable<ResultMsg> refreshToken(@Query("refresh_token")String refresh_token);
}
