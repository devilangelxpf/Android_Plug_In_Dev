package com.android.plugindev.bean;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.android.plugindev.manager.PluginDexManager;

import dalvik.system.DexClassLoader;

/**
 * Created by evil.xu  on 2015/4/6.
 */
public class ApkInfo {

    public static final String TAG = ApkInfo.class.getSimpleName();

    /**
     * 插件的Application名
     */
    public String applicationName;
    /**
     * 插件的Application
     */
    public Application pluginApplication;
    /**
     * 插件路径
     */
    public String pluginPath;
    /**
     * 插件资源管理器
     */
    public AssetManager pluginAssets;
    /**
     * 插件资源
     */
    public Resources pluginRes;
    /**
     * 插件包信息
     */
    public PackageInfo pluginPkgInfo;
    /**
     * 插件加载器
     */
    public DexClassLoader pluginLoader;

    /**
     * 绑定apk路径
     * @param apkPath
     */
    public void attach(String apkPath){
        pluginPath = apkPath;
    }

    public void setPluginPkgInfo(PackageInfo pluginPkgInfo) {
        this.pluginPkgInfo = pluginPkgInfo;
    }

    public void setPluginRes(Resources pluginRes) {
        this.pluginRes = pluginRes;
    }

    public void setPluginAssets(AssetManager pluginAssets) {
        this.pluginAssets = pluginAssets;
    }

    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    public void setPluginApplication(Application pluginApplication) {
        this.pluginApplication = pluginApplication;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * 是否能够使用？
     * @return
     */
    public boolean canUse(){
        return pluginPkgInfo != null && pluginLoader != null && pluginPath != null;
    }

    /**
     * 仅供测试使用
     */
    public void debug(){
        Log.i(TAG,"Plugin Path = " + pluginPath );
        Log.i(TAG,"Plugin Resources = " + pluginRes);
        Log.i(TAG,"Plugin Assets = " + pluginAssets);
        Log.i(TAG,"Plugin Loader = " + pluginLoader);
        Log.i(TAG,"Plugin PackageInfo = " + pluginPkgInfo);
        Log.i(TAG,"Plugin Application name = " + applicationName);
        Log.i(TAG,"Plugin Application = " + pluginApplication);
    }

    /**
     * 与一个类加载器绑定
     * @param ctx
     */
    public void bindDexLoader(Context ctx){
        pluginLoader = PluginDexManager.getClassLoader(pluginPath, ctx, PluginDexManager.getSystemLoader() == null ? ctx.getClassLoader() : PluginDexManager.getSystemLoader());
    }

    /**
     * 擦除所有保留的信息
     * NOTE:不要在其它地方保留本类中对象的引用
     */
    public void recycle(){
        applicationName = null;
        pluginPkgInfo = null;
        pluginPath = null;
        pluginLoader = null;
        pluginAssets = null;
        pluginApplication = null;
        pluginRes = null;
    }

}

