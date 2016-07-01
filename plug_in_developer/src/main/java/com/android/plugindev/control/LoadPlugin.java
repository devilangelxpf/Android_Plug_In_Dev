package com.android.plugindev.control;

import android.app.Activity;

import com.android.plugindev.config.ActivityPluginConfig;


/**
 * Created by evil.xu  on 2015/3/27.
 */
public interface LoadPlugin {
    /**
     * 根据apk路径加载一个插件
     * @param proxyParent 代理activity
     * @param apkPath apk路径
     * @return 插件的bean
     */
    ActivityPluginConfig loadPlugin(Activity proxyParent, String apkPath);


    /**
     * 根据apk路径加载一个插件
     * @param proxyParent 代理activity
     * @param apkPath apk路径
     * @param activityName 启动的activity名
     * @return
     */
    ActivityPluginConfig loadPlugin(Activity proxyParent, String apkPath, String activityName);

    /**
     * 装载一个插件<br>
     * 一个刚刚初始化的插件只有apk路径信息，
     * 本操作能够使其变得完整(装载资源，类加载器等)。
     * @param plugin 需要装载的插件
     */
    void fillPlugin(ActivityPluginConfig plugin);

}
