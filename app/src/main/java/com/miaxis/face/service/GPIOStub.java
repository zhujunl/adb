package com.miaxis.face.service;

import android.app.smdt.SmdtManager;
import android.content.Context;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import com.ivsign.android.IDCReader.IDCReaderSDK;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.util.LogUtil;
import com.miaxis.gpioaidl.IGPIOControl;

public class GPIOStub /*extends IGPIOControl.Stub*/{
//
//    private Context context;
//    private SmdtManager smdtManager;
//    public UnsupportedOperationException mException = new UnsupportedOperationException("");
//
//    GPIOStub(Context context) {
//        this.context = context;
//        smdtManager = new SmdtManager(context);
//    }
//
//    @Override
//    public int getGpio(int io) throws RemoteException {
//        // TODO Auto-generated method stub
//        try {
//            return smdtManager.smdtReadGpioValue(io);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            throw mException;
//        }
//    }
//
//    @Override
//    public int setGpio(int io, boolean isTrue) throws RemoteException {
//        // TODO Auto-generated method stub
//        try {
//            return smdtManager.smdtSetGpioValue(io,isTrue);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            throw mException;
//        }
//    }
//
//    @Override
//    public int decodeIdCardPhoto(String wltDataStr) throws RemoteException {
////        try {
////            String filepath = FileUtil.getAvailableWltPath();
////            byte[] wltData = Base64.decode(wltDataStr, Base64.DEFAULT);
////            long t1 = System.currentTimeMillis();
////            int ret = IDCReaderSDK.wltInit(filepath);
////            long t2 = System.currentTimeMillis();
////            if (ret != 0) {
////                return -1;
////            } else {
////                ret = IDCReaderSDK.unpack(wltData);
////                long t3 = System.currentTimeMillis();
////                Log.e("stub", "解码身份证照片： wltInit " + (t2 - t1) + " unpack " + (t3 - t2));
////                return ret != 1 ? -2 : 0;
////            }
////        } catch (Exception e) {
////            LogUtil.writeLog("decodeIdPhoto exception" + e.getMessage());
////            return -3;
////        }
//        return -3;
//    }
}
