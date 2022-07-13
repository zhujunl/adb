package com.miaxis.face.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.jiangdg.usbcamera.UVCCameraHelper;
import com.miaxis.face.R;
import com.miaxis.face.adapter.PreviewPageAdapter;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.app.OnFragmentInteractionListener;
import com.miaxis.face.bean.Config;
import com.miaxis.face.constant.MXCamera;
import com.miaxis.face.constant.ZZResponse;
import com.miaxis.face.event.CmdSmDoneEvent;
import com.miaxis.face.event.CutDownEvent;
import com.miaxis.face.manager.CameraHelper;
import com.miaxis.face.util.BitmapUtils;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.util.MyUtil;
import com.miaxis.face.view.custom.PreviewPictureEntity;
import com.miaxis.face.view.custom.ZoomImageView;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.widget.CameraViewInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author ZJL
 * @date 2022/5/17 16:09
 * @des
 * @updateAuthor
 * @updateDes
 */
public class HightFragment extends BaseFragment implements CameraDialog.CameraDialogParent, CameraViewInterface.Callback {

    @BindView(R.id.uvc_view)
    public View uvcTextureView;
    @BindView(R.id.sBar)
    SeekBar sBar;
    @BindView(R.id.btn_screen)
    Button btnScreen;
    @BindView(R.id.btn_try_again)
    Button btnTryAgain;
    @BindView(R.id.rv_content)
    ListView rvContent;
    @BindView(R.id.tv_second)
    TextView tv_second;
    private Activity mActivity;

    private final String TAG = "HightFragment";
    private List<PreviewPictureEntity> pathList = new ArrayList<>();
    private PreviewPageAdapter mAdapter;
    private File mFilePath;
    private Config config;
    private EventBus eventbus;
    private OnFragmentInteractionListener mListener;

    private UVCCameraHelper mCameraHelper;
    private CameraViewInterface mUVCCameraView;
    private boolean isPreview;
    private boolean isRequest;
    public static final int HEIGHT_DEVICE_VID = 12936;
    public static final int HEIGHT_DEVICE_PID = 7119;

    public HightFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity= (Activity) context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_high;
    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        // step.2 register USB event broadcast
        if (mCameraHelper != null) {
            Log.d(TAG, "registerUSB");
            mCameraHelper.registerUSB();
        }
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mFilePath = new File(FileUtil.FACE_MAIN_PATH + File.separator + "height_camera");
        eventbus = EventBus.getDefault();
        eventbus.register(this);
        config = Face_App.getInstance().getDaoSession().getConfigDao().loadByRowId(1L);
        // step.1 initialize UVCCameraHelper
        mUVCCameraView = (CameraViewInterface) uvcTextureView;
        mUVCCameraView.setCallback(this);
        mCameraHelper = UVCCameraHelper.getInstance(2048, 1536);
        mCameraHelper.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_MJPEG);
        mCameraHelper.initUSBMonitor(mActivity, mUVCCameraView, listener);
        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ZZResponse<MXCamera> mxCamera = CameraHelper.getInstance().find(config.getSm());
                if (ZZResponse.isSuccess(mxCamera)) {
                    mxCamera.getData().setZoom(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != pathList && pathList.size() >= 3) {
                    Toast.makeText(mActivity, "最多只能拍摄3张照片", Toast.LENGTH_SHORT).show();
                    return;
                }
                String fileName = "sm" + System.currentTimeMillis() + ".jpg";
                File file = new File(mFilePath, fileName);
                String picPath = file.getPath();

                mCameraHelper.capturePicture(picPath, new AbstractUVCCameraHandler.OnCaptureListener() {
                    @Override
                    public void onCaptureResult(String path) {
                        Log.d(TAG, "Capture---》" + Thread.currentThread().getName());
                        if (TextUtils.isEmpty(path)) {
                            return;
                        }
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565; //图片颜色配置
                        options.inSampleSize = 2;
                        Bitmap bit = BitmapFactory.decodeFile(path,options);
                        //final Bitmap bit = BitmapUtils.getBitmap(path);
                        //Bitmap bit = MyUtil.compressBitmap(bit2,50);
                        if (bit != null) {
                            if (mActivity != null) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        PreviewPictureEntity entity = new PreviewPictureEntity("", bit);
                                        pathList.add(entity);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                        }

                });
            }
        });

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pathList.size() > 0) {
                    Bitmap bitmap = pathList.get(0).getBase64();
                    Matrix matrix = new Matrix();
                    matrix.setScale(0.99f, 0.99f);
                    Bitmap bit = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    eventbus.post(new CmdSmDoneEvent(MyUtil.bitmapTo64(bit)));
                    CameraHelper.getInstance().free();
                    Face_App.getInstance().getThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            mListener.showWaitDialog("正在上传中，请稍后");
                            SystemClock.sleep(1000);
                            try {
                                if(pathList.size()>0) {
                                    File file = new File(pathList.get(0).getPath());
                                    FileUtil.deleteDirWihtFile(file.getParentFile());
                                    mListener.dismissWaitDialog("上传成功");
                                    mListener.backToStack(0);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        mAdapter = new PreviewPageAdapter(mActivity, pathList, new PreviewPageAdapter.PreViewListner() {
            @Override
            public void onClick(int position) {
                PreviewPictureEntity str = mAdapter.getPathList().get(position);
                showBigPicture(str.getBase64());
            }

            @Override
            public void onLongClick(final int position) {
                new android.app.AlertDialog.Builder(getContext())
                        .setTitle("注意")
                        .setMessage("是否删除此图片")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PreviewPictureEntity entity = pathList.get(position);
                                pathList.remove(entity);
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .show();
            }
        });
        rvContent.setAdapter(mAdapter);
        tv_second.bringToFront();
    }


    private void showBigPicture(Bitmap path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View v = LayoutInflater.from(mActivity).inflate(R.layout.dialog_to_view_big_picture, null, false);
        ZoomImageView img = v.findViewById(R.id.img);
//        Glide.with(getContext()).load(new File(path)).into(img);
        img.setImageBitmap(path);
        builder.setView(v);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        eventbus.unregister(this);
        // step.3 unregister USB event broadcast
        if (mCameraHelper != null) {
            Log.d(TAG, "closeCamera");
            mCameraHelper.closeCamera();
            Log.d(TAG, "unregisterUSB");
            mCameraHelper.unregisterUSB();
        }
        CameraHelper.getInstance().free();
        pathList.clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mCameraHelper != null) {
            Log.d(TAG, "onStop---release");
            mCameraHelper.release();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCutDownEvent(CutDownEvent event) {
        tv_second.setVisibility(View.VISIBLE);
        tv_second.setText(String.valueOf(event.getTime()));
    }

    private void requestCurrHightDevicePermission() {
        List<DeviceInfo> infoList = getUSBDevInfo();
        if (infoList == null || infoList.isEmpty()) {
            Toast.makeText(HightFragment.this.getContext(), "Find devices failed.", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < infoList.size(); i++) {
            DeviceInfo deviceInfo = infoList.get(i);
            Log.d(TAG, "Device：PID_" + deviceInfo.getPID() + " & " + "VID_" + deviceInfo.getVID());
            if (deviceInfo.getPID() == HEIGHT_DEVICE_PID && deviceInfo.getVID() == HEIGHT_DEVICE_VID) {
                mCameraHelper.requestPermission(i);
                return;
            }
        }
    }


    private List<DeviceInfo> getUSBDevInfo() {
        if (mCameraHelper == null)
            return null;
        List<DeviceInfo> devInfos = new ArrayList<>();
        List<UsbDevice> list = mCameraHelper.getUsbDeviceList();
        //target:  vid==7119       pid==12936
        for (UsbDevice dev : list) {
            DeviceInfo info = new DeviceInfo();
            info.setPID(dev.getVendorId());
            info.setVID(dev.getProductId());
            devInfos.add(info);
        }
        return devInfos;
    }

    @Override
    public USBMonitor getUSBMonitor() {
        return mCameraHelper.getUSBMonitor();
    }

    @Override
    public void onDialogResult(boolean canceled) {

    }

    @Override
    public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
        Log.d(TAG, "onSurfaceCreated");
        if (!isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.startPreview(mUVCCameraView);
            isPreview = true;
        }
    }

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        Log.d(TAG, "onSurfaceDestroy");
        if (isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.stopPreview();
            isPreview = false;
        }
    }


    private UVCCameraHelper.OnMyDevConnectListener listener = new UVCCameraHelper.OnMyDevConnectListener() {

        @Override
        public void onAttachDev(UsbDevice device) {
            // request open permission
            Log.d(TAG, "onAttachDev");
            if (!isRequest) {
                isRequest = true;
                requestCurrHightDevicePermission();
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            // close camera
            Log.d(TAG, "onDettachDev");
            if (isRequest) {
                isRequest = false;
                mCameraHelper.closeCamera();
                //showShortMsg(device.getDeviceName() + " is out");
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            Log.d(TAG, "onConnectDev");
            if (!isConnected) {
                //showShortMsg("fail to connect,please check resolution params");
                isPreview = false;
            } else {
                isPreview = true;
                //showShortMsg("connecting");

            }
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            Log.d(TAG, "onDisConnectDev");
            //showShortMsg("disconnecting");
        }
    };
}
