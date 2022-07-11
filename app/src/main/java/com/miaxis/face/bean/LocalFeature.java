package com.miaxis.face.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xu.nan on 2018/3/27.
 */
@Entity
public class LocalFeature {
    @Id
    private Long id;
    private String cardNo;
    private String name;
    private String filePath;
    private byte[] feature;

    public LocalFeature() {
    }

    public LocalFeature(String cardNo, String name, String filePath) {
        this.cardNo = cardNo;
        this.name = name;
        this.filePath = filePath;
    }

    @Generated(hash = 972886204)
    public LocalFeature(Long id, String cardNo, String name, String filePath,
            byte[] feature) {
        this.id = id;
        this.cardNo = cardNo;
        this.name = name;
        this.filePath = filePath;
        this.feature = feature;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public byte[] getFeature() {
        return feature;
    }

    public void setFeature(byte[] feature) {
        this.feature = feature;
    }
}
