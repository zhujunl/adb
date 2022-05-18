package com.miaxis.face.view.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.MxRGBImage;
import com.miaxis.face.bean.PhotoFaceFeature;
import com.miaxis.face.bean.Record;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.event.CmdGetFingerDoneEvent;
import com.miaxis.face.event.CutDownEvent;
import com.miaxis.face.event.ReadCardEvent;
import com.miaxis.face.event.ResultEvent;
import com.miaxis.face.greendao.gen.RecordDao;
import com.miaxis.face.manager.CameraManager;
import com.miaxis.face.manager.FaceManager;
import com.miaxis.face.manager.FingerManager;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.util.MyUtil;
import com.miaxis.face.view.custom.RectSurfaceView;
import com.miaxis.face.view.custom.ResultView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zz.api.MXFaceInfoEx;

import java.util.Date;

import butterknife.BindView;

/**
 * @author ZJL
 * @date 2022/5/17 13:40
 * @des
 * @updateAuthor
 * @updateDes
 */
public class VerifyFragment extends BaseFragment{

    @BindView(R.id.tv_second)
    TextView tv_second;
    @BindView(R.id.sv_main)
    SurfaceView sv_main;
    @BindView(R.id.sv_preview_nir)
    SurfaceView sv_preview_nir;
    @BindView(R.id.rsv_rect)
    RectSurfaceView rsv_rect;
    @BindView(R.id.rv_result)
    ResultView rv_result;
    @BindView(R.id.tv_pass)
    TextView tv_pass;
    @BindView(R.id.fl_camera_root)
    FrameLayout fl_camera_root;

    private final String TAG="VerifyFragment";

    private Record record;
    private Bitmap cardimg=null;
    private EventBus eventbus;
    private RecordDao recordDao;
    private boolean comparFlag=true;
    private int verifyMode;

    public static VerifyFragment getInstance(boolean comparFlag){
        VerifyFragment verifyFragment=new VerifyFragment();
        verifyFragment.setComparFlag(comparFlag);
        return verifyFragment;
    }

    public static VerifyFragment getInstance(int verifyMode){
        VerifyFragment verifyFragment=new VerifyFragment();
        verifyFragment.setVerifyMode(verifyMode);
        return verifyFragment;
    }


    public VerifyFragment() {

    }

    @Override
    protected int initLayout() { return R.layout.fragment_preview; }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        eventbus=EventBus.getDefault();
        eventbus.register(this);
        recordDao = Face_App.getInstance().getDaoSession().getRecordDao();
        tv_second.bringToFront();
        CameraManager.getInstance().open(sv_main);
        powerControl(true);
        if (!comparFlag){
            rv_result.bringToFront();
            rv_result.clear();
            rv_result.setFingerMode(true);
            rv_result.setResultMessage("请按手指");
            rv_result.setVisibility(View.VISIBLE);
            record=new Record();
            initFingerDevice();
        }
    }

    private void powerControl(boolean enable) {
        Intent intent = new Intent("com.miaxis.power");
        intent.putExtra("type", 0x12);
        intent.putExtra("value", enable);
        getActivity().sendBroadcast(intent);
    }

    public void setComparFlag(boolean comparFlag) {
        this.comparFlag = comparFlag;
    }

    public void setVerifyMode(int verifyMode) {
        this.verifyMode = verifyMode;
    }

    private void setFaceView(final boolean clear){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rsv_rect.bringToFront();
                rsv_rect.setRootSize(fl_camera_root.getWidth(), fl_camera_root.getHeight());
                rsv_rect.setZoomRate((float) fl_camera_root.getWidth() / Constants.PRE_WIDTH);

                rv_result.bringToFront();
                if (clear){
                    rv_result.clear();
                }
                rv_result.showCardImage(cardimg);
                rv_result.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setFingerView(final boolean clear){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rv_result.bringToFront();
                if (clear){
                    rv_result.clear();
                }
                rv_result.setFingerMode(true);
                rv_result.showCardImage(cardimg);
                rv_result.setVisibility(View.VISIBLE);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN,priority = 1)
    public void onCardImgEvent(ReadCardEvent event){
        Log.e(TAG, "onCardImgEvent" +event.toString());
        cardimg=event.getFace();
        record=event.getRecord();
        int v=event.getVerifyMode();
        switch (v) {
            case Config.MODE_FACE_ONLY:
            case Config.MODE_ONE_FACE_FIRST:
            case Config.MODE_TWO_FACE_FIRST:
                CameraManager.getInstance().nir_open(sv_preview_nir);
                FaceManager.getInstance().startLoop();
                FaceManager.getInstance().setFaceHandleListener(faceListener);

                setFaceView(true);
                break;
            case Config.MODE_FINGER_ONLY:;
            case Config.MODE_ONE_FINGER_FIRST:
            case Config.MODE_TWO_FINGER_FIRST:
                initFingerDevice();

                setFingerView(true);
                break;

            default:
                break;
        }
    }

    @Subscribe(threadMode=ThreadMode.MAIN,priority = 2)
    public void onCutDownEvent(CutDownEvent event){
        tv_second.setVisibility(View.VISIBLE);
        tv_second.setText(String.valueOf(event.getTime()));
    }

    /**
     * 人脸
     * */

    FaceManager.OnFaceHandleListener faceListener=new FaceManager.OnFaceHandleListener() {
        @Override
        public void onFeatureExtract(MxRGBImage mxRGBImage, MXFaceInfoEx mxFaceInfoEx, byte[] feature) {
            if (cardimg!=null){
                onFaceVerify(mxRGBImage, mxFaceInfoEx, feature,cardimg);
            }
        }

        @Override
        public void onFaceDetect(int faceNum, MXFaceInfoEx[] faceInfoExes) {
            Log.e(TAG, "onFaceDetect");
            drawFaceRect(faceInfoExes, faceNum);
        }
    };

    public void drawFaceRect(MXFaceInfoEx[] faceInfo, int faceNum) {
        rsv_rect.drawRect(faceInfo, faceNum);
    }

    private void onFaceVerify(MxRGBImage mxRGBImage, MXFaceInfoEx mxFaceInfoEx, byte[] feature, final Bitmap bitmap) {
        PhotoFaceFeature cardFaceFeature = FaceManager.getInstance().getCardFaceFeatureByBitmapPosting(bitmap);
        float faceMatchScore = FaceManager.getInstance().matchFeature(feature, cardFaceFeature.getFaceFeature());
        Log.e(TAG, "onFaceVerify==" + faceMatchScore);
        final boolean result=faceMatchScore>0.7;
        Bitmap bit=null;
        byte[] fileImage = FaceManager.getInstance().imageEncode(mxRGBImage.getRgbImage(), mxRGBImage.getWidth(), mxRGBImage.getHeight());
        Bitmap faceBitmap = BitmapFactory.decodeByteArray(fileImage, 0, fileImage.length);
        float fw = Constants.pam * mxFaceInfoEx.width;
        float fh = Constants.pam * mxFaceInfoEx.height;
        int x=(int) Math.max(0,mxFaceInfoEx.x-fw);
        int y=(int) Math.max(0,mxFaceInfoEx.y-fh);
        int width=(int) Math.min(mxFaceInfoEx.width*(1+2* Constants.pam),faceBitmap.getWidth());
        int height=(int) Math.min(mxFaceInfoEx.height*(1+2*Constants.pam),faceBitmap.getHeight());
        Log.e("asd", "人脸比对中"+"_____人脸图片：x="+x+",y="+y+",width="+width+",height="+height);
        final Bitmap rectBitmap = Bitmap.createBitmap(faceBitmap, x, y,width, height);//截取
        FaceManager.getInstance().stopLoop();
        CameraManager.getInstance().nirClose();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rsv_rect.clearDraw();
                rv_result.setFaceResult(result);
                rv_result.showCameraImage(rectBitmap);
                rv_result.setResultMessage( result ? "人脸通过" : "人脸失败");
            }
        });
        if (true){
            Matrix matrix = new Matrix();
            matrix.setScale(0.5f, 0.5f);
            bit = Bitmap.createBitmap(faceBitmap, 0, 0, faceBitmap.getWidth(), faceBitmap.getHeight(), matrix, true);
        }else {
            bit=rectBitmap;
        }
        record.setFaceImgData(MyUtil.getBytesByBitmap(bit));
        FileUtil.saveRecordImg(record, getActivity());
        record.setCreateDate(new Date());
        record.setStatus( result ? "人脸通过" : "人脸失败");
        if (verifyMode==Config.MODE_FACE_ONLY||verifyMode==Config.MODE_TWO_FINGER_FIRST||verifyMode==Config.MODE_ONE_FACE_FIRST){
            recordDao.insert(record);
            eventbus.post(new ResultEvent(result?ResultEvent.FACE_SUCCESS:ResultEvent.FACE_FAIL, record));
        }else {
            initFingerDevice();
            setFingerView(false);
        }
    }


    /**
     * 指纹
     * */

    public void initFingerDevice() {
        Face_App.getInstance().getThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                FingerManager.getInstance().initDevice(fingerStatusListener);
            }
        });
        FingerManager.getInstance().setFingerListener(fingerReadListener);
    }

    public void releaseFingerDevice() {
        FingerManager.getInstance().setFingerListener(null);
    }

    private final FingerManager.OnFingerStatusListener fingerStatusListener = new FingerManager.OnFingerStatusListener() {
        @Override
        public void onFingerStatus(boolean result) {
            Log.e(TAG, "fingerStatusListener"  );
            if (result) {
                byte[] decode = record.getFinger0();
                byte[] decode2 = record.getFinger1();
                result = readFinger(decode, decode2);
                if (!result) {
                    result = readFinger(decode, decode2);
                }
            }
            if (!result) {
                FingerManager.getInstance().release();
            }
        }
    };

    private final FingerManager.OnFingerReadListener fingerReadListener = new FingerManager.OnFingerReadListener() {
        @Override
        public void onFingerRead(byte[] feature, Bitmap image) {
            Log.e(TAG, "fingerReadListener:  onFingerRead,采集"  );
            if (image==null) {
                SystemClock.sleep(1000);
                try {
                    readFinger(record.getFinger0(), record.getFinger1());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Log.e(TAG, "指纹采集成功"  );
                setFingerReadFile(feature, image);
            }

        }

        @Override
        public void onFingerReadComparison(byte[] feature, Bitmap image, final int state) {
            //            Timber.e("FingerRead:" + (feature == null) + "   " + (image == null) + "===结果" + state);
            Log.e(TAG, "fingerReadListener:  onFingerReadComparison,state==="+state  );
            if (image == null) {
                SystemClock.sleep(1000);
                try {
                    readFinger(record.getFinger0(), record.getFinger1());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (state == 0) {
                    Log.e(TAG, "指纹比对成功"  );
                    setFingerReadFile(feature, image);
                } else {
                    if (null != feature) {
                        Log.e(TAG, "指纹比对失败"  );
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rv_result.setFingerResult(state==0);
                        rv_result.setResultMessage(state==0 ? "指纹通过" : "指纹失败");
                    }
                });
                record.setCreateDate(new Date());
                record.setStatus( state==0 ? "指纹通过" : "指纹失败");
                if(verifyMode==Config.MODE_FINGER_ONLY||verifyMode==Config.MODE_ONE_FINGER_FIRST||verifyMode==Config.MODE_TWO_FACE_FIRST){
                    recordDao.insert(record);
                    eventbus.post(new ResultEvent(state==0?ResultEvent.FINGER_SUCCESS:ResultEvent.FINGER_FAIL, record));
                }else {
                    CameraManager.getInstance().nir_open(sv_preview_nir);
                    FaceManager.getInstance().startLoop();
                    FaceManager.getInstance().setFaceHandleListener(faceListener);
                    setFaceView(false);
                }
            }
        }

        private void setFingerReadFile(byte[] feature, Bitmap image) {
            if (image != null) {
                if (!comparFlag){
                    EventBus.getDefault().post(new CmdGetFingerDoneEvent(Base64.encodeToString(feature, Base64.DEFAULT)));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rv_result.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                FingerManager.getInstance().releaseDevice();
                FingerManager.getInstance().setFingerListener(null);
            } else {
                SystemClock.sleep(1000);
                try {
                    readFinger(record.getFinger0(), record.getFinger1());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private boolean readFinger(final byte[] bytes, final byte[] bytes2) {
        String device = FingerManager.getInstance().deviceInfo();
        if (TextUtils.isEmpty(device)) {
            Log.e(TAG, "readFinger:  未找到指纹设备"  );
            return false;
        } else {
            Log.e(TAG, "readFinger:  请按压手指"  );
            FingerManager.getInstance().setFingerListener(fingerReadListener);
            Face_App.getInstance().getThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                if (!comparFlag) {
                    FingerManager.getInstance().readFinger(2);//指纹采集
                } else {
                    FingerManager.getInstance().redFingerComparison(bytes, bytes2);//指纹比对
                }
                }
            });
            return true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventbus.unregister(this);
        powerControl(false);
        releaseFingerDevice();
    }
}