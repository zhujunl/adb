package com.miaxis.face.bean;

public class MxRGBImage {

    private byte[] rgbImage;
    private int width;
    private int height;

    public MxRGBImage(byte[] rgbImage, int width, int height) {
        this.rgbImage = rgbImage;
        this.width = width;
        this.height = height;
    }

    public byte[] getRgbImage() {
        return rgbImage;
    }

    public void setRgbImage(byte[] rgbImage) {
        this.rgbImage = rgbImage;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
