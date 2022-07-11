package com.miaxis.face.manager;

import android.hardware.Camera;
import android.util.Log;

import com.miaxis.face.constant.CameraConfig;
import com.miaxis.face.constant.MXCamera;
import com.miaxis.face.constant.MXCameraErrorCode;
import com.miaxis.face.constant.ZZResponse;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Admin
 * @version $
 * @des
 * @updateAuthor $
 * @updateDes
 */
public class CameraHelper {

    private CopyOnWriteArrayList<MXCamera> mMXCameras;

    private CameraHelper() {
        this.mMXCameras = new CopyOnWriteArrayList<>();
    }

    private static class CameraHelperHolder {
        static CameraHelper mCameraHelper = new CameraHelper();
    }

    public synchronized static CameraHelper getInstance() {
        return CameraHelperHolder.mCameraHelper;
    }

    public ZZResponse<?> init() {
        free();
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras <= 0) {
            return ZZResponse.CreateFail(MXCameraErrorCode.CODE_FAIL_NO_CAMERA, MXCameraErrorCode.MSG_FAIL_NO_CAMERA);
        }
        //        if (numberOfCameras < 2) {
        //            return ZZResponse.CreateFail(MXCameraErrorCode.CODE_FAIL_CAMERA_COUNTS_LESS, MXCameraErrorCode.MSG_FAIL_CAMERA_COUNTS_LESS + ":" + numberOfCameras);
        //        }
        return ZZResponse.CreateSuccess();
    }

    public void free() {
        if (this.mMXCameras != null && !this.mMXCameras.isEmpty()) {
            for (MXCamera camera : this.mMXCameras) {
                camera.stop();
            }
            this.mMXCameras.clear();
        }
    }

    public ZZResponse<MXCamera> createMXCamera(CameraConfig cameraConfig) {
        if (cameraConfig == null) {
            return ZZResponse.CreateFail(-90, "config error");
        }
        ZZResponse<MXCamera> mxCameraZZResponse = find(cameraConfig.CameraId);
        if (ZZResponse.isSuccess(mxCameraZZResponse)) {
            return mxCameraZZResponse;
        }
        MXCamera mxCamera = new MXCamera();
        int init = mxCamera.init();
        if (init == 0) {
            ZZResponse<MXCamera> open = mxCamera.open(cameraConfig.CameraId, cameraConfig.width, cameraConfig.height);
            if (ZZResponse.isSuccess(open)) {
                Log.e("CameraHelper:", "打开成功，相机id=" + open.getData().getCameraId());
                int setOrientation = mxCamera.setOrientation(cameraConfig.previewOrientation);
                if (setOrientation == 0) {
                    addMXCamera(mxCamera);
                } else {
//                    return ZZResponse.CreateFail(MXCameraErrorCode.CODE_FAIL_CAMERA_ORIENTATION, MXCameraErrorCode.MSG_FAIL_CAMERA_ORIENTATION);
                }
            }
            return open;
        } else {
            return ZZResponse.CreateFail(MXCameraErrorCode.CODE_FAIL_NO_CAMERA, MXCameraErrorCode.MSG_FAIL_NO_CAMERA);
        }
    }

    public synchronized ZZResponse<MXCamera> find(CameraConfig cameraConfig) {
        if (cameraConfig == null) {
            return ZZResponse.CreateFail(-90, "config error");
        }
        return find(cameraConfig.CameraId);
    }

    public synchronized ZZResponse<MXCamera> find(int cameraId) {
        if (cameraId < 0) {
            return ZZResponse.CreateFail(MXCameraErrorCode.CODE_FAIL_CAMERA_ID, null);
        }
        if (this.mMXCameras != null) {
            for (MXCamera mxCamera : this.mMXCameras) {
                if (mxCamera.getCameraId() == cameraId) {
                    return ZZResponse.CreateSuccess(mxCamera);
                }
            }
        }
        return ZZResponse.CreateFail(MXCameraErrorCode.CODE_FAIL_CAMERA_ID_NOT_FOUND, null);
    }

    private synchronized void addMXCamera(MXCamera mxCamera) {
        if (mxCamera == null) {
            return;
        }
        if (this.mMXCameras != null) {
            this.mMXCameras.add(mxCamera);
        }
    }

    public synchronized int resume() {
        if (this.mMXCameras != null && !this.mMXCameras.isEmpty()) {
            for (MXCamera mxCamera : this.mMXCameras) {
                if (mxCamera != null) {
                    mxCamera.resume();
                }
            }
        }
        return 0;
    }

    public synchronized int pause() {
        if (this.mMXCameras != null && !this.mMXCameras.isEmpty()) {
            for (MXCamera mxCamera : this.mMXCameras) {
                if (mxCamera != null) {
                    mxCamera.pause();
                }
            }
        }
        return 0;
    }

    public synchronized int stop() {
        if (this.mMXCameras != null && !this.mMXCameras.isEmpty()) {
            for (MXCamera mxCamera : this.mMXCameras) {
                if (mxCamera != null) {
                    mxCamera.stop();
                }
            }
        }
        return 0;
    }


}
