package com.miaxis.livedetect.jni.vo;

import android.graphics.Point;
import android.graphics.Rect;

import com.miaxis.image.MXImage;

import java.util.ArrayList;

/**
 * @date: 2018/11/12 10:37
 * @author: zhang.yw
 * @project: FaceRecognition2
 */
public class FaceInfo {
    private MXImage image;
    private FaceQuality quality;
    private FaceFeature feature;

    private Rect area;
    private ArrayList<Point> keyPoints;

    public Rect getArea() {
        return area;
    }

    public FaceQuality getQuality() {
        return quality;
    }

    public void setArea(Rect area) {
        this.area = area;
    }

    public ArrayList<Point> getKeyPoints() {
        return keyPoints;
    }

    public void setKeyPoints(ArrayList<Point> keyPoints) {
        this.keyPoints = keyPoints;
    }

    public FaceInfo() {

    }

    public FaceInfo(Rect area, ArrayList<Point> keyPoints) {
        this.area = area;
        this.keyPoints = keyPoints;
    }

    public MXImage getImage() {
        return image;
    }

    public void setImage(MXImage image) {
        this.image = image;
    }


    public void setQuality(FaceQuality quality) {
        this.quality = quality;
    }

    public FaceFeature getFeature() {
        return feature;
    }

    public void setFeature(FaceFeature feature) {
        this.feature = feature;
    }

    @Override
    public String toString() {
        return "FaceInfo{" +
                "image=" + image +
                ", quality=" + quality +
                ", faceArea=" + area +
                ", keyPoints=" + (null == keyPoints ? 0 : keyPoints.size()) +
                ", feature=" + feature +
                '}';
    }
}
