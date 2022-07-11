package com.miaxis.face.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
@Entity
public class Config implements Serializable {

    public static final int MODE_FACE_ONLY          = 0;
    public static final int MODE_FINGER_ONLY        = 1;
    public static final int MODE_TWO_FACE_FIRST     = 2;
    public static final int MODE_TWO_FINGER_FIRST   = 3;
    public static final int MODE_ONE_FACE_FIRST     = 4;
    public static final int MODE_ONE_FINGER_FIRST   = 5;

    public static final int MODE_LOCAL_FEATURE      = 6;

    private static final long serialVersionUID = 1L;

    @Id
    private long id;
    private String ip;
    private int port;
    private String upTime;
    private float  passScore;
    private float quality;
    private String banner;
    private int intervalTime;
    private String orgId;
    private String orgName;
    private boolean netFlag;
    private boolean queryFlag;
    private String password;
    private int verifyMode;
    private boolean whiteFlag;      // 是否启用白名单验证
    private boolean blackFlag;      // 是否启用黑名单验证
    private Boolean advertiseFlag;  //是否启用广告
    private Integer advertiseDelayTime; //广告显示延迟

    private Integer fingerImg;//指纹图像
    private boolean scence;//抓拍图像选择
    private boolean liveness;//活体监测
    private Integer rgb;//可见光
    private Integer nir;//近红外
    private Integer sm;//高拍仪

    @Generated(hash = 1770992650)
    public Config(long id, String ip, int port, String upTime, float passScore, float quality, String banner,
            int intervalTime, String orgId, String orgName, boolean netFlag, boolean queryFlag, String password,
            int verifyMode, boolean whiteFlag, boolean blackFlag, Boolean advertiseFlag,
            Integer advertiseDelayTime, Integer fingerImg, boolean scence, boolean liveness, Integer rgb,
            Integer nir, Integer sm) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.upTime = upTime;
        this.passScore = passScore;
        this.quality = quality;
        this.banner = banner;
        this.intervalTime = intervalTime;
        this.orgId = orgId;
        this.orgName = orgName;
        this.netFlag = netFlag;
        this.queryFlag = queryFlag;
        this.password = password;
        this.verifyMode = verifyMode;
        this.whiteFlag = whiteFlag;
        this.blackFlag = blackFlag;
        this.advertiseFlag = advertiseFlag;
        this.advertiseDelayTime = advertiseDelayTime;
        this.fingerImg = fingerImg;
        this.scence = scence;
        this.liveness = liveness;
        this.rgb = rgb;
        this.nir = nir;
        this.sm = sm;
    }

    @Generated(hash = 589037648)
    public Config() {
    }

    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getIp() {
        return this.ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public int getPort() {
        return this.port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String getUpTime() {
        return this.upTime;
    }
    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }
    public float getPassScore() {
        return this.passScore;
    }
    public void setPassScore(float passScore) {
        this.passScore = passScore;
    }
    public String getBanner() {
        return this.banner;
    }
    public void setBanner(String banner) {
        this.banner = banner;
    }
    public int getIntervalTime() {
        return this.intervalTime;
    }
    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }
    public String getOrgId() {
        return this.orgId;
    }
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    public String getOrgName() {
        return this.orgName;
    }
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    public boolean getNetFlag() {
        return this.netFlag;
    }
    public void setNetFlag(boolean netFlag) {
        this.netFlag = netFlag;
    }
    public boolean getQueryFlag() {
        return this.queryFlag;
    }
    public void setQueryFlag(boolean queryFlag) {
        this.queryFlag = queryFlag;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getVerifyMode() {
        return this.verifyMode;
    }
    public void setVerifyMode(int verifyMode) {
        this.verifyMode = verifyMode;
    }
    public boolean getWhiteFlag() {
        return this.whiteFlag;
    }
    public void setWhiteFlag(boolean whiteFlag) {
        this.whiteFlag = whiteFlag;
    }
    public boolean getBlackFlag() {
        return this.blackFlag;
    }
    public void setBlackFlag(boolean blackFlag) {
        this.blackFlag = blackFlag;
    }
    public Boolean getAdvertiseFlag() {
        return this.advertiseFlag;
    }
    public void setAdvertiseFlag(Boolean advertiseFlag) {
        this.advertiseFlag = advertiseFlag;
    }
    public Integer getAdvertiseDelayTime() {
        return this.advertiseDelayTime;
    }
    public void setAdvertiseDelayTime(Integer advertiseDelayTime) {
        this.advertiseDelayTime = advertiseDelayTime;
    }
    public Integer getFingerImg() {
        return fingerImg;
    }
    public void setFingerImg(Integer fingerImg) {
        this.fingerImg = fingerImg;
    }
    public boolean isScence() {
        return scence;
    }
    public void setScence(boolean scence) {
        this.scence = scence;
    }
    public boolean isLiveness() {
        return liveness;
    }
    public void setLiveness(boolean liveness) {
        this.liveness = liveness;
    }
    public Integer getRgb() {
        return rgb;
    }
    public void setRgb(Integer rgb) {
        this.rgb = rgb;
    }
    public Integer getNir() {
        return nir;
    }
    public void setNir(Integer nir) {
        this.nir = nir;
    }
    public Integer getSm() {
        return sm;
    }
    public void setSm(Integer sm) {
        this.sm = sm;
    }
    public boolean getScence() {
        return this.scence;
    }
    public boolean getLiveness() {
        return this.liveness;
    }
    public float getQuality() {
        return quality;
    }
    public void setQuality(float quality) {
        this.quality = quality;
    }
}
