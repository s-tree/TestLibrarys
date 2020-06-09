package com.jingxi.smartlife.pad.sdk.doorServer.base.utils;

import android.text.TextUtils;

import com.jingxi.smartlife.pad.sdk.doorServer.base.AppFinals;
import com.jingxi.smartlife.pad.sdk.doorServer.base.AppSettings;
import com.jingxi.smartlife.pad.sdk.doorServer.base.DoorServerInitUtil;
import com.jingxi.smartlife.pad.sdk.doorServer.base.DoorServerKit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ServerFileUtils {
    private static final String DEFAULT_INET = "[conf]\n" +
            "server={transit}\n" +
            "ssl_cert_private_key=\n" +
            "ssl_cert_path=\n";

    private static final String DEFAULT_SIP_AUTH = "[client]\n" +
            "sip_register_server={sip}\n" +
            "auth_user_name=\n" +
            "auth_user_password=\n" +
            "interval=5\n" +
            "\n" +
            "[proxy]\n" +
            "sip_register_server={sip}\n" +
            "auth_user_name=\n" +
            "auth_user_password=\n" +
            "interval=5\n";

    private static final String DEFAULT_SERVER = "{\"server\":[{\"url\": \"{deployUrl}\",\"key\": \"{deployKey}\"}]}\n";

    private static final int CONF_VERSION = 3;

    public static void checkDefaultFile() {
        File dir = new File(AppSettings.WORK_DIR);
        File tagFile = new File(dir, "default.tag");
        if(tagFile.exists()){
            int version = readFileVersion(tagFile);
            if(version == CONF_VERSION){
                return;
            }
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        copyConf(dir, "conf", "");
        FileUtil.createTagFile(tagFile, CONF_VERSION);
    }

    public static void writeAppVersions(String mainAppVersion,
                                  String intercomAppVersion,
                                  String serverAppVersion){
        File dir = new File(AppSettings.WORK_DIR_CONF);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File ini = new File(dir,AppSettings.ANDOIRD_INI);
        if(ini.exists()){
            ini.delete();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("[ver]\n");
        builder.append("android_main_app_ver=");
        builder.append(mainAppVersion);
        builder.append("\n");
        builder.append("android_intercom_server_ver=");
        builder.append(intercomAppVersion);
        builder.append("\n");
        builder.append("android_intercom_client_ver=");
        builder.append(serverAppVersion);
        builder.append("\n");
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(ini);
            outputStream.write(builder.toString().getBytes());
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static int readFileVersion(File file){
        InputStream inputStream = null;
        try{
            StringBuilder builder = new StringBuilder();
            inputStream = new FileInputStream(file);
            byte[] bytes = new byte[10240];
            int k = 0;
            while ((k = inputStream.read(bytes)) != -1){
                builder.append(new String(bytes,0,k));
            }
            return Integer.parseInt(builder.toString());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    private static void copyConf(File dir, String fileName, String dirName) {
        if (fileName.contains(".")) {
            /**
             * 文件
             */
            File newFile = new File(dir, fileName);
            if(newFile.exists()){
                try {
                    newFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileUtil.startCopyFile(TextUtils.isEmpty(dirName) ? fileName : dirName + "/" + fileName, newFile);
            return;
        }
        try {
            dir = new File(dir, fileName);
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs();
            }
            String path = dirName + (TextUtils.isEmpty(dirName) ? "" : "/") + fileName;
            String[] filesNames = DoorServerInitUtil.getInstance().getContext().getAssets().list(path);
            for (String name : filesNames) {
                copyConf(dir, name, path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initServerUrls(){
        saveFile(DEFAULT_SIP_AUTH,
                new String[]{AppFinals.ASSETS_KEY_SIP}, new String[]{DoorServerKit.getOptions().sipUrl},
                AppSettings.SIP_PATH);
        saveFile(DEFAULT_INET,
                new String[]{AppFinals.ASSETS_KEY_TRANSIT}, new String[]{DoorServerKit.getOptions().transitUrl},
                AppSettings.TRANSIT_PATH);
        saveFile(DEFAULT_SERVER,
                new String[]{AppFinals.ASSETS_KEY_DEPLOY_URL,AppFinals.ASSETS_KEY_DEPLOY_KEY},
                new String[]{DoorServerKit.getOptions().deployUrl,DoorServerKit.getOptions().deployKey},
                AppSettings.DEPLOY_PATH);
    }

    private static void saveFile(String sourceData,String[] regs,String[] datas,String outPath){
        String content = sourceData;
        if(TextUtils.isEmpty(content)){
            return;
        }
        int size = Math.min(regs.length,datas.length);
        for(int i = 0 ; i < size; i++){
            content = content.replaceAll(regs[i],datas[i]);
        }
        FileUtil.saveFile(outPath,content);
    }

    private static String readAssets(String fileName){
        InputStream reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = DoorServerInitUtil.getInstance().getContext().getAssets().open(fileName);
            byte[] bytes = new byte[20480];
            int k;
            while ((k = reader.read(bytes)) != -1) {
                builder.append(new String(bytes, 0, k));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
}
