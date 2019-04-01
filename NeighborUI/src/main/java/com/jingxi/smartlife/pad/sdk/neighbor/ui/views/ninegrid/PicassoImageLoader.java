package com.jingxi.smartlife.pad.sdk.neighbor.ui.views.ninegrid;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

public class PicassoImageLoader implements NineGridView.ImageLoader {
    private static LruCache mCache;
    private static Picasso myPicasso;
    public static final int HEAD_IMG_PX = 100;
    public static final int GOD_IMG_PX = 200;
    public static final int NEIGHBOUR_IMG_PX = 500;

    //这30M空间单独划给社区新鲜事和广告图片使用
    public static Picasso getMyPicasso(){
        if(myPicasso == null){
            Picasso.Builder builder = new Picasso.Builder(JXContextWrapper.context);
            mCache = new LruCache(30 * 1024 * 1024 );
            builder.memoryCache(mCache);
            myPicasso = builder.build();
        }
        return myPicasso;
    }

    @Override
    public void onDisplayImage(Context context, ImageView imageView, String url,int width) {
        getMyPicasso().load(checkUrl(url))
                .placeholder(R.mipmap.ic_placeholderimg)//
                .error(R.mipmap.ico_failure)//
                .resize(width,width)
                .centerCrop()
                .config(Bitmap.Config.RGB_565)
                .into(imageView);
    }

    @Override
    public Bitmap getCacheImage(String url) {
        return null;
    }

    public static String checkUrl(String string) {
        if (TextUtils.isEmpty(string)) {
            string = "http://222.com";
        }
        return string;
    }

}
