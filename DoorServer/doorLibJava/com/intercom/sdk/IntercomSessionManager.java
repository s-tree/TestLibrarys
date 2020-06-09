package com.intercom.sdk;

import java.util.HashMap;
import java.util.Map;

/**
 * 监控底层门禁代理中的会话，仅仅作为调试之用
 */
public class IntercomSessionManager {
    //会话方向
    public static class SessionDir {
        public static final int SessionDirCallout = 0;
        public static final int SessionDirCallin = 1;
    }

    /**
     * 会话状态
     */
    public static class SessionStatus {
        //一个新的会话被创建
        public static final int Session_Created = -1;
        //会话被接受
        public static final int Session_Accepted = 0;
        //会话被pickup
        public static final int Session_Connected = 1;
        //会话被销毁
        public static final int Session_Destroyed = 2;
    }

    /**
     * 会话中代理的状态
     */
    public static class SessionClientStatus {
        //代理参与了本次会话
        public static final int Client_Add = 0;
        //代理离开了本次会话
        public static final int Client_Remove = 1;
        //代理摘机了
        public static final int Client_PickUp = 2;
        //代理开锁了
        public static final int Client_Unlock = 3;
    }

    public Map<String, IntercomSession> sessions;

    public IntercomSessionManager() {
        this.sessions = new HashMap<>();
    }

    public void add(IntercomSession session) {
        this.sessions.put(session.session_id, session);
    }

    public void remove(String session_id) {
        this.sessions.remove(session_id);
    }

    public IntercomSession find(String session_id) {
        return this.sessions.get(session_id);
    }

    public void update(String session_id, int status, String command, int err) {
        IntercomSession session = find(session_id);
        if (session == null)
            return;
        session.state = status;

        if (status == SessionStatus.Session_Destroyed) {
            this.sessions.remove(session_id);
        }
    }

    public void clear() {
        this.sessions.clear();
    }

    public static class IntercomSession {
        public int state;
        public final int dir;
        public final String session_id;
        public Map<String, SessionClient> clients;

        /**
         * 主持本次会话的终端信息
         * callin：第一个响应会话的终端
         * callout：就是发起会话的那个终端
         */
        public SessionClient presenter;

        //通过哪个设备发起的呼叫
        public final String device_id;

        //呼叫的房号
        public final String username;

        public IntercomSession(int dir, String device_id, String session_id, String username) {
            this.state = SessionStatus.Session_Created;
            this.dir = dir;
            this.device_id = device_id;
            this.session_id = session_id;
            this.username = username;
            this.clients = new HashMap<>();
        }

        public void addClient(SessionClient client) {
            if (clients.isEmpty()) {
                presenter = client;
            }
            clients.put(client.netClient.getClient_id(), client);
        }

        public void removeClient(String client_id) {
            clients.remove(client_id);
        }

        public void updateClient(String session_id, int router, NetClient net_client, int status) {

            if (status == SessionClientStatus.Client_Add) {
                SessionClient client = new SessionClient(net_client, router, status);
                addClient(client);
            } else if (status == SessionClientStatus.Client_Remove) {
                removeClient(net_client.getClient_id());
            } else if (status == SessionClientStatus.Client_PickUp) {
                SessionClient client = clients.get(net_client.getClient_id());
                if (client != null) {
                    client.status = status;
                }
            }
        }
    }

    public static class SessionClient {
        public NetClient netClient;
        public int router;
        public int status;

        public SessionClient(NetClient netClient, int router, int status) {
            this.netClient = netClient;
            this.router = router;
            this.status = status;
        }
    }
}
