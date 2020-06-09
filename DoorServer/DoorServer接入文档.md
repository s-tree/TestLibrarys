# 慧管家门禁接入文档

|   更新时间   |   更新人   |    版本号    |    更新内容    |
|-------------|-----------|--------------|:-------------------:|
|  2020-5-7  |   卞俊杰   |   1.0.0.1   |   第一次更新   |

___

## 概念介绍
+ sip : 可视对讲需要云端通信时，使用的sip账号管理服务器
+ transit : 可是对讲需要云端通信时，音视频流的转发服务器
+ deploy : 代理端部署服务器

___

## 一 接入
1.项目的build.gradle 中加入仓库url
```
maven { url "https://raw.githubusercontent.com/s-tree/JxRepository/master/releases/"}
```

2.在app的build.gradle 中导入入门禁
```
compile "com.jingxi.smartlife.pad.sdk:doorServer:1.0.0.2"
```

3.导入 gson 和 apache commons库(sdk 内部必要库)
```
compile 'com.alibaba:fastjson:1.2.59'
compile 'com.google.code.gson:gson:2.8.5'
compile 'org.apache.commons:commons-compress:1.18'
```

4.所需权限
```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.RECORD_AUDIO"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_LOGS" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.RAISED_THREAD_PRIORITY" />
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
<uses-permission android:name="android.permission.CONNECTIVITY_INTERNAL"/>
<uses-permission android:name="android.permission.INTERACT_ACROSS_USERS_FULL"/>
<uses-permission android:name="android.permission.INTERACT_ACROSS_USERS"/>
<uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE"/>
<uses-permission android:name="android.permission.WRITE_SETTINGS" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
```

___

## 二 接入
1.设置监听回调
```java
DoorServerManager.getInstance().setServerDeployListener(DoorServerDeployListener serverDeployListener);
```

2.初始化代理
```java
DoorServerManager.getInstance().initServer(Context context);
```

3.开启/关闭代理配置更新服务
```java
DoorServerManager.getInstance().enableDeploy(boolean isEnabled);
```

3.启动服务
```java
DoorAccessManager.getInstance().startFamily(familyId,buttonKey)
```

4.部分参数定制(定制参数后最好重启一下代理)
```java
DoorServerKit.Options options = DoorServerKit.getOptions();
...
DoorServerKit.init(options);
```

5.停止代理
```java
DoorAccessManager.getInstance().uninitServer();
```
