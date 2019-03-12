package com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.method;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.Bus;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.MethodInfo;

import java.util.Set;

/**
 * User: mcxiaoke
 * Date: 15/8/4
 * Time: 18:17
 */
public class NamedMethodFinder implements MethodFinder {
    public static final String DEFAULT_NAME = "onEvent";

    private final String name;

    public NamedMethodFinder() {
        this(DEFAULT_NAME);
    }

    public NamedMethodFinder(final String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("invalid method name");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Set<MethodInfo> find(final Bus bus, final Class<?> targetClass) {
        return MethodHelper.findSubscriberMethodsByName(targetClass, name);
    }
}
