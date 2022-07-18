package com.miaxis.face.view.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.miaxis.face.R;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.event.CmdScanDoneEvent;
import com.miaxis.face.event.CutDownEvent;
import com.miaxis.face.view.activity.MainActivity2;
import com.wsm.hidlib.HIDManager;
import com.wsm.hidlib.callback.HIDDataListener;
import com.wsm.hidlib.callback.HIDOpenListener;
import com.wsm.hidlib.constant.ConnectCostant;
import com.wsm.hidlib.constant.FormatConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * @author ZJL
 * @date 2022/5/25 9:31
 * @desF
 * @updateAuthor
 * @updateDes
 */
public class ScanFragment extends BaseFragment {
    private final String TAG = "ScanFragment";

    @BindView(R.id.tv_second)
    TextView tv_second;
    @BindView(R.id.scanResult)
    TextView scanResult;

    private EventBus eventBus;
    private HIDOpenListener mHIDOpenListener;
    private HIDDataListener mHidDataListener;


    @Override
    protected int initLayout() {
        return R.layout.fragment_scan;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        eventBus=EventBus.getDefault();
        eventBus.register(this);
    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        initListener();
        initHID();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventBus.unregister(this);
    }

    @Subscribe(threadMode= ThreadMode.MAIN,priority = 2)
    public void onCutDownEvent(CutDownEvent event){
        tv_second.setVisibility(View.VISIBLE);
        tv_second.setText(String.valueOf(event.getTime()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanResult(CmdScanDoneEvent event){
        scanResult.setText("扫码结果："+event.getContent());
    }

    private void initListener() {
        //回调数据有关页面展示的请在主线程进行。
        mHIDOpenListener = new HIDOpenListener() {
            @Override
            public void openSuccess(final int openSuccessStatus) {
                Log.d(TAG," openSuccess");

            }

            @Override
            public void openError(int openErrorStatus) {
                if (openErrorStatus == ConnectCostant.USB_DISCONNECT) {
                    //USB断开
                    Log.d(TAG," 断开USB");
                    //txt_connect.setText("断开USB");
                    //Toast.makeText(MainActivity.this, "断开USB", Toast.LENGTH_LONG).show();
                }
                if (openErrorStatus == ConnectCostant.COMMUNICATION_CLOSE) {
                    //服务销毁
                    Log.d(TAG," 服务销毁");

                }
            }

        };

        mHidDataListener = new HIDDataListener() {
            @Override
            public void onDataReceived(byte status,String dataMessage) {
                if (!TextUtils.isEmpty(dataMessage)) {
                    String result = dataMessage;
                    //Log.d(TAG, "onDataReceived: length:" + dataMessage.length() + "   content:" + result);

                    Face_App.getInstance().getThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            SystemClock.sleep(1000);
                            //Log.e(TAG,"扫码:" + result);
                            eventBus.post(new CmdScanDoneEvent(result.trim()));
                            try {
                                MainActivity2 mainActivity2= (MainActivity2) ScanFragment.this.getActivity();
                                mainActivity2.showWaitDialog("正在上传中，请稍后");
                                SystemClock.sleep(1000);
                                mainActivity2.dismissWaitDialog("上传成功");
                                mainActivity2.backToStack(10);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }

            @Override
            public void onOriginalDataReceived(byte status,byte[] bytes, int length) {
//                String formatHexString = HexUtil.formatHexString(bytes);
                Log.d(TAG, "onOriginalDataReceived: " + length);

            }
        };


    }

    private void initHID() {
        HIDManager.getInstance().enableLog(true);
        HIDManager.getInstance().openHID(this.getActivity(), mHIDOpenListener, mHidDataListener);
        HIDManager.getInstance().setFormat(FormatConstant.FORMAT_GBK);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HIDManager.getInstance().closeHID();
        Log.d(TAG," onDestroy");
    }
}