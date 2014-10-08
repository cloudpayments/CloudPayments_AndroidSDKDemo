package ru.cloudpayments.sdk.demo.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Nastya on 28.09.2014.
 */
public class Logger {

    public static void log(String msg) {
        Log.d("cloudPayments", msg);
    }

    public static void log(Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        Log.e("cloudPayments", errors.toString());
    }


}
