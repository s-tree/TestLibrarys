# 慧管家室内机社区服务器部署方案文档

## 更新日志
+ 1.0.1(2020-11-11)
  + 第一版更新

Demo 地址 https://github.com/s-tree/JxSDKDemo/tree/master/app/src/main/java/com/jingxi/smartlife/pad/sdk/demo/configure
___

## 一  方案流程
室内机通过社区服务区获取到该社区的房间及设备信息表，之后将该表已层级方式展示给用户，选中到设备所在房间后将所有关联设备写入配置

## 二  接入准备
1.项目的build.gradle 中加入仓库url
```
maven { url "https://raw.githubusercontent.com/s-tree/JxRepository/master/releases/"}
```

2.在app的build.gradle 中导入项目
```
compile "com.jingxi.smartlife.pad.sdk:configure:1.0.1"
```

3.导入 fastJson,rxJava2,rxAndroid2,OKHttp3 必要库
```
compile 'com.alibaba:fastjson:1.2.59'
compile 'com.squareup.okhttp3:okhttp:3.12.0'
compile "io.reactivex.rxjava2:rxjava:2.2.6"
compile 'io.reactivex.rxjava2:rxandroid:2.1.1'
```

4.所需权限
```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission android:name="android.permission.REBOOT"/>
```

## 三  接入项目
1.初始化数据存储
```
ConfigureManager.initConfigure(this);
```

2.设置社区服务器地址（此例中的 192.168.125.243:8089 为demo地址，实际ip可能不一样）
```
ConfigureManager.setServerIp("192.168.125.243:8089");
```

3.请求社区配置信息
```
ConfigureManager.queryCommunityConfs();
```

4.解析社区配置信息
```
ConfigureManager.parseCommunityInfo(String data);
```

4.根据解析的社区配置信息下载社区房号表并转化为层级的房间信息
```
String data = ConfigureManager.downloadConf(communityInfo.getRoomFile())
List<RoomBean> roomBeanList = ConfigureManager.decodeDownloadedConf(data);
```

5.创建一个Adapter，继承自 BaseConfigureAdapter,实现ui部分的展示

6.选择到具体的房间后调用部署，部署成功后需要重启设备才能生效网口的静态ip配置
```
boolean isSuccess = ConfigureManager.saveConfig(configureAdapter);
if(isSuccess){
    ConfigureManager.reboot();
}
```

7.判断设备是否部署成功
```
boolean isConfigured = ConfigureManager.isConfigured();
```
