package com.miaxis.face.manager;

import android.hardware.Camera;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;

import com.miaxis.face.constant.Constants;

import java.util.List;

/**
 * @author ZJL
 * @date 2022/9/2 10:19
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CameraManager {

    private CameraManager() {
    }

    public static CameraManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final CameraManager instance = new CameraManager();
    }

    public static int PRE_WIDTH = 640;
    public static int PRE_HEIGHT = 480;
    public static int PIC_WIDTH = 640;
    public static int PIC_HEIGHT = 480;
    public static int ORIENTATION = 180;

    private  Camera camera;
    private OnCameraOpenListener listener;
    private volatile boolean monitorFlag = false;
    private long lastCameraCallBackTime;
    private SurfaceHolder shMain;

    public synchronized void openCamera(@NonNull SurfaceHolder holder, @NonNull CameraManager.OnCameraOpenListener listener) {
        try {
            this.listener = listener;
//            if (shMain == null) {
//                shMain=surfaceView.getHolder();
//                shMain.addCallback();
//            }

            //            textureViewFlip(textureView);
            openVisibleCamera();
            if (shMain != null) {
                camera.setPreviewDisplay(holder);
            }
            listener.onCameraOpen(camera.getParameters().getPreviewSize(), "");
            //            if (surfaceTexture == null) {
            //                textureView.setSurfaceTextureListener(textureListener);
            //            } else {
            //                camera.setPreviewTexture(surfaceTexture);
            //            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onCameraOpen(null, "异常: "+e);
        }
    }

    private void openVisibleCamera() {
        //        try {
        for (int i = 0; i < 5; i++) {
            if (camera==null){
                try {
                    camera = Camera.open(0);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (camera!=null){
                    break;
                }
                SystemClock.sleep(500);
            }
        }
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        int maxWidth = 0;
        int maxHeight = 0;
        for (Camera.Size size : supportedPreviewSizes) {
            maxWidth = Math.max(size.width, maxWidth);
            maxHeight = Math.max(size.height, maxHeight);
        }
        ORIENTATION = maxWidth * maxHeight >= (200 * 10000) ? 0 : (!Constants.VERSION?0:180);//处理摄像头，500W不需要旋转
        if(ORIENTATION==0&&!Constants.VERSION){
            PRE_WIDTH = 800;
            PRE_HEIGHT = 600;
            PIC_WIDTH = 800;
            PIC_HEIGHT = 600;
        }
        parameters.setPreviewSize(PRE_WIDTH, PRE_HEIGHT);
        parameters.setPictureSize(PIC_WIDTH, PIC_HEIGHT);
        //对焦模式设置
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.size() > 0) {
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
        }
        try {
            camera.setParameters(parameters);
        }catch (Exception e){
            e.printStackTrace();
        }
        camera.setDisplayOrientation(ORIENTATION);

        camera.startPreview();
        //        } catch (Exception e) {
        //            e.printStackTrace();
        ////            new Thread(() -> {
        ////                if (retryTime <= RETRY_TIMES) {
        ////                    retryTime++;
        ////                    openVisibleCamera();
        ////                }
        ////            }).start();
        //        }
    }

    public void closeCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //            new Thread(() -> {
            //                if (retryTime <= RETRY_TIMES) {
            //                    retryTime++;
            //                    closeCamera();
            //                }
            //            }).start();
        }
    }

    public Camera getCamera() {
        return camera;
    }


    public interface OnCameraOpenListener {
        void onCameraOpen(Camera.Size previewSize, String message);
        void onCameraError();
    }



}
