package com.squareplaces.squareplaces;

/**
 * Created by WHITE ROSE on 3/24/2017.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceOverride.setDefaultFont(this, getString(R.string.font_system_default), getString(R.string.font_regular));
    }
}
