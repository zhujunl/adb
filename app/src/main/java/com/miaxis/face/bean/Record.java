package com.miaxis.face.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/5/17 0017.
 */
@Entity
public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    @Index(name = "idx_id")
    private Long id;
    private String name;
    private String cardNo;
    private String sex;
    private String birthday;
    private String address;
    private String busEntity;
    private String status;      //通过 不通过
    private String cardImg;
    private String faceImg;
    private String finger0;
    private String finger1;
    private String printFinger;
    private String location;
    private String longitude;
    private String latitude;
    private Date createDate;
    private String devsn;
    private String cardId;
    private boolean hasUp;
    private String validate;
    private String race;
    private String regOrg;

    private String type;
    //********外国人******************
    private String chineseName;
    //********************************
    //*************港澳台*****************
    private String passNum;
    private String issueNum;
    //*************************

    @Transient
    private byte fingerPosition0 = 0;
    @Transient
    private byte fingerPosition1 = 0;
    @Transient
    private byte[] faceImgData;
    @Transient
    private byte[] cardImgData;

    @Generated(hash = 2147220666)
    public Record(Long id, String name, String cardNo, String sex, String birthday,
            String address, String busEntity, String status, String cardImg,
            String faceImg, String finger0, String finger1, String printFinger,
            String location, String longitude, String latitude, Date createDate,
            String devsn, String cardId, boolean hasUp, String validate,
            String race, String regOrg, String type, String chineseName,
            String passNum, String issueNum) {
        this.id = id;
        this.name = name;
        this.cardNo = cardNo;
        this.sex = sex;
        this.birthday = birthday;
        this.address = address;
        this.busEntity = busEntity;
        this.status = status;
        this.cardImg = cardImg;
        this.faceImg = faceImg;
        this.finger0 = finger0;
        this.finger1 = finger1;
        this.printFinger = printFinger;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.createDate = createDate;
        this.devsn = devsn;
        this.cardId = cardId;
        this.hasUp = hasUp;
        this.validate = validate;
        this.race = race;
        this.regOrg = regOrg;
        this.type = type;
        this.chineseName = chineseName;
        this.passNum = passNum;
        this.issueNum = issueNum;
    }

    @Generated(hash = 477726293)
    public Record() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBusEntity() {
        return busEntity;
    }

    public void setBusEntity(String busEntity) {
        this.busEntity = busEntity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCardImg() {
        return cardImg;
    }

    public void setCardImg(String cardImg) {
        this.cardImg = cardImg;
    }

    public String getFaceImg() {
        return faceImg;
    }

    public void setFaceImg(String faceImg) {
        this.faceImg = faceImg;
    }

    public String getFinger0() {
        return finger0;
    }

    public void setFinger0(String finger0) {
        this.finger0 = finger0;
    }

    public String getFinger1() {
        return finger1;
    }

    public void setFinger1(String finger1) {
        this.finger1 = finger1;
    }

    public String getPrintFinger() {
        return printFinger;
    }

    public void setPrintFinger(String printFinger) {
        this.printFinger = printFinger;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getDevsn() {
        return devsn;
    }

    public void setDevsn(String devsn) {
        this.devsn = devsn;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public boolean isHasUp() {
        return hasUp;
    }

    public void setHasUp(boolean hasUp) {
        this.hasUp = hasUp;
    }

    public boolean getHasUp() {
        return this.hasUp;
    }

    public byte getFingerPosition0() {
        return fingerPosition0;
    }

    public void setFingerPosition0(byte fingerPosition0) {
        this.fingerPosition0 = fingerPosition0;
    }

    public byte getFingerPosition1() {
        return fingerPosition1;
    }

    public void setFingerPosition1(byte fingerPosition1) {
        this.fingerPosition1 = fingerPosition1;
    }

    public byte[] getFaceImgData() {
        return faceImgData;
    }

    public void setFaceImgData(byte[] faceImgData) {
        this.faceImgData = faceImgData;
    }

    public byte[] getCardImgData() {
        return cardImgData;
    }

    public void setCardImgData(byte[] cardImgData) {
        this.cardImgData = cardImgData;
    }

    public String getValidate() {
        return this.validate;
    }

    public void setValidate(String validate) {
        this.validate = validate;
    }

    public String getRace() {
        return this.race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getRegOrg() {
        return this.regOrg;
    }

    public void setRegOrg(String regOrg) {
        this.regOrg = regOrg;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChineseName() {
        return this.chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getPassNum() {
        return this.passNum;
    }

    public void setPassNum(String passNum) {
        this.passNum = passNum;
    }

    public String getIssueNum() {
        return this.issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }
}