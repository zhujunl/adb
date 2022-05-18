package com.miaxis.face.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.miaxis.face.constant.Constants;
import com.miaxis.face.util.RawBitmapUtils;

import org.zz.api.MXFingerAPI;


public class FingerStrategy implements FingerManager.FingerStrategy {


    private final int SUCCESS = 0;
    private final int ERROR = -1;
    private MXFingerAPI fingerStrategy;
    private FingerManager.OnFingerStatusListener statusListener;
    int iTimeout = 15 * 1000;
    private FingerManager.OnFingerReadListener readListener;

    private Context mContext;

    public FingerStrategy(Context context) {
        this.mContext = context;
    }

    @Override
    public void init(FingerManager.OnFingerStatusListener statusListener) {
        try {
            this.statusListener = statusListener;
           // MxMscBigFingerApiFactory fingerFactory = new MxMscBigFingerApiFactory(mContext.getApplicationContext());
            int iVID = 0x821B;
            int iPID = 0x0202;
            fingerStrategy = new MXFingerAPI(this.mContext, iPID, iVID);
            String s = deviceInfo();
            if (!TextUtils.isEmpty(s)){
                statusListener.onFingerStatus(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setFingerListener(FingerManager.OnFingerReadListener readListener) {
        this.readListener = readListener;
    }

    @Override
    public String deviceInfo() {
        if (fingerStrategy != null) {
            byte[] version = new byte[100];
            int i = fingerStrategy.mxGetDevVersion(version);
            if (i==0){
                return new String(version).trim();
            }
        }
        return "";
    }

    @Override
    public void readFinger(int type) {
        try {
            if (fingerStrategy != null) {
                int iImageX = 256;
                int iImageY = 360;
                int iImageSize = iImageX * iImageY;
                byte[] bFingerImage = new byte[iImageSize];
                int ID_TZ_SIZE = 512;
                byte[] m_bFingerMbId = new byte[ID_TZ_SIZE];
//                int ret = fingerStrategy.mxExtractFeatureID(bFingerImage, iTimeout, 0, m_bFingerMbId);
                int ret = fingerStrategy.mxExtractFeature(bFingerImage, iTimeout, 0, m_bFingerMbId);
                if (ret == 0) {
                    bFinger=bFingerImage;
                    iX=iImageX;
                    iY=iImageY;
                  Bitmap m_bitmap = null;
                  switch (type) {
                      case Constants.ORIGINAL:
                        m_bitmap=fingerStrategy.Raw2Bimap(bFingerImage, iImageX, iImageY);
                          break;
                      case Constants.GETBLACK:
                          m_bitmap=getBit(bFingerImage, iImageX, iImageY,true);
                          break;
                      case Constants.GETRED:
                          m_bitmap=getBit(bFingerImage, iImageX, iImageY,false);
                          break;
                      default:
                          m_bitmap=fingerStrategy.Raw2Bimap(bFingerImage, iImageX, iImageY);
                          break;
                  }
                    if (readListener != null&&m_bitmap!=null) {
                        readListener.onFingerRead(m_bFingerMbId, m_bitmap);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (readListener != null) {
            readListener.onFingerRead(null, null);
        }
    }
    private Bitmap trans;
    /**
     * 算法指纹信息指纹
     * */
    private Bitmap getBit(byte[] bFingerImage, int iImageX,int iImageY,boolean type){
        byte[] pngData = new byte[iImageX * iImageY * 4];
        int[] pngDataLength = {0};
        int getPNG=-1;
        if (type){
            getPNG= fingerStrategy.mxRaw2PNG(bFingerImage, iImageX, iImageY, pngData, pngDataLength);
        } else {
            getPNG= fingerStrategy.mxRaw2RedPNG(bFingerImage, iImageX, iImageY, pngData, pngDataLength);
        }
        Log.e("OnClickGetImage", "getPNG:" + getPNG);
        if (getPNG == 0) {
            if (trans != null) {
                if (!trans.isRecycled()) {
                    trans.recycle();
                }
            }

            trans = BitmapFactory.decodeByteArray(pngData, 0, pngDataLength[0]);
            return trans;
        }
        return null;
    }

    @Override
    public void release() {
        statusListener = null;
        readListener = null;
        if (fingerStrategy!=null){
            try {
//                fingerStrategy.mxCancelCaptue();
                fingerStrategy.mxCancelCapture();
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                fingerStrategy.unRegUsbMonitor();
            }catch (Exception e){
                e.printStackTrace();
            }
            fingerStrategy=null;
        }
    }

    @Override
    public void releaseDevice() {
        this.statusListener = null;
    }

    @Override
    public void comparison(byte[] b, byte[] b2) {
        try {
            if (fingerStrategy != null) {
                int iImageX = 256;
                int iImageY = 360;
                int iImageSize = iImageX * iImageY;
                byte[] bFingerImage = new byte[iImageSize];
                int ID_TZ_SIZE = 512;
                byte[] m_bFingerMbId = new byte[ID_TZ_SIZE];
                int ret = fingerStrategy.mxExtractFeature(bFingerImage, iTimeout, 0, m_bFingerMbId);
//                int ret = fingerStrategy.mxExtractFeatureID(bFingerImage, iTimeout, 0, m_bFingerMbId);
                if (ret == 0) {
                    int match = fingerStrategy.mxMatchFeature(m_bFingerMbId, b,3);
//                    int match = fingerStrategy.mxMatchFeatureID(m_bFingerMbId, b,3);
                    int m;
                    if (match== SUCCESS){
                        m =  SUCCESS;
                    }else {
                        int match2 = fingerStrategy.mxMatchFeature(m_bFingerMbId, b2, 3);
//                        int match2 = fingerStrategy.mxMatchFeatureID(m_bFingerMbId, b2, 3);
                        if (match2== SUCCESS) {
                            m =  SUCCESS;
                        } else {
                            m =  ERROR;
                        }
                    }
                    Bitmap bitmap = RawBitmapUtils.raw2Bimap(bFingerImage, iImageX, iImageY);
                    if (readListener != null) {
                        readListener.onFingerReadComparison(m_bFingerMbId, bitmap, m);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (readListener != null) {
            readListener.onFingerReadComparison(null, null,  ERROR);
        }
    }

    @Override
    public void comparison(byte[] b) {
        try {
            if (fingerStrategy != null) {
                int iImageX = 256;
                int iImageY = 360;
                int iImageSize = iImageX * iImageY;
                byte[] bFingerImage = new byte[iImageSize];
                int ID_TZ_SIZE = 512;
                byte[] m_bFingerMbId = new byte[ID_TZ_SIZE];
                int ret = fingerStrategy.mxExtractFeature(bFingerImage, iTimeout, 0, m_bFingerMbId);
//                int ret = fingerStrategy.mxExtractFeatureID(bFingerImage, iTimeout, 0, m_bFingerMbId);
                if (ret == 0) {
                    int match = fingerStrategy.mxMatchFeature(m_bFingerMbId,b,3);
//                    int match = fingerStrategy.mxMatchFeatureID(m_bFingerMbId,b,3);
                    int m;
                    if (match ==  SUCCESS){
                        m =  SUCCESS;
                    }else {
                        m =  ERROR;
                    }
                    Bitmap bitmap = RawBitmapUtils.raw2Bimap(bFingerImage, iImageX, iImageY);
                    if (readListener != null) {
                        readListener.onFingerReadComparison(m_bFingerMbId, bitmap, m);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (readListener != null) {
            readListener.onFingerReadComparison(null, null,  ERROR);
        }
    }


    private byte[] bFinger;
    private int iX = 256;
    private int iY = 360;

    public byte[] getbFinger() {
        return bFinger;
    }

    public int getiX() {
        return iX;
    }

    public int getiY() {
        return iY;
    }
}
