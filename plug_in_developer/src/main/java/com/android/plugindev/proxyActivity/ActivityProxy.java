package com.android.plugindev.proxyActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import com.android.plugindev.control.LoadPlugin;
import com.android.plugindev.config.PluginConfig;
import com.android.plugindev.config.ActivityPluginConfig;
import com.android.plugindev.control.PluginActivityCallback;
import com.android.plugindev.control.PluginActivityControl;
import com.android.plugindev.manager.ApkManager;
import com.android.plugindev.manager.CallbackManager;
import com.android.plugindev.proxyservice.ProxyService;
import com.android.plugindev.tools.PluginTool;

import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Created by evil.xu on 2015/3/27.
 */
public class ActivityProxy extends Activity implements LoadPlugin {

	private static final String TAG = ActivityProxy.class.getSimpleName();
	private ActivityPluginConfig remotePlugin;

	@Override
	public ActivityPluginConfig loadPlugin(Activity ctx, String apkPath) {
		// 插件必须要确认有没有经过初始化，不然只是空壳
		remotePlugin = new ActivityPluginConfig(ctx, apkPath);
		return remotePlugin;

	}

	@Override
	public ActivityPluginConfig loadPlugin(Activity ctx, String apkPath, String activityName) {
		ActivityPluginConfig plugin = loadPlugin(ctx, apkPath);
		plugin.setTopActivityName(activityName);
		fillPlugin(plugin);
		return plugin;
	}

	/**
	 * 装载插件
	 * 
	 * @param plugin
	 */
	@Override
	public void fillPlugin(ActivityPluginConfig plugin) {
		String apkPath = plugin.getPluginPath();
		File apk = new File(apkPath);

		if (!this.remotePlugin.from().canUse()) {
			Log.i(TAG, "Plugin is not been init,init it now！");
			ApkManager.initApk(plugin.from(), this);
			// remotePlugin.from().debug();

		} else {
			Log.i(TAG, "Plugin have been init.");
		}
		fillPluginTheme(plugin);
		fillPluginActivity(plugin);

	}

	private void fillPluginTheme(ActivityPluginConfig plugin) {

		Theme pluginTheme = plugin.from().pluginRes.newTheme();
		pluginTheme.setTo(super.getTheme());
		plugin.setTheme(pluginTheme);

		PackageInfo packageInfo = plugin.from().pluginPkgInfo;
		String mClass = plugin.getTopActivityName();

		Log.i(TAG, "Before fill Plugin 's Theme,We check the plugin:info = " + packageInfo + "topActivityName = " + mClass);

		int defaultTheme = packageInfo.applicationInfo.theme;
		ActivityInfo curActivityInfo = null;
		for (ActivityInfo a : packageInfo.activities) {
			if (a.name.equals(mClass)) {
				curActivityInfo = a;
				if (a.theme != 0) {
					defaultTheme = a.theme;
				} else if (defaultTheme != 0) {
					// ignore
				} else {
					// 支持不同系统的默认Theme
					if (Build.VERSION.SDK_INT >= 14) {
						defaultTheme = android.R.style.Theme_DeviceDefault;
					} else {
						defaultTheme = android.R.style.Theme;
					}
				}
				break;
			}
		}

		pluginTheme.applyStyle(defaultTheme, true);
		setTheme(defaultTheme);
		if (curActivityInfo != null) {
			getWindow().setSoftInputMode(curActivityInfo.softInputMode);
		}

		if (PluginConfig.usePluginTitle) {
			CharSequence title = null;
			try {
				title = PluginTool.getAppName(this, plugin.getPluginPath());
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
			if (title != null)
				setTitle(title);
		}

	}

	/**
	 * 装载插件的Activity
	 * 
	 * @param plugin
	 */
	@SuppressLint("NewApi")
	private void fillPluginActivity(ActivityPluginConfig plugin) {
		try {
			String top = plugin.getTopActivityName();
			if (top == null) {
				top = plugin.from().pluginPkgInfo.activities[0].name;
				plugin.setTopActivityName(top);
			}
			Activity myPlugin = (Activity) plugin.from().pluginLoader.loadClass(plugin.getTopActivityName()).newInstance();
			plugin.setCurrentPluginActivity(myPlugin);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle pluginMessage = getIntent().getExtras();
		/**
		 * 获取启动可能传进来的actionName，dexPath
		 */
		String pluginActivityName = null;
		String pluginDexPath = null;
		if (pluginMessage != null) {
			pluginActivityName = pluginMessage.getString(PluginConfig.KEY_PLUGIN_ACT_NAME);
			if (!PluginTool.isNotEmpt(pluginActivityName)) {
				pluginActivityName = PluginConfig.DEF_PLUGIN_CLASS_NAME;
			}
			pluginDexPath = pluginMessage.getString(PluginConfig.KEY_PLUGIN_DEX_PATH);
			if (!PluginTool.isNotEmpt(pluginDexPath)) {
				pluginDexPath = PluginConfig.DEF_PLUGIN_DEX_PATH;
			}
		}
		/**
		 * 加载插件dex
		 */
		remotePlugin = loadPlugin(ActivityProxy.this, pluginDexPath);
		/**
		 * 如果没有设置跳转，默认跳转到第一个Activity
		 */
		remotePlugin.setTopActivityName(pluginActivityName);
		/**
		 * 填充插件，初始化activity
		 */
		fillPlugin(remotePlugin);
		// remotePlugin.from().debug();
		/**
		 * 设置控制器
		 */
		PluginActivityControl control = new PluginActivityControl(ActivityProxy.this, remotePlugin.getCurrentPluginActivity(), remotePlugin.from().pluginApplication);
		remotePlugin.setControl(control);
		control.dispatchProxyToPlugin();
		try {
			control.callOnCreate(savedInstanceState);
			CallbackManager.callAllOnCreate(savedInstanceState);
		} catch (Exception e) {
			processError(e);
		}

	}

	private void processError(Exception e) {
		e.printStackTrace();
	}

	@Override
	public Resources getResources() {
		if (remotePlugin == null)
			return super.getResources();
		return remotePlugin.from().pluginRes == null ? super.getResources() : remotePlugin.from().pluginRes;
	}

	@Override
	public Theme getTheme() {
		if (remotePlugin == null)
			return super.getTheme();
		return remotePlugin.getTheme() == null ? super.getTheme() : remotePlugin.getTheme();
	}

	@Override
	public AssetManager getAssets() {
		if (remotePlugin == null)
			return super.getAssets();
		return remotePlugin.from().pluginAssets == null ? super.getAssets() : remotePlugin.from().pluginAssets;
	}

	@Override
	public ClassLoader getClassLoader() {
		if (remotePlugin == null) {
			return super.getClassLoader();
		}
		if (remotePlugin.from().canUse()) {
			return remotePlugin.from().pluginLoader;
		}
		return super.getClassLoader();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (remotePlugin == null) {
			return;
		}
		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {
			caller.callOnResume();
			CallbackManager.callAllOnResume();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (remotePlugin == null) {
			return;
		}

		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {

			try {
				caller.callOnStart();
				CallbackManager.callAllOnStart();
			} catch (Exception e) {

				processError(e);

			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (remotePlugin == null) {
			return;
		}
		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {

			{
				try {
					caller.callOnDestroy();
					CallbackManager.callAllOnDestroy();
				} catch (Exception e) {
					processError(e);
				}
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (remotePlugin == null) {
			return;
		}
		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {

			try {
				caller.callOnPause();
				CallbackManager.callAllOnPause();
			} catch (Exception e) {
				processError(e);
			}
		}
	}

	@Override
	public void onBackPressed() {

		if (remotePlugin == null) {
			super.onBackPressed();
		}

		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {
			try {
				caller.callOnBackPressed();
				CallbackManager.callAllOnBackPressed();
			} catch (Exception e) {
				processError(e);
			}

		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (remotePlugin == null) {
			return;
		}
		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {

			{
				try {
					caller.callOnStop();
					CallbackManager.callAllOnStop();
				} catch (Exception e) {
					processError(e);
				}

			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (remotePlugin == null) {
			return;
		}

		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {
			try {
				caller.callOnRestart();
				CallbackManager.callAllOnRestart();
			} catch (Exception e) {
				processError(e);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (remotePlugin == null) {
			return super.onKeyDown(keyCode, event);
		}
		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {

			CallbackManager.callAllOnKeyDown(keyCode, event);
			return caller.callOnKeyDown(keyCode, event);

		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public ComponentName startService(Intent service) {
		// TODO:转移Service跳转目标
		ProxyService.SERVICE_CLASS_NAME = service.getComponent().getClassName();
		service.setClass(this, ProxyService.class);
		return super.startService(service);
	}

	@SuppressLint("NewApi")
	@Override
	public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
		super.dump(prefix, fd, writer, args);
		if (remotePlugin == null) {
			return;
		}
		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {
			caller.callDump(prefix, fd, writer, args);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (remotePlugin == null) {
			return;
		}
		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {
			caller.callOnConfigurationChanged(newConfig);
		}
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		if (remotePlugin == null) {
			return;
		}
		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {
			caller.callOnPostResume();
		}
	}

	@Override
	public void onDetachedFromWindow() {
		if (remotePlugin == null) {
			return;
		}
		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {
			caller.callOnDetachedFromWindow();
		}
		super.onDetachedFromWindow();
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		if (remotePlugin == null) {
			return super.onCreateView(name, context, attrs);
		}
		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {
			return caller.callOnCreateView(name, context, attrs);
		}
		return super.onCreateView(name, context, attrs);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
		if (remotePlugin == null) {
			return super.onCreateView(parent, name, context, attrs);
		}
		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {
			return caller.callOnCreateView(parent, name, context, attrs);
		}
		return super.onCreateView(parent, name, context, attrs);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (remotePlugin == null) {
			return;
		}
		PluginActivityCallback caller = remotePlugin.getControl();
		if (caller != null) {
			caller.callOnNewIntent(intent);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (remotePlugin == null) {
			return;
		}
		remotePlugin.getControl().getPluginRef().call("onActivityResult", requestCode, resultCode, data);
	}

	@SuppressLint("NewApi")
	@Override
	public void onAttachFragment(Fragment fragment) {

		super.onAttachFragment(fragment);
		if (remotePlugin == null) {
			return;
		}
		remotePlugin.getCurrentPluginActivity().onAttachFragment(fragment);
	}

	@Override
	public View onCreatePanelView(int featureId) {

		if (remotePlugin == null)
			return super.onCreatePanelView(featureId);
		return remotePlugin.getCurrentPluginActivity().onCreatePanelView(featureId);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {

		super.onOptionsMenuClosed(menu);
		if (remotePlugin == null) {
			return;
		}
		remotePlugin.getCurrentPluginActivity().onOptionsMenuClosed(menu);
	}

	@Override
	public void onPanelClosed(int featureId, Menu menu) {

		super.onPanelClosed(featureId, menu);
		if (remotePlugin == null) {
			return;
		}
		remotePlugin.getCurrentPluginActivity().onPanelClosed(featureId, menu);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (remotePlugin == null) {
			return super.onKeyUp(keyCode, event);
		}
		return remotePlugin.getCurrentPluginActivity().onKeyUp(keyCode, event);
	}

	@Override
	public void onAttachedToWindow() {

		super.onAttachedToWindow();
		if (remotePlugin == null) {
			return;
		}
		remotePlugin.getCurrentPluginActivity().onAttachedToWindow();
	}

	@Override
	public CharSequence onCreateDescription() {

		if (remotePlugin == null)
			return super.onCreateDescription();

		return remotePlugin.getCurrentPluginActivity().onCreateDescription();
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (remotePlugin == null)
			return super.onGenericMotionEvent(event);

		return remotePlugin.getCurrentPluginActivity().onGenericMotionEvent(event);
	}

	@Override
	public void onContentChanged() {

		super.onContentChanged();
		if (remotePlugin == null) {
			return;
		}
		remotePlugin.getCurrentPluginActivity().onContentChanged();
	}

	@Override
	public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {

		if (remotePlugin == null) {
			return super.onCreateThumbnail(outBitmap, canvas);
		}
		return remotePlugin.getCurrentPluginActivity().onCreateThumbnail(outBitmap, canvas);
	}

}
