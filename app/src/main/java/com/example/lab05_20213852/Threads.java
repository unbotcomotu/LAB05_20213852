package com.example.lab05_20213852;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Threads extends Application {
    public ExecutorService executorService= Executors.newFixedThreadPool(4);
}
