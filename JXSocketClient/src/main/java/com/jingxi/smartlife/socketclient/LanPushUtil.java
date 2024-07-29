package com.jingxi.smartlife.socketclient;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LanPushUtil {

    public static final int SEND_PORT = 35865;
    /**
     * 消息类型
     */
    public static final String KEY_TYPE = "eventType";
    /**
     * 消息数据
     */
    public static final String KEY_DATA = "eventData";
    /**
     * 报警消息第一条的报警时间
     * 用于取消报警时批量处理
     */
    public static final String KEY_START_TIME = "keyStartTime";
    /**
     * 当前报警的时间
     */
    public static final String KEY_TIMESTAMP = "timestamp";
    /**
     * 报警的数据(报警或者取消报警)
     */
    private static final String KEY_VALUE = "keyValue";
    /**
     * 报警房间房号
     */
    private static final String KEY_ROOM_NUMBER = "keyRoomNumber";
    private static Executor executor;

    private static long startAlarmTime = 0;

    private static final String TYPE_ALARM = "alarm";
    private static final int VALUE_ALARM = 1;
    private static final int VALUE_CANCEL = 0;

    /**
     * 发送报警数据
     * @param ipAddress
     * @param roomNumber
     * @param object
     */
    public static void sendAlarmMessage(String ipAddress,String roomNumber,Object object){
        if(startAlarmTime == 0){
            startAlarmTime = System.currentTimeMillis();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_TYPE,TYPE_ALARM);
        jsonObject.put(KEY_DATA,object);
        jsonObject.put(KEY_START_TIME,startAlarmTime);
        jsonObject.put(KEY_TIMESTAMP,System.currentTimeMillis());
        jsonObject.put(KEY_VALUE,VALUE_ALARM);
        jsonObject.put(KEY_ROOM_NUMBER,roomNumber);
        sendMessage(ipAddress,jsonObject.toJSONString().getBytes());
    }

    /**
     * 发送取消报警数据
     * @param ipAddress
     * @param roomNumber
     */
    public static void sendCancelAlarmMessage(String ipAddress, String roomNumber){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_TYPE,TYPE_ALARM);
        jsonObject.put(KEY_START_TIME,startAlarmTime);
        jsonObject.put(KEY_VALUE,VALUE_CANCEL);
        jsonObject.put(KEY_ROOM_NUMBER,roomNumber);
        sendMessage(ipAddress,jsonObject.toJSONString().getBytes());
        startAlarmTime = 0;
    }

    /**
     * 发送取消报警数据
     * @param ipAddress
     * @param roomNumber
     */
    public static void sendCancelAlarmMessage(List<String> ipAddress, String roomNumber){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_TYPE,TYPE_ALARM);
        jsonObject.put(KEY_START_TIME,startAlarmTime);
        jsonObject.put(KEY_VALUE,VALUE_CANCEL);
        jsonObject.put(KEY_ROOM_NUMBER,roomNumber);
        for(String ip : ipAddress){
            if(TextUtils.isEmpty(ip)){
                continue;
            }
            sendMessage(ip,jsonObject.toJSONString().getBytes());
        }
        startAlarmTime = 0;
    }

    public static void sendMessage(String ipAddress,String type,Object object){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_TYPE,type);
        jsonObject.put(KEY_DATA,object);
        sendMessage(ipAddress,jsonObject.toJSONString().getBytes());
    }

    public static void sendMessage(String ip,byte[] bytes){
        getExcutor().execute(new SendRunnable(ip,bytes));
    }

    public static boolean sendWithResult(String ip,byte[] bytes){
        return sendData(ip,bytes);
    }

    private static Executor getExcutor(){
        if(executor == null){
            synchronized (LanPushUtil.class){
                if(executor == null){
                    executor = Executors.newCachedThreadPool();
                }
            }
        }
        return executor;
    }

    private static class SendRunnable implements Runnable{
        String ip;
        byte[] bytes;

        public SendRunnable(String ip, byte[] bytes) {
            this.ip = ip;
            this.bytes = bytes;
        }

        @Override
        public void run() {
            sendData(ip,bytes);
        }
    }

    private static boolean sendData(String ip,byte[] bytes){
        Socket socket = null;
        try {
            socket = new Socket(ip,SEND_PORT);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(socket != null && !socket.isClosed()){
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
