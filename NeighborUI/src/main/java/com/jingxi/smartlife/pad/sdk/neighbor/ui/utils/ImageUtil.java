package com.jingxi.smartlife.pad.sdk.neighbor.ui.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ImageUtil {
    public static class ImageSize {
        public int width = 0;
        public int height = 0;

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public static Observable<File> getImageFileFromUri(final ContentResolver cr,Uri uri){
        return Observable.just(uri)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map(new Function<Uri, File>() {
                    @Override
                    public File apply(Uri uri) throws Exception {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        File tempFile = FileAccessor.getTempFile("jpg");
                        if(tempFile.exists()){
                            tempFile.delete();
                        }
                        else {
                            tempFile.createNewFile();
                        }
                        FileOutputStream outputStream = new FileOutputStream(tempFile);
                        bitmap.compress(CompressFormat.JPEG,100,outputStream);
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return tempFile;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    public final static float MAX_IMAGE_RATIO = 5f;

    public static File getScaledImageFileWithMD5(File imageFile, String mimeType) {
        String filePath = imageFile.getPath();

        if (!isInvalidPictureFile(mimeType)) {
            return null;
        }

        File tempImageFile = FileAccessor.getTempFile(LibAppUtils.getExtensionName(filePath));
        if (tempImageFile == null) {
            return null;
        }

        CompressFormat compressFormat = CompressFormat.JPEG;
        // 压缩数值由第三方开发者自行决定
        int maxWidth = 720;
        int quality = 60;

        if (scaleImage(imageFile, tempImageFile, maxWidth, compressFormat, quality)) {
            return tempImageFile;
        } else {
            return null;
        }
    }

    public static Boolean scaleImage(File srcFile, File dstFile, int dstMaxWH, CompressFormat compressFormat, int quality) {
        Boolean success = false;

        try {
            int inSampleSize = SampleSizeUtil.calculateSampleSize(srcFile.getAbsolutePath(), dstMaxWH * dstMaxWH);
            Bitmap srcBitmap = BitmapDecoder.decodeSampled(srcFile.getPath(), inSampleSize);
            if (srcBitmap == null) {
                return success;
            }

            // 旋转
            ExifInterface localExifInterface = new ExifInterface(srcFile.getAbsolutePath());
            int rotateInt = localExifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            float rotate = getImageRotate(rotateInt);

            Bitmap dstBitmap;
            float scale = (float) Math.sqrt(((float) dstMaxWH * (float) dstMaxWH) / ((float) srcBitmap.getWidth() * (float) srcBitmap.getHeight()));
            if (rotate == 0f && scale >= 1) {
                dstBitmap = srcBitmap;
            } else {
                try {
                    Matrix matrix = new Matrix();
                    if (rotate != 0) {
                        matrix.postRotate(rotate);
                    }
                    if (scale < 1) {
                        matrix.postScale(scale, scale);
                    }
                    dstBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
                } catch (OutOfMemoryError e) {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dstFile));
                    srcBitmap.compress(compressFormat, quality, bos);
                    bos.flush();
                    bos.close();
                    success = true;

                    if (!srcBitmap.isRecycled()) {
                        srcBitmap.recycle();
                    }
                    srcBitmap = null;

                    return success;
                }
            }

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dstFile));
            dstBitmap.compress(compressFormat, quality, bos);
            bos.flush();
            bos.close();
            success = true;

            if (!srcBitmap.isRecycled()) {
                srcBitmap.recycle();
            }
            srcBitmap = null;

            if (!dstBitmap.isRecycled()) {
                dstBitmap.recycle();
            }
            dstBitmap = null;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 获得旋转角度
     *
     * @param rotate
     * @return
     */
    public static float getImageRotate(int rotate) {
        float f;
        if (rotate == 6) {
            f = 90.0F;
        } else if (rotate == 3) {
            f = 180.0F;
        } else if (rotate == 8) {
            f = 270.0F;
        } else {
            f = 0.0F;
        }

        return f;
    }

    public static String makeThumbnail(File imageFile) {
        File thumbFile = FileAccessor.getTempFile(imageFile.getName());
        if (thumbFile == null) {
            return null;
        }

        boolean result = scaleThumbnail(
                imageFile,
                thumbFile,
                (int) (165.0 / 320.0 * DisplayUtil.getScreanWidth()),
                (int) (76.0 / 320.0 * DisplayUtil.getScreanWidth()),
                CompressFormat.JPEG,
                60);
        if (!result) {
            imageFile.delete();
            return null;
        }

        return thumbFile.getAbsolutePath();
    }

    public static Boolean scaleThumbnail(File srcFile, File dstFile, int dstMaxWH, int dstMinWH, CompressFormat compressFormat, int quality) {
        Boolean bRet = false;
        Bitmap srcBitmap = null;
        Bitmap dstBitmap = null;
        BufferedOutputStream bos = null;

        try {
            int[] bound = BitmapDecoder.decodeBound(srcFile);
            ImageSize size = getThumbnailDisplaySize(bound[0], bound[1], dstMaxWH, dstMinWH);
            srcBitmap = BitmapDecoder.decodeSampled(srcFile.getPath(), size.width, size.height);

            // 旋转
            ExifInterface localExifInterface = new ExifInterface(srcFile.getAbsolutePath());
            int rotateInt = localExifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            float rotate = getImageRotate(rotateInt);

            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);

            float inSampleSize = 1;

            if (srcBitmap.getWidth() >= dstMinWH && srcBitmap.getHeight() <= dstMaxWH
                    && srcBitmap.getWidth() >= dstMinWH && srcBitmap.getHeight() <= dstMaxWH) {
                //如果第一轮拿到的srcBitmap尺寸都符合要求，不需要再做缩放
            } else {
                if (srcBitmap.getWidth() != size.width || srcBitmap.getHeight() != size.height) {
                    float widthScale = (float) size.width / (float) srcBitmap.getWidth();
                    float heightScale = (float) size.height / (float) srcBitmap.getHeight();

                    if (widthScale >= heightScale) {
                        size.width = srcBitmap.getWidth();
                        size.height /= widthScale;//必定小于srcBitmap.getHeight()
                        inSampleSize = widthScale;
                    } else {
                        size.width /= heightScale;//必定小于srcBitmap.getWidth()
                        size.height = srcBitmap.getHeight();
                        inSampleSize = heightScale;
                    }
                }
            }

            matrix.postScale(inSampleSize, inSampleSize);

            if (rotate == 0 && inSampleSize == 1) {
                dstBitmap = srcBitmap;
            } else {
                dstBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, size.width, size.height, matrix, true);
            }

            bos = new BufferedOutputStream(new FileOutputStream(dstFile));
            dstBitmap.compress(compressFormat, quality, bos);
            bos.flush();
            bRet = true;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (srcBitmap != null && !srcBitmap.isRecycled()) {
                srcBitmap.recycle();
                srcBitmap = null;
            }

            if (dstBitmap != null && !dstBitmap.isRecycled()) {
                dstBitmap.recycle();
                dstBitmap = null;
            }
        }
        return bRet;
    }

    public static ImageSize getThumbnailDisplaySize(float srcWidth, float srcHeight, float dstMaxWH, float dstMinWH) {
        if (srcWidth <= 0 || srcHeight <= 0) { // bounds check
            return new ImageSize((int) dstMinWH, (int) dstMinWH);
        }

        float shorter;
        float longer;
        boolean widthIsShorter;

        //store
        if (srcHeight < srcWidth) {
            shorter = srcHeight;
            longer = srcWidth;
            widthIsShorter = false;
        } else {
            shorter = srcWidth;//313
            longer = srcHeight;//556
            widthIsShorter = true;
        }

        if (shorter < dstMinWH) {
            float scale = dstMinWH / shorter;
            shorter = dstMinWH;
            if (longer * scale > dstMaxWH) {
                longer = dstMaxWH;
            } else {
                longer *= scale;
            }
        } else if (longer > dstMaxWH) {
            float scale = dstMaxWH / longer;
            longer = dstMaxWH;
            if (shorter * scale < dstMinWH) {
                shorter = dstMinWH;
            } else {
                shorter *= scale;
            }
        }

        //restore
        if (widthIsShorter) {
            srcWidth = shorter;
            srcHeight = longer;
        } else {
            srcWidth = longer;
            srcHeight = shorter;
        }

        return new ImageSize((int) srcWidth, (int) srcHeight);
    }

    public static boolean isInvalidPictureFile(String mimeType) {
        String lowerCaseFilepath = mimeType.toLowerCase();
        return (lowerCaseFilepath.contains("jpg") || lowerCaseFilepath.contains("jpeg")
                || lowerCaseFilepath.toLowerCase().contains("png") || lowerCaseFilepath.toLowerCase().contains("bmp") || lowerCaseFilepath
                .toLowerCase().contains("gif"));
    }
}
