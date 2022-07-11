package com.miaxis.face.constant;

/**
 * Created by Administrator on 2017/5/18 0018.
 */

public class Constants {

    public static final int PRE_WIDTH = 1280;
    public static final int PRE_HEIGHT = 720;

    public static final int SM_WIDTH=2048;
    public static final int SM_HEIGHT=1536;

    public static final int ZOOM_WIDTH = 320;
    public static final int ZOOM_HEIGHT = 240;

    public static final int PIC_WIDTH = 640;
    public static final int PIC_HEIGHT = 480;

    public static final int CP_WIDTH = 1280;
    public static final int CP_HEIGHT = 960;
    public static final float zoomRate = CP_WIDTH / ZOOM_WIDTH;

    public static final int MAX_FACE_NUM       = 5;

    public static float PASS_SCORE             = 0.7f;        // 比对通过阈值

    public static final int GPIO_INTERVAL = 100;

    public static final int GET_CARD_ID = 0;
    public static final int NO_CARD     = 134;

    public static final String[] FOLK = { "汉", "蒙古", "回", "藏", "维吾尔", "苗", "彝", "壮", "布依", "朝鲜",
            "满", "侗", "瑶", "白", "土家", "哈尼", "哈萨克", "傣", "黎", "傈僳", "佤", "畲",
            "高山", "拉祜", "水", "东乡", "纳西", "景颇", "柯尔克孜", "土", "达斡尔", "仫佬", "羌",
            "布朗", "撒拉", "毛南", "仡佬", "锡伯", "阿昌", "普米", "塔吉克", "怒", "乌孜别克",
            "俄罗斯", "鄂温克", "德昂", "保安", "裕固", "京", "塔塔尔", "独龙", "鄂伦春", "赫哲",
            "门巴", "珞巴", "基诺", "", "", "穿青人", "家人", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "其他", "外国血统", "",
            "" };

    public static final int PHOTO_SIZE         = 38862;        // 解码后身份证图片长度
    public static final int mFingerDataSize    = 512;          // 指纹数据长度
    public static final int mFingerDataB64Size = 684;          // 指纹数据Base64编码后的长度

    public static final int MAX_COUNT = 40000;
    public static final String DEFAULT_IP = "183.129.171.153";
    public static final int DEFAULT_PORT = 9200;
    public static final String DEFAULT_UPTIME = "3 : 00";
    public static final float DEFAULT_SCORE = 0.71f;
    public static final boolean DEFAULT_NET = false;
    public static final boolean DEFAULT_FINGER = false;
    public static final boolean DEFAULT_QUERY = false;
    public static final int DEFAULT_INTERVAL = 4;
    public static final String DEFAULT_BANNER = "";
    public static final String DEFAULT_PASSWORD = "666666";
    public static final boolean DEFAULT_ADVERTISE_FLAG = true;
    public static final int DEFAULT_ADVERTISE_DELAY_TIME = 30;

    public static final String PROJECT_NAME = "faceid";
    public static final String CHECK_VERSION = "app/getAppInfo";
    public static final String DOWN_VERSION = "app/getApp";
    public static final String UPLOAD_PERSON = "person/uploadPerson";

    public static final int TASK_DELAY = 1000 * 60 * 60 * 24;

    public static final int RESULT_CODE_FINISH = 51243123;

    public static final int LEVEL              = 2;            // 指纹比对级别
    public static final int TIME_OUT           = 10 * 1000;    // 等待按手指的超时时间，单位：ms
    public static final int IMAGE_X_BIG        = 256;          // 指纹图像宽高 大小
    public static final int IMAGE_Y_BIG        = 360;
    public static final int IMAGE_SIZE_BIG     = IMAGE_X_BIG * IMAGE_Y_BIG;
    public static final int TZ_SIZE            = 512;          // 指纹特征长度  BASE64

    public static final float LEFT_VOLUME = 1.0f, RIGHT_VOLUME = 1.0f;
    public static final int PRIORITY = 1, LOOP = 0;
    public static final float SOUND_RATE = 1.0f;//正常速率

    public static final int PATH_TF_CARD = 0;
    public static final int PATH_LOCAL = 1;

    public static final int FINGER_RIGHT_0 = 11;
    public static final int FINGER_RIGHT_1 = 12;
    public static final int FINGER_RIGHT_2 = 13;
    public static final int FINGER_RIGHT_3 = 14;
    public static final int FINGER_RIGHT_4 = 15;

    public static final int FINGER_LEFT_0 = 16;
    public static final int FINGER_LEFT_1 = 17;
    public static final int FINGER_LEFT_2 = 18;
    public static final int FINGER_LEFT_3 = 19;
    public static final int FINGER_LEFT_4 = 20;

    public static final int SOUND_SUCCESS = 1;
    public static final int SOUND_FAIL = 2;
    public static final int PLEASE_PRESS = 3;
    public static final int SOUND_OR = 4;
    public static final int SOUND_OTHER_FINGER  = 5;
    public static final int SOUND_VALIDATE_FAIL  = 6;
    public static final int SOUND_PUT_CARD  = 7;

    public static final float pam=0.3f;//人脸图片截取扩大
    public static final int ORIGINAL=0;//原始图像
    public static final int GETBLACK=1;//黑色低图
    public static final int GETRED=2;//红色低图

    public static final boolean DEFAULTSCENE=true;//默认现场图片
    public static final boolean DEFAULTLIVE=true;//默认开启活体监测
    public static final int DEFAULTRGB=1;//默认可见光摄像头
    public static final int DEFAULTNIR=0;//默认近红外摄像头
    public static final int DEFAULTSM=2;//默认高拍仪摄像头
    public static final float QUALITY=50;
    public static final int SQLSIZE=10000;
}
