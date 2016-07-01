package com.android.plugindev;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import com.android.plugindev.config.PluginConfig;
import com.android.plugindev.proxyActivity.ActivityProxy;
import com.android.plugindev.proxyservice.ProxyService;

/**
 * Created by evil.xu on 2015/3/24.
 */
public class PluginApkLoader {

	/**
	 * 根据路径启动apk
	 * @param context
	 * @param pluginPath 待启动apk路径
     */
	public static void startApk(Context context, String pluginPath) {
		Intent i = new Intent(context, ActivityProxy.class);
		Bundle bundle = new Bundle();
		bundle.putString(PluginConfig.KEY_PLUGIN_DEX_PATH, pluginPath);
		i.putExtras(bundle);
		context.startActivity(i);
	}

	/**
	 * 根据路径启动apk,并携带数据
	 * @param context
	 * @param pluginPath 待启动apk路径
	 * @param args 携带数据
     */
	public static void startApk(Context context, String pluginPath,
			Bundle args) {
		Intent i = new Intent(context, ActivityProxy.class);
		args.putString(PluginConfig.KEY_PLUGIN_DEX_PATH, pluginPath);
		i.putExtras(args);
		context.startActivity(i);
	}

	/**
	 * 根据路径启动apk，并打开指定页面
	 * @param context
	 * @param pluginPath 待启动apk路径
	 * @param activityName Activity全称
     */
	public static void startApkByActivityName(Context context, String pluginPath,
			String activityName) {
		Intent i = new Intent(context, ActivityProxy.class);
		Bundle bundle = new Bundle();
		bundle.putString(PluginConfig.KEY_PLUGIN_DEX_PATH, pluginPath);
		bundle.putString(PluginConfig.DEF_PLUGIN_CLASS_NAME, activityName);
		i.putExtras(bundle);
		context.startActivity(i);
	}

	/**
	 * 根据路径启动apk，并打开指定页面,并携带数据
	 * @param context
	 * @param pluginPath 待启动apk路径
	 * @param activityName Activity全称
     * @param args 携带数据
     */
	public static void startApkByActivityName(Context context, String pluginPath,
			String activityName, Bundle args) {
		Intent i = new Intent(context, ActivityProxy.class);
		args.putString(PluginConfig.KEY_PLUGIN_DEX_PATH, pluginPath);
		args.putString(PluginConfig.DEF_PLUGIN_CLASS_NAME, activityName);
		i.putExtras(args);
		context.startActivity(i);
	}

	/**
	 * 根据路径启动apk，并拉起对应的service
	 * @param context
	 * @param pluginPath 待启动apk路径
	 * @param serviceName Service全称
     */
	public static void startService(Context context, String pluginPath,
			String serviceName) {
		Intent i = new Intent(context, ProxyService.class);
		ProxyService.SERVICE_APK_PATH = pluginPath;
		ProxyService.SERVICE_CLASS_NAME = serviceName;
		context.startService(i);
	}

	/**
	 *根据路径启动apk，并拉起对应的service,并携带数据
	 * @param context
	 * @param pluginPath 待启动apk路径
	 * @param serviceName Service全称
	 * @param args 携带数据
	 */
	public static void startService(Context context, String pluginPath,
									String serviceName, Bundle args) {
		Intent i = new Intent(context, ProxyService.class);
		i.putExtras(args);
		ProxyService.SERVICE_APK_PATH = pluginPath;
		ProxyService.SERVICE_CLASS_NAME = serviceName;
		context.startService(i);
	}

	/**
	 * 根据路径启动apk，绑定对应的service
	 * @param context
	 * @param pluginPath 待启动apk路径
	 * @param serviceName Service全称
	 * @param intent 跳转intent
	 * @param conn 连接对象
     * @param flags bind操作选项
     */
	public static void bindService(Context context, String pluginPath,
			String serviceName, Intent intent, ServiceConnection conn, int flags) {
		Intent i = new Intent(context, ProxyService.class);
		ProxyService.SERVICE_APK_PATH = pluginPath;
		ProxyService.SERVICE_CLASS_NAME = serviceName;
		i.setAction(intent.getAction());
		i.setPackage(intent.getPackage());
		context.bindService(i, conn, flags);
	}

	/**
	 * 解除绑定
	 * @param context
	 * @param conn 连接对象
     */
	public static void unBindService(Context context, ServiceConnection conn) {
		context.unbindService(conn);
	}

}
