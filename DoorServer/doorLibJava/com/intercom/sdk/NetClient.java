package com.intercom.sdk;

import com.google.gson.Gson;

public class NetClient {


    /**
     * address : 192.168.3.56:10000
     * alias :
     * button_key : 04
     * client_id : 1526036785183095
     * family_id : 00CA450100880000
     * number :
     * port : 16310
     * rand : 13175162943485077
     * sub_type : 0
     * type : 1
     * pushid : 1212
     * version : 2
     * platform : 1
     */

    private String address;
    private String alias;
    private String button_key;
    private String client_id;
    private String family_id;
    private String number;
    private int port;
    private String rand;
    private int sub_type;
    private int type;
    private int platform;  //NetClientOSPlatformType
    private String pushid;
    private String sn;
    private int version;

    public static NetClient objectFromData(String str) {

        return new Gson().fromJson(str, NetClient.class);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getButton_key() {
        return button_key;
    }

    public void setButton_key(String button_key) {
        this.button_key = button_key;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getFamily_id() {
        return family_id;
    }

    public void setFamily_id(String family_id) {
        this.family_id = family_id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRand() {
        return rand;
    }

    public void setRand(String rand) {
        this.rand = rand;
    }

    public int getSub_type() {
        return sub_type;
    }

    public void setSub_type(int sub_type) {
        this.sub_type = sub_type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getPushid() {
        return pushid;
    }

    public void setPushid(String pushid) {
        this.pushid = pushid;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }


    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
