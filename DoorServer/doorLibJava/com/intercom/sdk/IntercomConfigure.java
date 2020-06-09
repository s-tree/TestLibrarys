package com.intercom.sdk;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class IntercomConfigure {

    public static class AppConf {
        //you can use app version
        public String app_version;
        public String channel;
        public String work_dir;
        public boolean log_to_file;
        public int log_level;
        public String timezone;
        /**
         * use serial number ON PAD,use user account ON user mobile
         */
        public String system_id;

        public AppConf(String app_version,
                       String channel,
                       String system_id,
                       String work_dir,
                       boolean log_to_file,
                       int log_level) {
            this.app_version = app_version;
            this.channel = (channel == null || channel.isEmpty()) ? "JX" : channel;
            this.work_dir = work_dir;
            this.log_to_file = log_to_file;
            this.log_level = log_level;
            this.system_id = system_id;
            TimeZone zone = TimeZone.getDefault();
            if (zone != null) {
                this.timezone = zone.getDisplayName(false, TimeZone.SHORT);
            }
        }

        public String toJson() {
            Bundle bundle = new Bundle();
            bundle.putString("app_ver", this.app_version);
            bundle.putString("channel", this.channel);
            bundle.putString("work_dir", this.work_dir);
            bundle.putBoolean("log_to_file", this.log_to_file);
            bundle.putInt("log_level", this.log_level);
            bundle.putString("system_id", this.system_id);
            bundle.putString("timezone", this.timezone);
            return BundleToJSON.toString(bundle);
        }
    }

    public static class ClientConf {
        public int type;
        public int sub_type;
        public String family_id;
        public String alias;
        public String button_key;
        public String pushid;
        public String sn;

        public ClientConf() {
            this.type = IntercomConstants.NetClientType.NetClientTypeProxy;
            this.sub_type = IntercomConstants.NetProxySubType.NetProxySubTypeIndoor;
            this.family_id = "";
            this.alias = "";
            this.button_key = "";
            this.pushid = "";
            this.sn = "";
        }

        public ClientConf(int type,
                          int sub_type,
                          String family_id,
                          String button_key,
                          String alias,
                          String pushid,
                          String sn) {
            this.type = type;
            this.sub_type = sub_type;
            this.family_id = family_id;
            this.button_key = button_key;
            this.alias = alias;
            this.pushid = pushid;
            this.sn = sn;
        }

        public String toJson() {
            Bundle bundle = new Bundle();
            bundle.putInt("type", this.type);
            bundle.putInt("sub_type", this.sub_type);
            bundle.putString("family_id", this.family_id);
            bundle.putString("alias", this.alias);
            bundle.putString("button_key", this.button_key);
            bundle.putString("pushid", this.pushid);
            bundle.putString("sn", this.sn);
            return BundleToJSON.toString(bundle);
        }
    }

    public static class SipAuth {
        public String username;
        public String userid;
        public String passwd;

        public SipAuth() {
            this.username = "";
            this.userid = "";
            this.passwd = "";
        }
    }

    public static class SipProxy {
        public String proxy;
        public String identity;
        public String route;
        public String contact_param;
        public int expires;

        public SipProxy() {
            proxy = "";
            identity = "";
            route = "";
            contact_param = "";
            expires = 300;
        }
    }

    /**
     * sipconf不需要从上层配置，这里仅仅为了测试
     * 底层会从指定的服务器获取sip配置
     */
    public static class SipConfig {
        public int session_expires;
        public boolean use_rport;
        public boolean reuse_authorization;
        public boolean expire_old_registration_contacts;
        public int dscp;
        public boolean use_ipv6;
        public int sip_port_range;
        public int sip_listen_port;
        public String transport;
        public boolean one_matching_codec;
        public boolean use_double_registrations;
        public boolean add_dates;
        public int keepalive_period;
        public boolean tcp_tls_keepalive;
        public String user_agent;
        public boolean use_exosip2_version;
        public String default_contact;
        public int max_calls;
        public String answer_options;
        public boolean prevent_colliding_calls;

        /**
         * 支持多个账号
         */
        public List<SipAuth> auths;
        public List<SipProxy> proxies;

        /**
         * ICE 配置，一般只需要配置 stun_server
         */
        public String turn_server;
        public String turn_username;
        public String turn_pwd;
        public String turn_auth_type;
        public String turn_pwd_type;
        public String turn_conn_type;
        public String turn_auth_realm;
        public String stun_server;
        public boolean loopaddr;
        public boolean aggressive;

        public SipConfig() {
            this.session_expires = 0;
            this.use_rport = true;
            this.reuse_authorization = false;
            this.expire_old_registration_contacts = false;
            this.dscp = 0x1a;
            this.use_ipv6 = false;
            this.sip_port_range = 0;
            this.sip_listen_port = 0;
            this.transport = "UDP"; //UDP | TCP
            this.one_matching_codec = false;
            this.use_double_registrations = false;
            this.add_dates = false;
            this.keepalive_period = 300;
            this.tcp_tls_keepalive = false;
            this.user_agent = "jxhousekeeper/1.0";
            this.use_exosip2_version = true;
            this.default_contact = "";
            this.max_calls = 10;
            this.answer_options = "";
            this.prevent_colliding_calls = false;
            this.auths = new ArrayList<>();
            this.proxies = new ArrayList<>();

            this.turn_server = "";
            this.turn_username = "";
            this.turn_pwd = "";
            this.turn_auth_type = "static";
            this.turn_pwd_type = "plain";
            this.turn_conn_type = "udp";
            this.turn_auth_realm = "";
            this.stun_server = "";
            this.loopaddr = true;
            this.aggressive = true;
        }

        public SipConfig(String user_id,
                         String password,
                         String transport,
                         String sip_server,
                         int server_port,
                         int stun_port,
                         boolean use_ice) {
            String proxy_name = "";
            String identity = "";
            String stun_server = "";

            if (sip_server != null) {
                proxy_name = String.format(Locale.US, "<sip:%s:%d>", sip_server, server_port);
                identity = String.format(Locale.US, "sip:%s@%s:%d", user_id, sip_server, server_port);
                stun_server = String.format(Locale.US, "%s:%d", sip_server, stun_port);
            }
            if(transport != null && !transport.equalsIgnoreCase("UDP")){
                this.transport = transport;
            }
            this.session_expires = 0;
            this.use_rport = true;
            this.reuse_authorization = false;
            this.expire_old_registration_contacts = false;
            this.dscp = 0x1a;
            this.use_ipv6 = false;
            this.sip_port_range = 0;
            this.sip_listen_port = 0;
            this.one_matching_codec = false;
            this.use_double_registrations = false;
            this.add_dates = false;
            this.keepalive_period = 300;
            this.tcp_tls_keepalive = false;
            this.user_agent = "jxhousekeeper/1.0";
            this.use_exosip2_version = true;
            this.default_contact = "";
            this.max_calls = 10;
            this.answer_options = "";
            this.prevent_colliding_calls = false;
            this.auths = new ArrayList<>();
            this.proxies = new ArrayList<>();
            if (user_id != null) {
                SipAuth auth = new SipAuth();
                auth.userid = user_id;
                auth.username = user_id;
                auth.passwd = password;
                this.auths.add(auth);
            }

            if (!proxy_name.isEmpty()) {
                SipProxy proxy = new SipProxy();
                proxy.proxy = proxy_name;
                proxy.identity = identity;
                this.proxies.add(proxy);
            }
            if (use_ice) {
                this.turn_server = "";
                this.turn_username = "";
                this.turn_pwd = "";
                this.turn_auth_type = "static";
                this.turn_pwd_type = "plain";
                this.turn_conn_type = "udp";
                this.turn_auth_realm = "";
                this.stun_server = stun_server;
                this.loopaddr = true;
                this.aggressive = true;
            }
        }

        public String toJson() {

            ArrayList<Bundle> auth_array = new ArrayList<>();

            for (SipAuth auth : auths) {
                Bundle dict = new Bundle();
                dict.putString("username", auth.username);
                dict.putString("userid", auth.userid);
                dict.putString("passwd", auth.passwd);
                auth_array.add(dict);
            }

            ArrayList<Bundle> proxy_array = new ArrayList<>();
            for (SipProxy proxy : proxies) {
                Bundle dict = new Bundle();
                dict.putString("proxy", proxy.proxy);
                dict.putString("identity", proxy.identity);
                dict.putString("route", proxy.route);
                dict.putString("contact_param", proxy.contact_param);
                dict.putInt("expires", proxy.expires);
                proxy_array.add(dict);
            }

            Bundle sip = new Bundle();
            sip.putInt("session_expires", this.session_expires);
            sip.putBoolean("use_rport", this.use_rport);
            sip.putBoolean("reuse_authorization", this.reuse_authorization);
            sip.putBoolean("expire_old_registration_contacts", this.expire_old_registration_contacts);
            sip.putInt("dscp", this.dscp);
            sip.putBoolean("use_ipv6", this.use_ipv6);
            sip.putInt("sip_port_range", this.sip_port_range);
            sip.putInt("sip_listen_port", this.sip_listen_port);
            sip.putString("transport", this.transport);
            sip.putBoolean("one_matching_codec", this.one_matching_codec);
            sip.putBoolean("use_double_registrations", this.use_double_registrations);
            sip.putBoolean("add_dates", this.add_dates);
            sip.putInt("keepalive_period", this.keepalive_period);
            sip.putBoolean("tcp_tls_keepalive", this.tcp_tls_keepalive);
            sip.putString("user_agent", this.user_agent);
            sip.putBoolean("use_exosip2_version", this.use_exosip2_version);
            sip.putString("default_contact", this.default_contact);
            sip.putInt("max_calls", this.max_calls);
            sip.putString("answer_options", this.answer_options);
            sip.putBoolean("prevent_colliding_calls", this.prevent_colliding_calls);

            if (auth_array.size() > 0) {
                sip.putSerializable("auth", auth_array);
            }
            if (proxy_array.size() > 0) {
                sip.putSerializable("proxy", proxy_array);
            }

            Bundle conf = new Bundle();
            if (!this.stun_server.isEmpty()) {
                Bundle ice = new Bundle();
                ice.putString("turn_server", this.turn_server);
                ice.putString("turn_username", this.turn_username);
                ice.putString("turn_pwd", this.turn_pwd);
                ice.putString("turn_auth_type", this.turn_auth_type);
                ice.putString("turn_pwd_type", this.turn_pwd_type);
                ice.putString("turn_conn_type", this.turn_conn_type);
                ice.putString("turn_auth_realm", this.turn_auth_realm);
                ice.putString("stun_server", this.stun_server);
                ice.putBoolean("loopaddr", this.loopaddr);
                ice.putBoolean("aggressive", this.aggressive);
                conf.putBundle("ice", ice);
            }

            conf.putBundle("sip", sip);
            return BundleToJSON.toString(conf);
        }
    }
}
