package com.android.plugindev.manager;


import com.android.plugindev.tools.EasyFor;
import com.android.plugindev.tools.PluginBug;
import com.android.plugindev.tools.PluginBugListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evil.xu  on 2015/4/3.
 */
public class PluginBugManager {
    private static final List<PluginBugListener> errorCollection = new ArrayList<PluginBugListener>();

    /**
     * 加入一个插件异常监听器
     * @param pluginBugListener
     */
    public static void addBugListener(PluginBugListener pluginBugListener){
        errorCollection.add(pluginBugListener);
    }

    /**
     * 移除一个插件异常监听器
     * @param pluginBugListener
     */
    public static void removeBugListener(PluginBugListener pluginBugListener){
        errorCollection.remove(pluginBugListener);
    }

    public static void callAllBugListener(final PluginBug error){
        if(errorCollection.size() == 0) return;
        new EasyFor<PluginBugListener>(errorCollection){
            @Override
            public void onNewElement(PluginBugListener element) {
                element.OnError(error);
            }
        };
    }

}
