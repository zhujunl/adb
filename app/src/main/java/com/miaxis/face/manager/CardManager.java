package com.miaxis.face.manager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;

import com.miaxis.face.constant.ApiResult;
import com.miaxis.face.util.IdCardParser;
import com.miaxis.sdt.Common;
import com.miaxis.sdt.Sdtapi;
import com.miaxis.sdt.bean.IdCard;
import com.miaxis.sdt.bean.IdCardMsg;
import com.zz.jni.Wlt2BmpCall;

import java.nio.charset.StandardCharsets;

import static java.lang.System.arraycopy;

/**
 * @author ZJL
 * @date 2022/5/11 15:05
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CardManager {

    private Sdtapi mSdtApi;

    private final String TAG="Cardmanager";


    private CardManager() {
    }

    public static CardManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final CardManager instance = new CardManager();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    //    public interface ReadIdCardCallback {
    //
    //        void readIdCardCallback(int code, String message, IdCardMsg idCardMsg, Bitmap bitmap);
    //
    //    }
    public boolean init(Activity activity) {
        try {
            try {
                mSdtApi = new Sdtapi(activity);
            } catch (Exception e) {
                e.printStackTrace();
                mSdtApi = new Sdtapi(activity);
            }
            IntentFilter filter = new IntentFilter();//意图过滤器
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);//USB设备拔出
            filter.addAction(Common.ACTION_USB_PERMISSION);//自定义的USB设备请求授权
            activity.registerReceiver(mUsbReceiver, filter);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void free(Activity activity) {
        try {
            activity.unregisterReceiver(mUsbReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void transformFingerprint(byte[] fingerData, IdCard idCard) {
        byte[] bFingerData0 = new byte[512];
        System.arraycopy(fingerData, 0, bFingerData0, 0, bFingerData0.length);
        //        idCard.fingerprint0 = Base64.encodeToString(bFingerData0, Base64.NO_WRAP);
        idCard.fp0=bFingerData0;
        idCard.fingerprintPosition0 = fingerPositionCovert(bFingerData0[5]);
        byte[] bFingerData1 = new byte[512];
        System.arraycopy(fingerData, 512, bFingerData1, 0, bFingerData1.length);
        idCard.fp1=bFingerData1;
        //            if (!isFingerDataEmpty(bFingerData1)) {
        //        idCard.fingerprint1 = Base64.encodeToString(bFingerData1, Base64.NO_WRAP);
        idCard.fingerprintPosition1 = fingerPositionCovert(bFingerData1[5]);
        //            }
    }

    public ApiResult<IdCard> read() {
        ApiResult<IdCard> result = new ApiResult<>();
        byte[] curCardId = new byte[64];
        mSdtApi.SDT_StartFindIDCard();//寻找身份证
        mSdtApi.SDT_SelectIDCard();//选取身份证
        byte[] pucPHMsg = new byte[1024];//头像
        int[] puiPHMsgLen = new int[1];
        byte[] pucFpMsg = new byte[1024];//两个指纹
        int[] puiFpMsgLen = new int[1];
        int ret = ReadBaseMsg(pucPHMsg, puiPHMsgLen, pucFpMsg, puiFpMsgLen);
        if (ret == 0x90) {
            byte[] pucCHMsg = new byte[256];
            try {
                char[] pucCHMsgStr = new char[128];
                DecodeByte(pucCHMsg, pucCHMsgStr);//将读取的身份证中的信息字节，解码
                IdCardMsg msg = new IdCardMsg();//身份证信息对象，存储身份证上的文字信息
                ret = ReadBaseMsgToStr(msg);
                if (ret == 0x90) {
                    IdCard idCard = new IdCard();
                    idCard.idCardMsg = msg;
                    transformFingerprint(pucFpMsg,idCard);
                    //是否需要拆分
                    //                    byte[] b=new byte[512];
                    //                    System.arraycopy(pucFpMsg,512,b,pucFpMsg.length,b.length-1);
                    //                    idCard.fp1=b;
                    byte[] bmp = new byte[38862];
                    Bitmap bitmap = GetImage(pucPHMsg, bmp);
                    if (bitmap != null) {
                        idCard.face = bitmap;
                        result.code = 0;
                    }
                    result.setData(idCard);
                } else {
                    result.code = -4;
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                result.code = -2;
                return result;
            }
        } else {
            result.code = -1;
            result.msg = "没有身份证";
            return result;
        }
    }

    //广播接收器
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Toast.makeText(context, "action:" + action, Toast.LENGTH_SHORT).show();
            //USB设备拔出广播
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                //                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                //                String deviceName = device.getDeviceName();
                //                int deviceId = device.getDeviceId();
                //                int productId = device.getProductId();
            } else if (Common.ACTION_USB_PERMISSION.equals(action)) {//USB设备未授权，从SDTAPI中发出的广播
                //                Message msg = new Message();
                //                msg.what = 3;
                //                msg.obj = "USB设备无权限";
                //                MyHandler.sendMessage(msg);
            }
        }
    };

    //读取身份证中的文字信息（可阅读格式的）
    public int ReadBaseMsgToStr(IdCardMsg msg) {
        int[] puiCHMsgLen = new int[1];
        int[] puiPHMsgLen = new int[1];
        byte[] pucCHMsg = new byte[256];
        byte[] pucPHMsg = new byte[1024];
        //sdtapi中标准接口，输出字节格式的信息。
        int ret = mSdtApi.SDT_ReadBaseMsg(pucCHMsg, puiCHMsgLen, pucPHMsg, puiPHMsgLen);
        if (ret == 0x90) {
            try {
                char[] pucCHMsgStr = new char[128];
                String type=isGreenCard(pucCHMsg);
                if ("I".equals(type)) {
                    IdGreenCardOarser(pucCHMsg,msg);
                }else {
                    IdCardParser(pucCHMsg,msg);
                }
//                DecodeByte(pucCHMsg, pucCHMsgStr);//将读取的身份证中的信息字节，解码成可阅读的文字
//                PareseItem(pucCHMsgStr, msg); //将信息解析到msg中
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    //字节解码函数
    void DecodeByte(byte[] msg, char[] msg_str) throws Exception {
        byte[] newmsg = new byte[msg.length + 2];
        newmsg[0] = (byte) 0xff;
        newmsg[1] = (byte) 0xfe;
        arraycopy(msg, 0, newmsg, 2, msg.length);
        String s = new String(newmsg, StandardCharsets.UTF_16);
        for (int i = 0; i < s.toCharArray().length; i++) {
            msg_str[i] = s.toCharArray()[i];
        }
    }

    //读取身份证中的头像信息
    public int ReadBaseMsg(byte[] pucPHMsg, int[] puiPHMsgLen, byte[] pucFpMsg, int[] puiFpMsgLen) {
        int[] puiCHMsgLen = new int[1];
        byte[] pucCHMsg = new byte[256];
        //sdtapi中标准接口，输出字节格式的信息。
        return mSdtApi.SDT_ReadBaseFPMsg(pucCHMsg, puiCHMsgLen, pucPHMsg, puiPHMsgLen, pucFpMsg, puiFpMsgLen);
    }

    /****************************************************************************
     功  能：居民身份证(港澳台居民居住证)- 图片
     参  数：cardInfo  - 输入，卡信息
     bBMPFile  - 输出，解码后BMP图片数据，38862字节
     返  回：ERRCODE_SUCCESS(0)	成功
     其他				失败
     *****************************************************************************/
    public Bitmap GetImage(byte[] cardInfo, byte[] bBMPFile) {
        byte[] tmp = new byte[1024];
        arraycopy(cardInfo, 0, tmp, 0, tmp.length);
        int mPhotoSize = 38862;
        if (bBMPFile.length < mPhotoSize)
            return null;
        int miaxis_wlt2BgrData = Wlt2BmpCall.miaxis_Wlt2BgrData(tmp, bBMPFile);
        if (miaxis_wlt2BgrData == 0) {
            return Wlt2BmpCall.miaxis_Bgr2Bitmap(102, 126, bBMPFile);
        }else {
            return null;
        }
    }

    //分段信息提取
    void PareseItem(char[] pucCHMsgStr, IdCardMsg msg) {
        msg.name = String.copyValueOf(pucCHMsgStr, 0, 15);
        String sex_code = String.copyValueOf(pucCHMsgStr, 15, 1);

        switch (sex_code) {
            case "1":
                msg.sex = "男";
                break;
            case "2":
                msg.sex = "女";
                break;
            case "9":
                msg.sex = "未说明";
                break;
            case "0":
            default:
                msg.sex = "未知";
        }

        try {
            String nation_code = String.copyValueOf(pucCHMsgStr, 16, 2);
            msg.nation_str = nation[Integer.parseInt(nation_code) - 1];
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            msg.nation_str="";
        }
        msg.birth_year = String.copyValueOf(pucCHMsgStr, 18, 4);
        msg.birth_month = String.copyValueOf(pucCHMsgStr, 22, 2);
        msg.birth_day = String.copyValueOf(pucCHMsgStr, 24, 2);
        msg.address = String.copyValueOf(pucCHMsgStr, 26, 35);
        msg.id_num = String.copyValueOf(pucCHMsgStr, 61, 18);
        msg.sign_office = String.copyValueOf(pucCHMsgStr, 79, 15);
        msg.useful_s_date_year = String.copyValueOf(pucCHMsgStr, 94, 4);
        msg.useful_s_date_month = String.copyValueOf(pucCHMsgStr, 98, 2);
        msg.useful_s_date_day = String.copyValueOf(pucCHMsgStr, 100, 2);
        msg.useful_e_date_year = String.copyValueOf(pucCHMsgStr, 102, 4);
        msg.useful_e_date_month = String.copyValueOf(pucCHMsgStr, 106, 2);
        msg.useful_e_date_day = String.copyValueOf(pucCHMsgStr, 108, 2);
    }

    /*民族列表*/
    private final String[] nation = {"汉", "蒙古", "回", "藏", "维吾尔", "苗", "彝", "壮", "布依", "朝鲜",
            "满", "侗", "瑶", "白", "土家", "哈尼", "哈萨克", "傣", "黎", "傈僳",
            "佤", "畲", "高山", "拉祜", "水", "东乡", "纳西", "景颇", "柯尔克孜", "土",
            "达斡尔", "仫佬", "羌", "布朗", "撒拉", "毛南", "仡佬", "锡伯", "阿昌", "普米",
            "塔吉克", "怒", "乌孜别克", "俄罗斯", "鄂温克", "德昂", "保安", "裕固", "京", "塔塔尔",
            "独龙", "鄂伦春", "赫哲", "门巴", "珞巴", "基诺", "", "", "穿青人", "家人",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "其他", "外国血统", "", ""
    };

    public static String fingerPositionCovert(byte finger) {
        switch ((int) finger) {
            case 11:
                return "右手拇指";
            case 12:
                return "右手食指";
            case 13:
                return "右手中指";
            case 14:
                return "右手环指";
            case 15:
                return "右手小指";
            case 16:
                return "左手拇指";
            case 17:
                return "左手食指";
            case 18:
                return "左手中指";
            case 19:
                return "左手环指";
            case 20:
                return "左手小指";
            case 97:
                return "右手不确定指位";
            case 98:
                return "左手不确定指位";
            case 99:
                return "其他不确定指位";
            default:
                return "其他不确定指位";
        }
    }

    /**
     * 解析身份证类型
    * */
    private String isGreenCard(byte[] bCardInfo) throws Exception{
        byte[] CardId=new byte[2];
        CardId[0]=bCardInfo[122];
        CardId[1]=bCardInfo[123];
        String card=unicode2String(CardId);
        if(isNumeric(card)){
            int id=Integer.parseInt(card);
            if (id>0&&id<9){
                return "";
            }else {
                return "-1";
            }
        }else {
            return "I";
        }
    }

    /**
     * 判断是否为数字
    * */
    public static boolean isNumeric(String str){
        for(int i=str.length();--i>=0;){
            int chr=str.charAt(i);
            if(chr<48 || chr>57)
                return false;
        }
        return true;
    }

    public static String unicode2String(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length / 2; i++) {
            int a = bytes[2 * i + 1];
            if (a < 0) {
                a = a + 256;
            }
            int b = bytes[2 * i];
            if (b < 0) {
                b = b + 256;
            }
            int c = (a << 8) | b;
            sb.append((char) c);
        }
        return sb.toString();
    }

    /**
     * 解析中国身份证信息
     * */
    public void IdCardParser(byte[] bCardInfo, IdCardMsg msg){
        msg.name=IdCardParser.getName(bCardInfo);
        msg.sex=IdCardParser.getGender(bCardInfo).equals("1")?"男":"女";
        msg.nation_str=TextUtils.isEmpty(IdCardParser.getNation(bCardInfo).trim())?"":nation[Integer.parseInt(IdCardParser.getNation(bCardInfo))-1];
        msg.birth_year=IdCardParser.getBirthYear(bCardInfo);
        msg.birth_month=IdCardParser.getBirthMonth(bCardInfo);
        msg.birth_day=IdCardParser.getBirthDay(bCardInfo);
        msg.address=IdCardParser.getAddress(bCardInfo);
        msg.id_num=IdCardParser.getCardNum(bCardInfo);
        msg.sign_office=IdCardParser.getIssuingAuthority(bCardInfo);
        msg.useful_s_date_year=IdCardParser.getStartYear(bCardInfo);
        msg.useful_s_date_month=IdCardParser.getStartMonth(bCardInfo);
        msg.useful_s_date_day=IdCardParser.getStartDay(bCardInfo);
        msg.useful_e_date_year=IdCardParser.getEndYear(bCardInfo);
        msg.useful_e_date_month=IdCardParser.getEndMonth(bCardInfo);
        msg.useful_e_date_day=IdCardParser.getEndDay(bCardInfo);
        msg.passnum=IdCardParser.getPassNum(bCardInfo);
        msg.issueCount=IdCardParser.getIssueNum(bCardInfo);
        msg.type=getCardType(bCardInfo);
    }


    /** 解析外国人永久居留证信息 */
    public void IdGreenCardOarser(byte[] bCardInfo, IdCardMsg msg){
        msg.name=IdCardParser.getEnglishName(bCardInfo);
        msg.sex=IdCardParser.getEnglishGender(bCardInfo).equals("1")?"男":"女";
        msg.nation_str=IdCardParser.getNationality(bCardInfo);
        msg.birth_year=IdCardParser.getEnglishY(bCardInfo);
        msg.birth_month=IdCardParser.getEnglishM(bCardInfo);
        msg.birth_day=IdCardParser.getEnglishD(bCardInfo);
        msg.address=IdCardParser.getAddress(bCardInfo);
        msg.id_num=IdCardParser.getCardNum(bCardInfo);
        msg.sign_office=IdCardParser.getAcceptMatter(bCardInfo);
        msg.useful_s_date_year=IdCardParser.getStartYear(bCardInfo);
        msg.useful_s_date_month=IdCardParser.getStartMonth(bCardInfo);
        msg.useful_s_date_day=IdCardParser.getStartDay(bCardInfo);
        msg.useful_e_date_year=IdCardParser.getEndYear(bCardInfo);
        msg.useful_e_date_month=IdCardParser.getEndMonth(bCardInfo);
        msg.useful_e_date_day=IdCardParser.getEndDay(bCardInfo);
        msg.chinesename=IdCardParser.getChineseName(bCardInfo);
        msg.version=IdCardParser.getVersion(bCardInfo);
        msg.type=IdCardParser.getCardType(bCardInfo);



    }

    /** 区分港澳台与二代证，不区分外国人*/
    private String getCardType(byte[] idCardData){
        if(TextUtils.isEmpty(IdCardParser.getCardType(idCardData))){
            if(TextUtils.isEmpty(IdCardParser.getPassNum(idCardData))&&!TextUtils.isEmpty(IdCardParser.getNation(idCardData))){
                return "";
            }else {
                return "J";
            }
        }else {
            return IdCardParser.getCardType(idCardData);
        }
    }
}
