package com.miaxis.face.manager;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.miaxis.face.bean.MxRGBImage;
import com.miaxis.face.bean.PhotoFaceFeature;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.util.FileUtil;
import com.miaxis.livedetect.jni.MXLiveDetectApi;

import org.zz.api.MXFaceAPI;
import org.zz.api.MXFaceInfoEx;
import org.zz.jni.mxImageTool;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * @author ZJL
 * @date 2022/5/12 11:05
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FaceManager {

    private final String TAG="FaceManager";

    private MXFaceAPI mxFaceAPI;
    private MXLiveDetectApi mxLiveDetectApi;
    private mxImageTool dtTool;

    private static final int MAX_FACE_NUM = 50;

    private static final Byte lock2 = 2;

    private static final Byte lock1=1;

    private final int zoomWidth=1280;
    private final int zoomHeight=720;
    private byte[] lastVisiblePreviewData;
    private byte[] nirVisiblePreviewData;

    private HandlerThread asyncDetectThread;
    private Handler asyncDetectHandler;
    private volatile boolean detectLoop = true;
    private HandlerThread asyncExtractThread;
    private Handler asyncExtractHandler;
    private volatile boolean extractLoop = true;

    private OnFaceHandleListener faceHandleListener;

    private FaceManager() {
    }

    private static class FaceManagerHolder {
        private static final FaceManager faceManager = new FaceManager();
    }

    public static FaceManager getInstance() {
        return FaceManagerHolder.faceManager;
    }


    public interface OnFaceHandleListener {
        void onFeatureExtract(MxRGBImage mxRGBImage, MXFaceInfoEx mxFaceInfoEx, byte[] feature);

        void onFaceDetect(int faceNum, MXFaceInfoEx[] faceInfoExes);
    }


    public int init(Context context){
        Log.e("初始化人脸算法","initFaceST");
        final String sLicence = FileUtil.readLicence();
        mxFaceAPI = new MXFaceAPI();
        mxLiveDetectApi = MXLiveDetectApi.INSTANCE;
        dtTool = new mxImageTool();
        int re = initFaceModel(context);
        if (re == 0) {
            re = mxFaceAPI.mxInitAlg(context, FileUtil.getFaceModelPath(), sLicence);
        }
        if (re == 0) {
            re = mxLiveDetectApi.initialize(FileUtil.getFaceModelPath());
        }
        Log.e("Facemanager:","算法版本："+mxFaceAPI.mxAlgVersion());
        initThread();
        return re;
    }

    public void setLastVisiblePreviewData(byte[] lastVisiblePreviewData) {
        this.lastVisiblePreviewData = lastVisiblePreviewData;
    }

    public void setNirVisiblePreviewData(byte[] nirVisiblePreviewData) {
        this.nirVisiblePreviewData = nirVisiblePreviewData;
    }

    public void startLoop() {
        detectLoop = true;
        extractLoop = true;
//        actionLiveResult = false;
//        actionLiveImageQuality = 0;
//        actionLiveImageData = null;
        lastVisiblePreviewData = null;
//        intermediaryData = null;
//        needNextFeature = true;
        asyncDetectHandler.sendEmptyMessage(0);
//        asyncExtractHandler.sendEmptyMessage(0);
    }

    public void stopLoop() {
        detectLoop = false;
        extractLoop = false;
//        needNextFeature = false;
//        actionLiveResult = false;
//        actionLiveImageQuality = 0;
//        actionLiveImageData = null;
        lastVisiblePreviewData = null;
        asyncDetectHandler.removeMessages(0);
//        asyncExtractHandler.removeMessages(0);
    }

    public void setFaceHandleListener(OnFaceHandleListener faceHandleListener) {
        this.faceHandleListener = faceHandleListener;
    }

    private void initThread() {
        asyncDetectThread = new HandlerThread("detect_thread");
        asyncDetectThread.start();
        asyncDetectHandler = new Handler(asyncDetectThread.getLooper()) {
            public void handleMessage(Message msg) {
                if (detectLoop) {
                    try {
                        previewDataLoop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
//        asyncExtractThread = new HandlerThread("extract_thread");
//        asyncExtractThread.start();
//        asyncExtractHandler = new Handler(asyncExtractThread.getLooper()) {
//            public void handleMessage(Message msg) {
//                if (extractLoop) {
//                    try {
//                        intermediaryDataLoop();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
    }

    private void previewDataLoop() {
        try {
            if (true){
                if (this.nirVisiblePreviewData!=null&&this.lastVisiblePreviewData!=null) {
                    livenessVerify(nirVisiblePreviewData,lastVisiblePreviewData);
                }
            }else {
                if (this.lastVisiblePreviewData != null) {
                    verify(lastVisiblePreviewData);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            asyncDetectHandler.sendEmptyMessage(0);
        }
    }

    private void verify(byte[] detectData) throws Exception {
        byte[] zoomedRgbData = cameraPreviewConvert(detectData,
                Constants.PRE_WIDTH,
                Constants.PRE_WIDTH,
                0,
                zoomWidth,
                zoomHeight);
        if (zoomedRgbData == null) {
            if (faceHandleListener != null) {
                faceHandleListener.onFaceDetect(0, null);
            }
            return;
        }
        int[] faceNum = new int[]{MAX_FACE_NUM};
        MXFaceInfoEx[] faceBuffer = makeFaceContainer(faceNum[0]);
        boolean result = faceDetect(zoomedRgbData, zoomWidth, zoomHeight, faceNum, faceBuffer);
        if (result) {
            if (faceHandleListener != null) {
                faceHandleListener.onFaceDetect(faceNum[0], faceBuffer);
            }
            MXFaceInfoEx mxFaceInfoEx = sortMXFaceInfoEx(faceBuffer);
            result = faceQuality(zoomedRgbData, zoomWidth, zoomHeight, 1, new MXFaceInfoEx[]{mxFaceInfoEx});
                if (result&&mxFaceInfoEx.quality>50) {
                    extract(mxFaceInfoEx,zoomedRgbData);
                }
            }
    }

    private void livenessVerify(byte[] nirdata,byte[] data) throws Exception {
        byte[] zoomedNirData = cameraPreviewConvert(nirdata,
                Constants.PIC_WIDTH,
                Constants.PIC_HEIGHT,
                0,
                Constants.PIC_WIDTH,
                Constants.PIC_HEIGHT);
        byte[] zoomPRGData=cameraPreviewConvert(data,
                Constants.PRE_WIDTH,
                Constants.PRE_HEIGHT,
                0,
                zoomWidth,
                zoomHeight);
        int[] faceNum = new int[]{MAX_FACE_NUM};
        int[] nirNum = new int[]{MAX_FACE_NUM};
        MXFaceInfoEx[] nirBuffer = makeFaceContainer(nirNum[0]);
        MXFaceInfoEx[] rgbBuffer = makeFaceContainer(faceNum[0]);
        boolean nirResult = faceDetect(zoomedNirData, Constants.PIC_WIDTH, Constants.PIC_HEIGHT, nirNum, nirBuffer);
        boolean rgbResult = faceDetect(zoomPRGData,zoomWidth,zoomHeight,faceNum,rgbBuffer);

        if (rgbResult&&nirResult){
            if (faceHandleListener != null) {
                faceHandleListener.onFaceDetect(faceNum[0], rgbBuffer);
            }
            MXFaceInfoEx mxFaceInfoEx = sortMXFaceInfoEx(rgbBuffer);
            rgbResult = faceQuality(zoomPRGData, zoomWidth, zoomHeight, 1, new MXFaceInfoEx[]{mxFaceInfoEx});
            if (rgbResult&&mxFaceInfoEx.quality>50) {
                extract(mxFaceInfoEx,zoomPRGData);
            }
        }

    }

    /**
     * 视频流转RGB
     *
     * @param yuv 视频流数据NV21
     */
    public byte[] yuv2Rgb(byte[] yuv, int width, int height) {
        if (this.dtTool == null) {
            return null;
        }
        byte[] pRGBImage = new byte[width * height * 3];
        this.dtTool.YUV2RGB(yuv, width, height, pRGBImage);

        return pRGBImage;
    }


    public Bitmap getPriviewPic(byte[] data) {//这里传入的data参数就是onpreviewFrame中需要传入的byte[]型数据
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        YuvImage yuvimage = new YuvImage(
                data,
                ImageFormat.NV21,
                1280,
                720,
                null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, 1280,720), 100, baos);// 80--JPG图片的质量[0-100],100最高
        byte[] rawImage = baos.toByteArray();
        //将rawImage转换成bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
        return bitmap;
    }


    private void extract(MXFaceInfoEx mxFaceInfoEx, byte[] data) throws Exception {
        Log.v("asd", "提特征中"+"_____人脸质量："+mxFaceInfoEx.quality);
        byte[] feature = extractFeature(data, zoomWidth, zoomHeight, mxFaceInfoEx);
        if (feature != null) {
            faceHandleListener.onFeatureExtract(new MxRGBImage(data, zoomWidth, zoomHeight),
                    mxFaceInfoEx,
                    feature);
        }
    }


    /**
     * 摄像头预览数据转换
     *
     * @param data        摄像头onPreviewFrame-data
     * @param width       摄像头实际分辨率-宽
     * @param height      摄像头实际分辨率-高
     * @param orientation 旋转角度
     * @param zoomWidth   实际分辨率旋转压缩后的宽度
     * @param zoomHeight  实际分辨率旋转压缩后的高度
     * @return
     */
    private byte[] cameraPreviewConvert(byte[] data, int width, int height, int orientation, int zoomWidth, int zoomHeight) {
        // 原始YUV数据转换RGB裸数据
        byte[] rgbData = new byte[width * height * 3];
        dtTool.YUV2RGB(data, width, height, rgbData);
        int[] rotateWidth = new int[1];
        int[] rotateHeight = new int[1];
        // 旋转相应角度
        int re = dtTool.ImageRotate(rgbData, width, height, orientation, rgbData, rotateWidth, rotateHeight);
        if (re != 1) {
            Log.e("asd", "旋转失败");
            return null;
        }
        //镜像后画框位置按照正常坐标系，不镜像的话按照反坐标系也可画框
        //        re = dtTool.ImageFlip(rgbData, rotateWidth[0], rotateHeight[0], 1, rgbData);
        //        if (re != 1) {
        //            Log.e("asd", "镜像失败");
        //            return null;
        //        }
        // RGB数据压缩到指定宽高
        byte[] zoomedRgbData = new byte[zoomWidth * zoomHeight * 3];
        re = dtTool.Zoom(rgbData, rotateWidth[0], rotateHeight[0], 3, zoomWidth, zoomHeight, zoomedRgbData);
        if (re != 1) {
            Log.e("asd", "压缩失败");
            return null;
        }
        return zoomedRgbData;
    }

    /**
     * 组装人脸信息存储容器数组
     *
     * @param size
     * @return
     */
    private MXFaceInfoEx[] makeFaceContainer(int size) {
        MXFaceInfoEx[] pFaceBuffer = new MXFaceInfoEx[size];
        for (int i = 0; i < size; i++) {
            pFaceBuffer[i] = new MXFaceInfoEx();
        }
        return pFaceBuffer;
    }

    /**
     * 检测人脸信息
     *
     * @param rgbData    RGB裸图像数据
     * @param width      图像数据宽度
     * @param height     图像数据高度
     * @param faceNum    native输出，检测到的人脸数量
     * @param faceBuffer native输出，人脸信息
     * @return true - 算法执行成功，并且检测到人脸，false - 算法执行失败，或者执行成功但是未检测到人脸
     */
    private boolean faceDetect(byte[] rgbData, int width, int height, int[] faceNum, MXFaceInfoEx[] faceBuffer) {
        synchronized (lock2) {
            int result = mxFaceAPI.mxDetectFace(rgbData, width, height, faceNum, faceBuffer);
            return result == 0 && faceNum[0] > 0;
        }
    }

    /**
    * 提取人脸信息
     *
     * @return MXFaceInfoEx
    * */
    private MXFaceInfoEx sortMXFaceInfoEx(MXFaceInfoEx[] mxFaceInfoExList) {
        MXFaceInfoEx maxMXFaceInfoEx = mxFaceInfoExList[0];
        for (MXFaceInfoEx mxFaceInfoEx : mxFaceInfoExList) {
            if (mxFaceInfoEx.width > maxMXFaceInfoEx.width) {
                maxMXFaceInfoEx = mxFaceInfoEx;
            }
        }
        return maxMXFaceInfoEx;
    }

    /**
     * 人脸质量检测
     *
     * @param rgbData    RGB裸图像数据
     * @param width      图像数据宽度
     * @param height     图像数据高度
     * @param faceNum    检测到人脸数量
     * @param faceBuffer 输入，人脸检测结果
     * @return
     */
    private boolean faceQuality(byte[] rgbData, int width, int height, int faceNum, MXFaceInfoEx[] faceBuffer) {
        int result = mxFaceAPI.mxFaceQuality(rgbData, width, height, faceNum, faceBuffer);
        return result == 0;
    }

    /**
     * RGB裸图像数据提取人脸特征
     *
     * @param pImage
     * @param width
     * @param height
     * @param faceInfo
     * @return
     */
    private byte[] extractFeature(byte[] pImage, int width, int height, MXFaceInfoEx faceInfo) {
        synchronized (lock1) {
            byte[] feature = new byte[mxFaceAPI.mxGetFeatureSize()];
            int result = mxFaceAPI.mxFeatureExtract(pImage, width, height, 1, new MXFaceInfoEx[]{faceInfo}, feature);
            return result == 0 ? feature : null;
        }
    }

    /**
    * Rgb数据解析
    * */
    public byte[] imageEncode(byte[] rgbBuf, int width, int height) {
        byte[] fileBuf = new byte[width * height * 4];
        int[] fileLength = new int[]{0};
        int re = dtTool.ImageEncode(rgbBuf, width, height, ".jpg", fileBuf, fileLength);
        if (re == 1 && fileLength[0] != 0) {
            byte[] fileImage = new byte[fileLength[0]];
            System.arraycopy(fileBuf, 0, fileImage, 0, fileImage.length);
            return fileImage;
        } else {
            return null;
        }
    }


    /**
     * 比对特征，人证比对0.7，人像比对0.8
     *
     * @param alpha
     * @param beta
     * @return
     */
    public float matchFeature(byte[] alpha, byte[] beta) {
        if (alpha != null && beta != null) {
            float[] score = new float[1];
            int re = mxFaceAPI.mxFeatureMatch(alpha, beta, score);
            if (re == 0) {
                return score[0];
            }
            return -1;
        }
        return 0;
    }

    /**
    *提取bitmap中人脸特征
    * */
    public PhotoFaceFeature getCardFaceFeatureByBitmapPosting(Bitmap bitmap) {
        String message = "";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] rgbData = imageFileDecode(outputStream.toByteArray(), bitmap.getWidth(), bitmap.getHeight());
        if (rgbData == null) {
            message = "图片转码失败";
            return new PhotoFaceFeature(message);
        }
        int[] pFaceNum = new int[]{0};
        MXFaceInfoEx[] pFaceBuffer = makeFaceContainer(MAX_FACE_NUM);
        boolean result = faceDetect(rgbData, bitmap.getWidth(), bitmap.getHeight(), pFaceNum, pFaceBuffer);
        if (result && pFaceNum[0] > 0) {
            MXFaceInfoEx mxFaceInfoEx = sortMXFaceInfoEx(pFaceBuffer);
            byte[] faceFeature = extractFeature(rgbData, bitmap.getWidth(), bitmap.getHeight(), mxFaceInfoEx);
            if (faceFeature != null) {
                return new PhotoFaceFeature(faceFeature, "提取成功");
            } else {
                message = "提取特征失败";
            }
        } else {
            message = "未检测到人脸";
        }
        return new PhotoFaceFeature(message);
    }

    /**
     * 图像文件解码成RGB裸数据
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    public byte[] imageFileDecode(byte[] data, int width, int height) {
        byte[] rgbData = new byte[width * height * 3];
        int[] oX = new int[1];
        int[] oY = new int[1];
        int result = dtTool.ImageDecode(data, data.length, rgbData, oX, oY);
        if (result > 0) {
            return rgbData;
        }
        return null;
    }


    private int initFaceModel(Context context) {
        String hsLibDirName = "zzFaceModel";
        String modelFile1 = "MIAXIS_V5.0.0_FaceDetect.model";
        String modelFile2 = "MIAXIS_V5.0.0_FaceQuality.model";
        String modelFile3 = "mx_eyeblink_detect.pb";
        File modelDir = new File(FileUtil.getFaceModelPath());
        if (modelDir.exists()) {
            if (!new File(modelDir + File.separator + modelFile1).exists()) {
                FileUtil.copyAssetsFile(context, hsLibDirName + File.separator + modelFile1, modelDir + File.separator + modelFile1);
            }
            if (!new File(modelDir + File.separator + modelFile2).exists()) {
                FileUtil.copyAssetsFile(context, hsLibDirName + File.separator + modelFile2, modelDir + File.separator + modelFile2);
            }
            if (!new File(modelDir + File.separator + modelFile3).exists()) {
                FileUtil.copyAssetsFile(context, hsLibDirName + File.separator + modelFile3, modelDir + File.separator + modelFile3);
            }
            return 0;
        } else {
            return -1;
        }

    }

//    /**
//     * 视频流转RGB
//     *
//     * @param yuv 视频流数据NV21
//     */
//    public byte[] yuv2Rgb(byte[] yuv, int width, int height) {
//        if (this.mMxImageTool == null) {
//            return null;
//        }
//        byte[] pRGBImage = new byte[width * height * 3];
//        this.mMxImageTool.YUV2RGB(yuv, width, height, pRGBImage);
//
//
//        //        byte[] p = new byte[pRGBImage.length];
//        //        this.mMxImageTool.ImageFlip(pRGBImage,width,height,1,p);
//
//        return pRGBImage;
//    }

//    public Size rotate(byte[] pRGBImage, int iRGBWidth, int iRGBHeight, int rotation, byte[] out) {
//        if (this.mMxImageTool == null || pRGBImage == null || pRGBImage.length == 0
//                || out == null || out.length == 0) {
//            return null;
//        }
//        int[] width = new int[1];
//        int[] height = new int[1];
//        int imageFlip = mMxImageTool.ImageRotate(pRGBImage, iRGBWidth, iRGBHeight, rotation, out, width, height);
//        if (imageFlip != 1) {
//            return null;
//        }
//        return new Size(width[0], height[0]);
//    }

    /**
     * 活体检测
     *
     * @return 10000-Live，10001-no live，others-image quality is not satisfied , negative Number is error code
     */
//    public int liveDetect(byte[] rgbFrameData, int frameWidth, int frameHeight, byte[] nirFrameData) {
//        if (this.mxFaceAPI == null) {
//            return -99;
//        }
//        if (rgbFrameData == null || rgbFrameData.length == 0) {
//            return -98;
//        }
//        if (nirFrameData == null || nirFrameData.length == 0) {
//            return -97;
//        }
//        if (this.lastVisiblePreviewData == null || this.lastVisiblePreviewData.length == 0) {
//            return -96;
//        }
//        if (this.nirVisiblePreviewData == null || this.nirVisiblePreviewData.length == 0) {
//            return -95;
//        }
//        if (this.mFaceInfoExesRgb == null || this.mFaceInfoExesRgb.length == 0) {
//            return -94;
//        }
//        if (this.mFaceInfoExesNir == null || this.mFaceInfoExesNir.length == 0) {
//            return -93;
//        }
////        if (this.mFaceNumberRgb == null || this.mFaceNumberRgb.length == 0) {
////            return -96;
////        }
////        if (this.mFaceNumberNir == null || this.mFaceNumberNir.length == 0) {
////            return -95;
////        }
////        if (this.mFaceInfoExesRgb == null || this.mFaceInfoExesRgb.length == 0) {
////            return -94;
////        }
////        if (this.mFaceInfoExesNir == null || this.mFaceInfoExesNir.length == 0) {
////            return -93;
////        }
////        return this.mxFaceAPI.mxDetectLive(rgbFrameData, nirFrameData, frameWidth, frameHeight,
////                this.mFaceNumberRgb, this.mFaceInfoExesRgb, this.mFaceNumberNir, this.mFaceInfoExesNir);
//        return
//    }
}
