package com.jingxi.smartlife.pad.sdk.doorServer.base.utils;

import com.jingxi.smartlife.pad.sdk.doorServer.base.DoorServerInitUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtil {
    public static String getMD5Checksum(File file) {
        if (!file.isFile()) {
            return null;
        }
        byte[] b = createChecksum(file);
        if(null == b){
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString().toLowerCase();
    }

    private static byte[] createChecksum(File file) {
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead = -1;
            while ((numRead = fis.read(buffer)) != -1) {
                complete.update(buffer, 0, numRead);
            }
            return complete.digest();
        } catch (FileNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException e) {
            }
        }
        return null;
    }

    public static void startCopyFile(String fileName, File outFile) {
        InputStream reader = null;
        OutputStream writer = null;
        try {
            reader = DoorServerInitUtil.getInstance().getContext().getAssets().open(fileName);
            writer = new FileOutputStream(outFile);
            byte[] bytes = new byte[20480];
            int k;
            while ((k = reader.read(bytes)) != -1) {
                writer.write(bytes, 0, k);
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * tagFile 的版本
     * @param tagFile
     * @return  true : 已是新版本
     *           false : 是旧版本
     */
    public static boolean isNewTagFile(File tagFile,int SSH_VERSION){
        if(!tagFile.exists()){
            return false;
        }
        boolean isNew = false;
        FileInputStream fileInputStream = null;
        try{
            fileInputStream = new FileInputStream(tagFile);
            byte[] bytes = new byte[50];
            int k = fileInputStream.read(bytes);
            String data = new String(bytes,k);
            int version = Integer.parseInt(data);
            if(version >= SSH_VERSION){
                isNew = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isNew;
    }

    public static void createTagFile(File tagFile,int SSH_VERSION){
        if(tagFile.exists()){
            try {
                tagFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        FileOutputStream outputStream = null;
        try{
            outputStream = new FileOutputStream(tagFile);
            outputStream.write(String.valueOf(SSH_VERSION).getBytes());
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

    public static void saveFile(String filePath,String data){
        File file = new File(filePath);
        if(file.exists()){
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        FileOutputStream outputStream = null;
        try{
            outputStream = new FileOutputStream(file);
            outputStream.write(data.getBytes());
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
}
