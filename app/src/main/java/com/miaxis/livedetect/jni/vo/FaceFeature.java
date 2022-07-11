package com.miaxis.livedetect.jni.vo;

/**
 * @date: 2018/11/12 10:39
 * @author: zhang.yw
 * @project: FaceRecognition2
 */
public class FaceFeature {
    private final byte[] faceFeature;

    public FaceFeature(byte[] faceFeature) {
        this.faceFeature = faceFeature;
    }

    public byte[] getData() {
        return faceFeature;
    }

    @Override
    public String toString() {
        return "FaceFeature{" +
                "faceFeature=" + (faceFeature == null ? 0 : faceFeature.length) +
                '}';
    }
}
