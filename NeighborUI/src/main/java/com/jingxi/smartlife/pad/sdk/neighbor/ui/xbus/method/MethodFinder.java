package com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.method;


import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.Bus;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.MethodInfo;

import java.util.Set;

/**
 * User: mcxiaoke
 * Date: 15/8/4
 * Time: 18:14
 */
public interface MethodFinder {

    Set<MethodInfo> find(final Bus bus, final Class<?> targetClass);
}
