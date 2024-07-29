package com.jingxi.lib.jbind;

import android.view.View;

import java.lang.reflect.Field;

public class JBind {

    public static void bind(Object object, View rootView,Class R_id){
        Class cls = object.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(JView.class)) {
                bindView(object,field,rootView,R_id);
            }
            if (field.isAnnotationPresent(JClick.class)){
                bindClick(object,field);
            }
        }
    }

    private static void bindView(Object object, Field field,View rootView,Class R_id){
        String fieldName = field.getName();
        int id = 0;
        try {
            id = (int) R_id.getField(fieldName).get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (id > 0) {
            field.setAccessible(true);
            try {
                field.set(object, rootView.findViewById(id));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void bindClick(Object object, Field field){
        if(object == null || field == null){
            return;
        }
        if(!(object instanceof View.OnClickListener)){
            return;
        }
        try {
            field.setAccessible(true);
            Object view = field.get(object);
            if(!(view instanceof View)){
                return;
            }
            ((View) view).setOnClickListener((View.OnClickListener) object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
