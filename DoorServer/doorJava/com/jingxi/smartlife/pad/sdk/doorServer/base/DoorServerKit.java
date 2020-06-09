package com.jingxi.smartlife.pad.sdk.doorServer.base;

import com.jingxi.smartlife.pad.sdk.doorServer.base.utils.ServerFileUtils;

public class DoorServerKit {

    /**
     * 初始化一些参数
     *
     * @param options
     */
    public static void init(Options options) {
        if (options == null) {
            return;
        }
        AppSettings.options = options;
        ServerFileUtils.initServerUrls();
    }

    public static Options getOptions() {
        return AppSettings.options;
    }

    public static class Options{
        protected static Options getDefault() {
            return new Options();
        }

        private Options() {

        }

        /**
         * 渠道
         */
        public String channel = "jx";

        /**
         * debug模式
         */
        public boolean isDebug = false;

        public String sipUrl = AppSettings.DEFAULT_SIP_URL;

        public String transitUrl = AppSettings.DEFAULT_TRANSIT_URL;

        public String deployUrl = AppSettings.DEFAULT_DEPLOY_URL;

        public String deployKey = AppSettings.DEFAULT_DEPLOY_KEY;
    }
}
