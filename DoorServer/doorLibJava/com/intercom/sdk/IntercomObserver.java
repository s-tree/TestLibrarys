package com.intercom.sdk;

public class IntercomObserver {

    public interface OnIntercomAppEventListener {
        /**
         * @param result: 0:success, 1: failed, 2:系统未部署
         */
        void onAppInitialized(int result);
    }

    public interface OnProxyEventListener {
        /**
         * @param online: Internet代理上线/下线
         */
        void onInternetStateChanged(boolean online);

        /**
         * @param router:终端路由
         * @param online:上线/下线
         * @param net_client:终端信息
         */
        void onClientStateChanged(int router, boolean online, NetClient net_client);
    }

    public interface OnIntercomEventListener {
        /**
         * sip登陆状态
         *
         * @param identity
         * @param state:   IntercomConstants.SipRegistrationState
         */
        void onSipStateChanged(String identity, int state);

        /**
         * 底层创建新的门禁呼叫会话
         *
         * @param dir        IntercomSessionManager.SessionDir
         * @param device_id
         * @param session_id
         * @param username
         */
        void onIntercomSessionCreated(int dir, String device_id, String session_id, String username);

        /**
         * 门禁会话变更
         *
         * @param session_id
         * @param status     IntercomSessionManager.SessionStatus
         * @param command
         * @param err
         */
        void onIntercomSessionStateChanged(String session_id, int status, String command, int err);

        /**
         * 门禁会话中终端的状态变更
         * 如果是callout会话，第一个收到这个事件的终端就是发起呼叫的终端
         *
         * @param session_id
         * @param router
         * @param net_client
         * @param status     IntercomSessionManager.SessionClientStatus
         */
        void onIntercomSessionClientStateChanged(String session_id, int router, NetClient net_client, int status);

        /**
         * 会话流媒体状态变更
         *
         * @param type:IntercomConstants.TransportMediaType
         * @param session_id
         * @param user_id
         * @param state                                     : IntercomConstants.StreamState
         */
        void onIntercomClientStreamStateChanged(int type, String session_id, String user_id, int state);

        /**
         * 会话传输层状态更变，这个事件可能发生在会话终止之后
         *
         * @param type:IntercomConstants.TransportMediaType
         * @param session_id
         * @param user_id
         */
        void onIntercomTransportStarted(int type, String session_id, String user_id);


        void onIntercomTransportIceNegotiationResult(int type, String session_id, String user_id, int result);
    }

    public static class AppEventListener extends EventListener<OnIntercomAppEventListener> {
        public void onAppInitialized(int result) {
            for (OnIntercomAppEventListener listener : listeners.keySet()) {
                if (listener != null) {
                    listener.onAppInitialized(result);
                }
            }
        }
    }

    public static class ProxyEventListener extends EventListener<OnProxyEventListener> {

        public void onInternetStateChanged(boolean online) {
            for (OnProxyEventListener listener : listeners.keySet()) {
                if (listener != null) {
                    listener.onInternetStateChanged(online);
                }
            }
        }

        public void onClientStateChanged(int router, boolean online, NetClient net_client) {
            for (OnProxyEventListener listener : listeners.keySet()) {
                if (listener != null) {
                    listener.onClientStateChanged(router, online, net_client);
                }
            }
        }
    }

    public static class IntercomEventListener extends EventListener<OnIntercomEventListener> {
        public void onSipStateChanged(String identity, int state) {
            for (OnIntercomEventListener listener : listeners.keySet()) {
                if (listener != null) {
                    listener.onSipStateChanged(identity, state);
                }
            }
        }

        public void onIntercomSessionCreated(int dir, String device_id, String session_id, String username) {
            for (OnIntercomEventListener listener : listeners.keySet()) {
                if (listener != null) {
                    listener.onIntercomSessionCreated(dir, device_id, session_id, username);
                }
            }
        }

        public void onIntercomSessionStateChanged(String session_id, int status, String command, int err) {
            for (OnIntercomEventListener listener : listeners.keySet()) {
                if (listener != null) {
                    listener.onIntercomSessionStateChanged(session_id, status, command, err);
                }
            }
        }

        public void onIntercomSessionClientStateChanged(String session_id, int router, NetClient net_client, int status) {
            for (OnIntercomEventListener listener : listeners.keySet()) {
                if (listener != null) {
                    listener.onIntercomSessionClientStateChanged(session_id, router, net_client, status);
                }
            }
        }

        public void onIntercomClientStreamStateChanged(int type, String session_id, String user_id, int state) {
            for (OnIntercomEventListener listener : listeners.keySet()) {
                if (listener != null) {
                    listener.onIntercomClientStreamStateChanged(type, session_id, user_id, state);
                }
            }
        }

        public void onIntercomTransportStarted(int type, String session_id, String user_id) {
            for (OnIntercomEventListener listener : listeners.keySet()) {
                if (listener != null) {
                    listener.onIntercomTransportStarted(type, session_id, user_id);
                }
            }
        }

        public void onIntercomTransportIceNegotiationResult(int type, String session_id, String user_id, int result) {
            for (OnIntercomEventListener listener : listeners.keySet()) {
                if (listener != null) {
                    listener.onIntercomTransportIceNegotiationResult(type, session_id, user_id, result);
                }
            }
        }
    }
}
