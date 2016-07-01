package com.android.plugindev.manager;

import android.content.Context;

import com.android.plugindev.nativeso.NativeLibUnpacker;
import com.android.plugindev.reflect.Reflect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by evil.xu  on 2015/3/24.
 * 插件的核心加载器<br>
 * 已支持Native
 */
public class PluginDexManager extends DexClassLoader {

    private static final Map<String, PluginDexManager> pluginLoader = new ConcurrentHashMap<String, PluginDexManager>();
    public static String finalApkPath;

    protected PluginDexManager(String dexPath, String optimizedDirectory,
                               String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
        finalApkPath = dexPath;
        NativeLibUnpacker.unPackSOFromApk(dexPath, libraryPath);
    }

    /**
     * 返回apk对应的加载器，不会重复加载同样的资源
     */
    public static PluginDexManager getClassLoader(String dexPath, Context cxt,
                                                  ClassLoader parent) {
        PluginDexManager pluginDexLoader = pluginLoader.get(dexPath);
        if (pluginDexLoader == null) {
            // 获取到app的启动路径
            final String dexOutputPath = cxt
                    .getDir("plugin", Context.MODE_PRIVATE).getAbsolutePath();
            final String libOutputPath = cxt
                    .getDir("plugin_lib", Context.MODE_PRIVATE).getAbsolutePath();

            pluginDexLoader = new PluginDexManager(dexPath, dexOutputPath, libOutputPath, parent);
            pluginLoader.put(dexPath, pluginDexLoader);
        }
        return pluginDexLoader;
    }

    public static ClassLoader getSystemLoader(){
        Context context = Reflect.on("android.app.ActivityThread").call("currentActivityThread").call("getSystemContext").get();
        return context == null ? null : context.getClassLoader();
    }
}
