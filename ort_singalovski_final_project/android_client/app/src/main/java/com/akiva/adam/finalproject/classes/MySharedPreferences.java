package com.akiva.adam.finalproject.classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.akiva.adam.finalproject.interfaces.ISharedPreferences;

/**
 * A class used to write and read data from SharedPreferences
 */
public class MySharedPreferences implements ISharedPreferences {

    private final Context context;
    private final SharedPreferences sharedPreferences;

    // Static variables holding string values
    private static final String PREFERENCES_NAME = "MySharedPreferences";
    private static final String TYPE_MUST_BE = "Type must be either Boolean, Float, Integer, Long or String";
    private static final String CLASS_MUST_BE = "Class must be either Boolean, Float, Integer, Long or String";

    // Static variables holding the default return types
    private static final Boolean DEFAULT_BOOLEAN_VALUE = false;
    private static final Float DEFAULT_FLOAT_VALUE = -1F;
    private static final Integer DEFAULT_INTEGER_VALUE = -1;
    private static final Long DEFAULT_LONG_VALUE = -1L;

    /**
     * A constructor for MySharedPreferences class
     *
     * @param context Application context
     */
    public MySharedPreferences(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * A method to write generic data to shared preferences
     *
     * @param key  String value holding the index
     * @param type The data to write
     * @param <T>  Generic type for the type of data (Boolean, Float, Integer, Long, String)
     * @throws IllegalArgumentException Thrown if the data type is mismatched
     */
    public <T> void writeToPreferences(String key, T type) throws IllegalArgumentException {
        if (type instanceof Boolean) {
            sharedPreferences.edit().putBoolean(key, (Boolean) type).apply();
        } else if (type instanceof Float) {
            sharedPreferences.edit().putFloat(key, (Float) type).apply();
        } else if (type instanceof Integer) {
            sharedPreferences.edit().putInt(key, (Integer) type).apply();
        } else if (type instanceof Long) {
            sharedPreferences.edit().putLong(key, (Long) type).apply();
        } else if (type instanceof String) {
            sharedPreferences.edit().putString(key, (String) type).apply();
        } else {
            throw new IllegalArgumentException(TYPE_MUST_BE);
        }
    }

    /**
     * A method used to read generic data from shared preferences
     *
     * @param key String value holding the index
     * @param cls The class of data
     * @param <T> Generic type for the type of data (Boolean, Float, Integer, Long, String)
     * @return T Generic data
     * @throws IllegalArgumentException Thrown if the data type is mismatched
     */
    public <T> T readFromStorage(String key, Class<T> cls) throws IllegalArgumentException {
        if (sharedPreferences.contains(key)) {
            if (cls == Boolean.class) {
                return cls.cast(sharedPreferences.getBoolean(key, DEFAULT_BOOLEAN_VALUE));
            } else if (cls == Float.class) {
                return cls.cast(sharedPreferences.getFloat(key, DEFAULT_FLOAT_VALUE));
            } else if (cls == Integer.class) {
                return cls.cast(sharedPreferences.getInt(key, DEFAULT_INTEGER_VALUE));
            } else if (cls == Long.class) {
                return cls.cast(sharedPreferences.getLong(key, DEFAULT_LONG_VALUE));
            } else if (cls == String.class) {
                return cls.cast(sharedPreferences.getString(key, null));
            } else {
                throw new IllegalArgumentException(CLASS_MUST_BE);
            }
        }
        return null;
    }
}
