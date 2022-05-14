package com.miaxis.sdt;

import android.app.Activity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Sdtapi {
    public Sdtusbapi usbapi;
    Common common;

    public Sdtapi(final Activity instance) throws Exception {
        this.common = new Common();
        this.usbapi = new Sdtusbapi(instance);
    }

    public int SDT_ResetSAM() {
        final int[] RecvLen = {0};
        final byte[] SendData = new byte[this.common.MAX_RECVLEN];
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 16;
        SendData[3] = -1;
        final int ret = this.usbapi.usbsendrecv(SendData, 4, SendData, RecvLen);
        if (ret != 144) {
            return this.byte2int(SendData[4]);
        }
        return ret;
    }

    public int SDT_GetSAMStatus() {
        final int[] RecvLen = {0};
        final byte[] SendData = new byte[this.common.MAX_RECVLEN];
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 17;
        SendData[3] = -1;
        final int ret = this.usbapi.usbsendrecv(SendData, 4, SendData, RecvLen);
        if (ret == 144) {
            return this.byte2int(SendData[4]);
        }
        return ret;
    }

    public int SDT_GetSAMID(final byte[] pucSAMID) {
        final byte[] SendData = new byte[this.common.MAX_RECVLEN];
        final int[] puiRecvLen = {0};
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 18;
        SendData[3] = -1;
        final int ret = this.usbapi.usbsendrecv(SendData, 4, SendData, puiRecvLen);
        if (ret == 144) {
            if (puiRecvLen[0] - 5 > 0 && puiRecvLen[0] > 0 && SendData[4] == -112) {
                for (int i = 0; i < this.common.SAMID_LEN; ++i) {
                    pucSAMID[i] = SendData[i + 5];
                }
            }
            return this.byte2int(SendData[4]);
        }
        return ret;
    }

    public int SDT_GetSAMIDToStr(final char[] pucSAMID) {
        final byte[] SendData = new byte[this.common.MAX_RECVLEN];
        final int[] puiRecvLen = {0};
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 18;
        SendData[3] = -1;
        final int ret = this.usbapi.usbsendrecv(SendData, 4, SendData, puiRecvLen);
        this.usbapi.writefile("in Stdapi.java ret=" + ret);
        if (ret == 144) {
            if (puiRecvLen[0] - 5 <= 0 || puiRecvLen[0] <= 0 || SendData[4] != -112) {
                this.usbapi.writefile("this.puiRecvLen=" + puiRecvLen[0]);
                this.usbapi.writefile("RecvData[4]=" + String.format("%x", SendData[4]));
                return this.byte2int(SendData[4]);
            }
            String ss = "";
            for (int i = 0; i < puiRecvLen[0] - 5; ++i) {
                SendData[i] = SendData[i + 5];
                ss = String.valueOf(ss) + String.format("%x", SendData[i]) + " ";
            }
            this.usbapi.writefile("ss=" + ss);
            this.SamIDIntTostr(SendData, pucSAMID);
        }
        return ret;
    }

    public int SDT_StartFindIDCard() {
        final int[] RecvLen = {0};
        final byte[] SendData = new byte[this.common.MAX_RECVLEN];
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 32;
        SendData[3] = 1;
        final int ret = this.usbapi.usbsendrecv(SendData, 4, SendData, RecvLen);
        if (ret == 144) {
            return this.byte2int(SendData[4]);
        }
        return ret;
    }

    public int SDT_SelectIDCard() {
        final int[] RecvLen = {0};
        final byte[] SendData = new byte[this.common.MAX_RECVLEN];
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 32;
        SendData[3] = 2;
        final int ret = this.usbapi.usbsendrecv(SendData, 4, SendData, RecvLen);
        if (ret == 144) {
            return this.byte2int(SendData[4]);
        }
        return ret;
    }

    public int SDT_ReadBaseMsg(final byte[] pucCHMsg, final int[] puiCHMsgLen, final byte[] pucPHMsg, final int[] puiPHMsgLen) {
        final int[] RecvLen = {0};
        final byte[] SendData = new byte[4 + this.common.MAX_RECVLEN];
        puiCHMsgLen[0] = (puiPHMsgLen[0] = 0);
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 48;
        SendData[3] = 1;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        final String s1 = sdf.format(new Date());
        final int ret = this.usbapi.usbsendrecv(SendData, 4, SendData, RecvLen);
        final String s2 = sdf.format(new Date());
        if (ret == 144) {
            if (RecvLen[0] - 5 > 0 && RecvLen[0] > 0 && SendData[4] == -112) {
                puiCHMsgLen[0] = SendData[5] * 256 + SendData[6];
                puiPHMsgLen[0] = SendData[7] * 256 + SendData[8];
                if (puiCHMsgLen[0] > 256) {
                    puiCHMsgLen[0] = 256;
                }
                if (puiPHMsgLen[0] > 1024) {
                    puiPHMsgLen[0] = 1024;
                }
                for (int i = 0; i < puiCHMsgLen[0]; ++i) {
                    pucCHMsg[i] = SendData[i + 9];
                }
                for (int i = 0; i < puiPHMsgLen[0]; ++i) {
                    pucPHMsg[i] = SendData[i + 9 + puiCHMsgLen[0]];
                }
            }
            return this.byte2int(SendData[4]);
        }
        return ret;
    }

    public int SDT_ReadBaseFPMsg(final byte[] pucCHMsg, final int[] puiCHMsgLen, final byte[] pucPHMsg, final int[] puiPHMsgLen, final byte[] pucFPMsg, final int[] puiFPMsgLen) {
        final int[] RecvLen = {0};
        final byte[] SendData = new byte[4 + this.common.MAX_RECVLEN];
        final int n = 0;
        final int n2 = 0;
        final int n3 = 0;
        final int n4 = 0;
        puiFPMsgLen[n3] = n4;
        puiCHMsgLen[n] = (puiPHMsgLen[n2] = n4);
        SendData[0] = 0;
        SendData[1] = 3;
        SendData[2] = 48;
        SendData[3] = 16;
        final int ret = this.usbapi.usbsendrecv(SendData, 4, SendData, RecvLen);
        if (ret == 144) {
            if (RecvLen[0] - 5 > 0 && RecvLen[0] > 0 && SendData[4] == -112) {
                puiCHMsgLen[0] = SendData[5] * 256 + SendData[6];
                puiPHMsgLen[0] = SendData[7] * 256 + SendData[8];
                puiFPMsgLen[0] = SendData[9] * 256 + SendData[10];
                if (puiCHMsgLen[0] > 256) {
                    puiCHMsgLen[0] = 256;
                }
                if (puiPHMsgLen[0] > 1024) {
                    puiPHMsgLen[0] = 1024;
                }
                if (puiFPMsgLen[0] > 1024) {
                    puiFPMsgLen[0] = 1024;
                }
                for (int i = 0; i < puiCHMsgLen[0]; ++i) {
                    pucCHMsg[i] = SendData[i + 11];
                }
                for (int i = 0; i < puiPHMsgLen[0]; ++i) {
                    pucPHMsg[i] = SendData[i + 11 + puiCHMsgLen[0]];
                }
                for (int i = 0; i < puiFPMsgLen[0]; ++i) {
                    pucFPMsg[i] = SendData[i + 11 + puiCHMsgLen[0] + puiPHMsgLen[0]];
                }
            }
            return this.byte2int(SendData[4]);
        }
        return ret;
    }

    void SamIDIntTostr(final byte[] pucSAMID, final char[] pcSAMID) {
        String temp = "";
        int iTemp = pucSAMID[0] + pucSAMID[1] * 256;
        temp = String.format("%02d", iTemp);
        iTemp = pucSAMID[2] + pucSAMID[3] * 256;
        temp = String.valueOf(temp) + String.format(".%02d", iTemp);
        for (int i = 0; i < 3; ++i) {
            final long dwTemp = Long.valueOf(String.format("%02x", pucSAMID[i * 4 + 4]), 16) + Long.valueOf(String.format("%02x", pucSAMID[i * 4 + 5]), 16) * 256L + Long.valueOf(String.format("%02x", pucSAMID[i * 4 + 6]), 16) * 256L * 256L + Long.valueOf(String.format("%02x", pucSAMID[i * 4 + 7]), 16) * 256L * 256L * 256L;
            if (i == 1) {
                temp = String.valueOf(temp) + "-" + String.format("%010d", dwTemp);
            } else {
                temp = String.valueOf(temp) + "-" + dwTemp;
            }
        }
        final char[] tt = temp.toCharArray();
        System.arraycopy(tt, 0, pcSAMID, 0, tt.length);
    }

    int byte2int(final byte b) {
        return Integer.valueOf(String.format("%x", b), 16);
    }
}
