package com.jingxi.smartlife.pad.sdk.neighbor.ui.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.UUID;


public class FileAccessor {

    private static final String FILE_SUFFIX_JPG = ".jpg";

    /**
     * 获取一个随机文件名
     *
     * @return
     */
    public static final String getRandomJPGFile() {
        return TextUtils.concat(UUID.randomUUID().toString(), FILE_SUFFIX_JPG).toString();
    }

    /**
     * 是否有外存卡
     */
    public static boolean isExistExternalStore() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 外置存储卡的路径
     */
    public static String getExternalStorePath() {
        if (isExistExternalStore()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取相机返回的照片
     * @return
     */
    public static File getTackPicFilePath() {
        File localFile = new File(getExternalStorePath() + "/jingxi/Picture", "temp.jpg");
        if ((!localFile.getParentFile().exists())
                && (!localFile.getParentFile().mkdirs())) {
            localFile = null;
        }
        return localFile;
    }

    /**
     * 获取相机返回的照片
     * @return
     */
    public static File getTempFile(String extension) {
        String name = "temp_image_" + UUID.randomUUID().toString() + "." + extension;
        File localFile = new File(getExternalStorePath() + "/jingxi/temp", name);
        if ((!localFile.getParentFile().exists())
                && (!localFile.getParentFile().mkdirs())) {
            localFile = null;
        }
        return localFile;
    }

    public static File getPhoto(String path) {
        File imageFile = new File(path);
        String mimeType = LibAppUtils.getExtensionName(path);
        imageFile = ImageUtil.getScaledImageFileWithMD5(imageFile, mimeType);
        if (imageFile == null) {
            return null;
        } else {
            ImageUtil.makeThumbnail(imageFile);
        }
        return imageFile;
    }
}
