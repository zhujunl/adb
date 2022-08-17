package org.zz.api;

public class MXErrorCode {
    public static final int ERR_OK                =  0;//成功
    public static final int ERR_CHECK             =  1;//校验失败

    public static final int ERR_INIT              = 11;//初始化失败
    public static final int ERR_NO_INIT           = 12;//未初始化
    public static final int ERR_FACE_DETECT       = 13;//人脸检测失败
    public static final int ERR_FACE_LANDMARK     = 14;//关键点定位失败
    public static final int ERR_MULTIPLE_FACES    = 15;//多张人脸
    public static final int ERR_FACE_DEWARE       = 16;//去网纹失败
    public static final int ERR_TZ_CHECK          = 17;//特征格式校验失败
    public static final int ERR_FACE_EXTRACT      = 18;//人脸特征提取失败
    public static final int ERR_FACE_MATCH        = 19;//人脸比对失败
    public static final int ERR_NO_MODEL_FILE     = 20;//模型文件不存在
    public static final int ERR_HASH_INVALID      = 21;//Hash不合法
    public static final int ERR_MEMORY_OUT        = 22;//内存越界
    public static final int ERR_OUT_FACES         = 23;//人脸个数超过额定值（100）
    public static final int ERR_NO_DETECT_MODEL   = 24;//检测模型不存在
    public static final int ERR_NO_QUALITY_MODEL  = 25;//质量评价模型不存在
    public static final int ERR_NO_RECOG_MODEL    = 26;//识别模型不存在
    public static final int ERR_FACE_SIZE         = 27;//待检测人脸图像小于100x100

    public static final int ERR_IMAGE_DECODE      = 31;//图像解析失败
    public static final int ERR_FACE_QUALITY      = 32;//图像质量不达标

    public static final int ERR_READ_IMAGE        = 41;//读取图像失败

    public static final int ERR_EXPIRED           = 100;//授权过期或无效
    public static final int ERR_INVALID           = 101;//授权文件非法
    public static final int ERR_LICENSE_FILE      = 102;//授权文件找不到
    public static final int ERR_KEY_INVALID       = 103;//授权UKey无效



}
