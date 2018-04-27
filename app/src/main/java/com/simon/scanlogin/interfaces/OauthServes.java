package com.simon.scanlogin.interfaces;

import com.simon.scanlogin.domain.AccessToken;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * oauth接口
 *
 * @author simon
 * @create 2018-04-26 10:21
 **/

public interface OauthServes {

    /**
     * 根据用户名和密码获取token
     * @param grantType 固定值password
     * @param username
     * @param password
     * @return
     */
    @POST("/oauth/token")
    Observable<AccessToken> getToken(
            @Query("grant_type") String grantType,
            @Query("username") String username,
            @Query("password") String password);

    /**
     * 根据refresh_token获取新的token
     * @param grantType 固定值refresh_token
     * @param refresh_token
     * @return
     */
    @POST("/oauth/token")
    Observable<AccessToken> getToken(
            @Query("grant_type") String grantType,
            @Query("refresh_token") String refresh_token
    );
}
