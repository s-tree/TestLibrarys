package com.jingxi.lib.paging.util;

import android.text.TextUtils;

import com.jingxi.lib.paging.source.BaseCachePagingSource;
import com.jingxi.lib.paging.source.BasePagingSource;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldUtil {

    public static <T> BasePagingSource<T> cloneNew(BasePagingSource<T> oldInstance){
        Class<? extends BasePagingSource> _class = oldInstance.getClass();
        try {
            BasePagingSource<T> instance = _class.newInstance();
            if(oldInstance instanceof BaseCachePagingSource){
                Class tempClass = _class;
                while (tempClass != null && !TextUtils.equals(tempClass.getSimpleName(),BaseCachePagingSource.class.getSimpleName())){
                    cloneField(oldInstance,_class,instance);
                    tempClass = tempClass.getSuperclass();
                }
                cloneField(oldInstance,tempClass,instance);
            }
            else{
                cloneField(oldInstance,_class,instance);
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static void cloneField(Object oldInstance,Class _class,Object newInstance){
        if(_class == null){
            return;
        }
        Field[] fields = _class.getDeclaredFields();
        if(fields == null || fields.length == 0){
            return;
        }
        for(Field field : fields){
            field.setAccessible(true);
            if((field.getModifiers() & Modifier.FINAL) == Modifier.FINAL){
                /**
                 * final
                 */
                continue;
            }
            if((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC){
                /**
                 * final
                 */
                continue;
            }
            try {
                field.set(newInstance,field.get(oldInstance));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
