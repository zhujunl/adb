package com.miaxis.face.view.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.miaxis.face.R;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.bean.Record;
import com.miaxis.face.event.CmdGetFingerDoneEvent;
import com.miaxis.face.event.ReadCardEvent;
import com.miaxis.face.greendao.gen.RecordDao;
import com.miaxis.face.manager.CameraManager;
import com.miaxis.face.manager.FingerManager;
import com.miaxis.face.view.custom.ResultView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * @author ZJL
 * @date 2022/5/16 14:10
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FingerFragment extends BaseFragment{


    @BindView(R.id.sv_main)
    SurfaceView sv_main;
    @BindView(R.id.rv_result)
    ResultView rv_result;

    private final String TAG="FingerFragment";
    private Record record;
    private Bitmap cardimg=null;
    private EventBus eventbus;
    private RecordDao recordDao;
    private boolean comparFlag=true;

    public static FingerFragment getInstance(boolean comparFlag){
        FingerFragment fingerFragment=new FingerFragment();
        fingerFragment.setComparFlag(comparFlag);
        return fingerFragment;
    }


    public FingerFragment() {

    }

    @Override
    protected int initLayout() { return R.layout.fragment_preview; }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        eventbus=EventBus.getDefault();
        eventbus.register(this);
        CameraManager.getInstance().open(sv_main,1);
        powerControl(true);
        Log.e(TAG, "comparFlag==" + comparFlag);
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

    @Subscribe(threadMode = ThreadMode.MAIN,priority = 1)
    public void onCardImgEvent(ReadCardEvent event){
        Log.e(TAG, "onCardImgEvent" );
        cardimg=event.getFace();
        record=event.getRecord();

        rv_result.bringToFront();
        rv_result.clear();
        rv_result.setFingerMode(true);
        rv_result.showCardImage(cardimg);
        rv_result.setVisibility(View.VISIBLE);


        initFingerDevice();
    }

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
//            Timber.e("FingerRead:" + (feature == null) + "   " + (image == null));
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
        public void onFingerReadComparison(byte[] feature, Bitmap image, int state) {
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
                    SystemClock.sleep(1000);
                    try {
                        readFinger(record.getFinger0(), record.getFinger1());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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


//                String base64Str = Base64.encodeToString(feature, Base64.NO_WRAP);
//                String[] strings = new String[]{base64Str};
//                FingerprintEntity fingerprintEntity = new FingerprintEntity();
//                fingerprintEntity.fingerprints = strings;
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
                        FingerManager.getInstance().readFinger(0);//指纹采集
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
