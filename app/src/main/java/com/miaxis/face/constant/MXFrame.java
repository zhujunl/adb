package com.miaxis.face.constant;


/**
 * @author Tank
 * @version $
 * @des
 * @updateAuthor $
 * @updateDes
 */
public class MXFrame {

    public int width;
    public int height;
    public MXCamera camera;
    public byte[] buffer;
    public int orientation;

    public MXFrame(MXCamera camera, byte[] buffer, int width, int height, int orientation) {
        this.width = width;
        this.height = height;
        this.camera = camera;
        this.buffer = buffer;
        this.orientation = orientation;
    }

    public boolean isBufferEmpty() {
        return buffer == null || buffer.length == 0;
    }

    public boolean isSizeLegal() {
        return this.width > 0 && this.height > 0;
    }

    public static boolean isSizeLegal(MXFrame frame) {
        return frame != null && frame.isSizeLegal();
    }

    public boolean isNullCamera() {
        return camera == null;
    }

    public static MXFrame processFrame(MXFrame frame, int orientation) {
//        if (frame != null && !frame.isBufferEmpty() && frame.isSizeLegal()) {
//            byte[] rgb = FaceManager.getInstance().yuv2Rgb(frame.buffer, frame.width, frame.height);
////            byte[] out = new byte[rgb.length];
////            Size rotate = FaceManager.getInstance().rotate(rgb, frame.width, frame.height, orientation, out);
////            if (rotate != null) {
////                return new MXFrame(frame.camera, out, rotate.getWidth(), rotate.getHeight(), orientation);
////            }
//            return new MXFrame(frame.camera,rgb,frame.width, frame.height,orientation);
//        }
        return null;
    }

    public static boolean isNullCamera(MXFrame frame) {
        return frame == null || frame.isNullCamera();
    }

    public static boolean isBufferEmpty(MXFrame frame) {
        return frame == null || frame.isBufferEmpty();
    }
}
