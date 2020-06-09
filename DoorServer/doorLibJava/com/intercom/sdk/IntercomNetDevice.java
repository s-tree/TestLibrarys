package com.intercom.sdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * 在代理中配置了一个或者多个设备
 * 包含门禁设备和安防设备，每个设备的基本格式都是一样
 * 在configure中包含了该设备的配置，如果是门禁设备还可能包含子设备的信息
 * 如室外机，围墙机，管理中心机...
 * 智能家居设备应该不包含子设备，configure中只有一些关于该设备的基本配置
 * 设备的类型由scheme来决定，设备的子类别由class来决定
 * 设备的子类表明该设备从同一个类创建，在逻辑和功能上具有一致性
 * 但该设备的具体功能和逻辑还是由它的配置来决定
 * proxy_id:表明该设备归属于哪个代理
 * 在configure中还包含该代理的基本配置，比如网络配置，固件配置，版本和渠道信息
 * 如果有多个定制版本，可以考虑设置不同的渠道编码，在升级的时候，可以根据渠道编码来升级
 * 相同的scheme，class的设备，name一定不同
 * {"devices":
 * [
 * {"alias":"camera",
 * "configure":"W2RldmljZV0NCmF1ZGlvX2R1cGxleD0xDQptdWx0aV9zZXNzaW9uPTENCm51bWJlcl9tYXNrPTIsMiwxLDIsMiwyLDANCnAycF9udW1iZXJfbWFzaz0wLDIsMiwyLDIsMCwwDQpbaXBdDQowPTEwMDBALDAsMCwwDQoxPTEwMDFALOWupOWkluacuiwxLDAsMQ0K","device_class":"camera","name":"dahua","scheme":"icom"},{"alias":"knx","configure":"W2RldmljZV0NCmJyb2FkY2FzdD0wDQpjb25uZWN0X2xpbWl0PTANCmlwX2NvbnZlcnRfYWRkcmVzcz0xMjcuMC4wLjE6ODg4OA0KcmVsYXlfc2VydmVyX3BvcnQ9ODg4Nw0K",
 * "class":"bus",
 * "name":"knx",
 * "scheme":"gateway"
 * },
 * {"alias":"quanshitong",
 * "configure":"W2RldmljZV0NCmF1ZGlvX2R1cGxleD0xDQptdWx0aV9zZXNzaW9uPTENCm51bWJlcl9tYXNrPTIsMiwxLDIsMiwyLDANCnAycF9udW1iZXJfbWFzaz0wLDIsMiwyLDIsMCwwDQpbaXBdDQowPTAxMDExMDUwMTAxQDE5Mi4xNjguMjUwLjUwLOWupOWGheacuiwwLDAsMA0KMT0wMUAxOTIuMTY4LjI1MC44LOeuoeeQhuS4reW/gywxLDEsMg0KMj0wMkAxOTIuMTY4LjI1MC45LOWbtOWimeacuiwxLDAsMw0KMz0wMTAxMTAxQDE5Mi4xNjguMjUwLjEwLOWupOWkluacuiwxLDAsMQ0K",
 * "class":"commonsip",
 * "name":"quanshitong",
 * "scheme":"icom"
 * },
 * {"alias":"security",
 * "configure":"W2RldmljZV0NCmJ1c19hZGRyZXNzPTEyNy4wLjAuMTo2NDAwMQ0KZGV2aWNlX2FsaWFzPeeDn+mbvuS8oOaEn+WZqDvngavnhLDkvKDmhJ/lmag757qi5aSW5YWl5L615Lyg5oSf5ZmoO+mch+WKqOS8oOaEn+WZqDvpl6jno4HkvKDmhJ/lmag75Y+v54eD5rCU5L2T5Lyg5oSf5ZmoO+S4gOawp+WMlueis+S8oOaEn+WZqDvmsLTmtbjkvKDmhJ/lmagNCmRldmljZV90aW1lb3V0PTMwDQpkZXZpY2VfdHlwZT0xOzI7Mzs0OzU7Njs3OzgNCmRpc2FsbG93X2VtZD0wDQpmaWx0ZXI9YWxlcnQsc3dpdGNoLGNhbmNlbCxldmVudCxzdGF0ZQ0KbWF4X2NhY2hlX21lc3NhZ2U9MTANCm9uX2RlbGF5PTMwDQo=",
 * "class":"security",
 * "name":"security",
 * "scheme":"gateway"
 * }
 * ],
 * "firmware":"W2Zpcm13YXJlXQ0Kc3lzLmFwcC5jaGFubmVsPQ0Kc3lzLmFwcC52ZXJzaW9uPQ0Kc3lzLmJ1cz0wLjAuMC4wOjgxMjMNCnN5cy5maXJtd2FyZS50eXBlPQ0Kc3lzLmZpcm13YXJlLnZlcnNpb249DQpzeXMubGFuLm1hYz0yNDo3NzowMzpmNjo3Yjo4MA0K",
 * "proxy_id":"13175671982934774"
 * }
 */

public class IntercomNetDevice {

    public JINIParser firmware;

    public String proxy_id;

    public List<NetDeviceItem> devices;

    public static IntercomNetDevice objectFromData(String str) {
        return new IntercomNetDevice(str);
    }

    private IntercomNetDevice(String str) {
        this.firmware = null;
        this.proxy_id = null;
        this.devices = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(str);
            this.proxy_id = json.getString("proxy_id");

            if (json.has("firmware")) {
                String firmware_string = json.getString("firmware");
                if (firmware_string != null) {
                    //base64编码的
                    this.firmware = JINIParser.loadFromString(IntercomServerManager.base64(false, firmware_string));
                }
            }

            if (json.has("devices") && !json.isNull("devices")) {
                JSONArray deviceArray = json.getJSONArray("devices");

                if (deviceArray != null) {

                    for (int i = 0; i < deviceArray.length(); ++i) {

                        JSONObject device = deviceArray.getJSONObject(i);

                        String scheme = device.getString("scheme");
                        String alias = device.getString("alias");
                        String device_class = device.getString("class");
                        String name = device.getString("name");
                        String conf = null;
                        if (device.has("configure")) {
                            conf = IntercomServerManager.base64(false, device.getString("configure"));
                        }
                        NetDeviceItem item = new NetDeviceItem(name, scheme, device_class, alias, conf);
                        this.devices.add(item);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 门禁设备的属性
     * [device]
     * audio_duplex=1
     * multi_session=1
     * p2p_number_mask=0,2,2,2,2,0,0
     * number_mask=2,2,1,2,2,2,0
     */
    public static class DeviceProperty {
        /**
         * 是否支持双工
         */
        public boolean audioDuplex;

        /**
         * 是否支持多路会话
         */
        public boolean multiSession;

        public int[] numberMask;

        public int[] p2pNumberMask;

        public DeviceProperty(JINIParser.Section section) {
            this.audioDuplex = true;

            this.multiSession = true;

            this.numberMask = null;

            this.p2pNumberMask = null;

            if (section != null) {
                if (section.keyExist("audio_duplex")) {
                    this.audioDuplex = section.get("audio_duplex", true);
                }
                if (section.keyExist("multi_session")) {
                    this.multiSession = section.get("multi_session", true);
                }
                if (section.keyExist("number_mask")) {
                    this.numberMask = utils.parserStringToArray(section.get("number_mask"), ",");
                }
                if (section.keyExist("p2p_number_mask")) {
                    this.p2pNumberMask = utils.parserStringToArray(section.get("p2p_number_mask"), ",");
                }
            }
        }
    }

    /**
     * 根据IP表示创建门禁设备，其中IntercomDeviceTypeIndoor：表示室内机自身，不用显示出来
     * 类似： 0101101@192.168.250.10,室外机,1,0,1
     * format:name@ip,alias,duplex,hasVoice,deviceType
     * 其中hasVoice是指monitor模式下设备是否支持声音
     */
    public static class SubDevice {

        /**
         * 门禁室外机类型，目前定义的就是这几种
         */
        public static class IntercomDeviceType {
            //室内机
            public static final int IntercomDeviceTypeIndoor = 0;
            //室外机
            public static final int IntercomDeviceTypeOutdoor = 1;
            //物业管理中心
            public static final int IntercomDeviceTypeAdministrator = 2;
            //围墙机
            public static final int IntercomDeviceTypeWall = 3;
            //别墅门口机
            public static final int IntercomDeviceTypeVillaOutdoor = 4;
            //摄像头
            public static final int IntercomDeviceTypeCamera = 5;
        }

        public final String index;

        public final String name;

        public final String ip;

        public final String alias;

        public final boolean duplex;

        public final boolean hasVoice;

        public final int deviceType;  //IntercomDeviceType

        public final boolean ptzSupport;

        private SubDevice(String index,
                          String name,
                          String ip,
                          String alias,
                          boolean duplex,
                          boolean hasVoice,
                          int deviceType,
                          boolean ptzSupport) {
            this.index = index;
            this.name = name;
            this.ip = ip;
            this.alias = alias;
            this.duplex = duplex;
            this.hasVoice = hasVoice;
            this.deviceType = deviceType;
            this.ptzSupport = ptzSupport;
        }

        public static SubDevice createFromString(String index, String str) {
            StringTokenizer t = new StringTokenizer(str, "@");
            if (t.countTokens() < 2)
                return null;

            String name = t.nextToken();
            String[] arr = t.nextToken().split(",", 10);

            String ip = null;
            String alias = null;
            boolean duplex = true;
            boolean hasVoice = true;
            int deviceType = IntercomDeviceType.IntercomDeviceTypeIndoor;
            boolean ptzSupport = false;

            for (int i = 0; i < arr.length; ++i) {
                if (i == 0) {
                    ip = arr[i];
                } else if (i == 1) {
                    alias = arr[i];
                } else if (i == 2) {
                    duplex = utils.stringToBoolean(arr[i], true);
                } else if (i == 3) {
                    hasVoice = utils.stringToBoolean(arr[i], true);
                } else if (i == 4) {
                    deviceType = utils.stringToInteger(arr[i], 0);
                } else if (i == 5) {
                    ptzSupport = utils.stringToBoolean(arr[i], false);
                }
            }
            return new SubDevice(index, name, ip, alias, duplex, hasVoice, deviceType, ptzSupport);
        }
    }

    /**
     * 设备的具体描述，可能有门禁设备，智能家居设备等
     * {
     * "alias":"quanshitong",
     * "configure":"W2RldmljZV0NCmF1ZGlvX2R1cGxleD0xDQptdWx0aV9zZXNzaW9uPTENCm51bWJlcl9tYXNrPTIsMiwxLDIsMiwyLDANCnAycF9udW1iZXJfbWFzaz0wLDIsMiwyLDIsMCwwDQpbaXBdDQowPTAxMDExMDUwMTAxQDE5Mi4xNjguMjUwLjUwLOWupOWGheacuiwwLDAsMA0KMT0wMUAxOTIuMTY4LjI1MC44LOeuoeeQhuS4reW/gywxLDEsMg0KMj0wMkAxOTIuMTY4LjI1MC45LOWbtOWimeacuiwxLDAsMw0KMz0wMTAxMTAxQDE5Mi4xNjguMjUwLjEwLOWupOWkluacuiwxLDAsMQ0K",
     * "class":"commonsip",
     * "name":"quanshitong",
     * "scheme":"icom"
     * }
     */
    public static class NetDeviceItem {

        public final String alias;

        public final String device_class;

        public final String name;

        public final String scheme;

        /**
         * configure：包含一个ini格式的配置信息，我们解析到字典里
         * [device]
         * audio_duplex=1
         * multi_session=1
         * number_mask=2,2,1,2,2,2,0
         * p2p_number_mask=0,2,2,2,2,0,0
         * [ip]
         * 0=01011050101@192.168.250.50,室内机,0,0,0
         * 1=01@192.168.250.8,管理中心,1,1,2
         * 2=02@192.168.250.9,围墙机,1,0,3
         * 3=0101101@192.168.250.10,室外机,1,0,1
         */
        public JINIParser configure;

        /**
         * 设备属性解析了[device]中的信息
         */
        public DeviceProperty deviceProperty;

        /**
         * 子设备列表，解析了[ip]中的信息
         */
        public List<SubDevice> subDevices;

        public NetDeviceItem(String name,
                             String scheme,
                             String device_class,
                             String alias,
                             String configure) {
            this.alias = alias;
            this.device_class = device_class;
            this.scheme = scheme;
            this.name = name;
            if (configure != null) {
                this.configure = JINIParser.loadFromString(configure);
            }
            this.subDevices = new ArrayList<>();

            JINIParser.Section device = this.configure.get("device");
            if (device != null) {
                this.deviceProperty = new DeviceProperty(device);
            }

            JINIParser.Section ip = this.configure.get("ip");
            if (ip != null) {
                List<JINIParser.KVBean> kvBeans = ip.getAll();
                for (JINIParser.KVBean bean : kvBeans) {
                    if (bean.type == JINIParser.kTypeComment)
                        continue;
                    SubDevice subDevice = SubDevice.createFromString(bean.key, bean.value);
                    if (subDevice != null) {
                        this.subDevices.add(subDevice);
                    }
                }
            }
        }

        /**
         * 是否是门禁设备
         *
         * @return
         */
        public boolean icomDevice() {
            return scheme.equalsIgnoreCase(IntercomConstants.kIntercomScheme);
        }

        /**
         * 是否是智能家居设备
         *
         * @return
         */
        public boolean smarthomeDevice() {
            return scheme.equalsIgnoreCase(IntercomConstants.kSmarthomeScheme);
        }

        public boolean defenceDevice() {
            if (!smarthomeDevice())
                return false;
            return device_class.equalsIgnoreCase("security");
        }
    }
}
