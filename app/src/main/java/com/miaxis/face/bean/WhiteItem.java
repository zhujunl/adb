package com.miaxis.face.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xu.nan on 2018/3/26.
 */
@Entity
public class WhiteItem {

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String cardNo;
    private String featureUrl;

    public WhiteItem(String cardNo) {
        this.cardNo = cardNo;
    }

    @Generated(hash = 76827643)
    public WhiteItem(Long id, String name, String cardNo, String featureUrl) {
        this.id = id;
        this.name = name;
        this.cardNo = cardNo;
        this.featureUrl = featureUrl;
    }
    @Generated(hash = 1454278319)
    public WhiteItem() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCardNo() {
        return this.cardNo;
    }
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
    public String getFeatureUrl() {
        return this.featureUrl;
    }
    public void setFeatureUrl(String featureUrl) {
        this.featureUrl = featureUrl;
    }



}
