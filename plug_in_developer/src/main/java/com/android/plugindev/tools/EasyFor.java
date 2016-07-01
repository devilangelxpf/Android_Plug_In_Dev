package com.android.plugindev.tools;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by evil.xu  on 2015/3/31.
 * 一个新颖的遍历工具类，效率高于for(E : Collection)
 * @author evil.xu
 */
public abstract class EasyFor<E> {

    public EasyFor(Collection<E> list){

        if(list == null || list.size() == 0) return;

        Iterator<E> iterator = list.iterator();
        while (iterator.hasNext()){
            onNewElement(iterator.next());
        }

    }

    public abstract void onNewElement(E element);

}
