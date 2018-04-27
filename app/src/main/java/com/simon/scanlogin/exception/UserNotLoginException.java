package com.simon.scanlogin.exception;

/**
 * Created by simon on 2017/5/13.
 */

public class UserNotLoginException extends Exception {
    public UserNotLoginException(){
        super("您当前未登录，请登录");
    }
}
