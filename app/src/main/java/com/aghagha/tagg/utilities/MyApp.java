package com.aghagha.tagg.utilities;

import android.app.Application;

/**
 * Created by aghagha on 19/05/2017.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Lato-Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
    }
}