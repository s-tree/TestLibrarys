package com.intercom.sdk;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.intercom.base.annotations.CalledByNative;

import java.lang.ref.WeakReference;

public class IntercomServerManager {
    private static final String TAG = "server";

    /**
     * Accessed by native methods: provides access to C++ IntercomServerManager object
     */
    @SuppressWarnings("unused")
    private long mNativeJavaObj;

    private final CallbackHandler callback;

    //代理自己的终端信息
    private NetClient netClient;

    public IntercomClientManager clientManager;

    public IntercomSessionManager sessionManager;

    public IntercomObserver.AppEventListener app_listener;

    public IntercomObserver.ProxyEventListener proxy_listener;

    public IntercomObserver.IntercomEventListener intercom_listener;

    public IntercomServerManager() {
        this.netClient = null;

        this.callback = new CallbackHandler(this);

        this.intercom_listener = new IntercomObserver.IntercomEventListener();

        this.proxy_listener = new IntercomObserver.ProxyEventListener();

        this.app_listener = new IntercomObserver.AppEventListener();

        this.clientManager = new IntercomClientManager();

        this.sessionManager = new IntercomSessionManager();
    }

    public static void globalInitialize(Context context, String app_conf) {
        nativeGlobalInitialize(context, app_conf);
    }

    public boolean start(String client_conf) {
        String client_string = nativeStart(new WeakReference<IntercomServerManager>(this), client_conf);
        if (client_string != null) {
            this.netClient = new Gson().fromJson(client_string, NetClient.class);
            return true;
        }
        return false;
    }

    public void stop() {
        nativeStop();
    }

    public void release() {
        nativeRelease();
        this.netClient = null;
        this.clientManager.clients.clear();
        this.sessionManager.sessions.clear();
    }

    @Override
    protected void finalize() {
        nativeFinalize();
    }

    public static IntercomNetDevice getNetDevices() {
        String str = nativeGetNetDevices();
        if (str != null) {
            return IntercomNetDevice.objectFromData(str);
        }
        return null;
    }

    public static String getSDKVersion() {
        return nativeGetSDKVersion();
    }

    public static String base64(boolean encode, String content) {
        return nativeBase64(encode, content);
    }

    public static byte[] encode(boolean encode, String key, byte[] content) {
        return nativeEncode(encode, key, content);
    }

    public NetClient getNetClient() {
        return this.netClient;
    }


    public void sendIntercomCommand(Bundle bundle) {
        nativeAppCommand(bundle);
    }

    //---------------------------------------------------------
    // Java methods called from the native side
    //--------------------
    @CalledByNative
    @SuppressWarnings("unused")
    private static void onNativeAppMessageArrival(Object thiz, Bundle bundle) {
        IntercomServerManager manager = (IntercomServerManager) ((WeakReference) thiz).get();
        if (manager != null && manager.callback != null) {
            Message m = new Message();
            m.what = 0;
            m.setData(bundle);
            manager.callback.sendMessage(m);
        }
    }

    private static class CallbackHandler extends Handler {
        private final WeakReference<IntercomServerManager> manager;

        public CallbackHandler(IntercomServerManager manager) {
            this.manager = new WeakReference<>(manager);
        }

        @Override
        public void handleMessage(Message message) {
            IntercomServerManager manager = this.manager.get();
            if (manager != null && message.what == 0) {
                manager.onIntercomMessageEvent(message.getData());
            } else {
                super.handleMessage(message);
            }
        }
    }


    /**
     * java层对底层消息的解析，整个事件的解析和分发都在这里完成
     *
     * @param bundle
     */
    void onIntercomMessageEvent(Bundle bundle) {
        String scheme = bundle.getString("scheme");
        if (scheme == null || !bundle.containsKey("event"))
            return;

        int event = bundle.getInt("event");

        if (scheme.equalsIgnoreCase("app")) {
            if (event == IntercomConstants.AppEvent.Event_System_Initialize) {
                clientManager.clear();
                sessionManager.clear();

                int result = bundle.getInt("result");

                /**
                 * result = 2,表示系统还没有部署
                 */
                app_listener.onAppInitialized(result);

            }
        } else if (scheme.equalsIgnoreCase(IntercomConstants.kProxyScheme)) {
            if (event == IntercomConstants.ProxyEvent.Event_Internet_State_Changed) {
                /**
                 * 云端代理上下线通知
                 */
                boolean online = bundle.getBoolean("online");

                clientManager.changeInternetState(online);

                proxy_listener.onInternetStateChanged(online);
            } else if (event == IntercomConstants.ProxyEvent.Event_Client_State_Changed) {
                /**
                 * 终端状态发生变化
                 */
                int router = bundle.getInt("router");
                boolean online = bundle.getBoolean("online");
                String client_string = bundle.getString("client");
                NetClient net_client = NetClient.objectFromData(client_string);
                clientManager.update(router, online, net_client);
                proxy_listener.onClientStateChanged(router, online, net_client);
            }

        } else if (scheme.equalsIgnoreCase(IntercomConstants.kIntercomScheme)) {
            if (event == IntercomConstants.IntercomEvent.Event_Sip_State_Changed) {
                /**
                 * 门禁代理中的sip状态发生变化
                 * identity: sip:xxx@ip:port，表示sip账号
                 */
                String identity = bundle.getString("proxy");
                //state：SipRegistrationState
                int state = bundle.getInt("state");
                intercom_listener.onSipStateChanged(identity, state);

            } else if (event == IntercomConstants.IntercomEvent.Event_Intercom_Session_Create) {
                /**
                 * 某个会话被创建了，可能是callin，callout，依赖dir
                 */
                int dir = bundle.getInt("dir");
                String device_id = bundle.getString("device_id");
                String session_id = bundle.getString("session_id");
                String username = bundle.getString("username");
                IntercomSessionManager.IntercomSession session = new IntercomSessionManager.IntercomSession(dir, device_id, session_id, username);
                sessionManager.add(session);
                intercom_listener.onIntercomSessionCreated(dir, device_id, session_id, username);

            } else if (event == IntercomConstants.IntercomEvent.Event_Intercom_Session_State_Changed) {
                /**
                 * 会话状态发生变化
                 */
                String session_id = bundle.getString("session_id");
                int status = bundle.getInt("status");
                String command = bundle.getString("command");
                int err = bundle.getInt("err");

                sessionManager.update(session_id, status, command, err);
                intercom_listener.onIntercomSessionStateChanged(session_id, status, command, err);

            } else if (event == IntercomConstants.IntercomEvent.Event_Intercom_Client_State_Changed) {
                String session_id = bundle.getString("session_id");
                int router = bundle.getInt("router");
                int status = bundle.getInt("status");
                String client_string = bundle.getString("client");
                NetClient netClient = NetClient.objectFromData(client_string);
                IntercomSessionManager.IntercomSession session = sessionManager.find(session_id);
                if (session != null) {
                    session.updateClient(session_id, router, netClient, status);
                }
                intercom_listener.onIntercomSessionClientStateChanged(session_id, router, netClient, status);
            } else if (event == IntercomConstants.IntercomEvent.Event_Intercom_Stream_State_Changed) {
                int type = bundle.getInt("type");
                String session_id = bundle.getString("session_id");
                String userid = bundle.getString("userid");
                int state = bundle.getInt("state");
                intercom_listener.onIntercomClientStreamStateChanged(type, session_id, userid, state);

            } else if (event == IntercomConstants.IntercomEvent.Event_Intercom_Transport_Started) {
                int type = bundle.getInt("type");
                String session_id = bundle.getString("session_id");
                String userid = bundle.getString("userid");
                intercom_listener.onIntercomTransportStarted(type, session_id, userid);
            } else if (event == IntercomConstants.IntercomEvent.Event_Intercom_Transport_Ice_Negotiation) {
                int type = bundle.getInt("type");
                String session_id = bundle.getString("session_id");
                String userid = bundle.getString("userid");
                int result = bundle.getInt("result");
                intercom_listener.onIntercomTransportIceNegotiationResult(type, session_id, userid, result);
            }
        }
    }

    private static native final void nativeGlobalInitialize(Context context, String app_conf);

    private native final String nativeStart(Object weak_this, String client_conf);

    private native final void nativeStop();

    private native final void nativeRelease();

    private native final void nativeFinalize();

    private native final void nativeAppCommand(Bundle bundle);

    private static native final String nativeGetNetDevices();

    private static native final String nativeGetSDKVersion();

    private static native final String nativeBase64(boolean encode, String content);

    private static native final byte[] nativeEncode(boolean encode, String key, byte[] content);

    static {
        System.loadLibrary("intercomserver");
    }
}
