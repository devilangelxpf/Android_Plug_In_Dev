package com.android.plugindev.tools;


import com.android.plugindev.config.ActivityPluginConfig;

/**
 * Created by evil.xu  on 2015/4/3.
 */
public class PluginBug {
    public Throwable error;
    public long errorTime;
    public Thread errorThread;
    public ActivityPluginConfig errorPlugin;
    public int processId;
}
