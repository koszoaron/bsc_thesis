package com.github.koszoaron.maguscada.util;

import android.util.Log;

public class Logger {
    private String tag;

    public Logger(String tag) {
        this.tag = tag;
    }
    
    public void e(String message) {
        Log.e(tag, "E - " + message);
    }
    
    public void w(String message) {
        Log.w(tag, "W - " + message);
    }
    
    public void d(String message) {
        Log.d(tag, "D - " + message);
    }
    
    public void r(String message) {
        Log.e(tag, "R - " + message);
    }
    
    public void i(String message) {
        Log.e(tag, "I - " + message);
    }

    public void v(String message) {
        Log.v(tag, "V - " + message);
    }
}
