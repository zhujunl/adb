
package org.zz.api;

import android.content.Context;
import android.text.TextUtils;

import org.zz.jni.JustouchFaceApi;


public class MXFaceAPI {

	private boolean      m_bInit = false;
	private JustouchFaceApi m_dllFaceApi = new JustouchFaceApi();

	/**
	 * @author   chen.gs
	 * @category 获取算法版本
	 * @param
	 * @return   算法版本
	 * */
	public String mxAlgVersion() {
		return m_dllFaceApi.getAlgVersion();
	}

	/**
	 * @author   chen.gs
	 * @category 初始化算法
	 * @param    context        - 输入，上下文句柄
	 * 			 szModelPath    - 输入，模型路径
	 * 			 szLicense      - 输入，授权码
	 * @return   0-成功，其他-失败
	 * */
	public int mxInitAlg(Context context, String szModelPath, String szLicense)
	{
		int nRet = 0;
		if (TextUtils.isEmpty(szLicense)){
			nRet = m_dllFaceApi.initAlg(context,szModelPath,"");
		}else {
			nRet = m_dllFaceApi.initAlg(context,szModelPath,szLicense);
		}

		if(nRet!=0){
			return nRet;
		}
		m_bInit = true;
		return 0;
	}


	/**
	 * @author   chen.gs
	 * @category 释放算法
	 * @param
	 * @return   0-成功，其他-失败
	 * */
	public int mxFreeAlg() {
		if (m_bInit==true) {
			m_dllFaceApi.freeAlg();
			m_bInit = false;
		}
		return 0;
	}

	/**
	 * @author   chen.gs
	 * @category 人脸检测,用于静态图像检测
	 * @param    pImage    - 输入，RGB图像数据
	 * 			 nWidth    - 输入，图像宽度
	 * 			 nHeight   - 输入，图像高度
	 * 			 pFaceNum  - 输入/输出，人脸数
	 * 			 pFaceInfo - 输出，人脸信息,内存分配大小 new int[262*100],结构详见MXFaceInfoEx
	 * @return   0-成功，其他-失败
	 * */
	public int mxDetectFace(byte[] pImage, int nWidth, int nHeight,
							int[] pFaceNum, MXFaceInfo[] pFaceInfo) {
		if (m_bInit!=true){
			return MXErrorCode.ERR_NO_INIT;
		}
		int[] bInfo = new int[MXFaceInfo.SIZE * MXFaceInfo.iMaxFaceNum];
		int nRet = m_dllFaceApi.detectFace(pImage, nWidth, nHeight,pFaceNum,bInfo);
		if(nRet!=0){
			pFaceNum[0] = 0;
			return nRet;
		}
		MXFaceInfo.Int2MXFaceInfoEx(pFaceNum[0],bInfo,pFaceInfo);
		return 0;
	}

	/**
	 * @author   chen.gs
	 * @category 获取人脸特征长度
	 * @param
	 * @return   人脸特征长度
	 * */
	public int mxGetFeatureSize()
	{
		int iFeaLen = 0;
		if (m_bInit==true){
			iFeaLen = m_dllFaceApi.getFeatureSize();
		}
		return iFeaLen;
	}

	/**
	 * @author   chen.gs
	 * @category 人脸特征提取
	 * @param    pImage       - 输入，RGB图像数据
	 * 			 nWidth       - 输入，图像宽度
	 * 			 nHeight      - 输入，图像高度
	 * 			 nFaceNum     - 输入，人脸个数
	 * 			 pFaceInfo    - 输入，人脸信息
	 * 			 pFaceFeature - 输出，人脸特征，特征长度*人脸个数
	 * @return   0-成功，其他-失败
	 * */
	public int mxFeatureExtract(byte[] pImage, int nWidth, int nHeight,
								int nFaceNum, MXFaceInfo[] pFaceInfo, byte[] pFaceFeature)
	{
		if (m_bInit!=true){
			return MXErrorCode.ERR_NO_INIT;
		}
		int[] bInfo = new int[MXFaceInfo.SIZE * MXFaceInfo.iMaxFaceNum];
		MXFaceInfo.MXFaceInfoEx2Int(nFaceNum,bInfo,pFaceInfo);
		int nRet = m_dllFaceApi.featureExtract(pImage, nWidth, nHeight,nFaceNum, bInfo,pFaceFeature);
		if(nRet!=0){
			return MXErrorCode.ERR_FACE_EXTRACT;
		}
		return nRet;
	}

	/**
	 * @author   chen.gs
	 * @category 人脸特征比对
	 * @param    pFaceFeatureA - 输入，人脸特征A
	 * 			 pFaceFeatureB - 输入，人脸特征B
	 * 			 fScore    - 输出，相似性度量值，0~1.0 ，越大越相似。
	 * @return   0-成功，其他-失败
	 * */
	public int mxFeatureMatch(byte[] pFaceFeatureA,byte[] pFaceFeatureB,float[] fScore)
	{
		if (m_bInit!=true){
			return MXErrorCode.ERR_NO_INIT;
		}
		return m_dllFaceApi.featureMatch(pFaceFeatureA, pFaceFeatureB,fScore);
	}

	/**
	 * @author   chen.gs
	 * @category 根据人脸检测结果，进行人脸图像质量评价，用于过滤参与人脸比对识别的图像
	 * @param     pImage     	- 输入，RGB图像数据
	 * 			  nWidth        - 输入，图像宽度
	 * 			  nHeight       - 输入，图像高度
	 * 			  nFaceNum    	- 输入，人脸数
	 * 			  pFaceInfo     - 输入/输出，人脸信息，质量分数通过MXFaceInfoEx结构体quality属性获取
	 * @return   0-成功，其他-失败
	 * */
	public int mxFaceQuality(byte[] pImage, int nWidth, int nHeight,
							 int nFaceNum, MXFaceInfo[] pFaceInfo){
		if (m_bInit!=true){
			return MXErrorCode.ERR_NO_INIT;
		}
		int[] bInfo = new int[MXFaceInfo.SIZE * MXFaceInfo.iMaxFaceNum];
		MXFaceInfo.MXFaceInfoEx2Int(nFaceNum,bInfo,pFaceInfo);
		int nRet = m_dllFaceApi.faceQuality(pImage, nWidth, nHeight,nFaceNum,bInfo);
		if(nRet!=0){
			return nRet;
		}
		MXFaceInfo.Int2MXFaceInfoEx(nFaceNum,bInfo,pFaceInfo);
		return nRet;
	}

	/**
	 * @author   chen.gs
	 * @category 根据人脸检测结果，进行人脸图像质量评价，用于人脸图像注册。
	 * @note     建议采用高清USB摄像头采集，质量分大于90分，用于注册
	 * @param     pImage     	- 输入，RGB图像数据
	 * 			  nImgWidth     - 输入，图像宽度
	 * 			  nImgHeight    - 输入，图像高度
	 * 			  nFaceNum    	- 输入，人脸数
	 * 			  pFaceInfo 	- 输入/输出，人脸信息，质量分数通过MXFaceInfoEx结构体quality属性获取
	 * @return   0-成功，其他-失败
	 * */
	public int mxFaceQuality4Reg(byte[] pImage, int nWidth, int nHeight,
								 int nFaceNum, MXFaceInfo[] pFaceInfo){
		if (m_bInit!=true){
			return MXErrorCode.ERR_NO_INIT;
		}
		int[] bInfo = new int[MXFaceInfo.SIZE * MXFaceInfo.iMaxFaceNum];
		MXFaceInfo.MXFaceInfoEx2Int(nFaceNum,bInfo,pFaceInfo);
		for(int i=0;i<nFaceNum;i++){
			int[] bInfoTmp = new int[MXFaceInfo.SIZE];
			System.arraycopy(bInfo, i* MXFaceInfo.SIZE, bInfoTmp, 0, MXFaceInfo.SIZE);
			m_dllFaceApi.faceQuality4Reg(pImage,nWidth,nHeight,bInfoTmp);
			pFaceInfo[i].quality = bInfoTmp[8 + 2 * MXFaceInfo.MAX_KEY_POINT_NUM];
		}
		return 0;
	}

	/**
	 * @author   chen.gs
	 * @category  可见光活体检测（配合指定型号双目摄像头）
	 * @param     pImage        - 输入，可见光图像数据
	 * 			  nImgWidth  	- 输入，图像宽度
	 * 			  nImgHeight  	- 输入，图像高度
	 * 			  nFaceNum    	- 输入，人脸数
	 * 			  pFaceInfo 	- 输入/输出，人脸信息，活体分数通过MXFaceInfoEx结构体liveness属性获取
	 * @return   0-成功，其他-失败
	 * */
	public int mxVISLivenessDetect(byte[] pImage, int nImgWidth, int nImgHeight, int nFaceNum, MXFaceInfo[] pFaceInfo)
	{
		if (m_bInit!=true){
			return -10;
		}
		int[] bInfo = new int[MXFaceInfo.SIZE * nFaceNum];
		MXFaceInfo.MXFaceInfoEx2Int(nFaceNum,bInfo,pFaceInfo);
		int nRet = m_dllFaceApi.visLivenessDetect(pImage, nImgWidth, nImgHeight,nFaceNum,bInfo);
		MXFaceInfo.Int2MXFaceInfoEx(nFaceNum,bInfo,pFaceInfo);
		return nRet;
	}

	/**
	 * @author   chen.gs
	 * @category  近红外活体检测（配合指定型号双目摄像头）
	 * @param     pImage        - 输入，近红外图像数据
	 * 			  nImgWidth  	- 输入，图像宽度
	 * 			  nImgHeight  	- 输入，图像高度
	 * 			  nFaceNum    	- 输入，人脸数
	 * 			  pFaceInfo 	- 输入/输出，人脸信息，活体分数通过MXFaceInfoEx结构体liveness属性获取
	 * @return   0-成功，其他-失败
	 * */
	public int mxNIRLivenessDetect(byte[] pImage, int nImgWidth, int nImgHeight, int nFaceNum, MXFaceInfo[] pFaceInfo)
	{
		if (m_bInit!=true){
			return MXErrorCode.ERR_NO_INIT;
		}

		int[] bInfo = new int[MXFaceInfo.SIZE * nFaceNum];
		MXFaceInfo.MXFaceInfoEx2Int(nFaceNum,bInfo,pFaceInfo);
		int nRet = m_dllFaceApi.nirLivenessDetect(pImage, nImgWidth, nImgHeight,nFaceNum,bInfo);
		MXFaceInfo.Int2MXFaceInfoEx(nFaceNum,bInfo,pFaceInfo);
		return nRet;
	}

	/**
	 * @author   chen.gs
	 * @category  检测人脸是否戴口罩
	 * @param     pImage        - 输入，近红外图像数据
	 * 			  nImgWidth  	- 输入，图像宽度
	 * 			  nImgHeight  	- 输入，图像高度
	 * 			  nFaceNum    	- 输入，人脸数
	 * 			  pFaceInfo 	- 输入/输出，人脸信息，活体分数通过MXFaceInfoEx结构体mask属性获取
	 * @return   0-成功，其他-失败
	 * */
	public int mxMaskDetect(byte[] pImage, int nWidth, int nHeight,
							int nFaceNum, MXFaceInfo[] pFaceInfo)
	{
		if (m_bInit!=true){
			return MXErrorCode.ERR_NO_INIT;
		}
		int[] bInfo = new int[MXFaceInfo.SIZE * nFaceNum];
		MXFaceInfo.MXFaceInfoEx2Int(nFaceNum,bInfo,pFaceInfo);
		int nRet = m_dllFaceApi.maskDetect(pImage, nWidth, nHeight,nFaceNum,bInfo);
		MXFaceInfo.Int2MXFaceInfoEx(nFaceNum,bInfo,pFaceInfo);
		return nRet;
	}

	/**
	 * @author   chen.gs
	 * @category 人脸特征提取,用于比对（戴口罩算法）
	 * @param    pImage       - 输入，RGB图像数据
	 * 			 nWidth       - 输入，图像宽度
	 * 			 nHeight      - 输入，图像高度
	 * 			 nFaceNum     - 输入，人脸个数
	 * 			 pFaceInfo    - 输入，人脸信息
	 * 			 pFaceFeature - 输出，人脸特征，特征长度*人脸个数
	 * @return   0-成功，其他-失败
	 * */
	public int mxMaskFeatureExtract(byte[] pImage, int nWidth, int nHeight,
									int nFaceNum, MXFaceInfo[] pFaceInfo, byte[] pFaceFeature)
	{
		if (m_bInit!=true){
			return MXErrorCode.ERR_NO_INIT;
		}
		int[] bInfo = new int[MXFaceInfo.SIZE * MXFaceInfo.iMaxFaceNum];
		MXFaceInfo.MXFaceInfoEx2Int(nFaceNum,bInfo,pFaceInfo);
		int nRet = m_dllFaceApi.maskFeatureExtract(pImage, nWidth, nHeight,nFaceNum, bInfo,pFaceFeature);
		if(nRet!=0){
			return MXErrorCode.ERR_FACE_EXTRACT;
		}
		return nRet;
	}


	/**
	 * @author   chen.gs
	 * @category 人脸特征提取,用于注册（戴口罩算法）
	 * @param    pImage       - 输入，RGB图像数据
	 * 			 nWidth       - 输入，图像宽度
	 * 			 nHeight      - 输入，图像高度
	 * 			 nFaceNum     - 输入，人脸个数
	 * 			 pFaceInfo    - 输入，人脸信息
	 * 			 pFaceFeature - 输出，人脸特征，特征长度*人脸个数
	 * @return   0-成功，其他-失败
	 * */
	public int mxMaskFeatureExtract4Reg(byte[] pImage, int nWidth, int nHeight,
										int nFaceNum, MXFaceInfo[] pFaceInfo, byte[] pFaceFeature)
	{
		if (m_bInit!=true){
			return MXErrorCode.ERR_NO_INIT;
		}
		int[] bInfo = new int[MXFaceInfo.SIZE * MXFaceInfo.iMaxFaceNum];
		MXFaceInfo.MXFaceInfoEx2Int(nFaceNum,bInfo,pFaceInfo);
		int nRet = m_dllFaceApi.maskFeatureExtract4Reg(pImage, nWidth, nHeight,nFaceNum, bInfo,pFaceFeature);
		if(nRet!=0){
			return MXErrorCode.ERR_FACE_EXTRACT;
		}
		return nRet;
	}

	/**
	 * @author   chen.gs
	 * @category 人脸特征比对（戴口罩算法）
	 * @param    pFaceFeatureA - 输入，人脸特征A
	 * 			 pFaceFeatureB - 输入，人脸特征B
	 * 			 fScore    - 输出，相似性度量值，0~1.0 ，越大越相似。
	 * @return   0-成功，其他-失败
	 * */
	public int mxMaskFeatureMatch(byte[] pFaceFeatureA,byte[] pFaceFeatureB,float[] fScore)
	{
		if (m_bInit!=true){
			return MXErrorCode.ERR_NO_INIT;
		}
		return m_dllFaceApi.maskFeatureMatch(pFaceFeatureA, pFaceFeatureB,fScore);
	}
}
