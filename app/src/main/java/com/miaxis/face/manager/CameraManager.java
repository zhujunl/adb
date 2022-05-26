package com.miaxis.face.manager;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.miaxis.callback.FaceCallback;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.constant.CameraConfig;
import com.miaxis.face.constant.MXFrame;
import com.miaxis.face.constant.ZZResponse;
import com.miaxis.face.util.LogUtil;

import java.io.IOException;

import static com.miaxis.face.constant.Constants.PIC_HEIGHT;
import static com.miaxis.face.constant.Constants.PIC_WIDTH;
import static com.miaxis.face.constant.Constants.PRE_HEIGHT;
import static com.miaxis.face.constant.Constants.PRE_WIDTH;

/**
 * @author ZJL
 * @date 2022/5/11 15:06
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CameraManager {

    private Camera mCamera;
    private Camera mirCamera;
    private byte[] buffer;
    private byte[] nirbuffer;
    private final String TAG="CameraManager";
    private SurfaceHolder shMain;
    private long noActionSecond = 0;
    private boolean monitorFlag = true;
    private long lastCameraCallBackTime = 9999999999999L;
    private SurfaceTexture surfaceTexture = null;

    MXFrame rgbFrame;

    public CameraManager() {}

    public static CameraManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final CameraManager instance = new CameraManager();
    }



    public void open(SurfaceView surfaceView,int cameraId) {
        try {
            mCamera = Camera.open(cameraId);
            Camera.Parameters parameters = mCamera.getParameters();
//            List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
//            for (Camera.Size size  : supportedPictureSizes) {
//            	Log.e(TAG, "supportedPictureSizes.width==" + size.width+"     h=="+size.height);
//            }
//            Log.e(TAG, "====================================");
//            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
//            for (Camera.Size size  : supportedPreviewSizes) {
//                Log.e(TAG, "supportedPreviewSizes.width==" + size.width+"     h=="+size.height);
//            }
            parameters.setPreviewSize(PRE_WIDTH, PRE_HEIGHT);
            parameters.setPictureSize(PRE_WIDTH, PRE_HEIGHT);
            buffer=new byte[((PRE_WIDTH * PRE_HEIGHT) * ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8];
            mCamera.addCallbackBuffer(buffer);
            mCamera.setParameters(parameters);
            mCamera.setPreviewCallbackWithBuffer(mPreviewCallback);
            mCamera.startPreview();
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try {
                        if (mCamera!=null) {
                            mCamera.setPreviewDisplay(surfaceHolder);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    FaceManager.getInstance().stopLoop();
                    close();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void nir_open(SurfaceView surfaceView,int cameraId,boolean nirFlag) {
        if (!nirFlag){
            return;
        }
        try {
            mirCamera = Camera.open(cameraId);
            Camera.Parameters parameters = mirCamera.getParameters();
//            List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
//            for (Camera.Size size  : supportedPictureSizes) {
//                Log.e(TAG, "supportedPictureSizes.width==" + size.width+"     h=="+size.height);
//            }
//            Log.e(TAG, "====================================");
//            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
//            for (Camera.Size size  : supportedPreviewSizes) {
//                Log.e(TAG, "supportedPreviewSizes.width==" + size.width+"     h=="+size.height);
//            }
            parameters.setPreviewSize(PIC_WIDTH, PIC_HEIGHT);
            parameters.setPictureSize(PIC_WIDTH, PIC_HEIGHT);
            mirCamera.setParameters(parameters);
            nirbuffer=new byte[((PIC_WIDTH * PIC_HEIGHT) * ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8];
            mirCamera.addCallbackBuffer(nirbuffer);
            mirCamera.setPreviewCallback(nirPreView);
            mirCamera.startPreview();
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try {
                        if (mirCamera!=null){
                            mirCamera.setPreviewDisplay(surfaceHolder);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    FaceManager.getInstance().stopLoop();
                    nirClose();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void open(SurfaceView surfaceView,Camera.PreviewCallback previewCallback,int cameraId) {
        try {
            mCamera = Camera.open(cameraId);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(PRE_WIDTH, PRE_HEIGHT);
            parameters.setPictureSize(PIC_WIDTH, PIC_HEIGHT);
            buffer=new byte[((PRE_WIDTH * PRE_HEIGHT) * ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8];
            mCamera.addCallbackBuffer(buffer);
            mCamera.setParameters(parameters);
            mCamera.setPreviewCallbackWithBuffer(previewCallback);
            mCamera.startPreview();
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try {
                        mCamera.setPreviewDisplay(surfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    FaceManager.getInstance().stopLoop();
                    close();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void close() {
        try {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } else {
                Log.e(TAG, "mCamera == null");
            }
        } catch (Exception e) {
            LogUtil.writeLog("关闭摄像头异常" + e.getMessage());
        }
    }

    public void nirClose() {
        try {
            if (mirCamera != null) {
                mirCamera.setPreviewCallback(null);
                mirCamera.stopPreview();
                mirCamera.release();
                mirCamera = null;
            } else {
                Log.e(TAG, "mirCamera == null");
            }
        } catch (Exception e) {
            LogUtil.writeLog("关闭摄像头异常" + e.getMessage());
        }
    }

    public int takePicture(Camera.PictureCallback jpeg) {
        if (this.mCamera == null) {
            return -1;
        }

        this.mCamera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
            }
        }, jpeg);
        return 0;
    }


    Camera.PreviewCallback mPreviewCallback=new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            camera.addCallbackBuffer(buffer);
            FaceManager.getInstance().setLastVisiblePreviewData(bytes);
        }
    };

    Camera.PreviewCallback nirPreView=new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            camera.addCallbackBuffer(nirbuffer);
            FaceManager.getInstance().setNirVisiblePreviewData(bytes);
        }
    };

    public Camera getCamera() {
        return mCamera;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    /* 线程 监控视频流回调 onPreviewFrame  是否有数据返回，设置时间内无数据返回 重启摄像头 */
    private class MonitorThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    noActionSecond++;
                    if (monitorFlag) {
                        long cur = System.currentTimeMillis();
//                        if ((cur - lastCameraCallBackTime) >= config.getIntervalTime() * 1000) {
                            LogUtil.writeLog("开始修复视频卡顿");
                            Thread.sleep(1000);
                            Thread.sleep(1000);
                            LogUtil.writeLog("修复视频卡顿结束");
//                        }

                    }
                } catch (Exception e) {
                    LogUtil.writeLog("修复视频卡顿线程 异常" + e.getMessage());
                }
            }
        }
    }

    public void processRgbFrame(final MXFrame frame, final FaceCallback captureCallback) {
        Log.e(TAG, "processRgbFrame" );
        Face_App.getInstance().getThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                rgbFrame = MXFrame.processFrame(frame, CameraConfig.Camera_RGB.bufferOrientation);
                if (MXFrame.isBufferEmpty(rgbFrame)) {
                    captureCallback.onError(ZZResponse.CreateFail(-80, "RGB帧数据处理失败"));
                } else {
                    captureCallback.onRgbProcessReady();
                }
            }
        });
    }


    byte[] rgbFrameFaceFeature;
    /**
     * @param nirFrame 摄像头视频帧数据
     */
    public void detectLive(final MXFrame nirFrame, final FaceCallback captureCallback) {
        rgbFrameFaceFeature = null;
        Face_App.getInstance().getThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                MXFrame nir = MXFrame.processFrame(nirFrame, CameraConfig.Camera_NIR.bufferOrientation);
                if (nir != null) {

                    //测试  test保存黑白
                    //                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/A/A/" + System.currentTimeMillis() + ".bmp";
                    //                FaceManager.getInstance().saveRgbTiFile(nir.buffer, nir.width, nir.height, path);
                    /*==============================================*/
                    //获取RGB可见光摄像头
                    if (!MXFrame.isBufferEmpty(rgbFrame)) {
//                        {
//                            int liveDetect = FaceManager.getInstance().liveDetect(rgbFrame.buffer, rgbFrame.width, rgbFrame.height, nir.buffer);
//
//                            //测试   test 可见光
//                            //                    String paths = Environment.getExternalStorageDirectory().getAbsolutePath() + "/A/C/" + System.currentTimeMillis() + ".bmp";
//                            //                    FaceManager.getInstance().saveRgbTiFile(rgbFrame.buffer, rgbFrame.width, rgbFrame.height, paths);
//                            try {
//                                RectF rgbFaceRect = FaceManager.getInstance().getRgbFaceRect();
//                                //                        faceRect.postValue(rgbFaceRect);
//                                //test 人脸矩形
//                                //                        ZZResponse<MXCamera> mxCameraZZResponse = CameraHelper.getInstance().find(CameraConfig.Camera_RGB);
//                                //                        if (ZZResponse.isSuccess(mxCameraZZResponse)) {
//                                //                            mxCameraZZResponse.getData().setNextFrameEnable();
//                                //                        } else {
//                                //                            captureCallback.onMatchReady(false);
//                                //                        }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                //                        faceRect.postValue(null);
//                            }
//
//                            if (liveDetect == 10000) {//活体
//                                livingBodyCount = 0;
//                                faceTips.postValue("活体检测完成");/
//                                int faceQualityRGB = FaceManager.getInstance().getFaceQualityRGB(rgbFrame.buffer, rgbFrame.width, rgbFrame.height);
//                                if (faceQualityRGB == 0) {
//                                    MXFaceInfoEx faceInfoRGB = FaceManager.getInstance().getFaceInfoRGB(0);
//                                    int quality = faceInfoRGB.quality;
//                                    if (quality > FaceConfig.faceComparison) {
//                                        //                                faceTips.postValue("人脸质量检测完成:" + quality);
//                                        byte[] feature = new byte[FaceManager.getInstance().getFeatureSize()];
//                                        int extractFeatureRgb = FaceManager.getInstance().extractFeatureRgb(rgbFrame.buffer, rgbFrame.width, rgbFrame.height, false, feature);
//                                        if (extractFeatureRgb == 0) {
//                                            //测试  test 保存活体
//                                            //                            String p = Environment.getExternalStorageDirectory().getAbsolutePath() + "/A/B/" + System.currentTimeMillis() + ".bmp";
//                                            //                            FaceManager.getInstance().saveRgbTiFile(rgbFrame.buffer, rgbFrame.width, rgbFrame.height, p);
//                                            //                         /*=========================================================*/
//                                            rgbFrameFaceFeature = feature;
//                                            captureCallback.onLiveReady(nirFrame, true);
//                                        } else {
//                                            //                                    faceTips.postValue("提取特征失败");
//                                            captureCallback.onError(ZZResponse.CreateFail(-83, "提取特征失败"));
//                                        }
//                                    } else {
//                                        //                                faceTips.postValue("人脸质量检测过低：" + quality);
//                                        captureCallback.onError(ZZResponse.CreateFail(-86, "人脸质量检测过低,请重试！"));
//                                    }
//                                } else {
//                                    //                            faceTips.postValue("人脸质量检测失败：" + faceQualityRGB);
//                                    captureCallback.onError(ZZResponse.CreateFail(-86, "人脸质量检测失败！"));
//                                }
//                            } else if (liveDetect == 10001) {//非活体
//                                //                        faceTips.postValue("非活体");
//                                livingBodyCount = livingBodyCount + 1;
//                                if (livingBodyCount < 5) {
//                                    ZZResponse<MXCamera> mxCameraZZResponse = CameraHelper.getInstance().find(CameraConfig.Camera_RGB);
//                                    if (ZZResponse.isSuccess(mxCameraZZResponse)) {
//                                        mxCameraZZResponse.getData().setNextFrameEnable();
//                                    } else {
//                                        livingBodyCount = 0;
//                                        captureCallback.onError(ZZResponse.CreateFail(liveDetect, "非活体"));
//                                    }
//                                } else {
//                                    livingBodyCount = 0;
//                                    captureCallback.onError(ZZResponse.CreateFail(liveDetect, "非活体"));
//                                }
//                            } else if (liveDetect < 0) {
//                                //                        faceTips.postValue("活体检测异常");
//                                captureCallback.onError(ZZResponse.CreateFail(liveDetect, "活体检测异常"));
//                            } else {
//                                String liveError = FaceManager.getInstance().getLiveError(liveDetect);
//                                faceTips.postValue(liveError);
//                                captureCallback.onLiveReady(nirFrame, false);
//                            }
//                        }
                    } else {
                        //                    faceTips.postValue("RGB摄像头数据为空");
                        captureCallback.onError(ZZResponse.CreateFail(-82, "RGB摄像头数据为空"));
                    }
                } else {
                    captureCallback.onError(ZZResponse.CreateFail(-81, "NIR摄像头数据处理异常"));
                }
            }
        });
    }

}
