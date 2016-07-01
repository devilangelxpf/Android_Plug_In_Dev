package com.android.plugindev.manager;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.android.plugindev.tools.PluginTool;
import com.android.plugindev.bean.ApkInfo;
import com.android.plugindev.reflect.Reflect;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * apk dex容器类
 */
public final class ApkManager {
	private static final String TAG = ApkManager.class.getSimpleName();
	private static final Map<String, ApkInfo> apks = new ConcurrentHashMap<String, ApkInfo>();

	public static ApkInfo get(String apkPath) {
		ApkInfo apk;
		apk = apks.get(apkPath);
		if (apk == null) {
			apk = new ApkInfo();
			apk.attach(apkPath);
			apks.put(apkPath, apk);
		}
		return apk;
	}

	/**
	 * 填充activity
	 * 
	 * @param apk
	 * @param ctx
	 */
	public static void initApk(ApkInfo apk, Context ctx) {
		String apkPath = apk.pluginPath;
		File file = new File(apkPath);
		Log.i(TAG, "Init a plugin on" + apkPath);
		if (!apk.canUse()) {
			Log.i(TAG, "Plugin is not been init,init it now！");
			fillPluginInfo(apk, ctx);
			fillPluginRes(apk, ctx);
			fillPluginApplication(apk, ctx);
		} else {
			Log.i(TAG, "Plugin have been init.");
		}

	}

	private static void fillPluginInfo(ApkInfo apk, Context ctx) {
		PackageInfo info = null;
		try {
			info = PluginTool.getAppInfo(ctx, apk.pluginPath);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		apk.setPluginPkgInfo(info);
		apk.setApplicationName(info.applicationInfo.className);
	}

	private static void fillPluginRes(ApkInfo apk, Context ctx) {
		try {
			AssetManager assetManager = AssetManager.class.newInstance();
			Reflect assetRef = Reflect.on(assetManager);
			assetRef.call("addAssetPath", apk.pluginPath);
			Log.i(TAG, "Assets = " + assetManager);
			apk.setPluginAssets(assetManager);

			Resources pluginRes = new Resources(assetManager, ctx.getResources().getDisplayMetrics(), ctx.getResources().getConfiguration());
			Log.i(TAG, "Res = " + pluginRes);
			apk.setPluginRes(pluginRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void fillPluginApplication(ApkInfo apk, Context ctx) {
		String applicationName = apk.applicationName;
		if (applicationName == null || "".equals(applicationName))
			return;

		ClassLoader loader = apk.pluginLoader;
		try {
			Application pluginApp = (Application) loader.loadClass(applicationName).newInstance();
			Reflect.on(pluginApp).call("attachBaseContext", ctx.getApplicationContext());
			apk.pluginApplication = pluginApp;
			pluginApp.onCreate();

		} catch (InstantiationException e) {
			// throw new PluginCreateFailedException(e.getMessage());
		} catch (IllegalAccessException e) {
			// throw new PluginCreateFailedException(e.getMessage());
		} catch (ClassNotFoundException e) {
			// throw new PluginCreateFailedException(e.getMessage());
		}
	}

	public static void clearApk() {
		apks.clear();
	}

}
