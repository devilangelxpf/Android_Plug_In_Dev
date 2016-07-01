package com.android.plugindev.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by evil.xu  on 2015/4/4.
 */
public class PluginInfo {

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public Drawable getApkIcon() {
        return apkIcon;
    }

    public void setApkIcon(Drawable apkIcon) {
        this.apkIcon = apkIcon;
    }

    /**
     * apk路径
     */
    String apkPath;
    /**
     * apk图标
     */
    Drawable apkIcon;

}
