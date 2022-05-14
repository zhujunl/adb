package com.miaxis.livedetect.jni.vo;

/**
 * @date: 2018/11/12 10:35
 * @author: zhang.yw
 * @project: FaceRecognition2
 */
public class FaceQuality {

    public final int quality;
    //public final int symmetry;
    public final int pitch;
    public final int eyeDistance;

//    public FaceQuality() {
//        this(0, 0, 0, 0);
//    }

    public FaceQuality() {
        this(0, 0, 0);
    }

    public int getQuality() {
        return quality;
    }

//    public int getSymmetry() {
//        return symmetry;
//    }

    public int getPitch() {
        return pitch;
    }

    public int getEyeDistance() {
        return eyeDistance;
    }

    public FaceQuality(int quality,  int pitch, int eyeDistance) {
        this.quality = quality;
        this.pitch = pitch;
        this.eyeDistance = eyeDistance;
    }

//    public FaceQuality(int quality, int symmetry, int pitch, int eyeDistance) {
//        this.quality = quality;
//        this.symmetry = symmetry;
//        this.pitch = pitch;
//        this.eyeDistance = eyeDistance;
//    }

    @Override
    public String toString() {
        return "FaceQuality{" +
                "quality=" + quality +
                "pitch=" + pitch +
                ", eyeDistance=" + eyeDistance +
                '}';
    }
}
