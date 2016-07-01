package com.android.plugindev.tools;

/**
 * Created by evil.xu  on 2015/4/3.
 */
public interface PluginLoadListener {
    void onLoadStart();
    void onLoading();
    void onLoadFinish();
}
