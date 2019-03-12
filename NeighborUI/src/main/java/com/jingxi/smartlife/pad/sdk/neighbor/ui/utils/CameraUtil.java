package com.jingxi.smartlife.pad.sdk.neighbor.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;

import java.io.File;

/**
 * 相机
 *
 * @author HJK
 */
public class CameraUtil {

    public static int CAMERA_REQUEST_CODE = 111;
    public static int PHOTO_REQUEST_CODE = 222;
    public static Uri saveUri;
    public static String imageFile;

    public static String getImageFile() {
        return imageFile;
    }

    public static void goToCam(Context context) {
        Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File file = FileAccessor.getTackPicFilePath();
        if (file != null) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        Uri uri = Uri.fromFile(file);
        saveUri = uri;
        if (uri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        imageFile = file.getAbsolutePath();
        ((Activity)context).startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public static void pickPhoto(Context context){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        ((Activity)context).startActivityForResult(Intent.createChooser(intent, StringUtils.getString(R.string.select_picture)), PHOTO_REQUEST_CODE);
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
