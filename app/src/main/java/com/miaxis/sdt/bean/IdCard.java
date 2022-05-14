package com.miaxis.sdt.bean;

import android.graphics.Bitmap;

/**
 * @author Tank
 * @date 2021/4/29 10:53 AM
 * @des
 * @updateAuthor
 * @updateDes
 */
public class IdCard {

    public IdCardMsg idCardMsg;

    public Bitmap face;

    public byte[] fp0;
    public byte[] fp1;

    public  String fingerprintPosition0;
    public  String fingerprintPosition1;
    public IdCard() {
    }

    @Override
    public String toString() {
        return "IdCard{" +
                "idCardMsg=" + idCardMsg +
                ", face=" + face +
                 "指纹一"+fingerprintPosition0+",指纹二"+fingerprintPosition1+""+
                '}';
    }
}
