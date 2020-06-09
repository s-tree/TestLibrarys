package com.intercom.sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 代理中终端管理，我们只是为了调试和打印信息
 * 本身没有什么作用
 */
public class IntercomClientManager {

    public Map<String, IntercomClient> clients;

    private boolean internetOnline;

    public IntercomClientManager() {
        this.clients = new HashMap<>();
        this.internetOnline = false;
    }

    /**
     * @param router:     终端上线的路由
     * @param online:     上线或者下线
     * @param net_client: 终端信息
     */
    public void update(int router, boolean online, NetClient net_client) {
        if (online) {
            IntercomClient currentClient = find(net_client.getClient_id());
            if (currentClient != null) {
                currentClient.add(router, net_client);
            } else {
                IntercomClient client = new IntercomClient();
                client.add(router, net_client);
                clients.put(net_client.getClient_id(), client);
            }
        } else {
            IntercomClient currentClient = find(net_client.getClient_id());
            if (currentClient != null) {
                currentClient.remove(router);
                if (currentClient.empty()) {
                    clients.remove(net_client.getClient_id());
                }
            }
        }
    }

    public void changeInternetState(boolean online) {
        this.internetOnline = online;
        if (!online) {
            Set<String> keySet = new HashSet<String>(clients.keySet());
            if (!keySet.isEmpty()) {
                List<String> tempKeys = new ArrayList<String>(keySet);
                for (String key : tempKeys) {
                    IntercomClient client = find(key);
                    client.remove(IntercomConstants.Router.PROXY);
                    if (client.empty()) {
                        clients.remove(client.client_id);
                    }
                }
            }
        }
    }

    public IntercomClient find(String client_id) {
        return clients.get(client_id);
    }

    public boolean isInternetOnline() {
        return internetOnline;
    }

    public void clear() {
        clients.clear();
        internetOnline = false;
    }

    public static class IntercomClient {
        /**
         * 每个终端可能既从LAN上线，也可能从WAN上线
         * 我们记录每个上线路由
         * 不管从哪个路由上线，终端信息都是一样的
         */
        public String client_id; //终端标识
        public NetClient lanClient;
        public NetClient wanClient;

        public IntercomClient() {
            this.wanClient = null;
            this.lanClient = null;
            this.client_id = null;
        }

        public boolean empty() {
            return (this.lanClient == null && this.wanClient == null);
        }

        //终端从某个路由上线
        public void add(int router, NetClient netClient) {
            if (router == IntercomConstants.Router.LAN) {
                this.lanClient = netClient;
            } else if (router == IntercomConstants.Router.PROXY) {
                this.wanClient = netClient;
            }
            if (this.client_id == null) {
                this.client_id = netClient.getClient_id();
            }
        }

        //终端从某个路由下线
        public void remove(int router) {
            if (router == IntercomConstants.Router.LAN) {
                this.lanClient = null;
            } else if (router == IntercomConstants.Router.PROXY) {
                this.wanClient = null;
            }
        }

        public boolean lanOnline() {
            return this.lanClient != null;
        }

        public boolean wanOnline() {
            return this.wanClient != null;
        }

        /**
         * 我们优先使用LAN
         *
         * @return NetClient/null
         */
        public NetClient get() {
            if (lanOnline())
                return this.lanClient;
            if (wanOnline())
                return this.wanClient;
            return null;
        }
    }
}
