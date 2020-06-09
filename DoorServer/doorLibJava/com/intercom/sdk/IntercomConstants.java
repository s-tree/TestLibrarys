package com.intercom.sdk;

public class IntercomConstants {

    public static class LogLevel {
        public static final int Info = 0;
        public static final int Warning = 1;
        public static final int Error = 2;
        public static final int Fatal = 3;
    }

    //路由，可以是LAN或者云端中转
    public static class Router {
        public static final int LAN = 0;
        public static final int PROXY = 1;
    }

    //终端类型，代理和终端都属于终端，具有同样的数据结构
    public static class NetClientType {
        public static final int NetClientTypeUndefined = 0;
        public static final int NetClientTypeProxy = 1;  //代理
        public static final int NetClientTypeClient = 2; //终端
    }

    //终端子类型
    public static class NetClientSubType {
        public static final int NetClientSubTypeUndefined = -1;
        public static final int NetClientSubTypePad = 0; //pad上的终端
        public static final int NetClientSubTypeMobile = 1; //移动端上的终端
        public static final int NetClientSubTypeTV = 2; //电视机上终端，目前没有
        public static final int NetClientSubTypeEmbedded = 3; //嵌入式底座上的终端
    }

    //代理子类型
    public static class NetProxySubType {
        public static final int NetProxySubTypeUndefined = -1;
        /**
         * 室内机代理，1.1版本只能安装在底座中
         * 新版本可以安装在底座和平板中
         */
        public static final int NetProxySubTypeIndoor = 0;
        /**
         * 室外机代理，目前还没有这种类型的代理
         * 室外机代理是将室外机本身当作代理来对待，可以直接从云端呼叫终端
         * 无需室内机代理。
         * 配置室外机代理的环境不再需要室内机代理
         */
        public static final int NetProxySubTypeIntercom = 1;
    }

    //终端或代理的所在OS类型，通过NetClient.platform来判断
    public static class NetClientOSPlatformType {
        public static final int NetClientOSPlatformUndefined = 0;
        public static final int NetClientOSPlatformOPENWRT = 1;
        public static final int NetClientOSPlatformLINUX = 2;
        public static final int NetClientOSPlatformANDROID = 3;
        public static final int NetClientOSPlatformIOS = 4;
        public static final int NetClientOSPlatformMAC = 5;
        public static final int NetClientOSPlatformWIN = 6;
    }

    /**
     * app callback
     * app初始化是异步的，需要通过callback来判断初始化状态
     */
    public static class AppEvent {
        public static final int Event_System_Initialize = 0;
    }

    /**
     * 代理的callback
     */
    public static class ProxyEvent {
        public static final int Event_Internet_State_Changed = 0;
        public static final int Event_Client_State_Changed = 1;
    }

    /**
     * 门禁会话的callback，此处主要是为了调试方便
     * 可以跟踪当前代理中发生的会话状态变化
     */
    public static class IntercomEvent {
        public static final int Event_Sip_State_Changed = 0;
        public static final int Event_Intercom_Session_Create = 1;
        public static final int Event_Intercom_Session_State_Changed = 2;
        public static final int Event_Intercom_Client_State_Changed = 3;
        public static final int Event_Intercom_Stream_State_Changed = 4;
        public static final int Event_Intercom_Transport_Started = 5;
        public static final int Event_Intercom_Transport_Ice_Negotiation = 6;
    }

    /**
     * sip注册状态变化
     */
    public static class SipRegistrationState {
        public static final int SipRegistrationNone = 0;
        public static final int SipRegistrationProgress = 1;
        public static final int SipRegistrationOk = 2;
        public static final int SipRegistrationCleared = 3;
        public static final int SipRegistrationFailed = 4;
    }

    /**
     * 媒体状态变化
     */
    public static class TransportMediaType {
        public static final int TransportMediaTypeNone = 0;
        public static final int TransportMediaTypeAudio = 1;
        public static final int TransportMediaTypeVideo = 2;
    }

    /**
     * 媒体流状态变化
     */
    public static class StreamState {
        public static final int StreamStateStart = 0;
        public static final int StreamStateStop = 1;
    }

    public static final String kIntercomScheme = "icom";

    public static final String kProxyScheme = "proxy";

    public static final String kSmarthomeScheme = "gateway";

    public static final String kNotifyScheme = "notify";
}
