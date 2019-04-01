package com.jingxi.smartlife.pad.sdk.neighbor.ui.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.StringRes;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/8/16
 *     desc  : 字符串相关工具类
 * </pre>
 */
public class StringUtils {

    /******************** 时间相关常量 ********************/
    /**
     * 秒与毫秒的倍数
     */
    public static final int SEC  = 1000;
    /**
     * 分与毫秒的倍数
     */
    public static final int MIN  = 60000;
    /**
     * 时与毫秒的倍数
     */
    public static final int HOUR = 3600000;
    /**
     * 天与毫秒的倍数
     */
    public static final int DAY  = 86400000;
    public static final String url_and = "&";
    public static final String url_with = "?";
    public static final String url_with_pattern = "\\?";
    public static final String url_is = "=";
    public static final String RMB = "¥";

    //数字提取
    private static final String number_reg = "\\d{0,9}";
    static Pattern number_pattern = Pattern.compile(number_reg);


    /**
     * 获取字符串中的数字
     * @param arg   字符串
     * @return  数字的集合
     */
    public static ArrayList<Integer> getIntegers(String arg){
        if(TextUtils.isEmpty(arg)){
            return null;
        }
        ArrayList<Integer> numbers = new ArrayList<>();
        Matcher matcher = number_pattern.matcher(arg);
        while(matcher.find()){
            String group = matcher.group();
            if(TextUtils.isEmpty(group)){
                continue;
            }
            numbers.add(Integer.parseInt(matcher.group()));
        }
        return numbers;
    }
    private StringUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 判断字符串是否为null或长度为0
     *
     * @param s 待校验字符串
     * @return {@code true}: 空<br> {@code false}: 不为空
     */
    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * 判断字符串是否为null或全为空格
     *
     * @param s 待校验字符串
     * @return {@code true}: null或全空格<br> {@code false}: 不为null且不全空格
     */
    public static boolean isSpace(String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * 判断两字符串是否相等
     *
     * @param a 待校验字符串a
     * @param b 待校验字符串b
     * @return {@code true}: 相等<br>{@code false}: 不相等
     */
    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) {return true;}
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) {return false;}
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 判断两字符串忽略大小写是否相等
     *
     * @param a 待校验字符串a
     * @param b 待校验字符串b
     * @return {@code true}: 相等<br>{@code false}: 不相等
     */
    public static boolean equalsIgnoreCase(String a, String b) {
        return (a == b) || (b != null) && (a.length() == b.length()) && a.regionMatches(true, 0, b, 0, b.length());
    }

    /**
     * 返回字符串长度
     *
     * @param s 字符串
     * @return null返回0，其他返回自身长度
     */
    public static int length(CharSequence s) {
        return s == null ? 0 : s.length();
    }

    /**
     * 获取http 链接中的参数map
     */
    public static ArrayMap<String,String> decodeHttpUrlParam(String url){
        ArrayMap<String,String> arrayMap = new ArrayMap();
        if(TextUtils.isEmpty(url) || !url.contains(url_and)){
            return arrayMap;
        }
        if(url.contains(url_with)){
            url = url.split(url_with_pattern)[1];
        }
        String[] urls = url.split(url_and);
        for(String param : urls){
            String[] params = param.split(url_is);
            if(params == null || params.length < 2){
                continue;
            }
            arrayMap.put(params[0],params[1]);
        }
        return arrayMap;
    }

    /**
     * 根据字符串判断是否为空，否则返回另一个字符串
     * @param source
     * @param messageRes
     * @return
     */
    public static String getNoMessage(String source,@StringRes int messageRes){
        if(TextUtils.isEmpty(source)){
            return getString(messageRes);
        }
        return source;
    }

    public static String getNoMessage(String source,String message){
        if(TextUtils.isEmpty(source)){
            return message;
        }
        return source;
    }
    /**
     * 获取http 链接的头
     */
    public static String getHttpUrlHead(String url){
        if(TextUtils.isEmpty(url) || !url.contains(url_with)){
            return "";
        }
        return url.split(url_with_pattern)[0];
    }

    /**
     * 首字母大写
     *
     * @param s 待转字符串
     * @return 首字母大写字符串
     */
    public static String upperFirstLetter(String s) {
        if (isEmpty(s) || !Character.isLowerCase(s.charAt(0))) {return s;}
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }

    /**
     * 首字母小写
     *
     * @param s 待转字符串
     * @return 首字母小写字符串
     */
    public static String lowerFirstLetter(String s) {
        if (isEmpty(s) || !Character.isUpperCase(s.charAt(0))) {return s;}
        return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
    }

    /**
     * 反转字符串
     *
     * @param s 待反转字符串
     * @return 反转字符串
     */
    public static String reverse(String s) {
        int len = length(s);
        if (len <= 1) {return s;}
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        char c;
        for (int i = 0; i < mid; ++i) {
            c = chars[i];
            chars[i] = chars[len - i - 1];
            chars[len - i - 1] = c;
        }
        return new String(chars);
    }

    /**
     * 转化为半角字符
     *
     * @param s 待转字符串
     * @return 半角字符串
     */
    public static String toDBC(String s) {
        if (isEmpty(s)) {return s;}
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 <= chars[i] && chars[i] <= 65374) {
                chars[i] = (char) (chars[i] - 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * 转化为全角字符
     *
     * @param s 待转字符串
     * @return 全角字符串
     */
    public static String toSBC(String s) {
        if (isEmpty(s)) {return s;}
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = (char) 12288;
            } else if (33 <= chars[i] && chars[i] <= 126) {
                chars[i] = (char) (chars[i] + 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    public static boolean isNewVersion(String newVersion,Context context){
        String myVersion = getAppVersionName(context);
        if(myVersion == null){
            return false;
        }
        if(TextUtils.equals(newVersion, myVersion)){
            return false;
        }
        String[] newVersionArray = newVersion.split("\\.");
        String[] myVersionArray = myVersion.split("\\.");
        int minLength = Math.min(newVersionArray.length,myVersionArray.length);
        int index = 0;
        int buff = 0;
        while(index < minLength &&  (buff = ( Integer.parseInt(newVersionArray[index]) - Integer.parseInt(myVersionArray[index])) ) == 0){
            index++;
        }
        if(buff > 0 || (buff == 0 && minLength == myVersionArray.length)){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     *
     * @param versionName  如 1.0.1
     * @return	   如 1_0_1
     */
    public static String getNewVersionName(String versionName){
        return versionName.replaceAll("\\.", "_");
    }

    public static  String getAppVersionName(Context context){
        try {
            String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMD5Checksum(String filename) {
        if (!new File(filename).isFile()) {
            return null;
        }
        byte[] b = createChecksum(filename);
        if(null == b){
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

    private static byte[] createChecksum(String filename) {
        InputStream fis = null;
        try {
            fis = new FileInputStream(filename);
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

    public static String getFileName(String url){
        if(TextUtils.isEmpty(url)){
            return url;
        }
        String[] strings = url.split("/");
        if(strings.length == 1){
            return url;
        }
        return strings[strings.length - 1];
    }

    public static String getString(@StringRes int stringRes){
        return JXContextWrapper.context.getString(stringRes);
    }

    public static String getString(@StringRes int stringRes,Object... placeString){
        return JXContextWrapper.context.getString(stringRes,placeString);
    }

    public static String getFormatTime(long time) {
        if (time == 0) {
            return "";
        }
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy年", Locale.getDefault());
        Date date = new Date(time);
        String nowYear = sdfYear.format(new Date(System.currentTimeMillis()));
        try {
            String baseTime = getFriendlyTimeSpanByNow(time);
            if (TextUtils.equals(nowYear, sdfYear.format(date))) {
                return baseTime;
            } else {
                return String.format("%tY ", time) + baseTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFriendlyTimeSpanByNow(long millis) {
        long now = System.currentTimeMillis();
        long span = now - millis;
        if (span < 0) {
            return String.format("%tR", millis);
        } else if (span < MIN) {
            return String.format("%tR", millis);
        } else if (span < HOUR) {
            return String.format("%tR", millis);
        }
        // 获取当天00:00
        long wee = (now / DAY) * DAY - 8 * HOUR;
        if (millis >= wee) {
            return String.format("%tR", millis);
        }
        else {
            return String.format("%tm-%td %tR", millis, millis, millis);
        }
    }

    /**
     * unicode 解码
     * @param unicodeStr    带解码字符串
     * @return              解码后字符串
     */
    public static String getStrFromUniCode(String unicodeStr){
        if (unicodeStr == null) {
            return null;
        }
        StringBuffer retBuf = new StringBuffer();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U'))) {
                    try {
                        retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                }
                else {
                    retBuf.append(unicodeStr.charAt(i));
                }
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }

    public static List<String> getImageList(String images) {
        if (null == images) {
            return null;
        } else {
            return Arrays.asList(images.split(","));
        }
    }

    private static long oldClicktime = 0;
    public static boolean isFastClick(){
        long now = System.currentTimeMillis();
        if(now - oldClicktime >= 1000){
            oldClicktime = now;
            return false;
        }
        return true;
    }

    public static boolean isMobileNo(String mobileNo) {
        Pattern mobilePattern = Pattern.compile("^((13[0-9])|(14[0-9])|(15([0-9]))|(17([0-9]))|(18[0-9]))\\d{8}$");
        return mobilePattern.matcher(mobileNo).matches();
    }
    /**
     *
     * @param myVersion     本地版本号
     * @param newVersion    新版本号
     * @return    result  :   是否是新版本
     */
    public static boolean checkNewVersion(String myVersion,String newVersion){
        if(TextUtils.equals(newVersion,myVersion)){
            return false;
        }
        String[] newVersionArray = newVersion.split("\\.");
        String[] myVersionArray = myVersion.split("\\.");
        int minLength = Math.min(newVersionArray.length,myVersionArray.length);
        int index = 0;
        int buff = 0;
        while(index < minLength &&  (buff = ( Integer.parseInt(newVersionArray[index]) - Integer.parseInt(myVersionArray[index])) ) == 0){
            index++;
        }
        if(buff > 0 || (buff == 0 && minLength == myVersionArray.length)){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     *
     * @param size  需要转换的字节大小
     * @return
     */
    public static String formatSize(long size){
        if(size < 1024){
            return size +" B";
        }
        if(size < 1024 * 1024){
            return getSize(size,1024) +" KB";
        }
        else if(size  < 1024 * 1024 * 1024){
            return getSize(size,1024 * 1024) +" MB";
        }
        else{
            return getSize(size,1024 * 1024 * 1024) +" GB";
        }
    }


    /**
     * 剩余内存优化
     *
     * @param size
     * @return
     */
    public static String getMemerySize(long size) {
        if (size < 1024) {
            return size + "B";
        }
        if (size / 1024 < 1024) {
            return size / 1024 + "KB";
        }
        return size / 1024 / 1024 + "M";
    }

    private static float getSize(long size, long xishu){
        int number = (int) (( size * 100) / xishu);
        return (float) number / 100f;
    }
}