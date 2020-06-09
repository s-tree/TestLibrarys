package com.intercom.sdk;

import java.util.WeakHashMap;

/**
 * callback abstract class
 * 这里保存observer的弱引用，observer被回收之前都是有效的
 * 通过手工添加System.gc()来测试一下
 *
 * @param <T>
 */

public abstract class EventListener<T> {
  final WeakHashMap<T, Object> listeners;

  EventListener() {
    listeners = new WeakHashMap<>();
  }

  public void add(T listener) {
    listeners.put(listener, null);
  }

  public void remove(T listener) {
    listeners.remove(listener);
  }
}