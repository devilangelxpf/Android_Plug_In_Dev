package com.android.plugindev.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.android.plugindev.reflect.Reflect;
import com.android.plugindev.reflect.ReflectException;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Created by evil.xu on 2015/3/26.
 * <p/>
 * 插件的控制器<br>
 * 派发插件事件和控制插件生命周期
 */
public class PluginActivityControl implements PluginActivityCallback {

    Activity proxy;// 代理Activity
    Activity plugin;// 插件Activity
    Reflect proxyRef;// 指向代理Activity的反射工具类
    Reflect pluginRef;// 指向插件Activity的反射工具类
    Application app;// 分派给插件的Application
    Instrumentation instrument;

    /**
     * @param proxy  代理Activity
     * @param plugin 插件Activity
     */
    public PluginActivityControl(Activity proxy, Activity plugin) {
        this(proxy, plugin, null);

    }

    /**
     * @param proxy  代理Activity
     * @param plugin 插件Activity
     * @param app    分派给插件的Application
     */
    public PluginActivityControl(Activity proxy, Activity plugin,
                                 Application app) {
        this.proxy = proxy;
        this.plugin = plugin;
        this.app = app;

        instrument = new Instrumentation();

        // 使反射工具类指向相应的对象
        proxyRef = Reflect.on(proxy);
        pluginRef = Reflect.on(plugin);
    }

    public void dispatchProxyToPlugin() {

        if (plugin.getBaseContext() != null)
            return;
        try {
            // Finals 修改以前的注入方式，采用原生的方式
            Instrumentation instrumentation = proxyRef.get("mInstrumentation");
            if (Build.VERSION.SDK_INT < 11) {
                pluginRef.call(
                        // 方法名
                        "attach",
                        // Context context
                        proxy,
                        // ActivityThread aThread
                        proxyRef.get("mMainThread"),
                        // Instrumentation instr
                        new PluginInstrument(instrumentation),
                        // IBinder token
                        proxyRef.get("mToken"),
                        // Application application
                        app == null ? proxy.getApplication() : app,
                        // Intent intent
                        proxy.getIntent(),
                        // ActivityInfo info
                        proxyRef.get("mActivityInfo"),
                        // CharSequence title
                        proxy.getTitle(),
                        // Activity parent
                        proxy.getParent(),
                        // String id
                        proxyRef.get("mEmbeddedID"),
                        // NonConfigurationInstances
                        // lastNonConfigurationInstances
                        proxy.getLastNonConfigurationInstance(),
                        // Configuration config
                        proxyRef.get("mCurrentConfig"));

            } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 21) {
                pluginRef.call(
                        // 方法名
                        "attach",
                        // Context context
                        proxy,
                        // ActivityThread aThread
                        proxyRef.get("mMainThread"),
                        // Instrumentation instr
                        new PluginInstrument(instrumentation),
                        // IBinder token
                        proxyRef.get("mToken"),
                        // int ident
                        proxyRef.get("mEmbeddedID") == null ? 0 : proxyRef
                                .get("mEmbeddedID"),
                        // Application application
                        app == null ? proxy.getApplication() : app,
                        // Intent intent
                        proxy.getIntent(),
                        // ActivityInfo info
                        proxyRef.get("mActivityInfo"),
                        // CharSequence title
                        proxy.getTitle(),
                        // Activity parent
                        proxy.getParent(),
                        // String id
                        proxyRef.get("mEmbeddedID"),
                        // NonConfigurationInstances
                        // lastNonConfigurationInstances
                        proxy.getLastNonConfigurationInstance(),
                        // Configuration config
                        proxyRef.get("mCurrentConfig"));

            } else if (Build.VERSION.SDK_INT >= 21
                    && Build.VERSION.SDK_INT < 22) {
                pluginRef.call(
                        // 方法名
                        "attach",
                        // Context context
                        proxy,
                        // ActivityThread aThread
                        proxyRef.get("mMainThread"),
                        // Instrumentation instr
                        new PluginInstrument(instrumentation),
                        // IBinder token
                        proxyRef.get("mToken"),
                        // int ident
                        proxyRef.get("mEmbeddedID") == null ? 0 : proxyRef
                                .get("mEmbeddedID"),
                        // Application application
                        app == null ? proxy.getApplication() : app,
                        // Intent intent
                        proxy.getIntent(),
                        // ActivityInfo info
                        proxyRef.get("mActivityInfo"),
                        // CharSequence title
                        proxy.getTitle(),
                        // Activity parent
                        proxy.getParent(),
                        // String id
                        proxyRef.get("mEmbeddedID"),
                        // NonConfigurationInstances
                        // lastNonConfigurationInstances
                        proxy.getLastNonConfigurationInstance(),
                        // Configuration config
                        proxyRef.get("mCurrentConfig"), null);
            } else if (Build.VERSION.SDK_INT >= 22 && Build.VERSION.SDK_INT < 24) {
                pluginRef.call(
                        // 方法名
                        "attach",
                        // Context context
                        proxy,
                        // ActivityThread aThread
                        proxyRef.get("mMainThread"),
                        // Instrumentation instr
                        new PluginInstrument(instrumentation),
                        // IBinder token
                        proxyRef.get("mToken"),
                        // int ident
                        proxyRef.get("mEmbeddedID") == null ? 0 : proxyRef
                                .get("mEmbeddedID"),
                        // Application application
                        app == null ? proxy.getApplication() : app,
                        // Intent intent
                        proxy.getIntent(),
                        // ActivityInfo info
                        proxyRef.get("mActivityInfo"),
                        // CharSequence title
                        proxy.getTitle(),
                        // Activity parent
                        proxy.getParent(),
                        // String id
                        proxyRef.get("mEmbeddedID"),
                        // NonConfigurationInstances
                        // lastNonConfigurationInstances
                        proxy.getLastNonConfigurationInstance(),
                        // Configuration config
                        proxyRef.get("mCurrentConfig"), null, null);
            } else if (Build.VERSION.SDK_INT >= 24) {
                //TODO 兼容Android N
            }
            pluginRef.set("mWindow", proxy.getWindow());
            plugin.getWindow().setCallback(plugin);
            Reflect.on(proxy.getBaseContext()).call("setOuterContext", plugin);

        } catch (ReflectException e) {
            e.printStackTrace();
        }

    }

    /**
     * @return 插件的Activity
     */
    public Activity getPlugin() {
        return plugin;
    }

    /**
     * 设置插件的Activity
     *
     * @param plugin
     */
    public void setPlugin(Activity plugin) {
        this.plugin = plugin;
        proxyRef = Reflect.on(plugin);
    }

    /**
     * 得到代理的Activity
     *
     * @return
     */
    public Activity getProxy() {
        return proxy;
    }

    /**
     * 设置代理的Activity
     *
     * @param proxy
     */
    public void setProxy(Activity proxy) {
        this.proxy = proxy;
        proxyRef = Reflect.on(proxy);
    }

    /**
     * @return 代理Activity的反射工具类
     */
    public Reflect getProxyRef() {
        return proxyRef;
    }

    /**
     * @return 插件Activity的反射工具类
     */
    public Reflect getPluginRef() {
        return pluginRef;
    }

    /**
     * 执行插件的onCreate方法
     *
     * @param saveInstance
     * @see Activity#onCreate(Bundle)
     */
    @Override
    public void callOnCreate(Bundle saveInstance) {
        instrument.callActivityOnCreate(plugin, saveInstance);
    }

    /**
     * 执行插件的onStart方法
     *
     * @see Activity#onStart()
     */
    @Override
    public void callOnStart() {
        instrument.callActivityOnStop(plugin);
    }

    /**
     * 执行插件的onResume方法
     *
     * @see Activity#onResume()
     */
    @Override
    public void callOnResume() {
        instrument.callActivityOnResume(plugin);
    }

    /**
     * 执行插件的onDestroy方法
     *
     * @see Activity#onDestroy()
     */
    @Override
    public void callOnDestroy() {
        instrument.callActivityOnDestroy(plugin);
    }

    /**
     * 执行插件的onStop方法
     *
     * @see Activity#onStop()
     */
    @Override
    public void callOnStop() {
        instrument.callActivityOnStop(plugin);
    }

    /**
     * 执行插件的onRestart方法
     *
     * @see Activity#onRestart()
     */
    @Override
    public void callOnRestart() {
        instrument.callActivityOnRestart(plugin);
    }

    /**
     * 执行插件的onSaveInstanceState方法
     *
     * @param outState
     * @see Activity#onSaveInstanceState(Bundle)
     */
    @Override
    public void callOnSaveInstanceState(Bundle outState) {
        getPluginRef().call("onSaveInstanceState", outState);
    }

    /**
     * 执行插件的onRestoreInstanceState方法
     *
     * @param savedInstanceState
     * @see Activity#onRestoreInstanceState(Bundle)
     */
    @Override
    public void callOnRestoreInstanceState(Bundle savedInstanceState) {
        getPluginRef().call("onRestoreInstanceState", savedInstanceState);
    }

    /**
     * 执行插件的onStop方法
     *
     * @see Activity#onStop()
     */
    @Override
    public void callOnPause() {
        instrument.callActivityOnPause(plugin);
    }

    /**
     * 执行插件的onBackPressed方法
     *
     * @see Activity#onBackPressed()
     */
    @Override
    public void callOnBackPressed() {
        plugin.onBackPressed();
    }

    /**
     * 执行插件的onKeyDown方法
     *
     * @param keyCode
     * @param event
     * @return
     * @see Activity#onKeyDown(int, KeyEvent)
     */
    @Override
    public boolean callOnKeyDown(int keyCode, KeyEvent event) {
        return plugin.onKeyDown(keyCode, event);
    }

    // Finals ADD 修复Fragment BUG
    @SuppressLint("NewApi")
    @Override
    public void callDump(String prefix, FileDescriptor fd, PrintWriter writer,
                         String[] args) {
        plugin.dump(prefix, fd, writer, args);
    }

    @Override
    public void callOnConfigurationChanged(Configuration newConfig) {
        plugin.onConfigurationChanged(newConfig);
    }

    @Override
    public void callOnPostResume() {
        getPluginRef().call("onPostResume");

    }

    @Override
    public void callOnDetachedFromWindow() {
        plugin.onDetachedFromWindow();
    }

    @Override
    public View callOnCreateView(String name, Context context,
                                 AttributeSet attrs) {
        return plugin.onCreateView(name, context, attrs);
    }

    @SuppressLint("NewApi")
    @Override
    public View callOnCreateView(View parent, String name, Context context,
                                 AttributeSet attrs) {
        return plugin.onCreateView(parent, name, context, attrs);
    }

    @Override
    public void callOnNewIntent(Intent intent) {
        instrument.callActivityOnNewIntent(plugin, intent);
    }

}
