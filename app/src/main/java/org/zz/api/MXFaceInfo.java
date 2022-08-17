package org.zz.api;

import java.util.Arrays;

public class MXFaceInfo {
	public static final int iMaxFaceNum        = 100;
	public static final int MAX_KEY_POINT_NUM  = 110;
	public static final int SIZE = (42+2*MAX_KEY_POINT_NUM);
	//face rect人脸框
	public int x;        // 左上角x坐标
	public int y;        // 坐上角y坐标
	public int width;    // 人脸宽
	public int height;   // 人脸高

	//face_point关键点
	public int keypt_num;						            // 关键点个数                               // 关键点得分
	public int[] keypt_x = new int[MAX_KEY_POINT_NUM];      // 关键点x坐标
	public int[] keypt_y = new int[MAX_KEY_POINT_NUM];      // 关键点y坐标

	// 人脸质量分
	public int quality;        //注册：>=90,  比对/识别：>=60
	// 人脸属性
	public int probability;    //人脸置信度
	public int completeness;   //人脸完整性,	 =0 提示：把脸移入框内
	public int eyeDistance;    //瞳距，		<30 提示：请靠近摄像头
	public int illumination;   //光照，		<50 提示：脸部过暗，>200 提示：脸部过亮
	public int blur;           //模糊，		>30 提示：图像模糊
	public int[] occlusion = new int[10];   //遮挡,         >=10,提示：对应区域有遮挡
	//0-total，1-lefteye，2-righteye，3-nose，4-mouth，
	//5-leftcheck，6-rightcheck，7-chin
	public int expression;     //表情
	public int skin;           //肤色
	public int makeup;         //浓妆
	public int leye_status;    //左眼睁闭程度
	public int reye_status;    //右眼睁闭程度
	public int mouth_status;   //嘴巴张闭程度
	public int glasses;        //眼镜
	public int mask;           //口罩
	public int liveness;       //活体
	public int age;            //年龄
	public int gender;         //性别
	// head_pose头部姿态
	public int pitch;         // 抬头、低头， >20-提示：请正对摄像头
	public int yaw;           // 左右转头，   >20-提示：请正对摄像头
	public int roll;          // 平面内偏头， >20-提示：请正对摄像头

	// 人脸ID
	public int detected;       //1-检测到的人脸,0-跟踪到的人脸
	public int trackId;        //人脸ID（ID<0表示没有进入跟踪）
	public int idmax;          //获取交并比最大的人脸下标
	public int reCog;          //判断该人脸是否被识别-识别标识
	public int reCogId;        //数据库中的识别ID
	public int reCogScore;     //数据库中识别ID的分数
	public int stranger;       //陌生人标识位

	public MXFaceInfo() {
	}

	public MXFaceInfo(MXFaceInfo mxFaceInfoEx) {
		this.x = mxFaceInfoEx.x;
		this.y = mxFaceInfoEx.y;
		this.width = mxFaceInfoEx.width;
		this.height = mxFaceInfoEx.height;
		this.keypt_num = mxFaceInfoEx.keypt_num;
		this.keypt_x = mxFaceInfoEx.keypt_x;
		this.keypt_y = mxFaceInfoEx.keypt_y;
		this.quality = mxFaceInfoEx.quality;
		this.probability = mxFaceInfoEx.probability;
		this.completeness = mxFaceInfoEx.completeness;
		this.eyeDistance = mxFaceInfoEx.eyeDistance;
		this.illumination = mxFaceInfoEx.illumination;
		this.blur = mxFaceInfoEx.blur;
		this.occlusion = mxFaceInfoEx.occlusion;
		this.expression = mxFaceInfoEx.expression;
		this.skin = mxFaceInfoEx.skin;
		this.makeup = mxFaceInfoEx.makeup;
		this.leye_status = mxFaceInfoEx.leye_status;
		this.reye_status = mxFaceInfoEx.reye_status;
		this.mouth_status = mxFaceInfoEx.mouth_status;
		this.glasses = mxFaceInfoEx.glasses;
		this.mask = mxFaceInfoEx.mask;
		this.liveness = mxFaceInfoEx.liveness;
		this.age = mxFaceInfoEx.age;
		this.gender = mxFaceInfoEx.gender;
		this.pitch = mxFaceInfoEx.pitch;
		this.yaw = mxFaceInfoEx.yaw;
		this.roll = mxFaceInfoEx.roll;
		this.detected = mxFaceInfoEx.detected;
		this.trackId = mxFaceInfoEx.trackId;
		this.idmax = mxFaceInfoEx.idmax;
		this.reCog = mxFaceInfoEx.reCog;
		this.reCogId = mxFaceInfoEx.reCogId;
		this.reCogScore = mxFaceInfoEx.reCogScore;
		this.stranger = mxFaceInfoEx.stranger;
	}

//	public MXFaceInfoEx(MXFaceInfoEx mxFaceInfoEx) {
//		this.x = mxFaceInfoEx.x;
//		this.y = mxFaceInfoEx.y;
//		this.width = mxFaceInfoEx.width;
//		this.height = mxFaceInfoEx.height;
//		this.keypt_num = mxFaceInfoEx.keypt_num;
//		this.age = mxFaceInfoEx.age;
//		this.gender = mxFaceInfoEx.gender;
//		this.expression = mxFaceInfoEx.expression;
//		this.quality = mxFaceInfoEx.quality;
//		this.eyeDistance = mxFaceInfoEx.eyeDistance;
//		this.liveness = mxFaceInfoEx.liveness;
//		this.detected = mxFaceInfoEx.detected;
//		this.trackId = mxFaceInfoEx.trackId;
//		this.idmax = mxFaceInfoEx.idmax;
//		this.reCog = mxFaceInfoEx.reCog;
//		this.reCogId = mxFaceInfoEx.reCogId;
//		this.reCogScore = mxFaceInfoEx.reCogScore;
//		this.mask = mxFaceInfoEx.mask;
//		this.stranger = mxFaceInfoEx.stranger;
//		this.pitch = mxFaceInfoEx.pitch;
//		this.yaw = mxFaceInfoEx.yaw;
//		this.roll = mxFaceInfoEx.roll;
//
//		this.keypt_x = new int[MAX_KEY_POINT_NUM];
//		System.arraycopy(mxFaceInfoEx.keypt_x, 0, this.keypt_x, 0, this.keypt_x.length);
//		this.keypt_y = new int[MAX_KEY_POINT_NUM];
//		System.arraycopy(mxFaceInfoEx.keypt_y, 0, this.keypt_y, 0, this.keypt_y.length);
//	}

	public static int Int2MXFaceInfoEx(int iFaceNum, int[] iFaceInfo, MXFaceInfo[] pMXFaceInfoEx) {
		for (int i = 0; i < iFaceNum; i++) {
			pMXFaceInfoEx[i].x         = iFaceInfo[i * MXFaceInfo.SIZE];
			pMXFaceInfoEx[i].y         = iFaceInfo[i * MXFaceInfo.SIZE + 1];
			pMXFaceInfoEx[i].width     = iFaceInfo[i * MXFaceInfo.SIZE + 2];
			pMXFaceInfoEx[i].height    = iFaceInfo[i * MXFaceInfo.SIZE + 3];
			pMXFaceInfoEx[i].keypt_num = iFaceInfo[i * MXFaceInfo.SIZE + 4];
			for (int j = 0; j < MAX_KEY_POINT_NUM; j++)
			{
				pMXFaceInfoEx[i].keypt_x[j] = iFaceInfo[i * MXFaceInfo.SIZE + 5 + j];
				pMXFaceInfoEx[i].keypt_y[j] = iFaceInfo[i * MXFaceInfo.SIZE + 5 + j + MXFaceInfo.MAX_KEY_POINT_NUM];
			}
			pMXFaceInfoEx[i].quality      = iFaceInfo[i * MXFaceInfo.SIZE + 5 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].probability  = iFaceInfo[i * MXFaceInfo.SIZE + 6 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].completeness = iFaceInfo[i * MXFaceInfo.SIZE + 7 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].eyeDistance  = iFaceInfo[i * MXFaceInfo.SIZE + 8 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].illumination = iFaceInfo[i * MXFaceInfo.SIZE + 9 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].blur         = iFaceInfo[i * MXFaceInfo.SIZE + 10 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			for (int k = 0; k < 10; k++)
			{
				pMXFaceInfoEx[i].occlusion[k] = iFaceInfo[i * MXFaceInfo.SIZE + 11 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM + k];
			}
			pMXFaceInfoEx[i].expression   = iFaceInfo[i * MXFaceInfo.SIZE + 21 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].skin         = iFaceInfo[i * MXFaceInfo.SIZE + 22 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].makeup       = iFaceInfo[i * MXFaceInfo.SIZE + 23 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].leye_status  = iFaceInfo[i * MXFaceInfo.SIZE + 24 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].reye_status  = iFaceInfo[i * MXFaceInfo.SIZE + 25 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].mouth_status = iFaceInfo[i * MXFaceInfo.SIZE + 26 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].glasses      = iFaceInfo[i * MXFaceInfo.SIZE + 27 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].mask         = iFaceInfo[i * MXFaceInfo.SIZE + 28 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].liveness     = iFaceInfo[i * MXFaceInfo.SIZE + 29 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].age          = iFaceInfo[i * MXFaceInfo.SIZE + 30 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].gender       = iFaceInfo[i * MXFaceInfo.SIZE + 31 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].pitch        = iFaceInfo[i * MXFaceInfo.SIZE + 32 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].yaw          = iFaceInfo[i * MXFaceInfo.SIZE + 33 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].roll         = iFaceInfo[i * MXFaceInfo.SIZE + 34 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].detected     = iFaceInfo[i * MXFaceInfo.SIZE + 35 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].trackId      = iFaceInfo[i * MXFaceInfo.SIZE + 36 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].idmax        = iFaceInfo[i * MXFaceInfo.SIZE + 37 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].reCog        = iFaceInfo[i * MXFaceInfo.SIZE + 38 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].reCogId      = iFaceInfo[i * MXFaceInfo.SIZE + 39 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].reCogScore   = iFaceInfo[i * MXFaceInfo.SIZE + 40 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
			pMXFaceInfoEx[i].stranger     = iFaceInfo[i * MXFaceInfo.SIZE + 41 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];

		}
		return 0;
	}

	public static int MXFaceInfoEx2Int(int iFaceNum, int[] iFaceInfo, MXFaceInfo[] pMXFaceInfoEx) {
		for (int i = 0; i < iFaceNum; i++) {
			iFaceInfo[i * MXFaceInfo.SIZE]     = pMXFaceInfoEx[i].x;
			iFaceInfo[i * MXFaceInfo.SIZE + 1] = pMXFaceInfoEx[i].y;
			iFaceInfo[i * MXFaceInfo.SIZE + 2] = pMXFaceInfoEx[i].width;
			iFaceInfo[i * MXFaceInfo.SIZE + 3] = pMXFaceInfoEx[i].height;
			iFaceInfo[i * MXFaceInfo.SIZE + 4] = pMXFaceInfoEx[i].keypt_num;
			for (int j = 0; j < MAX_KEY_POINT_NUM; j++)
			{
				iFaceInfo[i * MXFaceInfo.SIZE + 5 + j] = pMXFaceInfoEx[i].keypt_x[j];
				iFaceInfo[i * MXFaceInfo.SIZE + 5 + j + MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].keypt_y[j];
			}

			iFaceInfo[i * MXFaceInfo.SIZE + 5 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM]  = pMXFaceInfoEx[i].quality;
			iFaceInfo[i * MXFaceInfo.SIZE + 6 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM]  = pMXFaceInfoEx[i].probability;
			iFaceInfo[i * MXFaceInfo.SIZE + 7 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM]  = pMXFaceInfoEx[i].completeness;
			iFaceInfo[i * MXFaceInfo.SIZE + 8 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM]  = pMXFaceInfoEx[i].eyeDistance;
			iFaceInfo[i * MXFaceInfo.SIZE + 9 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM]  = pMXFaceInfoEx[i].illumination;
			iFaceInfo[i * MXFaceInfo.SIZE + 10 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].blur;
			for (int k = 0; k < 10; k++)
			{
				iFaceInfo[i * MXFaceInfo.SIZE + 11 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM + k] = pMXFaceInfoEx[i].occlusion[k];
			}
			iFaceInfo[i * MXFaceInfo.SIZE + 21 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].expression;
			iFaceInfo[i * MXFaceInfo.SIZE + 22 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].skin;
			iFaceInfo[i * MXFaceInfo.SIZE + 23 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].makeup;
			iFaceInfo[i * MXFaceInfo.SIZE + 24 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].leye_status;
			iFaceInfo[i * MXFaceInfo.SIZE + 25 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].reye_status;
			iFaceInfo[i * MXFaceInfo.SIZE + 26 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].mouth_status;
			iFaceInfo[i * MXFaceInfo.SIZE + 27 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].glasses;
			iFaceInfo[i * MXFaceInfo.SIZE + 28 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].mask;
			iFaceInfo[i * MXFaceInfo.SIZE + 29 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].liveness;
			iFaceInfo[i * MXFaceInfo.SIZE + 30 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].age;
			iFaceInfo[i * MXFaceInfo.SIZE + 31 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].gender;
			iFaceInfo[i * MXFaceInfo.SIZE + 32 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].pitch;
			iFaceInfo[i * MXFaceInfo.SIZE + 33 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].yaw;
			iFaceInfo[i * MXFaceInfo.SIZE + 34 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].roll;
			iFaceInfo[i * MXFaceInfo.SIZE + 35 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].detected;
			iFaceInfo[i * MXFaceInfo.SIZE + 36 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].trackId;
			iFaceInfo[i * MXFaceInfo.SIZE + 37 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].idmax;
			iFaceInfo[i * MXFaceInfo.SIZE + 38 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].reCog;
			iFaceInfo[i * MXFaceInfo.SIZE + 39 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].reCogId;
			iFaceInfo[i * MXFaceInfo.SIZE + 40 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].reCogScore;
			iFaceInfo[i * MXFaceInfo.SIZE + 41 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].stranger;
		}
		return 0;
	}

	public static int Copy(int iFaceNum, MXFaceInfo[] pMXFaceInfoEx, MXFaceInfo[] pMXFaceInfoExDst) {
		for (int i = 0; i < iFaceNum; i++) {
			pMXFaceInfoExDst[i].x         = pMXFaceInfoEx[i].x;
			pMXFaceInfoExDst[i].y         = pMXFaceInfoEx[i].y;
			pMXFaceInfoExDst[i].width     = pMXFaceInfoEx[i].width ;
			pMXFaceInfoExDst[i].height    = pMXFaceInfoEx[i].height;
			pMXFaceInfoExDst[i].keypt_num = pMXFaceInfoEx[i].keypt_num;
			for (int j = 0; j < MAX_KEY_POINT_NUM; j++)
			{
				pMXFaceInfoExDst[i].keypt_x[j] = pMXFaceInfoEx[i].keypt_x[j];
				pMXFaceInfoExDst[i].keypt_y[j] = pMXFaceInfoEx[i].keypt_y[j];
			}
			pMXFaceInfoExDst[i].quality      = pMXFaceInfoEx[i].quality;
			pMXFaceInfoExDst[i].probability  = pMXFaceInfoEx[i].probability;
			pMXFaceInfoExDst[i].completeness = pMXFaceInfoEx[i].completeness;
			pMXFaceInfoExDst[i].eyeDistance  = pMXFaceInfoEx[i].eyeDistance;
			pMXFaceInfoExDst[i].illumination = pMXFaceInfoEx[i].illumination;
			pMXFaceInfoExDst[i].blur         = pMXFaceInfoEx[i].blur;
			for (int k = 0; k < 10; k++)
			{
				pMXFaceInfoExDst[i].occlusion[k] = pMXFaceInfoEx[i].occlusion[k];
			}
			pMXFaceInfoExDst[i].expression   = pMXFaceInfoEx[i].expression;
			pMXFaceInfoExDst[i].skin         = pMXFaceInfoEx[i].skin;
			pMXFaceInfoExDst[i].makeup       = pMXFaceInfoEx[i].makeup;
			pMXFaceInfoExDst[i].leye_status  = pMXFaceInfoEx[i].leye_status;
			pMXFaceInfoExDst[i].reye_status  = pMXFaceInfoEx[i].reye_status;
			pMXFaceInfoExDst[i].mouth_status = pMXFaceInfoEx[i].mouth_status;
			pMXFaceInfoExDst[i].glasses      = pMXFaceInfoEx[i].glasses;
			pMXFaceInfoExDst[i].mask         = pMXFaceInfoEx[i].mask;
			pMXFaceInfoExDst[i].liveness     = pMXFaceInfoEx[i].liveness;
			pMXFaceInfoExDst[i].age          = pMXFaceInfoEx[i].age;
			pMXFaceInfoExDst[i].gender       = pMXFaceInfoEx[i].gender;
			pMXFaceInfoExDst[i].pitch        = pMXFaceInfoEx[i].pitch;
			pMXFaceInfoExDst[i].yaw          = pMXFaceInfoEx[i].yaw ;
			pMXFaceInfoExDst[i].roll         = pMXFaceInfoEx[i].roll;
			pMXFaceInfoExDst[i].detected     = pMXFaceInfoEx[i].detected;
			pMXFaceInfoExDst[i].trackId      = pMXFaceInfoEx[i].trackId;
			pMXFaceInfoExDst[i].idmax        = pMXFaceInfoEx[i].idmax;
			pMXFaceInfoExDst[i].reCog        = pMXFaceInfoEx[i].reCog ;
			pMXFaceInfoExDst[i].reCogId      = pMXFaceInfoEx[i].reCogId;
			pMXFaceInfoExDst[i].reCogScore   = pMXFaceInfoEx[i].reCogScore;
			pMXFaceInfoExDst[i].stranger     = pMXFaceInfoEx[i].stranger;
		}
		return 0;
	}

	@Override
	public String toString() {
		return "MXFaceInfoEx{" +
				"x=" + x +
				", y=" + y +
				", width=" + width +
				", height=" + height +
				", keypt_num=" + keypt_num +
				", keypt_x=" + Arrays.toString(keypt_x) +
				", keypt_y=" + Arrays.toString(keypt_y) +
				", age=" + age +
				", gender=" + gender +
				", expression=" + expression +
				", quality=" + quality +
				", eyeDistance=" + eyeDistance +
				", liveness=" + liveness +
				", detected=" + detected +
				", trackId=" + trackId +
				", idmax=" + idmax +
				", reCog=" + reCog +
				", reCogId=" + reCogId +
				", reCogScore=" + reCogScore +
				", mask=" + mask +
				", stranger=" + stranger +
				", pitch=" + pitch +
				", yaw=" + yaw +
				", roll=" + roll +
				'}';
	}
}
