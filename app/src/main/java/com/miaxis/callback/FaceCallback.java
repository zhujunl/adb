package com.miaxis.callback;


import com.miaxis.face.constant.MXFrame;
import com.miaxis.face.constant.ZZResponse;

/**
 * @author Tank
 * @date 2021/5/16 20:37
 * @des
 * @updateAuthor
 * @updateDes
 */
public interface FaceCallback {

    //    /**
    //     * 人脸质量检测通过
    //     * 可见光摄像头
    //     */
    //    void onRgbFaceReady();


    /**
     * 可见光数据处理完成
     */
    void onRgbProcessReady();

    /**
     * 活体检测通过
     */
    void onLiveReady(MXFrame nirFrame, boolean success);

    /**
     * 人证比对回调
     */
    void onMatchReady(boolean success);

    /**
     * 异常回调
     */
    void onError(ZZResponse<?> response);

    //    /**
    //     * 人脸检测回调
    //     */
    //    void onFaceDetectReady(byte[] frame, MXCamera mxCamera, int width, int height, boolean success);
    //
    //
    //    /**
    //     * 人脸活体检测回调
    //     */
    //    void onFaceLiveReady(byte[] frame, MXCamera mxCamera, int width, int height, boolean success);


}
