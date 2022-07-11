package com.miaxis.face.constant;

/**
 * @author Tank
 * @date 2021/5/16 13:19
 * @des
 * @updateAuthor
 * @updateDes
 */
public enum CameraConfig {

    //配置
    Camera_RGB(1,
            Constants.PRE_WIDTH,
            Constants.PRE_HEIGHT,
            0,
            0),
    Camera_NIR(0,
            Constants.PRE_WIDTH,
            Constants.PRE_HEIGHT,
            0,
            0),
    Camera_SM(2,
            1280,
            720,
            0,
            0);

    public int CameraId;
    public int width;
    public int height;
    public int previewOrientation;
    public int bufferOrientation;

    CameraConfig(int cameraId, int width, int height, int previewOrientation, int bufferOrientation) {
        this.CameraId = cameraId;
        this.width = width;
        this.height = height;
        this.previewOrientation = previewOrientation;
        this.bufferOrientation = bufferOrientation;
    }
}
