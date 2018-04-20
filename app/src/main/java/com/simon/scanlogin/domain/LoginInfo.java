package com.simon.scanlogin.domain;

public class LoginInfo {
    private UserInfo userInfo;
    private AccessToken token;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public AccessToken getToken() {
        return token;
    }

    public void setToken(AccessToken token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "userInfo=" + userInfo.toString() +
                ", token=" + token.toString() +
                '}';
    }
}
