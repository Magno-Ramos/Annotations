package com.appcode.annotations.service;

import com.appcode.annotations.callback.Callback;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface Service<T> {

    void fetchAll (Callback<LiveData<List<T>>> callback);

    void save(T t, Callback<T> callback);

    void edit(T t, Callback<T> callback);

    void delete(T t, Callback<T> callback);
}
