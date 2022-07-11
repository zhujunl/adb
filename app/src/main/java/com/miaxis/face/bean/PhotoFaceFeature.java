package com.miaxis.face.bean;

public class PhotoFaceFeature {

    private byte[] faceFeature;
    private String message;

    public PhotoFaceFeature(String message) {
        this.message = message;
    }

    public PhotoFaceFeature(byte[] faceFeature, String message) {
        this.faceFeature = faceFeature;
        this.message = message;
    }

    public byte[] getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(byte[] faceFeature) {
        this.faceFeature = faceFeature;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
