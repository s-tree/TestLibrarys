package com.intercom.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;

import com.intercom.sdk.BundleToJSON;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkUtils {

    public static String getWifiInfo(Context context) {
        String ssid = getWifiName(context);
        InetAddress address = getWifiApIpAddress();
        String mac = getMacAddress();
        Bundle bundle = new Bundle();
        bundle.putString("ssid", ssid);
        if (address != null) {
            bundle.putString("ip", address.getHostAddress().toString());
        }
        bundle.putString("mac", mac);
        return BundleToJSON.toString(bundle);
    }

    private static InetAddress getWifiApIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                            && (inetAddress.getAddress().length == 4)) {
                            return inetAddress;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }

    private static String getMacAddress() {
        String strMacAddr = null;
        try {
            InetAddress ip = getWifiApIpAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
        }
        return strMacAddr;
    }

    public static String getWifiName(Context context) {
        WifiInfo wifiInfo = null;
        String ssid = null;
        final Intent intent = context.registerReceiver(null, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        if (intent != null) {
            wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
        }
        if (wifiInfo == null) {
            WifiManager wifiManager =
                (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiInfo = wifiManager.getConnectionInfo();
        }
        if (wifiInfo != null) {
            ssid = wifiInfo.getSSID();
        }
        if (ssid == null || ssid.equals("<unknown ssid>")) {
            ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connManager != null;
            NetworkInfo info = connManager.getActiveNetworkInfo();
            if (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI) {
                ssid = info.getExtraInfo();
            }
        }
        if (ssid != null && !ssid.equals("<unknown ssid>")) {
            ssid = ssid.replace("\"", "");
        }

        return ssid;
    }

    public static long getTotalExternalMemorySize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    public static long getExternalFreeSpace() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize, availableBlocks;
        availableBlocks = stat.getAvailableBlocksLong();
        blockSize = stat.getBlockSizeLong();
        long size = availableBlocks * blockSize;
        return size;
    }

    public static String getBatteryInfo(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        Intent receiver = context.registerReceiver(null, filter);
        if (receiver == null)
            return "";

        Bundle bundle = new Bundle();

        int health = receiver.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        bundle.putInt(BatteryManager.EXTRA_HEALTH, health);

        int level = receiver.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        if (level >= 0) {
            int v = (level * 100) /
                receiver.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            bundle.putInt(BatteryManager.EXTRA_LEVEL, v);
        }

        int plugged = receiver.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        bundle.putInt(BatteryManager.EXTRA_PLUGGED, plugged);

        boolean present = receiver.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
        bundle.putBoolean(BatteryManager.EXTRA_PRESENT, present);

        int scale = receiver.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        bundle.putInt(BatteryManager.EXTRA_SCALE, scale);

        int status = receiver.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        bundle.putInt(BatteryManager.EXTRA_STATUS, status);

        String technology = receiver.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
        bundle.putString(BatteryManager.EXTRA_TECHNOLOGY, technology);

        int temperature = receiver.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        bundle.putInt(BatteryManager.EXTRA_TEMPERATURE, temperature);

        int voltage = receiver.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        bundle.putInt(BatteryManager.EXTRA_VOLTAGE, voltage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager manager = (BatteryManager)
                context.getSystemService(Context.BATTERY_SERVICE);
            if (manager != null) {
                int capacity = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                bundle.putInt("capacity", capacity);
                int charge_counter = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                bundle.putInt("charge_counter", charge_counter);
                int current_average = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
                bundle.putInt("current_average", (current_average != Integer.MIN_VALUE) ? current_average : -1);
                long energy_counter = manager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
                bundle.putLong("energy_counter", (energy_counter != Long.MIN_VALUE) ? energy_counter : -1);
                int current_now = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                bundle.putInt("current_now", (current_now != Integer.MIN_VALUE) ? current_now : -1);
            }
        }
        return BundleToJSON.toString(bundle);
    }

    public static int getBatteryLevel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager manager = (BatteryManager)
                context.getSystemService(Context.BATTERY_SERVICE);
            if (manager != null) {
                int capacity = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                return (capacity != Integer.MIN_VALUE) ? capacity : -1;
            }
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);

        Intent receiver = context.registerReceiver(null, filter);
        if (receiver == null)
            return -1;
        int level = receiver.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        if (level < 0)
            return -1;
        return (level * 100) /
            receiver.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    }

    public static int getBatteryPlugged(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);

        Intent receiver = context.registerReceiver(null, filter);
        if (receiver == null)
            return -1;
        return receiver.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
    }

    public static String getProductInfo() {
        Bundle bundle = new Bundle();
        bundle.putString("board", Build.BOARD);
        bundle.putString("brand", Build.BRAND);
        bundle.putString("device", Build.DEVICE);
        bundle.putString("display", Build.DISPLAY);
        bundle.putString("Hardware", Build.HARDWARE);
        bundle.putString("id", Build.ID);
        bundle.putString("Manufacturer", Build.MANUFACTURER);
        bundle.putString("model", Build.MODEL);
        bundle.putString("product", Build.PRODUCT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bundle.putString("serial", Build.getSerial());
        } else {
            bundle.putString("serial", Build.SERIAL);
        }
        bundle.putString("tags", Build.TAGS);
        bundle.putString("type", Build.TYPE);
        bundle.putString("user", Build.USER);
        bundle.putString("radio", Build.getRadioVersion());
        return BundleToJSON.toString(bundle);
    }

    public static boolean setEthernetInfo(Context context, String json) {
        return false;
    }

    public static String getEthernetInfo(Context context) {
        return null;
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }
}
