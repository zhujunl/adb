package com.miaxis.image;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;


/**
 * 图片数据
 *
 * @date: 2018/11/12 10:20
 * @author: zhang.yw
 * @project: FaceRecognition2
 */
public class MXImage implements Parcelable {

    @IntDef({FORMAT_YUV, FORMAT_BGR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Format {
    }

    @IntDef({CHANNEL_GRAY, CHANNEL_RGB})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Channel {
    }

    public static final int FORMAT_YUV = 0;
    public static final int FORMAT_BGR = 1;

    public static final int CHANNEL_GRAY = 1;
    public static final int CHANNEL_RGB = 3;

    private int width;
    private int height;
    private int format;
    private int channel;
    private byte[] data;
    private Object tag;

    public MXImage() {
    }

    public MXImage(int width, int height) {
        this(null, width, height, FORMAT_BGR, CHANNEL_RGB);
    }

    public MXImage(byte[] data, int width, int height) {
        this(data, width, height, FORMAT_BGR, CHANNEL_RGB);
    }

    public MXImage(byte[] data, int width, int height, @Format int format) {
        this(data, width, height, format, CHANNEL_RGB);
    }

    public MXImage(byte[] data, int width, int height, @Format int format, @Channel int channel) {
        this.width = width;
        this.height = height;
        this.format = format;
        this.channel = channel;
        this.data = data;
    }

    @Deprecated
    public MXImage(int width, int height, int format, int channel, byte[] data) {
        this.width = width;
        this.height = height;
        this.format = format;
        this.channel = channel;
        this.data = data;
    }

    protected MXImage(Parcel in) {
        width = in.readInt();
        height = in.readInt();
        format = in.readInt();
        channel = in.readInt();
        data = in.createByteArray();
    }


    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public void recycle() {

    }

    public boolean isRecycled() {
        return false;
    }


    public static final Creator<MXImage> CREATOR = new Creator<MXImage>() {
        @Override
        public MXImage createFromParcel(Parcel in) {
            return new MXImage(in);
        }

        @Override
        public MXImage[] newArray(int size) {
            return new MXImage[size];
        }
    };

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

    @Format
    public int getFormat() {
        return format;
    }

    public void setFormat(@Format int format) {
        this.format = format;
    }

    @Channel
    public int getChannel() {
        return channel;
    }

    public void setChannel(@Channel int channel) {
        this.channel = channel;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] bytes) {
        this.data = bytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MXImage mxImage = (MXImage) o;
        return width == mxImage.width &&
                height == mxImage.height &&
                format == mxImage.format &&
                channel == mxImage.channel &&
                Arrays.equals(data, mxImage.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(width, height, format, channel);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "MXImage{" +
                "width:" + getWidth() +
                ",height:" + getHeight() +
                ",format:" + getFormatName() +
                ",channel:" + getChannelName() +
                ",dataLength:" + (getData() == null ? 0 : getData().length) +
                ",address:" + Integer.toHexString(System.identityHashCode(this)) +
                "}";
    }

    public String getFormatName() {
        return MXImages.getFormatName(getFormat());
    }


    public String getChannelName() {
        return MXImages.getChannelName(getChannel());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(format);
        dest.writeInt(channel);
        dest.writeByteArray(data);
    }
}
