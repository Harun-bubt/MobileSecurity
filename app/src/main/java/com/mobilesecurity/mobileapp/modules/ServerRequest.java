package com.mobilesecurity.mobileapp.modules;

/**
 * Created by Dell on 7/13/2017.
 */

public class ServerRequest {

    private String operation;
    private User user;

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setUser(User user) {
        this.user = user;
    }
}