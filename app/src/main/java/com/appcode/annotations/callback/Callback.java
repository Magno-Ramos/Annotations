package com.appcode.annotations.callback;

public abstract  class Callback<T> {

    public abstract void onSuccess(T t);

    public void onError(String messsage) {


    }
}
