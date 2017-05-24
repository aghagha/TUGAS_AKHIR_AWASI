package com.aghagha.tagg.utilities;

import android.app.Application;

/**
 * Created by aghagha on 19/05/2017.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.setDefaultFont(this, "DEFAULT", "fonts/Lato-Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
        TypefaceUtil.setDefaultFont(this, "MONOSPACE", "fonts/Lato-Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
        TypefaceUtil.setDefaultFont(this, "SERIF", "fonts/Lato-Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
        TypefaceUtil.setDefaultFont(this, "SANS_SERIF", "fonts/Lato-Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
    }
}