package com.miaxis.image;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import org.zz.jni.MXImageUtils;

import java.io.ByteArrayOutputStream;

/**
 * MXImage Utils
 *
 * @date: 2018/11/13 15:23
 * @author: zhang.yw
 * @project: FaceRecognition2
 */
public class MXImages {

    static {
        System.loadLibrary("mx-image");
    }

    public static byte[] covertToJpeg(MXImage image) {
        if (image == null) {
            throw new NullPointerException("image = null !");
        }
        switch (image.getFormat()) {
            case MXImage.FORMAT_BGR:
                return MXImageUtils.encodeImage(image, ".jpg");
            case MXImage.FORMAT_YUV:
                YuvImage yuvImage = new YuvImage(image.getData(), ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
                ByteArrayOutputStream os = new ByteArrayOutputStream(image.getData().length);
                yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, os);
                return os.toByteArray();
            default:
                throw new IllegalArgumentException("UnKnow format " + image.getFormat() + ", Only Support format BGR or YUV! ");
        }
    }

    public static Bitmap covertToBitmap(MXImage image) {
        if (image == null) {
            throw new NullPointerException("image = null !");
        }
        switch (image.getFormat()) {
            case MXImage.FORMAT_BGR:
                /*return RGBBitmaps.rgb2Bitmap(image.getData(), image.getWidth(), image.getHeight());*/
                int[] colors = MXImages.BGR2Pixel(image.getData());
                return Bitmap.createBitmap(colors, 0, image.getWidth(), image.getWidth(), image.getHeight(), Bitmap.Config.RGB_565);
            case MXImage.FORMAT_YUV:
                YuvImage yuvImage = new YuvImage(image.getData(), ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
                ByteArrayOutputStream os = new ByteArrayOutputStream(image.getData().length);
                yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, os);
                byte[] buffer = os.toByteArray();
                return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
            //throw new IllegalArgumentException("UnSupport format " + image.getFormat() + ",  Only Support format BGR ! ");
            default:
                throw new IllegalArgumentException("UnKnow format " + image.getFormat() + ", Only Support format BGR or YUV! ");
        }
    }


    private static long avTime;
    private static int counter;

    public static MXImage yuv2BGR(MXImage image) {
        if (image == null) {
            throw new NullPointerException("image = null !");
        }
        if (image.getFormat() != MXImage.FORMAT_YUV) {
            throw new IllegalArgumentException("UnSupport format " + image.getFormatName() + ", Only Support format YUV ! ");
        }
        long startTime = System.nanoTime();
        byte[] bytes = convertYUV2BGR(image.getData(), image.getWidth(), image.getHeight());
        long estimatedTime = System.nanoTime() - startTime;
        avTime += estimatedTime;
        counter++;
        Log.i("MXImages", "yuv2BGR: " + (avTime / counter) + " now" + estimatedTime);
        return new MXImage(bytes, image.getWidth(), image.getHeight(), MXImage.FORMAT_BGR, image.getChannel());
    }

    public static MXImage mirror(MXImage image) {
        if (image == null) {
            throw new NullPointerException("image = null !");
        }
        if (image.getFormat() != MXImage.FORMAT_BGR) {
            throw new IllegalArgumentException("UnSupport format " + image.getFormatName() + ", Now Only Support format BGR ! ");
        }
        byte[] data = MXImages.mirrorBGR(image.getData(), image.getWidth(), image.getHeight());
        return new MXImage(data, image.getWidth(), image.getHeight(), image.getFormat(), image.getChannel());
    }

    public static MXImage crop(MXImage image, Rect rect) {
        if (image == null) {
            throw new NullPointerException("image = null !");
        }
        if (rect == null) {
            throw new NullPointerException("rect  = null !");
        }
        if (image.getFormat() != MXImage.FORMAT_YUV) {
            throw new IllegalArgumentException("UnSupport format " + image.getFormatName() + ", Only Support format YUV ! ");
        }
        if (rect.left < 0 || rect.top < 0 || rect.right > image.getWidth() || rect.bottom > image.getHeight()) {
            throw new IllegalArgumentException("rect not in image bound !");
        }
        byte[] bytes = MXImages.cropYUV(image.getData(), image.getWidth(), image.getHeight(), rect.left, rect.top, rect.width(), rect.height());

        return new MXImage(bytes, rect.width(), rect.height(), image.getFormat(), image.getChannel());
    }


    public static MXImage scale(MXImage image, float scale) {
        if (image == null) {
            throw new NullPointerException("image = null !");
        }
        if (scale >= 1) {
            return image;
        }
        if (image.getFormat() != MXImage.FORMAT_BGR) {
            throw new IllegalArgumentException("UnSupport format " + image.getFormatName() + ", Only Support format BGR ! ");
        }
        int targetWidth = (int) (image.getWidth() * scale);
        int targetHeight = (int) (image.getHeight() * scale);
        byte[] bytes = MXImages.scaleBGR(image.getData(), image.getWidth(), image.getHeight(), targetWidth, targetHeight, 24);
        return new MXImage(bytes, targetWidth, targetHeight, image.getFormat(), image.getChannel());
    }


    public static MXImage rotate(MXImage image, int degree) {
        if (image == null) {
            throw new NullPointerException("image = null !");
        }
        if (image.getFormat() != MXImage.FORMAT_YUV) {
            throw new IllegalArgumentException("UnSupport format " + image.getFormatName() + ", Now Only Support format YUV ! ");
        }
        switch (degree) {
            case 0:
                return new MXImage(image.getData(), image.getWidth(), image.getHeight(), image.getFormat(), image.getChannel());
            case 90: {
                byte[] data = MXImages.rotateYuv90(image.getData(), image.getWidth(), image.getHeight());
                return new MXImage(data, image.getHeight(), image.getWidth(), image.getFormat(), image.getChannel());
            }
            case 180: {
                byte[] data = MXImages.rotateYuv180(image.getData(), image.getWidth(), image.getHeight());
                return new MXImage(data, image.getWidth(), image.getHeight(), image.getFormat(), image.getChannel());
            }
            case 270: {
                byte[] data = MXImages.rotateYuv270(image.getData(), image.getWidth(), image.getHeight());
                return new MXImage(data, image.getHeight(), image.getWidth(), image.getFormat(), image.getChannel());
            }
            default:
                throw new IllegalArgumentException("UnSupport degree " + degree);
        }

    }

    public static String getFormatName(int format) {
        switch (format) {
            case MXImage.FORMAT_BGR:
                return "BGR";
            case MXImage.FORMAT_YUV:
                return "YUV";
            default:
                return "UnKnow";
        }
    }

    public static String getChannelName(int channel) {
        switch (channel) {
            case MXImage.CHANNEL_RGB:
                return "RGB";
            case MXImage.CHANNEL_GRAY:
                return "GRAY";
            default:
                return "UnKnow";
        }
    }

    private static native byte[] rotateYuv90(byte[] data, int width, int height);

    private static native byte[] rotateYuv180(byte[] data, int width, int height);

    private static native byte[] rotateYuv270(byte[] data, int width, int height);

    private static native byte[] cropYUV(byte[] bgrImage, int width, int height, int x, int y, int dw, int dh);

    private static native byte[] convertYUV2BGR(byte[] bgrImage, int width, int height);

    private static native byte[] scaleBGR(byte[] data, int width, int height, int targetWidth, int targetHeight, int colorBits);

    private static native int[] BGR2Pixel(byte[] data);

    private static native byte[] mirrorBGR(byte[] data, int width, int height);
}
