package com.android.plugindev.control;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.android.plugindev.proxyActivity.ActivityProxy;
import com.android.plugindev.config.PluginConfig;
import com.android.plugindev.manager.PluginDexManager;
import com.android.plugindev.reflect.Reflect;

/**
 * Created by evil.xu on 2015/3/27.
 * 
 * @author evil.xu
 * 
 *         负责转移插件的跳转目标<br>
 * @see Activity#startActivity(Intent)
 */
public class PluginInstrument extends Instrumentation {

	private static final String TAG = PluginInstrument.class.getSimpleName();
	Instrumentation pluginIn;
	Reflect instrumentRef;

	public PluginInstrument(Instrumentation pluginIn) {
		this.pluginIn = pluginIn;
		instrumentRef = Reflect.on(pluginIn);
	}

	/** @Override */
	public ActivityResult execStartActivity(Context who, IBinder contextThread,
			IBinder token, Activity target, Intent intent, int requestCode,
			Bundle options) {

		ComponentName componentName = intent.getComponent();
		if (componentName == null) {
			return instrumentRef.call("execStartActivity", who, contextThread,
					token, target, intent, requestCode, options).get();
		}
		String className = componentName.getClassName();
		if (className.contains("com.alipay.android") 
				|| className.equals("com.alipay.android.app.flybird.ui.window.FlyBirdWindowActivity")) {

		} else {
			intent.setClass(who, ActivityProxy.class);
		}

		Log.i(TAG, "Jump to " + className + "["
				+ PluginDexManager.finalApkPath + "]");

		intent.putExtra(PluginConfig.KEY_PLUGIN_DEX_PATH,
				PluginDexManager.finalApkPath);
		intent.putExtra(PluginConfig.KEY_PLUGIN_ACT_NAME, className);

		return instrumentRef.call("execStartActivity", who, contextThread,
				token, target, intent, requestCode, options).get();

	}

	/** @Override */
	public ActivityResult execStartActivity(Context who, IBinder contextThread,
			IBinder token, Activity target, Intent intent, int requestCode) {

		ComponentName componentName = intent.getComponent();
		if (componentName == null) {
			return instrumentRef.call("execStartActivity", who, contextThread,
					token, target, intent, requestCode).get();
		}
		String className = componentName.getClassName();
		if (className.contains("com.alipay.android") || className.equals("com.alipay.android.app.flybird.ui.window.FlyBirdWindowActivity")) {

		} else {
			intent.setClass(who, ActivityProxy.class);
		}

		Log.i(TAG, "Jump to " + className + "["
				+ PluginDexManager.finalApkPath + "]");

		intent.putExtra(PluginConfig.KEY_PLUGIN_DEX_PATH,
				PluginDexManager.finalApkPath);
		intent.putExtra(PluginConfig.KEY_PLUGIN_ACT_NAME, className);

		return instrumentRef.call("execStartActivity", who, contextThread,
				token, target, intent, requestCode).get();

	}

	@Override
	public void onStart() {
		pluginIn.onStart();
	}

	@Override
	public void onCreate(Bundle arguments) {
		pluginIn.onCreate(arguments);
	}

	@Override
	public void onDestroy() {
		pluginIn.onDestroy();
	}

	@Override
	public boolean onException(Object obj, Throwable e) {
		return pluginIn.onException(obj, e);
	}

	@Override
	public void callActivityOnCreate(Activity activity, Bundle icicle) {
		pluginIn.callActivityOnCreate(activity, icicle);
	}

	@Override
	public void callActivityOnNewIntent(Activity activity, Intent intent) {
		pluginIn.callActivityOnNewIntent(activity, intent);
	}

	@Override
	public void callApplicationOnCreate(Application app) {
		pluginIn.callApplicationOnCreate(app);
	}

	@Override
	public void callActivityOnDestroy(Activity activity) {
		pluginIn.callActivityOnDestroy(activity);
	}

	@Override
	public void callActivityOnPause(Activity activity) {
		pluginIn.callActivityOnDestroy(activity);
	}

}
