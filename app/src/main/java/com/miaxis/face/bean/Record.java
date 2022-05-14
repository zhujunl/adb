package com.miaxis.face.bean;

import com.miaxis.face.util.MyUtil;
import com.miaxis.sdt.bean.IdCard;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.Arrays;
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
    /**
     * 姓名
     * */
    private String name;
    /**
     *身份证号
     * */
    private String cardNo;
    /**
     *性别
     * */
    private String sex;
    /**
     *出生日期
     * */
    private String birthday;
    /**
     *住址
     * */
    private String address;
    /**
     *机构名称
     * */
    private String busEntity;
    private String status;      //通过 不通过
    /**
     *身份证照片路径
     * */
    private String cardImg;
    /**
     *现场照片路径
     * */
    private String faceImg;
    /**
     *指纹1
     * */
    private byte[] finger0;
    /**
     *指纹2
     * */
    private byte[] finger1;
    /**
     *
     * */
    private String printFinger;
    /**
     *当前地址
     * */
    private String location;
    /**
     *当前经度
     * */
    private String longitude;
    /**
     *当前维度
     * */
    private String latitude;
    /**
     *比对时间
     * */
    private Date createDate;
    /**
     *设备序列号
     * */
    private String devsn;
    /**
     *卡id
     * */
    private String cardId;
    /**
     *是否已更新
     * */
    private boolean hasUp;
    /**
     *有效期
     * */
    private String validate;
    /**
     *民族
     * */
    private String race;
    /**
     *签发机关
     * */
    private String regOrg;
    /** 卡片类型 空值=二代证，J=港澳台，I=外国人永久居留证 **/
    private String type;
    //********外国人******************
    private String chineseName;
    /** 港澳台：通行证号码 **/
    private String passNum;
    /** 港澳台：签发次数 **/
    private String issueNum;

    //*************************

    /**
     *指纹位置1
     * */
    @Transient
    private String  fingerPosition0;
    /**
     *指纹位置1
     * */
    @Transient
    private String  fingerPosition1;
    /**
     *现场人脸
     * */
    @Transient
    private byte[] faceImgData;
    /**
     *身份证人脸
     * */
    @Transient
    private byte[] cardImgData;


    @Generated(hash = 812876856)
    public Record(Long id, String name, String cardNo, String sex, String birthday,
            String address, String busEntity, String status, String cardImg,
            String faceImg, byte[] finger0, byte[] finger1, String printFinger,
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

    public Record(Builder builder){
        setId(builder.id);
        setName(builder.name);
        setCardNo(builder.cardNo);
        setSex(builder.sex);
        setBirthday(builder.birthday);
        setAddress(builder.address);
        setBusEntity(builder.busEntity);
        setStatus(builder.status);
        setCardImg(builder.cardImg);
        setFaceImg(builder.faceImg);
        setFinger0(builder.finger0);
        setFinger1(builder.finger1);
        setPrintFinger(builder.printFinger);
        setLocation(builder.location);
        setLongitude(builder.longitude);
        setLatitude(builder.latitude);
        setCreateDate(builder.createDate);
        setDevsn(builder.devsn);
        setCardId(builder.cardId);
        setHasUp(builder.hasUp);
        setValidate(builder.validate);
        setRace(builder.race);
        setRegOrg(builder.regOrg);
        setType(builder.type);
        setChineseName(builder.chineseName);
        setPassNum(builder.passNum);
        setIssueNum(builder.issueNum);
    }

    public Record(IdCard card,String location,String latitude,String longitude){
        this.name = card.idCardMsg.name;
        this.cardNo = card.idCardMsg.id_num;
        this.sex = card.idCardMsg.sex;
        this.birthday = card.idCardMsg.getBrithday();
        this.address = card.idCardMsg.address;
        this.finger0 = card.fp0;
        this.fingerPosition0=card.fingerprintPosition0;
        this.finger1 = card.fp1;
        this.fingerPosition1=card.fingerprintPosition1;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.devsn = MyUtil.getSerialNumber();
        this.hasUp = false;
        this.validate = card.idCardMsg.getValidate();
        this.race = card.idCardMsg.nation_str+"族";
        this.regOrg = card.idCardMsg.sign_office;
        this.cardImgData=MyUtil.getBytesByBitmap(card.face);
        this.type = card.idCardMsg.type;
        this.chineseName = card.idCardMsg.chinesename;
        this.passNum = card.idCardMsg.passnum;
        this.issueNum = card.idCardMsg.issueCount;
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


//    public String getFinger0() {
//
//        return Base64.encodeToString(finger0, Base64.DEFAULT);
//    }
//
//    public String getFinger1() {
//        return Base64.encodeToString(finger1, Base64.DEFAULT);
//    }


    public byte[] getFinger0() {
        return finger0;
    }

    public byte[] getFinger1() {
        return finger1;
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

    public void setFinger0(byte[] finger0) {
        this.finger0 = finger0;
    }

    public void setFinger1(byte[] finger1) {
        this.finger1 = finger1;
    }

    public String getFingerPosition0() {
        return fingerPosition0;
    }

    public void setFingerPosition0(String fingerPosition0) {
        this.fingerPosition0 = fingerPosition0;
    }

    public String getFingerPosition1() {
        return fingerPosition1;
    }

    public void setFingerPosition1(String fingerPosition1) {
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

    public static final class Builder {
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
        private byte[] finger0;
        private byte[] finger1;
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

        public Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder cardNo(String val) {
            cardNo = val;
            return this;
        }

        public Builder sex(String val) {
            sex = val;
            return this;
        }

        public Builder birthday(String val) {
            birthday = val;
            return this;
        }
        public Builder busEntity(String val) {
            busEntity = val;
            return this;
        }
        public Builder status(String val) {
            status = val;
            return this;
        }

        public Builder cardImg(String val) {
            cardImg = val;
            return this;
        }
        public Builder faceImg(String val) {
            faceImg = val;
            return this;
        }
        public Builder finger0(byte[] val) {
            finger0 = val;
            return this;
        }

        public Builder finger1(byte[] val) {
            finger1 = val;
            return this;
        }
        public Builder printFinger(String val) {
            printFinger = val;
            return this;
        }

        public Builder location(String val) {
            location = val;
            return this;
        }

        public Builder longitude(String val) {
            longitude = val;
            return this;
        }

        public Builder latitude(String val) {
            latitude = val;
            return this;
        }

        public Builder createDate(Date val) {
            createDate = val;
            return this;
        }
        public Builder devsn(String val) {
            devsn = val;
            return this;
        }

        public Builder cardId(String val) {
            cardId = val;
            return this;
        }

        public Builder hasUp(boolean val) {
            hasUp = val;
            return this;
        }

        public Builder validate(String val) {
            validate = val;
            return this;
        }

        public Builder race(String val) {
            race = val;
            return this;
        }

        public Builder regOrg(String val) {
            regOrg = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder chineseName(String val) {
            chineseName = val;
            return this;
        }

        public Builder passNum(String val) {
            passNum = val;
            return this;
        }

        public Builder issueNum(String val) {
            issueNum = val;
            return this;
        }


        public Record build() {
            return new Record(this);
        }
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", address='" + address + '\'' +
                ", busEntity='" + busEntity + '\'' +
                ", status='" + status + '\'' +
                ", cardImg='" + cardImg + '\'' +
                ", faceImg='" + faceImg + '\'' +
                ", finger0='" + finger0 + '\'' +
                ", finger1='" + finger1 + '\'' +
                ", printFinger='" + printFinger + '\'' +
                ", location='" + location + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", createDate=" + createDate +
                ", devsn='" + devsn + '\'' +
                ", cardId='" + cardId + '\'' +
                ", hasUp=" + hasUp +
                ", validate='" + validate + '\'' +
                ", race='" + race + '\'' +
                ", regOrg='" + regOrg + '\'' +
                ", type='" + type + '\'' +
                ", chineseName='" + chineseName + '\'' +
                ", passNum='" + passNum + '\'' +
                ", issueNum='" + issueNum + '\'' +
                ", fingerPosition0=" + fingerPosition0 +
                ", fingerPosition1=" + fingerPosition1 +
                ", faceImgData=" + Arrays.toString(faceImgData) +
                ", cardImgData=" + Arrays.toString(cardImgData) +
                '}';
    }
}