package com.zz.jni;

import android.graphics.Bitmap;

public class Wlt2BmpCall {

    static {
        System.loadLibrary("wlt2bmp-jni");
    }

    private static native int wlt2bmp(byte[] wlt, byte[] bmp);

    public static int miaxis_Wlt2BgrData(byte[] wlt, byte[] bgrBuf) {
        return wlt2bmp(wlt, bgrBuf);
    }


    /**
     * @param nWidth  头像宽102
     * @param nHeight 头像高126
     */
    public static Bitmap miaxis_Bgr2Bitmap(int nWidth, int nHeight, byte[] bgrbuf) {
        // BGR
        // RGB
        //ARGB
        //        Bitmap bmp = Bitmap.createBitmap(nWidth, nHeight, Bitmap.Config.RGB_565);
        //
        //        int[] pixs = new int[nWidth * nHeight];
        //        int x = 0;
        //        int y = 0;
        //        for (int i = 0; i < pixs.length; i++) {
        //            x = i % nWidth;
        //            y = i / nWidth;
        //            //            int pix_A = 0xFF;
        //
        //            int pix_B = bgrbuf[3 * i] & 0xFF;
        //
        //            int pix_G = bgrbuf[3 * i + 1] & 0xFF;
        //
        //            int pix_R = bgrbuf[3 * i + 2] & 0xFF;
        //
        //            pixs[i] = pix_R << 16 + pix_G << 8 + pix_B;
        //
        //            bmp.setPixel(x, y, pixs[i]);
        //        }
        //        bmp.setPixels(pixs, 0, nWidth, 0, 0, nWidth, nHeight);
        Bitmap bmp = Bitmap.createBitmap(nWidth, nHeight, Bitmap.Config.RGB_565);
        int row = 0;
        int col = nWidth - 1;
        for (int i = bgrbuf.length - 1; i >= 3; i -= 3) {
            int color = bgrbuf[i] & 0xFF;
            color += (bgrbuf[i - 1] << 8) & 0xFF00;
            color += ((bgrbuf[i - 2]) << 16) & 0xFF0000;
            bmp.setPixel(col, row, color);
            col--;
            if (col < 0) {
                col = nWidth - 1;
                row++;
            }
            if (row >= nHeight) {
                break;
            }
        }
        return bmp;
    }

}

