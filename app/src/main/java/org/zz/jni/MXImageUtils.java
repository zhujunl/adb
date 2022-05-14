package org.zz.jni;

import android.graphics.Rect;
import android.graphics.RectF;

import com.miaxis.image.MXImage;

import java.util.Arrays;

public class MXImageUtils {

    private static mxImageTool mxImageTool = new mxImageTool();

    private MXImageUtils() {

    }

    /**
     * 获取算法信息
     *
     * @return 返回：算法信息
     */
    public static String version() {
        byte[] version = new byte[1024];
        mxImageTool.ImageToolVersion(version);
        return new String(version);
    }

    /*******************************************************************************************
     功	能：	图像文件加载到内存
     @param path        - 输入	图像路径
     @param channel    - 输入  图像通道数，1-加载为灰度图像，3-加载为RGB图像
     @return  {@link MXImage} {@link}
     *******************************************************************************************/
    public static MXImage loadImage(String path, int channel) {
        int[] outImageWidth = new int[1];
        int[] outImageHeight = new int[1];
        int sizeResult = mxImageTool.ImageLoad(path, channel, null, outImageWidth, outImageHeight);
        if (sizeResult != 1) {
            return null;
        }
        byte[] outImageData = new byte[outImageWidth[0] * outImageHeight[0] * 3];
        int loadResult = mxImageTool.ImageLoad(path, channel, outImageData, outImageWidth, outImageHeight);
        if (loadResult != 1) {
            return null;
        }
        return new MXImage(outImageData, outImageWidth[0], outImageHeight[0], MXImage.FORMAT_BGR, channel);
    }

    /**
     * 保存图像数据
     *
     * @param path    目标路径
     * @param data    图像数据 {@link MXImage#}
     * @param width   图像宽度
     * @param height  图像高度
     * @param channel 图像通道 {@link #loadImage(String, int) 加载图片} {@link MXImage#}
     * @return 1-成功，其他-失败
     */
    public static int saveImage(String path, byte[] data, int width, int height, int channel) {
        return mxImageTool.ImageSave(path, data, width, height, channel);
    }

    /*******************************************************************************************
     功	能：	YUV数据转换为RGB数据(Android摄像头获取的数据为YUV格式)
     参	数：	pYUVImage	- 输入	YUV图像数据
     iImgWidth	- 输入	图像宽度
     iImgHeight	- 输入	图像高度
     pRGBImage	- 输出	RGB图像数据
     返	回：	1-成功，其他-失败
     备	注：	https://www.cnblogs.com/snailgardening/p/opencv_yuv_rgb.html
     *******************************************************************************************/
    public static byte[] yuv2BGR(byte[] pYUVImage, int width, int height) {
        byte[] result = new byte[width * height * 3];
        mxImageTool.YUV2RGB(pYUVImage, width, height, result);
        return result;
    }

    /*******************************************************************************************
     功	能：	RGB图像数据转换为灰度图像数据
     参	数：	pRGBImage	- 输入	RGB图像数据
     iImgWidth	- 输入	图像宽度
     iImgHeight	- 输入	图像高度
     pGrayImage	- 输出	灰度图像数据
     返	回：	1-成功，其他-失败
     *******************************************************************************************/
    public void RGB2GRAY(byte[] pRGBImage, int iImgWidth, int iImgHeight, byte[] pGrayImage) {
        mxImageTool.RGB2GRAY(pGrayImage, iImgWidth, iImgHeight, pGrayImage);
    }

    /*******************************************************************************************
     功	能：	对输入图像根据输入的目标宽度进行按比例缩放。
     参	数：	pImgBuf  		- 输入	图像缓冲区
     iImgWidth		- 输入	图像宽度
     iImgHeight		- 输入	图像高度
     iChannels       - 输入  图像通道
     iDstImgWidth    - 输入  目标图像宽度
     iDstImgHeight	- 输入  目标图像高度
     pDstImgBuf  	- 输出  目标图像缓冲区

     返	回：	1-成功，其他-失败
     *******************************************************************************************/
    public int Zoom(byte[] pImgBuf, int iImgWidth, int iImgHeight, int iChannels,
                    int iDstImgWidth, int iDstImgHeight, byte[] pDstImgBuf) {
        return mxImageTool.Zoom(pImgBuf, iImgWidth, iImgHeight, iChannels, iDstImgWidth, iDstImgHeight, pDstImgBuf);
    }

    /*******************************************************************************************
     功	能：	在输入的RGB图像上根据输入的Rect绘制矩形框
     参	数：	pRgbImgBuf  		- 输入	RGB图像缓冲区
     iImgWidth			- 输入	图像宽度
     iImgHeight			- 输入	图像高度
     iRect				- 输入	Rect[0]	=x;
     Rect[1]	=y;
     Rect[2]	=width;
     Rect[3]	=height;
     返	回：	1-成功，其他-失败
     *******************************************************************************************/
//    public static int drawRect(byte[] bytes, int width, int height, Rect rect) {
//        int[] r = new int[]{rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top};
//        return mxImageTool.DrawRect(bytes, width, height, r);
//    }

    /*******************************************************************************************
     功	能：	在输入的RGB图像上根据输入的点坐标绘制点
     参	数：	pRgbImgBuf  		- 输入	RGB图像缓冲区
     iImgWidth			- 输入	图像宽度
     iImgHeight			- 输入	图像高度
     iPointPos			- 输入	点坐标序列（x1,y1,x2,y2,...）
     iPointNum			- 输入  点个数
     返	回：	1-成功，其他-失败
     *******************************************************************************************/
//    public static int drawPoint(MXImage image, List<Point> points) {
//        int[] iPointPos = new int[points.size() * 2];
//        for (int i = 0; i < points.size(); i++) {
//            Point point = points.get(i);
//            iPointPos[i * 2] = point.x;
//            iPointPos[i * 2 + 1] = point.y;
//        }
//        return mxImageTool.DrawPoint(image.getData(), image.getWidth(), image.getHeight(), iPointPos, points.size());
//    }

    /*******************************************************************************************
     功	能：	在输入的RGB图像上根据输入的点坐标绘制点序号
     参	数：	pRgbImgBuf  			- 输入	RGB图像缓冲区
     iImgWidth			- 输入	图像宽度
     iImgHeight			- 输入	图像高度
     iPointX				- 输入	指定位置的X坐标
     iPointY				- 输入  	指定位置的Y坐标
     szText				- 输入  	显示文字
     返	回：	1-成功，其他-失败
     *******************************************************************************************/
//    public static int DrawText(byte[] pRgbImgBuf, int iImgWidth, int iImgHeight,
//                               int iPointX, int iPointY, String szText) {
//        return mxImageTool.DrawText(pRgbImgBuf, iImgWidth, iImgHeight, iPointX, iPointY, szText);
//    }

    /*******************************************************************************************
     功	能：	图像文件数据转换为RGB24内存数据
     参	数：	pFileDataBuf	- 输入	图像文件数据
     iFileDataLen	- 输入 	图像文件数据长度
     pRGB24Buf		- 输出	RGB24内存数据
     iWidth			- 输出	图像宽度
     iHeight			- 输出	图像高度
     返	回：	>=0成功，其他-失败
     *******************************************************************************************/
    public static int decodeImage(byte[] pFileDataBuf, int iFileDataLen,
                                  byte[] pRGB24Buf, int[] iWidth, int[] iHeight) {
        return mxImageTool.ImageDecode(pFileDataBuf, iFileDataLen, pRGB24Buf, iWidth, iHeight);
    }

    /*******************************************************************************************
     功	能：	RGB24内存数据转换为图像文件数据
     参	数：	pRGB24Buf		- 输入	RGB24内存数据
     iWidth			- 输入	图像宽度
     iHeight			- 输入	图像高度
     szTpye          - 输入  	文件后缀,比如.jpg;.bmp
     pFileDataBuf	- 输出	图像文件数据
     iFileDataLen	- 输出  	图像文件数据长度
     返	回：	1-成功，其他-失败
     *******************************************************************************************/
    public static byte[] encodeImage(MXImage image, String type) {
        byte[] buffer = new byte[image.getData().length * 2];
        int[] len = new int[1];
        int i = mxImageTool.ImageEncode(image.getData(), image.getWidth(), image.getHeight(), type, buffer, len);
        if (i != 0)
            return new byte[0];
        else
            return Arrays.copyOf(buffer, len[0]);
    }

    public static Rect scaleRect(Rect rect, float scaleX, float scaleY) {
        return new Rect((int) (rect.left * scaleX), (int) (rect.top * scaleY), (int) (rect.right * scaleX), (int) (rect.bottom * scaleY));
    }

    public static RectF scaleRect(RectF rect, float scaleX, float scaleY) {
        return new RectF(rect.left * scaleX, rect.top * scaleY, rect.right * scaleX, rect.bottom * scaleY);
    }
}
