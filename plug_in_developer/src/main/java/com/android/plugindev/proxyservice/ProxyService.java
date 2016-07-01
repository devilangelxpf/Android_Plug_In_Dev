package com.android.plugindev.proxyservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;

import com.android.plugindev.proxyActivity.ActivityProxy;
import com.android.plugindev.config.PluginConfig;
import com.android.plugindev.config.ServicePluginConfig;
import com.android.plugindev.manager.ApkManager;
import com.android.plugindev.manager.PluginDexManager;
import com.android.plugindev.reflect.Reflect;

public class ProxyService extends Service
{

	ServicePluginConfig remote;
	public static String SERVICE_CLASS_NAME = Service.class.getName();
	public static String SERVICE_APK_PATH = PluginDexManager.finalApkPath;
	
	@Override
	public IBinder onBind(Intent i)
	{
		fillService();
		return remote.getCurrentPluginService().onBind(i);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if(!remote.getCurrentPluginService().getClass().getName().equals(SERVICE_CLASS_NAME)){
			fillService();
			remote.getCurrentPluginService().onCreate();
		}
		return remote.getCurrentPluginService().onStartCommand(intent,flags,startId);
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		fillService();
		remote.getCurrentPluginService().onCreate();
	}

	@SuppressLint("NewApi")
	private void fillService()
	{
		remote = new ServicePluginConfig(this,SERVICE_APK_PATH);
		remote.setTopServiceName(SERVICE_CLASS_NAME);
		remote.from().debug();
		if(!remote.from().canUse()){
			ApkManager.initApk(remote.from(), this);
		}
		
		try
		{
			Service plugin = (Service) remote.from().pluginLoader.loadClass(remote.getTopServiceName()).newInstance();
			remote.setCurrentPluginService(plugin);
			Reflect thiz = Reflect.on(this);
			Reflect.on(plugin)
					.call("attach",
							this,
							thiz.get("mThread"),
							getClass().getName(),
							thiz.get("mToken"),
							remote.from().pluginApplication == null ? getApplication() : remote.from().pluginApplication,
							thiz.get("mActivityManager"));
			Reflect.on(this.getBaseContext()).call("setOuterContext", plugin);

		}
		catch (IllegalAccessException e)
		{}
		catch (ClassNotFoundException e)
		{}
		catch (InstantiationException e)
		{}
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
		remote.getCurrentPluginService().onStart(intent,startId);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		
		super.onConfigurationChanged(newConfig);
		remote.getCurrentPluginService().onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		remote.getCurrentPluginService().onUnbind(intent);
		return super.onUnbind(intent);

	}

	@Override
	public void onDestroy()
	{
		remote.getCurrentPluginService().onDestroy();
		super.onDestroy();
	}

	@Override
	public void onRebind(Intent intent)
	{
		
		super.onRebind(intent);
		remote.getCurrentPluginService().onRebind(intent);
	}

	@SuppressLint("NewApi")
	@Override
	public void onTrimMemory(int level)
	{
		
		super.onTrimMemory(level);
		remote.getCurrentPluginService().onTrimMemory(level);
	}

	@Override
	public void onLowMemory()
	{
		
		super.onLowMemory();
		remote.getCurrentPluginService().onLowMemory();
	}

	@SuppressLint("NewApi")
	@Override
	public void onTaskRemoved(Intent rootIntent)
	{
		
		super.onTaskRemoved(rootIntent);
		remote.getCurrentPluginService().onTaskRemoved(rootIntent);
	}

	@Override
    public Resources getResources() {
        if (remote == null)
            return super.getResources();
        return remote.from().pluginRes == null ? super.getResources() : remote.from().pluginRes;
    }


    @Override
    public AssetManager getAssets() {
        if (remote == null)
            return super.getAssets();
        return remote.from().pluginAssets == null ? super.getAssets() : remote.from().pluginAssets;
    }


    @Override
    public ClassLoader getClassLoader() {
        if (remote == null) {
            return super.getClassLoader();
        }
        if (remote.from().canUse()) {
            return remote.from().pluginLoader;
        }
        return super.getClassLoader();
    }

	@Override
	public ComponentName startService(Intent service)
	{
		ProxyService.SERVICE_CLASS_NAME = service.getComponent().getClassName();
		service.setClass(this,ProxyService.class);
		return super.startService(service);
	}
	
	@Override
	public void startActivity(Intent intent) {
		String lastIntentClassName = intent.getComponent().getClassName();
		intent.setClass(this, ActivityProxy.class);
		Bundle bundle = intent.getExtras();
		if(bundle ==null)
		{
			bundle = new Bundle();
		}
		bundle.putString(PluginConfig.KEY_PLUGIN_DEX_PATH, remote.from().pluginPath);
		bundle.putString(PluginConfig.KEY_PLUGIN_ACT_NAME, lastIntentClassName);
		intent.putExtras(bundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		super.startActivity(intent);
	}

}
