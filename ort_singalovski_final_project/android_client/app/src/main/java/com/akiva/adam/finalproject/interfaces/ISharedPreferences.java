package com.akiva.adam.finalproject.interfaces;

public interface ISharedPreferences {

    <T> void writeToPreferences(String key, T type) throws IllegalArgumentException;

    <T> T readFromStorage(String key, Class<T> cls) throws IllegalArgumentException;
}
