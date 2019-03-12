package com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.scheduler;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.Bus;

/**
 * User: mcxiaoke
 * Date: 15/8/4
 * Time: 16:00
 */
class SenderScheduler implements Scheduler {
    private Bus mBus;

    public SenderScheduler(final Bus bus) {
        mBus = bus;
    }

    @Override
    public void post(final Runnable runnable) {
        runnable.run();
    }
}
