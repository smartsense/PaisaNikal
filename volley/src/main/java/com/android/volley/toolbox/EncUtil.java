package com.android.volley.toolbox;

/**
 * Created by Viral on 08-07-2017.
 */



public class EncUtil {

    static {
        System.loadLibrary("keys");
    }

    //public native String getNativeKey();


    private static EncUtil utils;
    private String encryptionKey;

    private EncUtil() {

    }

    public static EncUtil getInstance() {
        if (utils == null)
            utils = new EncUtil();
        return utils;
    }
}
