package com.jingxi.smartlife.pad.sdk.neighbor.ui.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class LibAppUtils {

    /**
     * 设置当前语言
     *
     * @param context
     * @param languageCodeNew
     */
    public static void updateLanguage(Context context, String languageCodeNew) {
        String languageCodeOld = getCurrentPhoneLanguage(context);
        // 获得res资源对象
        Resources resources = context.getResources();
        // 获得设置对象
        Configuration config = resources.getConfiguration();
        // 获得屏幕参数：主要是分辨率，像素等。
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (!TextUtils.isEmpty(languageCodeNew)) {
            Locale locale = getLanguageLocale(languageCodeNew);
            if (locale != null) {
                config.locale = locale;
            }
//            if (languageCodeOld.equals(config.locale.getLanguage())) {
//                return;
//            }
            Locale.setDefault(config.locale);
            resources.updateConfiguration(config, dm);
        }
    }

    /**
     * 获取当前系统语言
     *
     * @param context
     * @return
     */
    public static String getCurrentPhoneLanguage(Context context) {

        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language;
    }

    private static Locale getLanguageLocale(String languageCode) {
        if (!TextUtils.isEmpty(languageCode)) {
            if (languageCode.equals("zh")) {
                return Locale.CHINA; // 简体中文
            } else if (languageCode.equals("en")) {
                return Locale.ENGLISH; // 英文
            }
//            else if (languageCode.equals("0007")) {
//                return new Locale("ru", "RU");
//            } else if (languageCode.equals("0034")) {
//                return new Locale("es", "ES");
//            }
        }
        return Locale.ENGLISH;
    }

    /**
     * 获取手机内部可用空间大小
     *
     * @return 大小，字节为单位
     */
    @SuppressLint("NewApi")
    static public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        //获取可用区块数量
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部空间总大小
     *
     * @return 大小，字节为单位
     */
    @SuppressLint("NewApi")
    static public long getTotalInternalMemorySize() {
        //获取内部存储根目录
        File path = Environment.getDataDirectory();
        //系统的空间描述类
        StatFs stat = new StatFs(path.getPath());
        //每个区块占字节数
        long blockSize = stat.getBlockSize();
        //区块总数
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    public static Date getDateToString(String DateStr, String template) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(template, JXContextWrapper.context.getResources().getConfiguration().locale);
        return sdf.parse(DateStr);
    }

    public static String loadAss(String fileName) {
        String localCache = "";
        try {
            InputStream inputStream = JXContextWrapper.context.getAssets().open(fileName);
            StringBuilder builder = new StringBuilder();
            byte[] bb = new byte[1024];
            int k = -1;
            while ((k = inputStream.read(bb)) != -1) {
                builder.append(new String(bb, 0, k));
            }
            localCache = builder.toString();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localCache;
    }

    /**
     * 小数点保留两位小数
     */
    public static String twoDecimal(Double price) {
        DecimalFormat df = new DecimalFormat("##0.00");
        return df.format(price);
    }

    /**
     * 小数点保留两位小数
     */
    public static double twoDecimalDouble(double price) {
        return new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 小数点保留两位小数
     */
    public static String twoAfterPoint(String price) {
        if (price.contains(".")) {
            if (price.substring(price.indexOf("."), price.length()).length() >= 3) {
                DecimalFormat df = new DecimalFormat("##0.00");
                return df.format(Double.parseDouble(price));
            } else {
                return price;
            }
        } else {
            return price;
        }
    }

    /**
     * pad调用自定义拍照
     */
    public static void toRKCamera(Activity activity, String packageName, String className, int requestCode, int uploaded) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        intent.putExtra("uploaded", uploaded);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo runningServiceInfo : myList) {
            if (TextUtils.equals(runningServiceInfo.service.getClassName(), serviceName)) {
                isWork = true;
            }
        }
        return isWork;
    }

    /**
     * 删除一个文件夹和里面的所有的文件
     */
    public static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                deleteFile(childFiles[i]);
            }
            file.delete();
        }
    }

    public static Bitmap getBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        //利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        //把view中的内容绘制在画布上
        v.draw(canvas);
        return bitmap;
    }

    public static Long getTimetoLong(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date1 = null;
        try {
            date1 = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1.getTime();
    }

    public static byte[] getByteFromBitmap(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        return baos.toByteArray();
    }

    public static Bitmap getPhotoByteToBitmap(byte[] photo) {
        if (photo.length != 0) {
            return BitmapFactory.decodeByteArray(photo, 0, photo.length);
        } else {
            return null;
        }
    }

    public static String getTimeDataToString(long date, String type) {

        return new SimpleDateFormat(type, JXContextWrapper.context.getResources().getConfiguration().locale).format(new Date(date));
    }

    public static String getTimeDataToString(String date, String type) {
        try {
            long long_date = Long.parseLong(date);
            return getTimeDataToString(long_date, type);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    //图片尺寸裁剪
    public static Bitmap ScaleBitmap(Bitmap bitmap, int maxHeight, int maxLength) {
        if (null == bitmap || (bitmap.getWidth() <= maxLength && bitmap.getHeight() <= maxHeight)) {
            return bitmap;
        }
        Bitmap result = null;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();
        maxLength = widthOrg > maxLength ? maxLength : widthOrg;
        maxHeight = heightOrg > maxHeight ? maxHeight : heightOrg;
        //从图中截取正中间的部分。
        int xTopLeft = Math.abs((maxLength - widthOrg) / 2);
        int yTopLeft = Math.abs((maxHeight - heightOrg) / 2);
        try {
            result = Bitmap.createBitmap(bitmap, xTopLeft, yTopLeft, maxLength, maxHeight);
            bitmap.recycle();
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    //图片尺寸缩放
    //created by wudi
    public static Bitmap resizeBaseWidth(Bitmap bitmap, float width) {
        Matrix matrix = new Matrix();
        matrix.postScale((width * 1f) / bitmap.getWidth(), (width * 1f) / bitmap.getWidth()); //长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    //压缩图片
    public static byte[] compressBitmap(Bitmap bitmap, int maxSize) {
        //压缩图片
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        //循环判断如果压缩后图片是否大于maxSize kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > maxSize && options > 0) {
            options = (int) (100 * (float) maxSize / (float) (baos.toByteArray().length / 1024));//每次都减少10
            if (options == 100) {
                options = 99;
            }
            baos.reset();//重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        bitmap.recycle();
        return baos.toByteArray();
    }

    public static int getMax(int[] nums) {
        int max = 0;
        for (int each : nums) {
            if (each > max) {
                max = each;
            }
        }
        return max;
    }

    //获取一个隐藏中间四位的电话
    public static String getInviibleMobile(String mobile) {
        if (TextUtils.isEmpty(mobile) || mobile.length() != 11) {
            return "";
        }
        return TextUtils.concat(mobile.substring(0, 3), "****", mobile.substring(7)).toString();
    }

    //获取App版本号
    public static String getAppVersionCode() {
        PackageManager pm = JXContextWrapper.context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(JXContextWrapper.context.getPackageName(), 0);
            return info.versionName + "." + String.valueOf(info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getMacAddress();
    }


    public static Spannable getTime(int size, String time) {
        Spannable wordtoSpan = new SpannableString(time);
        wordtoSpan.setSpan(new AbsoluteSizeSpan((int) (1.5 * size)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new AbsoluteSizeSpan(size), 2, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return wordtoSpan;
    }

    public static Spannable setDifSizeText(int size1, int size2, String time) {
        Spannable wordtoSpan = new SpannableString(time);
        wordtoSpan.setSpan(new AbsoluteSizeSpan(size1), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new AbsoluteSizeSpan(size2), 2, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return wordtoSpan;
    }

    public static String get36UUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }

    public static String decodeSpecialCharsWhenLikeUseBackslash(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        String afterDecode = content.replaceAll("'", "''");
        afterDecode = afterDecode.replaceAll("/", "//");
        afterDecode = afterDecode.replaceAll("%", "/%");
        afterDecode = afterDecode.replaceAll("_", "/_");
        return afterDecode;
    }


    public static String stringFilter(String str) throws PatternSyntaxException {
        String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5_]";
        return str.replaceAll(regEx, "").trim();
    }

    /**
     * textview显示不同颜色
     *
     * @param context
     * @return
     */
    public static SpannableString setDifTvColoc(Context context, int color, String showText, int start, int end) {
        SpannableString msp = null;
        msp = new SpannableString(showText);
        msp.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return msp;
    }

    /**
     * 设置过个字段颜色
     *
     * @return
     */
    public static SpannableString getTvStyle(Context context, int style, String showText, String... textArg) {
        SpannableString msp = new SpannableString(showText);
        for (String s : textArg) {
            msp.setSpan(new StyleSpan(style), showText.indexOf(s), showText.indexOf(s) + s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return msp;
    }

    public static boolean isMobileNo(String mobileNo) {
        Pattern mobilePattern = Pattern.compile("^((1))\\d{10}$");
        return mobilePattern.matcher(mobileNo).matches();
    }

    public static final String getProcessName() {
        String processName = null;

        // ActivityManager

        ActivityManager am = ((ActivityManager) JXContextWrapper.context.getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;

                    break;
                }
            }
            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }
            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static long lastClickTime;
    private static long lastLibClickTime;

    public static void clearTime() {
        lastClickTime = 0;
    }

    public static void clearLibTime() {
        lastLibClickTime = 0;
    }

    public static int getImageMaxEdge() {
        return (int) (165.0 / 320.0 * DisplayUtil.getScreanWidth());
    }

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 750) {
            lastClickTime = time;
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public synchronized static boolean isLibFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastLibClickTime < 750) {
            lastLibClickTime = time;
            return true;
        }
        lastLibClickTime = time;
        return false;
    }

    public static long getSecondsByMilliseconds(long milliseconds) {
        long seconds = new BigDecimal((float) milliseconds / (float) 1000).setScale(0,
                BigDecimal.ROUND_HALF_UP).intValue();
        return seconds;
    }

    public static float getDensity() {
        return JXContextWrapper.context.getResources().getDisplayMetrics().density;
    }

    public static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    public static String getFormatTime(long time) {
        if (time == 0) {
            return "";
        }
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy年", Locale.getDefault());
        Date date = new Date(time);
        String nowYear = sdfYear.format(new Date(System.currentTimeMillis()));
        try {
            String baseTime = StringUtils.getFriendlyTimeSpanByNow(time);
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

    /**
     * @param time
     * @return 14:51
     */
    public static String getFormatTime2(long time) {
        if (!TextUtils.isEmpty(timeStr.toString())) {
            timeStr.replace(0, timeStr.length(), "");
        }
        if (time < 60) {
            timeStr.append("00:").append(time < 10 ? "0" + time : time);
            return timeStr.toString();
        }
        int hour = (int) (time / 3600);
        if (hour != 0) {
            if (hour < 10) {
                timeStr.append("0");
            }
            timeStr.append(hour);
            timeStr.append(":");
        }
        int yu = (int) (time - (hour * 3600));
        int minute = yu / 60;

        if (minute < 10) {
            timeStr.append("0");
        }
        timeStr.append(minute);


        int yu2 = (int) (time - (hour * 3600 + minute * 60));
        int second = yu2 / 60;

        if (second < 10) {
            timeStr.append("0");
        }
        timeStr.append(second);

        return timeStr.toString();
    }

    public static String getFormatTime(String time) {
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy年", Locale.getDefault());
        Date date = new Date(Long.parseLong(time));
        String nowYear = sdfYear.format(new Date(System.currentTimeMillis()));
        try {
            String baseTime = StringUtils.getFriendlyTimeSpanByNow(Long.parseLong(time));
            if (TextUtils.equals(nowYear, sdfYear.format(date))) {
                return baseTime;
            } else {
                return String.format("%tY ", Long.parseLong(time)) + baseTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "...";
    }

    public static List<String> getImageList(String images) {
        if (null == images) {
            return null;
        } else {
            return Arrays.asList(images.split(","));
        }
    }

    public static String getImageString(List<String> images) {
        StringBuilder result = new StringBuilder();
        for (String each : images) {
            result.append(",");
            result.append(each);
        }
        return 0 != result.length() ? result.substring(1) : null;
    }

    public static String getMessageDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static int counterChars(String str) {
        // return
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            int tmp = (int) str.charAt(i);
            if (tmp > 0 && tmp < 127) {
                count += 1;
            } else {
                count += 2;
            }
        }
        return count;
    }

    public static String getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return simpleDateFormat.format(now.getTime());
    }

    public static long getAvailaleSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize, availableBlocks;
        if (Build.VERSION.SDK_INT > 18) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }
        return (availableBlocks * blockSize) / 1024 / 1024;
    }

    public static String getSn() {
//        return "AUK0BY0ZD4";
        return Build.SERIAL;
//        return "HS1JXY6M12D29000100";
//        return "GR8081707000049";
//        return "GR8081707000021";
    }

    /**
     * 获取型号
     *
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取系统版本号
     *
     * @return
     */
    public static String getSystemModel() {
        return Build.VERSION.RELEASE;
    }

    public static String unitFormat(int i) {
        String retStr;
        if (i >= 0 && i < 10) {
            retStr = "0" + Integer.toString(i);
        } else {
            retStr = "" + i;
        }
        return retStr;
    }

    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f;
        Object obj_get;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                f = imm.getClass().getDeclaredField(param);
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == destContext) {
                        f.set(imm, null);
                    } else {
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static String getImg(String string) {
        if (TextUtils.isEmpty(string)) {
            string = "http://222.com";
        }
        return string;
    }

    public static String unicodeToString(String unicode) {
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;
        while ((i = unicode.indexOf("\\u", pos)) != -1) {
            sb.append(unicode.substring(pos, i));
            if (i + 5 < unicode.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(unicode.substring(i + 2, i + 6), 16));
            }
        }

        return sb.toString();
    }

    public static void setSoftInputMode(Activity activity, int softInputMode) {
        if (null == activity) {
            return;
        }
        Window window = activity.getWindow();
        window.setSoftInputMode(softInputMode);

    }

    public static boolean startAPPByPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = JXContextWrapper.context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return false;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = JXContextWrapper.context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            //不保存历史记录
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
//            Activity activity = JXContextWrapper.context.getLastActivity();
//            if (activity == null || activity.isFinishing()) {
//                return false;
//            }
            JXContextWrapper.context.startActivity(intent);
        }
        return true;
    }

    public static int getAge(String birthDayStr) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date birthDay = sdf.parse(birthDayStr);
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }

    public static String twoNumber(String selectHour) {
        if (selectHour.length() < 2) {
            return "0" + selectHour;
        }
        return selectHour;
    }

    //小数点保留一位小数
    public static String oneDecimal(Double price) {
        DecimalFormat df = new DecimalFormat("##0.0");
        return df.format(price);
    }

    static StringBuilder timeStr = new StringBuilder();

    public static String formatTime(int time) {
        if (!TextUtils.isEmpty(timeStr.toString())) {
            timeStr.replace(0, timeStr.length(), "");
        }
        if (time <= 0) {
            return "00:01";
        }
        time = time + 59;
        int hour = time / 3600;
        if (hour < 10) {
            timeStr.append("0");
        }
        timeStr.append(hour);
        timeStr.append(":");
        int yu = time - (hour * 3600);
        int minute = yu / 60;

        if (minute < 10) {
            timeStr.append("0");
        }
        timeStr.append(minute);
        return timeStr.toString();
    }

    /**
     * 当天指定时间时间戳
     *
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static long getTimeStamp(int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        return cal.getTimeInMillis();
    }


    public static void closeIO(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Drawable getDrawableByRes(@DrawableRes int id) {
        Drawable drawable = ContextCompat.getDrawable(JXContextWrapper.context, id);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        return drawable;
    }

}
