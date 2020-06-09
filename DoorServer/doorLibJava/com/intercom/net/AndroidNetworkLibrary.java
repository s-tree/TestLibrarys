package com.intercom.net;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.os.Build;

import com.intercom.base.ContextUtils;
import com.intercom.base.annotations.CalledByNative;

import java.net.InetAddress;
import java.util.List;

public class AndroidNetworkLibrary {
    /**
     * Returns object representing the DNS configuration for the provided
     * network. If |network| is null, uses the active network.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @CalledByNative
    public static DnsStatus getDnsStatus(Network network) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) ContextUtils.getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return null;
        }
        if (network == null) {
            network = connectivityManager.getActiveNetwork();
        }
        if (network == null) {
            return null;
        }
        LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
        if (linkProperties == null) {
            return null;
        }
        List<InetAddress> dnsServersList = linkProperties.getDnsServers();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return new DnsStatus(dnsServersList, linkProperties.isPrivateDnsActive(),
                    linkProperties.getPrivateDnsServerName());
        } else {
            return new DnsStatus(dnsServersList, false, "");
        }
    }
}