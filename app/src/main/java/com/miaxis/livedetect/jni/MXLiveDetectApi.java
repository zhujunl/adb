package com.miaxis.livedetect.jni;

import com.miaxis.image.MXImage;
import com.miaxis.livedetect.jni.vo.FaceInfo;

import java.util.List;

/**
 * @date: 2018/11/19 14:24
 * @author: zhang.yw
 * @project: LiveDetection
 */
public enum MXLiveDetectApi {
    INSTANCE;
    //public static final MXLiveDetectApi INSTANCE = new MXLiveDetectApi();

    static {
        System.loadLibrary("live-detect");
    }

    boolean initialized = false;

    /**
     * 初始化算法
     *
     * @param modelPath 模型路径
     * @return 0 success ,otherwise fail!
     */
    public int initialize(String modelPath) {
        int init = -1;
//        if (initialized) {
//            //throw new IllegalArgumentException("initialized !!");
//        } else {
            free();
            init = nativeInitialize(modelPath);
            if (init == 0) {
                initialized = true;
            }
//        }
        return init;
    }

    /**
     * 释放算法
     */
    public void free() {
        if (initialized) {
            initialized = false;
            nativeFree();
        }
    }

    /**
     * 重置动作检测参数
     *
     * @return 0 success otherwise fail
     */
    public int InitLiveDetectParams() {
        int paramsInit = nativeParamsInitialize();
        return paramsInit;
    }

    /**
     * 人脸识别
     *
     * @see FaceInfo
     */
    public List<FaceInfo> faceDetect(MXImage image) {
        checkFormat(image);
        return nativeFaceDetect(image.getData(), image.getWidth(), image.getHeight());
    }


    /**
     * 眨眼检测
     *
     * @return 是否眨眼 0 否, 1 是
     */
    public int blinkDetect(MXImage image, FaceInfo faceInfo) {
        checkFormat(image);
        return nativeBlinkDetect(image.getData(), image.getWidth(), image.getHeight(), faceInfo);
    }

    /**
     * 点头检测
     *
     * @return 是否点头 0 否, 1 是
     */
    public int nodDetect(MXImage image, FaceInfo faceInfo) {
        checkFormat(image);
        return nativeNodDetect(image.getData(), image.getWidth(), image.getHeight(), faceInfo);
    }

    /**
     * 摇头检测
     *
     * @return 是否摇头 0 否, 1 是
     */
    public int shakingHeadDetect(MXImage image, FaceInfo faceInfo) {
        checkFormat(image);
        return nativeShakingHeadDetect(image.getData(), image.getWidth(), image.getHeight(), faceInfo);
    }

    /**
     * 张嘴检测
     *
     * @return 是否张嘴 0 否, 1 是
     */
    public int openMouthDetect(MXImage image, FaceInfo faceInfo) {
        checkFormat(image);
        return nativeOpenMouthDetect(image.getData(), image.getWidth(), image.getHeight(), faceInfo);
    }

    /**
     * 亮度检测
     *
     * @return 0 正常 ,1 过亮,2 过暗
     */
    public static int brightnessDetect(MXImage image) {
        checkFormat(image);
        return nativeBrightnessDetect(image.getData(), image.getWidth(), image.getHeight());
    }

    private static void checkFormat(MXImage image) {
        if (image.getFormat() != MXImage.FORMAT_BGR)
            throw new IllegalArgumentException("Detect only support image format BGR ! ,current image is " + image.getFormatName());
    }


    private native int nativeInitialize(String modelPath);
    //private native int nativeBlinkModel(String modelPath);

    private native void nativeFree();

    private native List<FaceInfo> nativeFaceDetect(byte[] bgrImage, int width, int height);

    private native int nativeBlinkDetect(byte[] bgrImage, int width, int height, FaceInfo faceInfo);

    //private native int nativemxBlinkDetect(byte[] bgrImage, int width, int height, FaceInfo faceInfo,String Modelpath);
    private native int nativeNodDetect(byte[] bgrImage, int width, int height, FaceInfo faceInfo);

    private native int nativeShakingHeadDetect(byte[] bgrImage, int width, int height, FaceInfo faceInfo);

    private native int nativeOpenMouthDetect(byte[] bgrImage, int width, int height, FaceInfo faceInfo);

    private static native int nativeBrightnessDetect(byte[] bgrImage, int width, int height);

    public static native byte[] crop(byte[] bgrImage, int width, int height, int x, int y, int dw, int dh);

    private static native byte[] drawInfo(byte[] bgrImage, int width, int height, FaceInfo faceInfo);

    private native int nativeParamsInitialize();
}
