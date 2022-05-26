package com.miaxis.face.constant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

import com.miaxis.face.util.BitmapUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


/**
 * @author Tank
 * @version $
 * @des
 * @updateAuthor $
 * @updateDes
 */
public class MXCamera implements Camera.AutoFocusCallback, Camera.PreviewCallback {

    private int width = 1280;
    private int height = 480;
    private boolean isPreview = false;
    private CameraPreviewCallback mCameraPreviewCallback;

    private Camera mCamera;
    private int mCameraId;
    private int orientation;


    public MXCamera() {
    }

    public int init() {
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras <= 0) {
            return -1;
        }
        return 0;
    }

    public int getCameraId() {
        return this.mCameraId;
    }

    public ZZResponse<MXCamera> open(int cameraId, int width, int height) {
        if (this.mCamera != null) {
            return ZZResponse.CreateFail(-2, "already open");
        }
        //        if (cameraId >= Camera.getNumberOfCameras()) {
        //            return -4;
        //        }
        for (int i = 0; i < 3; i++) {
            try {
                this.mCameraId = cameraId;
                this.mCamera = Camera.open(cameraId);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                SystemClock.sleep(500);
            }
        }
        if (this.mCamera == null) {
            return ZZResponse.CreateFail(-1, "open camera failed");
        }
        try {
            Camera.Parameters parameters = this.mCamera.getParameters();
            if (cameraId == CameraConfig.Camera_SM.CameraId) {
                Camera.Parameters params = mCamera.getParameters();
                List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
                for (Camera.Size size:pictureSizes) {
                    Log.e("Camera.Size:", "width=="+size.width+"    height==="+size.height );
                }
                int length = pictureSizes.size();
                int w = 0;
                int h = 0;
                for (int i = 0; i < length; i++) {
                    if (pictureSizes.get(i).width > w) {
                        w = pictureSizes.get(i).width;
                        h = pictureSizes.get(i).height;
                    }
                }
                if (w != 0 && h != 0) {
//                    width = w;
//                    height = h;
                    width=1280;
                    height=720;
                }
            }
            parameters.setPreviewSize(width, height);
            parameters.setPictureSize(width, height);
            this.mCamera.setParameters(parameters);
            this.width = width;
            this.height = height;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (this.mCamera != null) {
                    this.mCamera.release();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return ZZResponse.CreateFail(-3, "set camera parameters failed," + e.getMessage());
        }
        this.buffer = new byte[((this.width * this.height) * ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8];
        return ZZResponse.CreateSuccess(this);
    }

    public int setOrientation(int orientation) {
        if (this.mCamera == null) {
            return -1;
        }
        this.mCamera.setDisplayOrientation(orientation);
        this.orientation = orientation;
        return 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private byte[] buffer;

    public int setPreviewCallback(CameraPreviewCallback cameraPreviewCallback) {
        if (this.mCamera == null) {
            return -1;
        }
//        this.mCamera.addCallbackBuffer(buffer);
        this.mCamera.setPreviewCallbackWithBuffer(this);
        this.mCameraPreviewCallback = cameraPreviewCallback;
        Log.e("MXCamera:", "setPreviewCallbackWithBuffer" );
        return 0;
    }

    public int takePicture(Camera.PictureCallback jpeg) {
        if (this.mCamera == null) {
            return -1;
        }

        if (!this.isPreview) {
            return -2;
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

    public int setNextFrameEnable() {
        if (this.mCamera == null) {
            return -1;
        }
        this.mCamera.addCallbackBuffer(this.buffer);
        return 0;
    }

    public byte[] getCurrentFrame() {
        if (this.mCamera == null || !this.isPreview) {
            return null;
        }
        return this.buffer;
    }

    public int setFocus(boolean focus) {
        if (this.mCamera == null) {
            return -1;
        }
        Camera.Parameters parameters = this.mCamera.getParameters();
        boolean support = false;
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        for (String focusMode : supportedFocusModes) {
            if (Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO.equals(focusMode)) {
                support = true;
                break;
            }
        }
        if (!support) {
            return -2;
        }
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        this.mCamera.setParameters(parameters);
        this.mCamera.cancelAutoFocus();
        if (focus) {
            this.mCamera.autoFocus(this);
        }
        return 0;
    }

    public int start(SurfaceHolder holder) {
        if (this.mCamera == null) {
            return -1;
        }
        try {
            this.mCamera.setPreviewDisplay(holder);
            this.mCamera.startPreview();
            this.isPreview = true;
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -2;
    }

    public int startTexture(SurfaceTexture holder) {
        if (this.mCamera == null) {
            return -1;
        }
        try {
            this.mCamera.setPreviewTexture(holder);
            this.mCamera.startPreview();

            this.isPreview = true;
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -2;
    }


    public int resume() {
        if (this.mCamera == null) {
            return -1;
        }
        if (this.isPreview) {
            this.mCamera.startPreview();
        }
        return 0;
    }

    public int pause() {
        if (this.mCamera == null) {
            return -1;
        }
        this.mCamera.stopPreview();
        return 0;
    }

    public int stop() {
        if (this.mCamera == null) {
            return -1;
        }
        try {
            this.mCamera.stopPreview();
            this.mCamera.setPreviewCallback(null);
            this.mCamera.setPreviewCallbackWithBuffer(null);
            this.mCamera.release();
            this.mCamera = null;
            this.isPreview = false;
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -2;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
//        camera.addCallbackBuffer(buffer);
        if (mCameraPreviewCallback != null) {
            mCameraPreviewCallback.onPreview(this,new MXFrame(this, data, this.width, this.height, this.orientation));
        }
    }

    /**
     * 保存视频帧数据
     * 视频帧Buffer为 setNextFrameEnable() 后一帧数据
     *
     * @param savePath 保存文件路径
     * @return true:保存成功    false:失败
     */
    public boolean saveFrameImage(String savePath) {
        try {
            File file = new File(savePath);
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (parentFile != null && !parentFile.exists()) {
                    boolean mkdirs = parentFile.mkdirs();
                }
            } else {
                boolean newFile = file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            YuvImage image = new YuvImage(this.buffer, ImageFormat.NV21, this.width, this.height, null);
            //图像压缩
            boolean success = image.compressToJpeg(
                    new Rect(0, 0, image.getWidth(), image.getHeight()), 90, fileOutputStream);
            if (!success) {
                return false;
            }
            //将得到的照片进行270°旋转，使其竖直
            Bitmap bitmap = BitmapFactory.decodeFile(savePath);
            Matrix matrix = new Matrix();
            //图片保存旋转尺寸 根据设备不同旋转不同
//            matrix.preRotate(BuildConfig.ROTATION_SIZE);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return BitmapUtils.saveBitmap(bitmap, savePath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //进度条设置
    public void setZoom(int zoom) {
        if (this.mCamera == null) {
            return;
        }
        Camera.Parameters parameters = this.mCamera.getParameters();
        boolean zoomSupported = parameters.isZoomSupported();
//        boolean smoothZoomSupported = parameters.isSmoothZoomSupported();
        if (zoomSupported) {
            int maxZoom = parameters.getMaxZoom();
            if (zoom >= 1 && zoom <= maxZoom) {
                parameters.setZoom(zoom);
                this.mCamera.setParameters(parameters);
            }
        }
    }

    public int getMaxZoom() {
        if (this.mCamera == null) {
            return -1;
        }
        Camera.Parameters parameters = this.mCamera.getParameters();
        return parameters.getMaxZoom();
    }

    public Camera getCamera() {
        return mCamera;
    }
}
