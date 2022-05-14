package com.miaxis.sdt;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Sdtusbapi extends Activity {
    Common common;
    int debug;
    UsbDeviceConnection mDeviceConnection;
    UsbEndpoint epOut;
    UsbEndpoint epIn;
    final String FILE_NAME = "/file.txt";
    RandomAccessFile raf;
    File targetFile;
    Activity instance;

    public Sdtusbapi(final Activity instance) throws Exception {
        this.common = new Common();
        this.debug = 0;
        final int ret = this.initUSB(instance);
        this.instance = instance;
        if (this.debug == 1) {
            this.writefile("inintUSB ret=" + ret);
        }
        if (ret != this.common.SUCCESS) {
            final Exception e = new Exception();
            if (ret == this.common.ENOUSBRIGHT) {
                e.initCause(new Exception());
                this.writefile("error common.ENOUSBRIGHT");
            } else {
                e.initCause(null);
                this.writefile("error null");
            }
            throw e;
        }
    }

    int initUSB(final Activity instance) {
        this.openfile();
        UsbDevice mUsbDevice = null;
        final UsbManager manager = (UsbManager) instance.getSystemService(Context.USB_SERVICE);
        if (manager == null) {
            this.writefile("manager == null");
            return this.common.EUSBMANAGER;
        }
        if (this.debug == 1) {
            this.writefile("usb dev\uff1a" + String.valueOf(manager.toString()));
        }
        final HashMap<String, UsbDevice> deviceList = (HashMap<String, UsbDevice>) manager.getDeviceList();
        if (this.debug == 1) {
            this.writefile("usb dev\uff1a" + String.valueOf(deviceList.size()));
        }
        final Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        final ArrayList<String> USBDeviceList = new ArrayList<String>();
        while (deviceIterator.hasNext()) {
            final UsbDevice device = deviceIterator.next();
            USBDeviceList.add(String.valueOf(device.getVendorId()));
            USBDeviceList.add(String.valueOf(device.getProductId()));
            if (device.getVendorId() == 1024 && device.getProductId() == 50010) {
                mUsbDevice = device;
                if (this.debug != 1) {
                    continue;
                }
                this.writefile("zhangmeng:find device!");
            }
        }
        final int ret = this.findIntfAndEpt(manager, mUsbDevice);
        return ret;
    }

    int usbsendrecv(final byte[] pucSendData, final int uiSendLen, final byte[] RecvData, final int[] puiRecvLen) {
        final int iFD = 0;
        Boolean bRet = null;
        byte ucCheck = 0;
        final byte[] ucRealSendData = new byte[4096];
        final byte[] pucBufRecv = new byte[4096];
        final int[] iOffset = {0};
        if (4091 < uiSendLen) {
            return -1;
        }
        if (-1 == iFD) {
            final int iRet = this.common.ENOOPEN;
            return iRet;
        }
        int iLen = (pucSendData[0] << 8) + pucSendData[1];
        final byte[] array = ucRealSendData;
        final int n = 0;
        final byte[] array2 = ucRealSendData;
        final int n2 = 1;
        final byte[] array3 = ucRealSendData;
        final int n3 = 2;
        final byte b = -86;
        array3[n3] = b;
        array[n] = (array2[n2] = b);
        ucRealSendData[3] = -106;
        ucRealSendData[4] = 105;
        for (int iIter = 0; iIter < iLen + 1; ++iIter) {
            ucCheck ^= pucSendData[iIter];
        }
        for (int i = 0; i < iLen + 2; ++i) {
            ucRealSendData[i + 5] = pucSendData[i];
        }
        ucRealSendData[iLen + 6] = ucCheck;
        final int uiSizeSend = iLen + 2 + 5;
        int uiSizeRecv = 0;
        int iRet = this.mDeviceConnection.bulkTransfer(this.epOut, ucRealSendData, uiSizeSend, 5000);
        this.writefile("before uiSizeRecv error iRet=" + iRet);
        uiSizeRecv = this.mDeviceConnection.bulkTransfer(this.epIn, pucBufRecv, pucBufRecv.length, 1000);
        if (5 > uiSizeRecv || 4096 <= uiSizeRecv) {
            iRet = this.common.EDATALEN;
            this.writefile("uiSizeRecv error =" + uiSizeRecv);
            return iRet;
        }
        bRet = this.Usb_GetDataOffset(pucBufRecv, iOffset);
        if (!bRet) {
            iRet = this.common.EDATAFORMAT;
            this.writefile("iRet = EDATAFORMAT =" + bRet + "iOffset= " + iOffset);
            return iRet;
        }
        iLen = (pucBufRecv[iOffset[0] + 4] << 8) + pucBufRecv[iOffset[0] + 5];
        if (4089 < iLen) {
            iRet = this.common.EDATALEN;
            this.writefile("iRet = EDATALEN = " + iLen);
            return iRet;
        }
        final byte[] tempData = new byte[4096];
        for (int j = 0; j < pucBufRecv.length - iOffset[0] - 4; ++j) {
            tempData[j] = pucBufRecv[j + iOffset[0] + 4];
        }
        bRet = Usb_CheckChkSum(iLen + 2, tempData);
        if (!bRet) {
            iRet = this.common.EPCCRC;
            this.writefile("iRet = EPCCRC");
            return iRet;
        }
        for (int j = 0; j < iLen + 1; ++j) {
            RecvData[j] = pucBufRecv[j + iOffset[0] + 4];
        }
        puiRecvLen[0] = iLen + 1;
        this.writefile("stdapi.puiRecvLen =" + (iLen + 1));
        return this.common.SUCCESS;
    }

    boolean Usb_GetDataOffset(final byte[] dataBuffer, final int[] iOffset) {
        iOffset[0] = 0;
        int iIter;
        for (iIter = 0; iIter < 7 && (dataBuffer[iIter + 0] != -86 || dataBuffer[iIter + 1] != -86 || dataBuffer[iIter + 2] != -106 || dataBuffer[iIter + 3] != 105); ++iIter) {
        }
        if (7 <= iIter) {
            return false;
        }
        iOffset[0] = iIter;
        return true;
    }

    static boolean Usb_CheckChkSum(final int uiDataLen, final byte[] pucRecvData) {
        byte ucCheck = 0;
        for (int iIter = 0; iIter < uiDataLen - 1; ++iIter) {
            ucCheck ^= pucRecvData[iIter];
        }
        return ucCheck == pucRecvData[uiDataLen - 1];
    }

    private void openfile() {
        if (this.debug == 1) {
            final File sdCardDir = Environment.getExternalStorageDirectory();
            try {
                this.setTargetFile(new File(String.valueOf(sdCardDir.getCanonicalPath()) + "/file.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                this.setFile(new RandomAccessFile(this.targetFile, "rw"));
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
            }
            this.writefile("in open file()");
        }
    }

    public void writefile(final String context) {
        //        if (this.debug == 1 && Environment.getExternalStorageState().equals("mounted")) {
        //            final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        //            try {
        //                this.raf.seek(this.targetFile.length());
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //            try {
        //                this.raf.writeChars("\n" + sdf.format(new Date()) + " " + context);
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //        }
    }

    private void closefile() {
        if (this.debug == 1 && this.raf != null) {
            try {
                this.raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int findIntfAndEpt(final UsbManager manager, final UsbDevice mUsbDevice) {
        UsbInterface mInterface = null;
        if (mUsbDevice == null) {
            this.writefile("zhangmeng:no device found");
            return this.common.EUSBDEVICENOFOUND;
        }
        final int i = 0;
        if (i < mUsbDevice.getInterfaceCount()) {
            final UsbInterface intf = mInterface = mUsbDevice.getInterface(i);
        }
        if (mInterface == null) {
            this.writefile("zhangmeng:no interface");
            return this.common.ENOUSBINTERFACE;
        }
        UsbDeviceConnection connection = null;
        if (!manager.hasPermission(mUsbDevice)) {
            this.writefile("zhangmeng:no rights");
            new Thread() {
                @Override
                public void run() {
                    final Activity instance = Sdtusbapi.this.instance;
                    final int n = 0;
                    Sdtusbapi.this.common.getClass();
                    final PendingIntent pi = PendingIntent.getBroadcast((Context) instance, n, new Intent("com.android.USB_PERMISSION"), 0);
                    manager.requestPermission(mUsbDevice, pi);
                }
            }.start();
            return this.common.ENOUSBRIGHT;
        }
        connection = manager.openDevice(mUsbDevice);
        if (connection == null) {
            return this.common.EUSBCONNECTION;
        }
        if (connection.claimInterface(mInterface, true)) {
            this.getEndpoint(this.mDeviceConnection = connection, mInterface);
        } else {
            connection.close();
        }
        return this.common.SUCCESS;
    }

    private void getEndpoint(final UsbDeviceConnection connection, final UsbInterface intf) {
        if (intf.getEndpoint(1) != null) {
            this.epOut = intf.getEndpoint(1);
        }
        if (intf.getEndpoint(0) != null) {
            this.epIn = intf.getEndpoint(0);
        }
    }

    private void setFile(final RandomAccessFile raf) {
        this.raf = raf;
    }

    private void setTargetFile(final File f) {
        this.targetFile = f;
    }
}
