package com.jingxi.smartlife.pad.sdk.doorServer.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.StaticIpConfiguration;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 8.0+ EhternetManager
 */
public class EthernetUtilV25 {
    private static final String TAG = "EthernetUtilV25";
    private static final String ETHERNET_SERVICE = "ethernet";
    private static final String IPADDRESS_REGEX = "^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$";

    private static EthernetUtilV25 instance;
    private EthernetManager ethernetManager;
    private ConnectivityManager connectivityManager;

    public static void init(Context context){
        instance = new EthernetUtilV25(context);
    }

    public void addListener(EthernetManager.Listener listener){
        ethernetManager.addListener(listener);
    }

    public void removeListener(EthernetManager.Listener listener){
        ethernetManager.removeListener(listener);
    }
    private EthernetUtilV25(Context context){
        ethernetManager = (EthernetManager) context.getSystemService(ETHERNET_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
    }

    public static EthernetUtilV25 getInstance(){
        if(instance == null){
            throw new RuntimeException("need call init(Context) first !!!");
        }
        return instance;
    }

    public boolean isAvailable(){
        return ethernetManager.isAvailable();
    }

    public boolean isEthernetConnected(){
        return ethernetManager.getEthernetConnectState() == 1;
    }

    public IpConfiguration getIPConfiguration(){
        return ethernetManager.getConfiguration();
    }

    public void setIPConfiguration(IpConfiguration ipConfiguration){
        ethernetManager.setConfiguration(ipConfiguration);
    }

    /**
     * 获取指定网卡ip
     *
     * @return
     * @throws SocketException
     */
    public String getIpAddress() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            if(nis == null){
                return "";
            }
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                if (ni.getName().contains("eth")) {
                    Enumeration<InetAddress> ias = ni.getInetAddresses();
                    while (ias.hasMoreElements()) {
                        ia = ias.nextElement();
                        if (ia instanceof Inet6Address) {
                            continue;// skip ipv6
                        }
                        String ip = ia.getHostAddress();
                        // 过滤掉127段的ip地址
                        if (!"127.0.0.1".equals(ip)) {
                            hostIp = ia.getHostAddress();
                            break;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;
    }

    /**
     * 获取DHCPip地址
     *
     * @return
     */
    public String iprout() {
        Process cmdProcess = null;
        BufferedReader reader = null;
        String dnsIP = "";
        try {
            cmdProcess = Runtime.getRuntime().exec("/system/bin/ip rout");
            reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
            String temp = null;
            StringBuilder builder = new StringBuilder();
            while (!TextUtils.isEmpty(temp = reader.readLine())){
                builder.append(temp);
                builder.append("\n");
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
            cmdProcess.destroy();
        }
    }

    /**
     * 获取网关地址
     * default via 192.168.123.1 dev eth0  table 1163  proto static
     * @return
     */
    public List<String> getway() {
        List<String> results = new ArrayList<>();
        Process cmdProcess = null;
        BufferedReader reader = null;
        try {
//            cmdProcess = Runtime.getRuntime().exec("/system/bin/ip route list table 0 | grep default");
            cmdProcess = Runtime.getRuntime().exec("/system/bin/ip route list table 0");
            reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
            String temp = null;
            while (!TextUtils.isEmpty(temp = reader.readLine())){
                results.add(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
            cmdProcess.destroy();
            return results;
        }
    }

    //上图中子网掩码换算
    public static String getMaskUtil(int lenth) {
        lenth = (-1 >> (31 - (lenth - 1))) << (31 - (lenth - 1));
        StringBuilder maskStr = new StringBuilder();
        byte[] maskIp = new byte[4];
        for (int i = 0; i < maskIp.length; i++) {
            maskIp[i] = (byte) (lenth >> (maskIp.length - 1 - i) * 8);
            maskStr.append((maskIp[i] & 0xff));
            if (i < maskIp.length - 1) {
                maskStr.append(".");
            }
        }
        return maskStr.toString();
    }

    public static Object newInstance(String className, Class<?>[] parameterClasses, Object[] parameterValues) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        Class<?> clz = Class.forName(className);
        Constructor<?> constructor = clz.getConstructor(parameterClasses);
        return constructor.newInstance(parameterValues);
    }

    private static Object callMethod(Object object, String methodName, String[] parameterTypes, Object[] parameterValues) throws ClassNotFoundException, IllegalAccessException,IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        Class<?>[] parameterClasses = new Class<?>[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterClasses[i] = Class.forName(parameterTypes[i]);
        }
        return callMethod(object,methodName,parameterClasses,parameterValues,1);
    }

    private static Object callMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameterValues, int i) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        Method method = null;
        if(parameterTypes == null || parameterValues == null){
            method = object.getClass().getDeclaredMethod(methodName);
        }
        else{
            method = object.getClass().getDeclaredMethod(methodName, parameterTypes);
        }
        if(!method.isAccessible()){
            method.setAccessible(true);
        }
        return method.invoke(object, parameterValues);
    }

    private static final String KEY_IP = "ip";
    private static final String KEY_GATEWAY = "gateway";
    private static final String KEY_MASK = "mask";
    /**
     * ip， gateway，mask
     * @param ipJson
     * @return
     */
    public static boolean setStaticIP(String ipJson){
        boolean isSupport = EthernetUtilV25.getInstance().isAvailable();
        if(!isSupport){
            Log.w(TAG,"device is not support ethernet");
            return false;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(ipJson);
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG,"setEthernetInfo param is not json");
            return false;
        }

        IpConfiguration configuration = new IpConfiguration();
        StaticIpConfiguration staticIpConfiguration = configuration.staticIpConfiguration;
        if(staticIpConfiguration == null){
            staticIpConfiguration = new StaticIpConfiguration();
        }
        configuration.setIpAssignment(IpConfiguration.IpAssignment.STATIC);
        try {
            Object linkAddress = EthernetUtilV25.newInstance("android.net.LinkAddress",
                    new Class<?>[]{InetAddress.class, int.class},
                    new Object[]{NetworkUtils.numericToInetAddress(jsonObject.getString(KEY_IP)), 24});
            staticIpConfiguration.ipAddress = (LinkAddress) linkAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        staticIpConfiguration.gateway = NetworkUtils.numericToInetAddress(jsonObject.getString(KEY_GATEWAY));
        staticIpConfiguration.domains = jsonObject.getString(KEY_MASK);
        configuration.setStaticIpConfiguration(staticIpConfiguration);
        EthernetUtilV25.getInstance().setIPConfiguration(configuration);
        return true;
    }

    public static String getStaticIP(){
        boolean isSupport = EthernetUtilV25.getInstance().isAvailable();
        if(!isSupport){
            Log.w(TAG,"device is not support ethernet");
            return "";
        }
        IpConfiguration configuration = EthernetUtilV25.getInstance().getIPConfiguration();
        Log.w(TAG,"2 staticIpConfiguration == " + configuration.staticIpConfiguration);
        if(configuration.staticIpConfiguration == null){
            Log.w(TAG,"ethernet model is not static");
            return "";
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_IP,configuration.staticIpConfiguration.ipAddress.getAddress().getHostAddress());
        jsonObject.put(KEY_GATEWAY,configuration.staticIpConfiguration.gateway.getHostAddress());
        jsonObject.put(KEY_MASK,configuration.staticIpConfiguration.domains);
        return jsonObject.toJSONString();
    }

    public static int getMaskLength(String mask){
        String[] masks = mask.split("\\.");
        int maskLength = 0;
        for(String address : masks){
            if(TextUtils.isEmpty(address)){
                continue;
            }
            int data = Integer.parseInt(address);
            Log.w("test_bug","data = " + data);
            if(data == 0){
                continue;
            }
            String s = Integer.toBinaryString(data);
            Log.w("test_bug","s = " + s);
            maskLength += s.length();
        }
        return maskLength;
    }

    public static void setEthStaticIp(String ipAddress,int maskLength,String gateway,String dns){
        IpConfiguration configuration = new IpConfiguration();
        StaticIpConfiguration staticIpConfiguration = configuration.staticIpConfiguration;
        if(staticIpConfiguration == null){
            staticIpConfiguration = new StaticIpConfiguration();
        }
        configuration.setIpAssignment(IpConfiguration.IpAssignment.STATIC);
        try {
            Object linkAddress = EthernetUtilV25.newInstance("android.net.LinkAddress",
                    new Class<?>[]{InetAddress.class, int.class},
                    new Object[]{NetworkUtils.numericToInetAddress(ipAddress), maskLength});
            staticIpConfiguration.ipAddress = (LinkAddress) linkAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        staticIpConfiguration.gateway = NetworkUtils.numericToInetAddress(gateway);
        if(!TextUtils.isEmpty(dns)){
            staticIpConfiguration.dnsServers.add(NetworkUtils.numericToInetAddress(dns));
        }
        configuration.setStaticIpConfiguration(staticIpConfiguration);
        EthernetUtilV25.getInstance().setIPConfiguration(configuration);
    }

}
