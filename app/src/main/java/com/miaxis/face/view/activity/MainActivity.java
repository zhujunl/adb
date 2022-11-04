package com.miaxis.face.view.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.smdt.SmdtManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.ivsign.android.IDCReader.IDCReaderSDK;
import com.miaxis.face.R;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.LocalFeature;
import com.miaxis.face.bean.Record;
import com.miaxis.face.bean.WhiteItem;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.event.BtReadCardEvent;
import com.miaxis.face.event.CmdGetFingerEvent;
import com.miaxis.face.event.CmdIdCardDoneEvent;
import com.miaxis.face.event.CmdIdCardEvent;
import com.miaxis.face.event.CmdShutterEvent;
import com.miaxis.face.event.CmdShutterPhotoEvent;
import com.miaxis.face.event.DrawRectEvent;
import com.miaxis.face.event.HasCardEvent;
import com.miaxis.face.event.LoadProgressEvent;
import com.miaxis.face.event.NoCardEvent;
import com.miaxis.face.event.ResultEvent;
import com.miaxis.face.event.TimeChangeEvent;
import com.miaxis.face.event.ToastEvent;
import com.miaxis.face.greendao.gen.RecordDao;
import com.miaxis.face.greendao.gen.WhiteItemDao;
import com.miaxis.face.manager.MyActivityManager;
import com.miaxis.face.receiver.TimeReceiver;
import com.miaxis.face.service.UpLoadRecordService;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.util.LogUtil;
import com.miaxis.face.util.MyUtil;
import com.miaxis.face.util.YuvUtil;
import com.miaxis.face.view.custom.ContentLoadingDialog;
import com.miaxis.face.view.custom.ResultLayout;
import com.miaxis.face.view.fragment.AdvertiseDialog;
import com.miaxis.face.view.fragment.AlertDialog;
import com.zkteco.android.IDReader.IDPhotoHelper;
import com.zkteco.android.IDReader.WLTService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.zz.api.MXFaceAPI;
import org.zz.api.MXFaceInfo;
import org.zz.idcard_hid_driver.IdCardDriver;
import org.zz.jni.mxImageLoad;
import org.zz.jni.mxImageTool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.miaxis.face.constant.Constants.CP_WIDTH;
import static com.miaxis.face.constant.Constants.GET_CARD_ID;
import static com.miaxis.face.constant.Constants.GPIO_INTERVAL;
import static com.miaxis.face.constant.Constants.LEFT_VOLUME;
import static com.miaxis.face.constant.Constants.LOOP;
import static com.miaxis.face.constant.Constants.MAX_FACE_NUM;
import static com.miaxis.face.constant.Constants.NO_CARD;
import static com.miaxis.face.constant.Constants.PIC_HEIGHT;
import static com.miaxis.face.constant.Constants.PIC_WIDTH;
import static com.miaxis.face.constant.Constants.PRE_HEIGHT;
import static com.miaxis.face.constant.Constants.PRE_WIDTH;
import static com.miaxis.face.constant.Constants.PRIORITY;
import static com.miaxis.face.constant.Constants.RIGHT_VOLUME;
import static com.miaxis.face.constant.Constants.SOUND_RATE;
import static com.miaxis.face.constant.Constants.ZOOM_HEIGHT;
import static com.miaxis.face.constant.Constants.ZOOM_WIDTH;
import static com.miaxis.face.constant.Constants.mFingerDataSize;
import static com.miaxis.face.constant.Constants.zoomRate;

public class MainActivity extends BaseActivity implements SurfaceHolder.Callback, Camera.PreviewCallback, AMapLocationListener, WeatherSearch.OnWeatherSearchListener, ServiceConnection {

    long at1;

    private static final boolean WRITE_TIME = false;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_wel_msg)
    TextView tvWelMsg;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_weather)
    TextView tvWeather;
    @BindView(R.id.sv_main)
    SurfaceView svMain;
    @BindView(R.id.sv_rect)
    SurfaceView svRect;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.rv_result)
    ResultLayout rvResult;
    @BindColor(R.color.white)
    int white;
    @BindView(R.id.tv_pass)
    TextView tvPass;
    @BindView(R.id.iv_record)
    ImageView ivRecord;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.iv_import_from_u)
    ImageView ivImportFromU;
    @BindView(R.id.tv_second)
    TextView tvSecond;

    private Record mRecord;
    private int mCurSoundId;

    private Camera mCamera;
    private SurfaceHolder shMain;
    private SurfaceHolder shRect;

    private SoundPool soundPool;
    private Map<Integer, Integer> soundMap;

    private MXFaceAPI mxFaceAPI;
    private IdCardDriver idCardDriver;          // 身份证
    private mxImageLoad dtload;                 // 加载图像
    private org.zz.jni.mxImageTool mxImageTool;
    public AMapLocationClient mLocationClient;
    private SmdtManager smdtManager;
    private EventBus eventBus;
    private TimeReceiver timeReceiver;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();  //用来进行特征提取的线程池

    private boolean isExtractWorking;
    private boolean detectFlag;
    private boolean extractFlag;
    private boolean matchFlag;
    private boolean readIdFlag = false;
    private boolean noCardFlag = false;
    private boolean monitorFlag = true;
    private boolean localFlag = false;
    private boolean continuePlaySoundFlag = true;
    private boolean humanInductionFlag = false;
    private volatile boolean onlyReadCard = false;

    private byte[] idFaceFeature;               // 身份证照片 人脸特征
    private byte[] curFaceFeature;
    private byte[] curCameraImg;
    private MXFaceInfo curFaceInfo;

    private double latitude;
    private double longitude;
    private String location;

    private Config config;
    private RecordDao recordDao;

    private static final String TAG = "MainActivity";
    private long lastCameraCallBackTime = 9999999999999L;
    private long noActionSecond = 0;

    private List<WhiteItem> whiteItemList;
    private List<LocalFeature> localFeatureList;
    private ReadIdThread readIdThread;

    private ContentLoadingDialog loadingDialog;
    private Subscription mSubscription;
    private int max;
    private WhiteItemDao whiteItemDao;

    private int mState = 0;         // 记录点击次数
    private long firstTime = 0;
    private int toType;             // 0 SettingActivity   1 RecordActivity
    private final Byte lock1 = 1;
    private final Byte lock2 = 2;
    private final Byte cameraLock = 3;
    private AdvertiseDialog advertiseDialog;
	private boolean advertiseLock = true;
    private ProgressDialog progressDialog;
    private ReadSecondThread readSecondThread;
    private int ORIENTATION=0;
    private boolean usbActive=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Constants.VERSION) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_verify_860s);
        }
        ButterKnife.bind(this);
        initWindow();
        initData();
        initView();
        initSurface();
        initAMapSDK();
        initTimeReceiver();
        startMonitor();
    }

    private void initData() {

        mxFaceAPI = Face_App.getMxAPI();
        idCardDriver = new IdCardDriver(this);
        smdtManager = SmdtManager.create(this);
        dtload = new mxImageLoad();
        mxImageTool = new mxImageTool();
        eventBus = EventBus.getDefault();
        eventBus.register(this);


        soundPool = new SoundPool(21, AudioManager.STREAM_MUSIC, 0);
        soundMap = new HashMap<>();
        soundMap.put(Constants.SOUND_SUCCESS, soundPool.load(this, R.raw.success, 1));
        soundMap.put(Constants.SOUND_FAIL, soundPool.load(this, R.raw.fail, 1));

        soundMap.put(Constants.PLEASE_PRESS, soundPool.load(this, R.raw.please_press, 1));
        soundMap.put(Constants.SOUND_OR, soundPool.load(this, R.raw.sound_or, 1));
        soundMap.put(Constants.SOUND_OTHER_FINGER, soundPool.load(this, R.raw.please_press_finger, 1));
        soundMap.put(Constants.SOUND_VALIDATE_FAIL, soundPool.load(this, R.raw.validate_fail, 1));

        soundMap.put(Constants.SOUND_PUT_CARD, soundPool.load(this, R.raw.put_card, 1));

        soundMap.put(Constants.FINGER_RIGHT_0, soundPool.load(this, R.raw.finger_right_0, 1));
        soundMap.put(Constants.FINGER_RIGHT_1, soundPool.load(this, R.raw.finger_right_1, 1));
        soundMap.put(Constants.FINGER_RIGHT_2, soundPool.load(this, R.raw.finger_right_2, 1));
        soundMap.put(Constants.FINGER_RIGHT_3, soundPool.load(this, R.raw.finger_right_3, 1));
        soundMap.put(Constants.FINGER_RIGHT_4, soundPool.load(this, R.raw.finger_right_4, 1));
        soundMap.put(Constants.FINGER_LEFT_0, soundPool.load(this, R.raw.finger_left_0, 1));
        soundMap.put(Constants.FINGER_LEFT_1, soundPool.load(this, R.raw.finger_left_1, 1));
        soundMap.put(Constants.FINGER_LEFT_2, soundPool.load(this, R.raw.finger_left_2, 1));
        soundMap.put(Constants.FINGER_LEFT_3, soundPool.load(this, R.raw.finger_left_3, 1));
        soundMap.put(Constants.FINGER_LEFT_4, soundPool.load(this, R.raw.finger_left_4, 1));
    }

    private void initView() {
        progressDialog = new ProgressDialog(MainActivity.this);
        advertiseDialog = new AdvertiseDialog();
        advertiseDialog.setListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                noActionSecond = 0;
				restartCamera();
                advertiseDialog.dismiss();
                return false;
            }
        });
        loadingDialog = new ContentLoadingDialog();
        loadingDialog.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.dismiss();
                mSubscription.cancel();
                mSubscription = null;
            }
        });
        llTop.bringToFront();
        svRect.setZOrderOnTop(true);
        rvResult.bringToFront();
        tvSecond.bringToFront();
        if (Constants.VERSION){
            smdtManager.smdtSetStatusBar(this, false);
        }else {
            Face_App.getInstance().sendBroadcast(Constants.MOLD_STATUS,-1,false);
            Face_App.getInstance().sendBroadcast(Constants.MOLD_NAV,-1,false);
        }
    }

    private void initSurface() {
        shMain = svMain.getHolder();
        shMain.addCallback(this);
        shMain.setFormat(SurfaceHolder.SURFACE_TYPE_NORMAL);
        shRect = svRect.getHolder();
        shRect.setFormat(PixelFormat.TRANSLUCENT);
    }

    private void initAMapSDK() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setInterval(1000 * 60 * 5);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    private void initTimeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        timeReceiver = new TimeReceiver();
        registerReceiver(timeReceiver, filter);
        onTimeEvent(null);
    }
	
	private void restartCamera() {
        synchronized (cameraLock) {
            if (Constants.VERSION) {
                smdtManager.smdtSetGpioValue(2, true);
            }else {
                Face_App.getInstance().sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_CAMERA, true);
            }
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            openCamera();
            try {
                Thread.sleep(900);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            noActionSecond = 0;
            lastCameraCallBackTime = System.currentTimeMillis();
            monitorFlag = true;
        }
    }

    private void openCamera() {
        try {
            for (int i = 0; i < 5; i++) {
                if (mCamera==null){
                    try {
                        mCamera = Camera.open(0);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (mCamera!=null){
                        break;
                    }
                    SystemClock.sleep(500);
                }
            }
            Camera.Parameters parameters = mCamera.getParameters();
            //            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            int maxWidth = 0;
            int maxHeight = 0;
            for (Camera.Size size : supportedPreviewSizes) {
                maxWidth = Math.max(size.width, maxWidth);
                maxHeight = Math.max(size.height, maxHeight);
            }
            ORIENTATION = maxWidth * maxHeight >= (200 * 10000) ? 0 : (Constants.VERSION?180:0);//处理摄像头，500W不需要旋转

            parameters.setPreviewSize(PRE_WIDTH, PRE_HEIGHT);
            parameters.setPictureSize(PIC_WIDTH, PIC_HEIGHT);
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(ORIENTATION);
            mCamera.setPreviewDisplay(shMain);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();

        } catch (Exception e) {
            LogUtil.writeLog("打开摄像头异常" + e.getMessage());
        }
    }

    private void closeCamera() {
        try {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } else {
                Log.e(TAG, "mCamera == null");
            }
        } catch (Exception e) {
            LogUtil.writeLog("关闭摄像头异常" + e.getMessage());
        }
    }

    private void startReadId() {
        if (readIdThread == null) {
            readIdThread = new ReadIdThread();
            readIdThread.start();
        }
    }

    private void startMonitor() {
        Thread monitorThread = new MonitorThread();
        monitorThread.start();
    }

    private void playSound(int soundID) {
        continuePlaySoundFlag = false;
        soundPool.stop(mCurSoundId);
        mCurSoundId = soundPool.play(soundMap.get(soundID), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        lastCameraCallBackTime = System.currentTimeMillis();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        closeCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        lastCameraCallBackTime = System.currentTimeMillis();
        if (picFlag){
            picFlag=false;
            Camera.Parameters parameters = camera.getParameters();
            int width = parameters.getPreviewSize().width;
            int height = parameters.getPreviewSize().height;
            YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
            byte[] bytes = out.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(ORIENTATION);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            eventBus.post(new CmdShutterPhotoEvent(MyUtil.bitmapTo64(bitmap)));
            return;
        }
        if (!detectFlag) {
            return;
        }
        int[] pFaceNum = new int[1];
        pFaceNum[0] = MAX_FACE_NUM;
        MXFaceInfo[] pFaceBuffer = new MXFaceInfo[MAX_FACE_NUM];
        for (int i = 0; i < MAX_FACE_NUM; i++) {
            pFaceBuffer[i] = new MXFaceInfo();
        }
        byte[] rotateData =ORIENTATION==180? YuvUtil.rotateYUV420Degree180(data, PRE_WIDTH, PRE_HEIGHT):data;
        byte[] rgbData = new byte[PRE_WIDTH * PRE_HEIGHT * 3];
        mxImageTool.YUV2RGB(rotateData, PRE_WIDTH, PRE_HEIGHT, rgbData);
        int re;
        byte[] zoomedRgbData = new byte[ZOOM_WIDTH * ZOOM_HEIGHT * 3];
        re = mxImageTool.Zoom(rgbData, PRE_WIDTH, PRE_HEIGHT, 3, ZOOM_WIDTH, ZOOM_HEIGHT, zoomedRgbData);

        if (re != 1) {
            Log.e(TAG, "mxImageTool.Zoom = " + re);
        }
        synchronized (lock2) {
            long t1 = System.currentTimeMillis();
            //            re = mxFaceAPI.mxDetectFaceYUV(rotateData, PRE_WIDTH, PRE_HEIGHT, pFaceNum, pFaceBuffer);
            re = mxFaceAPI.mxDetectFace(zoomedRgbData, ZOOM_WIDTH, ZOOM_HEIGHT, pFaceNum, pFaceBuffer);
            Log.e(TAG, "mxDetectFace 耗时：" + (System.currentTimeMillis() - t1));
        }
        if (re == 0 && pFaceNum[0] > 0) {
            eventBus.post(new DrawRectEvent(pFaceNum[0], pFaceBuffer));
            if (!isExtractWorking && extractFlag) {
                isExtractWorking = true;
                ExtractAndMatch matchRunnable = new ExtractAndMatch(zoomedRgbData, pFaceBuffer);
                executorService.submit(matchRunnable);
            }
        } else {
            eventBus.post(new DrawRectEvent(0, null));
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDrawRectEvent(DrawRectEvent e) {
        Canvas canvas = shRect.lockCanvas(null);
        if (canvas == null) {
            return;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (e.getFaceNum() != 0) {
            drawFaceRect(e.getFaceInfos(), canvas, e.getFaceNum());
        }
        shRect.unlockCanvasAndPost(canvas);
    }

    @SuppressLint("CheckResult")
    @Subscribe(threadMode = ThreadMode.POSTING, priority = 100)
    public void onResultEvent(ResultEvent e) {
        recordDao = Face_App.getInstance().getDaoSession().getRecordDao();
        if (noCardFlag && !localFlag) {
            eventBus.cancelEventDelivery(e);
            return;
        }
        Record record = e.getRecord();
        switch (e.getResult()) {
            case ResultEvent.FACE_SUCCESS:
                if (config.getVerifyMode() == Config.MODE_FACE_ONLY
                        || config.getVerifyMode() == Config.MODE_ONE_FACE_FIRST
                        || config.getVerifyMode() == Config.MODE_ONE_FINGER_FIRST) {
                    record.setStatus("人脸通过");
                } else if (config.getVerifyMode() == Config.MODE_TWO_FINGER_FIRST) {
                    record.setStatus("人脸、指纹通过");
                } else if (config.getVerifyMode() == Config.MODE_LOCAL_FEATURE) {
                    record.setStatus("人脸通过");
                } else {
                    return;
                }
                playSound(Constants.SOUND_SUCCESS);
                break;
            case ResultEvent.FINGER_SUCCESS:
                if (config.getVerifyMode() == Config.MODE_FINGER_ONLY
                        || config.getVerifyMode() == Config.MODE_ONE_FINGER_FIRST
                        || config.getVerifyMode() == Config.MODE_ONE_FACE_FIRST) {
                    record.setStatus("指纹通过");
                } else if (config.getVerifyMode() == Config.MODE_TWO_FACE_FIRST) {
                    record.setStatus("人脸、指纹通过");
                } else if (config.getVerifyMode() == Config.MODE_TWO_FINGER_FIRST) {
                    detectFlag = true;
                    extractFlag = true;
                    matchFlag = true;
                    return;
                } else {
                    return;
                }
                playSound(Constants.SOUND_SUCCESS);
                break;
            case ResultEvent.FACE_FAIL:
                if (config.getVerifyMode() == Config.MODE_FACE_ONLY
                        || config.getVerifyMode() == Config.MODE_ONE_FINGER_FIRST) {
                    record.setStatus("人脸不通过");
                } else if (config.getVerifyMode() == Config.MODE_TWO_FINGER_FIRST
                        || config.getVerifyMode() == Config.MODE_TWO_FACE_FIRST) {
                    record.setStatus("人脸不通过");
                } else if (config.getVerifyMode() == Config.MODE_LOCAL_FEATURE) {
                    record.setStatus("人脸不通过");
                } else {
                    return;
                }
                playSound(Constants.SOUND_FAIL);
                break;
            case ResultEvent.FINGER_FAIL:
                if (config.getVerifyMode() == Config.MODE_FINGER_ONLY
                        || config.getVerifyMode() == Config.MODE_ONE_FACE_FIRST) {
                    record.setStatus("指纹不通过");
                    playSound(Constants.SOUND_FAIL);
                } else if (config.getVerifyMode() == Config.MODE_TWO_FINGER_FIRST) {
                    if (TextUtils.isEmpty(record.getFinger0()) || TextUtils.isEmpty(record.getFinger1())) {
                        detectFlag = true;
                        extractFlag = true;
                        matchFlag = true;
                        return;
                    }
                    record.setStatus("指纹不通过");
                    playSound(Constants.SOUND_FAIL);
                } else if (config.getVerifyMode() == Config.MODE_TWO_FACE_FIRST) {
                    if (TextUtils.isEmpty(record.getFinger0()) || TextUtils.isEmpty(record.getFinger1())) {
                        record.setStatus("人脸通过");
                        playSound(Constants.SOUND_SUCCESS);
                    } else {
                        record.setStatus("指纹不通过");
                        playSound(Constants.SOUND_FAIL);
                    }
                } else {
                    detectFlag = true;
                    extractFlag = true;
                    matchFlag = true;
                    return;
                }
                break;
            case ResultEvent.WHITE_LIST_FAIL:
                record.setStatus("白名单检验失败");
                playSound(Constants.SOUND_FAIL);
                break;
            case ResultEvent.BLACK_LIST_FAIL:
                record.setStatus("黑名单检验失败");
                playSound(Constants.SOUND_FAIL);
                break;
            case ResultEvent.VERIFY_FINGER:
                if (TextUtils.isEmpty(record.getFinger0()) && TextUtils.isEmpty(record.getFinger1())) {
                    return;
                }
                if (record.getFingerPosition0() >= 11
                        && record.getFingerPosition0() <= 20
                        && record.getFingerPosition1() >= 11
                        && record.getFingerPosition1() <= 20) {
                    playSound(Constants.PLEASE_PRESS, record.getFingerPosition1(), Constants.SOUND_OR, record.getFingerPosition0());
                } else {
                    playSound(Constants.SOUND_OTHER_FINGER);
                }
                return;
            case ResultEvent.VALIDATE_FAIL:
                playSound(Constants.SOUND_VALIDATE_FAIL);
                record.setStatus("身份证过期");
                break;
            default:
                return;
        }
        record.setCreateDate(new Date());
        record.setDevsn(MyUtil.getSerialNumber());
        record.setBusEntity(config.getOrgName());
        record.setLocation(location);
        record.setLatitude(latitude + "");
        record.setLongitude(longitude + "");
        FileUtil.saveRecordImg(record, this);
        recordDao.insert(record);
        if (config.getNetFlag()) {
            UpLoadRecordService.startActionUpLoad(this, record, config);
        }
    }

    /* 处理 时间变化 事件， 实时更新时间*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeEvent(TimeChangeEvent e) {
        DateFormat dateFormat = new SimpleDateFormat("E  yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String date = dateFormat.format(new Date());
        String time = timeFormat.format(new Date());
        tvTime.setText(time);
        tvDate.setText(date);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoCardEvent(NoCardEvent e) {
        Log.e("===", "onNoCardEvent");
        readIdFlag = false;
        tvSecond.setVisibility(View.GONE);
        detectFlag = false;
        extractFlag = false;
        matchFlag = false;
        idFaceFeature = null;
        curFaceFeature = null;
        closeLed();
        onDrawRectEvent(new DrawRectEvent(0, null));
        continuePlaySoundFlag = false;
//            soundPool.stop(mCurSoundId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHasCardEvent(HasCardEvent e) {
        tvPass.setVisibility(View.GONE);
        if (advertiseDialog.isAdded() || advertiseDialog.isVisible()) {
			restartCamera();
            advertiseDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConfig();
		noActionSecond = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        monitorFlag = false;
        readIdFlag = false;
        localFlag = false;
        humanInductionFlag = false;
//        if (countDownTimer != null){
//            countDownTimer.cancel();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
        unregisterReceiver(timeReceiver);
//        if (countDownTimer != null){
//            countDownTimer.cancel();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Constants.RESULT_CODE_FINISH) {
                finish();
            }
        }
    }

    /* 画人脸框 */
    private void drawFaceRect(MXFaceInfo[] faceInfos, Canvas canvas, int len) {
        float[] startArrayX = new float[len];
        float[] startArrayY = new float[len];
        float[] stopArrayX = new float[len];
        float[] stopArrayY = new float[len];
        if(ORIENTATION==180){
            for (int i = 0; i < len; i++) {
                startArrayX[i] = (CP_WIDTH - faceInfos[i].x * zoomRate);
                startArrayY[i] = (faceInfos[i].y * zoomRate);
                stopArrayX[i] = (CP_WIDTH - faceInfos[i].x * zoomRate - faceInfos[i].width * zoomRate);
                stopArrayY[i] = (faceInfos[i].y * zoomRate + faceInfos[i].height * zoomRate);
            }
        }else {
            for (int i = 0; i < len; i++) {
                startArrayX[i] = (faceInfos[i].x * zoomRate);
                startArrayY[i] = (faceInfos[i].y * zoomRate);
                stopArrayX[i] = (faceInfos[i].x * zoomRate + faceInfos[i].width * zoomRate);
                stopArrayY[i] = (faceInfos[i].y * zoomRate + faceInfos[i].height * zoomRate);
            }
        }
        canvasDrawLine(canvas, len, startArrayX, startArrayY, stopArrayX, stopArrayY);
    }

    /* 画线 */
    private void canvasDrawLine(Canvas canvas, int iNum, float[] startArrayX, float[] startArrayY, float[] stopArrayX, float[] stopArrayY) {
        int iLen = 50;
        Paint mPaint = new Paint();
        mPaint.setColor(white);
        float startX, startY, stopX, stopY;
        for (int i = 0; i < iNum; i++) {
            startX = startArrayX[i];
            startY = startArrayY[i];
            stopX = stopArrayX[i];
            stopY = stopArrayY[i];
            mPaint.setStrokeWidth(6);// 设置画笔粗细
            if (ORIENTATION==180){
                canvas.drawLine(startX, startY, startX - iLen, startY, mPaint);
                canvas.drawLine(stopX + iLen, startY, stopX, startY, mPaint);
                canvas.drawLine(startX, startY, startX, startY + iLen, mPaint);
                canvas.drawLine(startX, stopY - iLen, startX, stopY, mPaint);
                canvas.drawLine(stopX, stopY, stopX, stopY - iLen, mPaint);
                canvas.drawLine(stopX, startY + iLen, stopX, startY, mPaint);
                canvas.drawLine(stopX, stopY, stopX + iLen, stopY, mPaint);
                canvas.drawLine(startX - iLen, stopY, startX, stopY, mPaint);
            }else {
                canvas.drawLine(startX, startY, startX + iLen, startY, mPaint);
                canvas.drawLine(stopX - iLen, startY, stopX, startY, mPaint);
                canvas.drawLine(startX, startY, startX, startY + iLen, mPaint);
                canvas.drawLine(startX, stopY - iLen, startX, stopY, mPaint);
                canvas.drawLine(stopX, stopY, stopX, stopY - iLen, mPaint);
                canvas.drawLine(stopX, startY + iLen, stopX, startY, mPaint);
                canvas.drawLine(stopX, stopY, stopX - iLen, stopY, mPaint);
                canvas.drawLine(startX + iLen, stopY, startX, stopY, mPaint);

            }
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                latitude = aMapLocation.getLatitude();
                longitude = aMapLocation.getLongitude();
                location = aMapLocation.getAddress();
                queryWeather(aMapLocation.getCity());
            }
        } else {
            tvWeather.setText("无天气信息");
        }
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        if (i == 1000) {
            if (localWeatherLiveResult != null && localWeatherLiveResult.getLiveResult() != null) {
                LocalWeatherLive weatherLive = localWeatherLiveResult.getLiveResult();
                tvWeather.setText(String.format("%s%s℃", weatherLive.getWeather(), weatherLive.getTemperature()));
            } else {
                tvWeather.setText("无天气信息");
            }
        } else {
            tvWeather.setText("无天气信息");
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {
    }

    private void queryWeather(String city) {
        WeatherSearchQuery mQuery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        WeatherSearch mWeatherSearch = new WeatherSearch(this);
        mWeatherSearch.setOnWeatherSearchListener(this);
        mWeatherSearch.setQuery(mQuery);
        mWeatherSearch.searchWeatherAsyn(); //异步搜索
    }

    private void openLed() {
        if (!Constants.VERSION){
            Face_App.getInstance().sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_LED,true);
            return;
        }
        try {
            Thread.sleep(GPIO_INTERVAL);
            smdtManager.smdtSetGpioValue(3,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeLed() {
        if (!Constants.VERSION){
            Face_App.getInstance().sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_LED,false);
            return;
        }
        try {
            Thread.sleep(GPIO_INTERVAL);
            smdtManager.smdtSetGpioValue(3,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 解析身份证id 字符串 */
    private String getCardIdStr(byte[] cardId) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cardId.length; i++) {
            sb.append(String.format("%02x", cardId[i]));
        }
        String data = sb.toString();
        String cardIdStr = data.substring(0, 16);
        String errorCode = data.substring(16, 20);
        if (TextUtils.equals(errorCode, "9000")) {
            return cardIdStr;
        } else {
            return "";
        }
    }

    private String isGreenCard(byte[] bCardInfo) {
        byte[] id_isGreen = new byte[2];
        id_isGreen[0] = bCardInfo[248];
        id_isGreen[1] = bCardInfo[249];
        return MyUtil.unicode2String(id_isGreen).trim();
    }

    private Bitmap getBitmap2(byte[] wlt){
        WLTService dw = new WLTService();
        byte[] buffer = new byte[38556];
        int result = dw.wlt2Bmp(wlt, buffer);
        if (result==1) {
            return IDPhotoHelper.Bgr2Bitmap(buffer);
        }
        return null;
    }

    /* 读身份证 */
    private void readCard() throws Exception {
        byte[] bCardFullInfo = new byte[256 + 1024 + 1024];
        long t1 = System.currentTimeMillis();
        int re = idCardDriver.mxReadCardFullInfo(bCardFullInfo);
        long t2 = System.currentTimeMillis();
        if (WRITE_TIME) {
            LogUtil.writeLog("读卡耗时：" + (t2 - t1));
        }
        String type = isGreenCard(bCardFullInfo);
        if (re == 1) {
            if ("".equals(type)) {
                analysisIdCardInfo(bCardFullInfo);
            } else if ("I".equals(type)) {
                analysisGreenCard(bCardFullInfo);
            } else if ("J".equals(type)) {
                analysiGATCardInfo(bCardFullInfo);
            }
        } else if (re == 0) {
            if ("".equals(type)) {
                analysisIdCardInfo(bCardFullInfo);
            } else if ("I".equals(type)) {
                analysisGreenCard(bCardFullInfo);
            } else if ("J".equals(type)) {
                analysiGATCardInfo(bCardFullInfo);
            }
            byte[] bFingerData0 = new byte[mFingerDataSize];
            byte[] bFingerData1 = new byte[mFingerDataSize];
            int iLen = 256 + 1024;
            System.arraycopy(bCardFullInfo, iLen, bFingerData0, 0, bFingerData0.length);
            iLen += 512;
            System.arraycopy(bCardFullInfo, iLen, bFingerData1, 0, bFingerData1.length);
            mRecord.setFingerPosition0(bFingerData0[5]);
            mRecord.setFinger0(Base64.encodeToString(bFingerData0, Base64.DEFAULT));
            mRecord.setFingerPosition1(bFingerData1[5]);
            mRecord.setFinger1(Base64.encodeToString(bFingerData1, Base64.DEFAULT));
        } else {
            throw new Exception("读卡失败");
        }
        eventBus.post(new ResultEvent(ResultEvent.ID_PHOTO, mRecord));
    }

    /* 解析身份证信息 */
    private void analysisIdCardInfo(byte[] bCardInfo) {
        byte[] id_Name = new byte[30]; // 姓名
        byte[] id_Sex = new byte[2]; // 性别 1为男 其他为女
        byte[] id_Rev = new byte[4]; // 民族
        byte[] id_Born = new byte[16]; // 出生日期
        byte[] id_Home = new byte[70]; // 住址
        byte[] id_Code = new byte[36]; // 身份证号
        byte[] _RegOrg = new byte[30]; // 签发机关
        byte[] id_ValidPeriodStart = new byte[16]; // 有效日期 起始日期16byte 截止日期16byte
        byte[] id_ValidPeriodEnd = new byte[16];
        byte[] id_NewAddr = new byte[36]; // 预留区域
        byte[] id_pImage = new byte[1024]; // 图片区域
        int iLen = 0;
		mRecord.setType(" ");
        System.arraycopy(bCardInfo, iLen, id_Name, 0, id_Name.length);
        iLen = iLen + id_Name.length;
        mRecord.setName(MyUtil.unicode2String(id_Name).trim());

        System.arraycopy(bCardInfo, iLen, id_Sex, 0, id_Sex.length);
        iLen = iLen + id_Sex.length;

        if (id_Sex[0] == '1') {
            mRecord.setSex("男");
        } else {
            mRecord.setSex("女");
        }

        System.arraycopy(bCardInfo, iLen, id_Rev, 0, id_Rev.length);
        iLen = iLen + id_Rev.length;
        int iRev = Integer.parseInt(MyUtil.unicode2String(id_Rev));
        mRecord.setRace(Constants.FOLK[iRev - 1]);

        System.arraycopy(bCardInfo, iLen, id_Born, 0, id_Born.length);
        iLen = iLen + id_Born.length;
        mRecord.setBirthday(MyUtil.unicode2String(id_Born));

        System.arraycopy(bCardInfo, iLen, id_Home, 0, id_Home.length);
        iLen = iLen + id_Home.length;
        mRecord.setAddress(MyUtil.unicode2String(id_Home).trim());

        System.arraycopy(bCardInfo, iLen, id_Code, 0, id_Code.length);
        iLen = iLen + id_Code.length;
        mRecord.setCardNo(MyUtil.unicode2String(id_Code).trim());

        System.arraycopy(bCardInfo, iLen, _RegOrg, 0, _RegOrg.length);
        iLen = iLen + _RegOrg.length;
        mRecord.setRegOrg(MyUtil.unicode2String(_RegOrg).trim());

        System.arraycopy(bCardInfo, iLen, id_ValidPeriodStart, 0, id_ValidPeriodStart.length);
        iLen = iLen + id_ValidPeriodStart.length;
        System.arraycopy(bCardInfo, iLen, id_ValidPeriodEnd, 0, id_ValidPeriodEnd.length);
        iLen = iLen + id_ValidPeriodEnd.length;
        String validateStart = MyUtil.unicode2String(id_ValidPeriodStart).trim();
        String validateEnd = MyUtil.unicode2String(id_ValidPeriodEnd).trim();
        mRecord.setValidate(validateStart + "-" + validateEnd);

        System.arraycopy(bCardInfo, iLen, id_NewAddr, 0, id_NewAddr.length);
        iLen = iLen + id_NewAddr.length;
        System.arraycopy(bCardInfo, iLen, id_pImage, 0, id_pImage.length);
        Bitmap bitmap = getBitmap2(id_pImage);
        if (bitmap != null) {
            mRecord.setCardImgData(MyUtil.getBytesByBitmap(bitmap));
            String cardImgFilePath = FileUtil.getAvailableImgPath(this) + File.separator + mRecord.getCardNo() + "_" + mRecord.getName() + System.currentTimeMillis() + ".bmp";
            MyUtil.saveBitmap(cardImgFilePath, bitmap);
            mRecord.setCardImg(cardImgFilePath);
        } else {
            LogUtil.writeLog("decodeIdPhoto failed");
        }

    }

    /* 解析身份证信息 */
    private void analysiGATCardInfo(byte[] bCardInfo) {
        byte[] id_Name = new byte[30]; // 姓名
        byte[] id_Sex = new byte[2]; // 性别 1为男 其他为女
        byte[] id_Rev = new byte[4]; // 预留区
        byte[] id_Born = new byte[16]; // 出生日期
        byte[] id_Home = new byte[70]; // 住址
        byte[] id_Code = new byte[36]; // 身份证号
        byte[] id_RegOrg = new byte[30]; // 签发机关
        byte[] id_ValidPeriodStart = new byte[16]; // 有效日期 起始日期16byte 截止日期16byte
        byte[] id_ValidPeriodEnd = new byte[16];
//        byte[] id_NewAddr = new byte[36]; // 预留区域
        byte[] id_PassNum = new byte[18];
        byte[] id_IssueNum = new byte[4];
        byte[] id_NewAddr = new byte[14];
        byte[] id_pImage = new byte[1024]; // 图片区域
        int iLen = 0;
        mRecord.setType("J");
        System.arraycopy(bCardInfo, iLen, id_Name, 0, id_Name.length);
        iLen = iLen + id_Name.length;
        mRecord.setName(MyUtil.unicode2String(id_Name).trim());

        System.arraycopy(bCardInfo, iLen, id_Sex, 0, id_Sex.length);
        iLen = iLen + id_Sex.length;

        if (id_Sex[0] == '1') {
            mRecord.setSex("男");
        } else {
            mRecord.setSex("女");
        }

        System.arraycopy(bCardInfo, iLen, id_Rev, 0, id_Rev.length);
        iLen = iLen + id_Rev.length;
//        int iRev = Integer.parseInt(MyUtil.unicode2String(id_Rev));
        mRecord.setRace("");

        System.arraycopy(bCardInfo, iLen, id_Born, 0, id_Born.length);
        iLen = iLen + id_Born.length;
        mRecord.setBirthday(MyUtil.unicode2String(id_Born));

        System.arraycopy(bCardInfo, iLen, id_Home, 0, id_Home.length);
        iLen = iLen + id_Home.length;
        mRecord.setAddress(MyUtil.unicode2String(id_Home).trim());

        System.arraycopy(bCardInfo, iLen, id_Code, 0, id_Code.length);
        iLen = iLen + id_Code.length;
        mRecord.setCardNo(MyUtil.unicode2String(id_Code).trim());

        System.arraycopy(bCardInfo, iLen, id_RegOrg, 0, id_RegOrg.length);
        iLen = iLen + id_RegOrg.length;
        mRecord.setRegOrg(MyUtil.unicode2String(id_RegOrg).trim());

        System.arraycopy(bCardInfo, iLen, id_ValidPeriodStart, 0, id_ValidPeriodStart.length);
        iLen = iLen + id_ValidPeriodStart.length;
        System.arraycopy(bCardInfo, iLen, id_ValidPeriodEnd, 0, id_ValidPeriodEnd.length);
        iLen = iLen + id_ValidPeriodEnd.length;
        String validateStart = MyUtil.unicode2String(id_ValidPeriodStart).trim();
        String validateEnd = MyUtil.unicode2String(id_ValidPeriodEnd).trim();
        mRecord.setValidate(validateStart + "-" + validateEnd);

        System.arraycopy(bCardInfo, iLen, id_PassNum, 0, id_PassNum.length);
        iLen = iLen + id_PassNum.length;
        mRecord.setPassNum(MyUtil.unicode2String(id_PassNum).trim());

        System.arraycopy(bCardInfo, iLen, id_IssueNum, 0, id_IssueNum.length);
        iLen = iLen + id_IssueNum.length;
        mRecord.setIssueNum(MyUtil.unicode2String(id_IssueNum).trim());

        System.arraycopy(bCardInfo, iLen, id_NewAddr, 0, id_NewAddr.length);
        iLen = iLen + id_NewAddr.length;
        System.arraycopy(bCardInfo, iLen, id_pImage, 0, id_pImage.length);
        Bitmap bitmap = getBitmap2(id_pImage);
        if (bitmap != null) {
            mRecord.setCardImgData(MyUtil.getBytesByBitmap(bitmap));
            String cardImgFilePath = FileUtil.getAvailableImgPath(this) + File.separator + mRecord.getCardNo() + "_" + mRecord.getName() + System.currentTimeMillis() + ".bmp";
            MyUtil.saveBitmap(cardImgFilePath, bitmap);
            mRecord.setCardImg(cardImgFilePath);
        } else {
            LogUtil.writeLog("decodeIdPhoto failed");
        }

    }

    private void analysisGreenCard(byte[] bCardInfo) throws Exception {
        byte[] id_Name = new byte[120];    // 姓名
        byte[] id_Sex = new byte[2];      // 性别 1为男 其他为女
        byte[] id_cardNo = new byte[30];     // 永久居留证号码
        byte[] id_nation = new byte[6];      // 国籍或所在地区代码
        byte[] id_chinese_name = new byte[30];     // 中文姓名
        byte[] id_start_date = new byte[16];     // 证件签发日期
        byte[] id_end_date = new byte[16];     // 证件终止日期
        byte[] id_birthday = new byte[16];     // 出生日期
        byte[] id_version = new byte[4];      // 证件版本号
        byte[] id_reg_org = new byte[8];      // 当前申请受理机关代码
        byte[] id_type = new byte[2];      // 证件类型标识
        byte[] id_remark = new byte[6];      // 预留项
        byte[] id_pImage = new byte[1024];   // 照片
        mRecord.setType("I");
        int iLen = 0;
        System.arraycopy(bCardInfo, iLen, id_Name, 0, id_Name.length);
        iLen = iLen + id_Name.length;
        mRecord.setName(MyUtil.unicode2String(id_Name));

        System.arraycopy(bCardInfo, iLen, id_Sex, 0, id_Sex.length);
        iLen = iLen + id_Sex.length;

        if (id_Sex[0] == '1') {
            mRecord.setSex("男");
        } else {
            mRecord.setSex("女");
        }

        System.arraycopy(bCardInfo, iLen, id_cardNo, 0, id_cardNo.length);
        iLen += id_cardNo.length;
        mRecord.setCardNo(MyUtil.unicode2String(id_cardNo));

        System.arraycopy(bCardInfo, iLen, id_nation, 0, id_nation.length);
        iLen += id_nation.length;
        mRecord.setRace(MyUtil.unicode2String(id_nation));

        System.arraycopy(bCardInfo, iLen, id_chinese_name, 0, id_chinese_name.length);
        iLen = iLen + id_chinese_name.length;
        mRecord.setAddress(MyUtil.unicode2String(id_chinese_name));

        System.arraycopy(bCardInfo, iLen, id_start_date, 0, id_start_date.length);
        iLen = iLen + id_start_date.length;
        System.arraycopy(bCardInfo, iLen, id_end_date, 0, id_end_date.length);
        iLen = iLen + id_end_date.length;
        String validateStart = MyUtil.unicode2String(id_start_date).trim();
        String validateEnd = MyUtil.unicode2String(id_end_date).trim();
        mRecord.setValidate(validateStart + "-" + validateEnd);

        System.arraycopy(bCardInfo, iLen, id_birthday, 0, id_birthday.length);
        iLen = iLen + id_birthday.length;
        mRecord.setBirthday(MyUtil.unicode2String(id_birthday));

        System.arraycopy(bCardInfo, iLen, id_version, 0, id_version.length);
        iLen = iLen + id_version.length;
//        curId.setVersion(CommonUtil.unicode2String(id_version));

        System.arraycopy(bCardInfo, iLen, id_reg_org, 0, id_reg_org.length);
        iLen += id_reg_org.length;
        mRecord.setRegOrg(MyUtil.unicode2String(id_reg_org));

        System.arraycopy(bCardInfo, iLen, id_type, 0, id_type.length);
        iLen += id_type.length;

        System.arraycopy(bCardInfo, iLen, id_remark, 0, id_remark.length);
        iLen += id_remark.length;

        System.arraycopy(bCardInfo, iLen, id_pImage, 0, id_pImage.length);
        Bitmap bitmap = getBitmap2(id_pImage);
        if (bitmap != null) {
            mRecord.setCardImgData(MyUtil.getBytesByBitmap(bitmap));
            String cardImgFilePath = FileUtil.getAvailableImgPath(this) + File.separator + mRecord.getCardNo() + "_" + mRecord.getName() + System.currentTimeMillis() + ".bmp";
            MyUtil.saveBitmap(cardImgFilePath, bitmap);
            mRecord.setCardImg(cardImgFilePath);
        } else {
            LogUtil.writeLog("decodeIdPhoto failed");
        }

    }

    /* 提取特征 */
    private byte[] extractFeature(byte[] pImage, int width, int height, MXFaceInfo faceInfo) {
        synchronized (lock1) {
            byte[] feature = new byte[mxFaceAPI.mxGetFeatureSize()];
            detectFlag = false;
            int re = mxFaceAPI.mxFeatureExtract(pImage, width, height, 1, new MXFaceInfo[]{faceInfo}, feature);
            detectFlag = true;
            if (re == 0) {
                return feature;
            }
            return null;
        }
    }

    /* 提取特征 */
    private byte[] extractFeatureYUV(byte[] pImage, int width, int height, MXFaceInfo faceInfo) {
        synchronized (lock1) {
            byte[] feature = new byte[mxFaceAPI.mxGetFeatureSize()];
            detectFlag = false;
            int re = mxFaceAPI.mxFeatureExtract(pImage, width, height, 1, new MXFaceInfo[]{faceInfo}, feature);
            detectFlag = true;
            if (re == 0) {
                return feature;
            }
            return null;
        }
    }

    /* 获取身份证照片特征 */
    private void getIdPhotoFeature() {
        /** 加载图像 */
        int re = -1;
        int[] oX = new int[1];
        int[] oY = new int[1];
        // 获取图像大小
        String availablePath = FileUtil.getAvailableImgPath(this);
        File f = new File(mRecord.getCardImg());
        if (!f.exists()) {
            LogUtil.writeLog("身份证照片不存在！ 路径：" + f.getAbsolutePath());
            return;
        }
        re = dtload.LoadFaceImage(f.getPath(), null, null, oX, oY);
        if (re != 1) {
            LogUtil.writeLog("第一次加载图片失败 dtload.LoadFaceImage = " + re + "_" + mRecord.getCardImg());
            return;
        }
        byte[] pGrayBuff = new byte[oX[0] * oY[0]];
        byte[] pRGBBuff = new byte[oX[0] * oY[0] * 3];
        re = dtload.LoadFaceImage(f.getPath(), pRGBBuff, pGrayBuff, oX, oY);
        if (re != 1) {
            LogUtil.writeLog("第二次加载图片失败 dtload.LoadFaceImage = " + re + "_" + mRecord.getCardImg());
            return;
        }
        /** 检测人脸 */
        int[] pFaceNum = new int[1];
        pFaceNum[0] = 1;                //身份证照片只可能检测到一张人脸
        MXFaceInfo[] pFaceBuffer = new MXFaceInfo[1];
        pFaceBuffer[0] = new MXFaceInfo();
        int iX = oX[0];
        int iY = oY[0];
        detectFlag = false;
        synchronized (lock2) {
            long t1 = System.currentTimeMillis();
            re = mxFaceAPI.mxDetectFace(pRGBBuff, iX, iY, pFaceNum, pFaceBuffer);
            long t2 = System.currentTimeMillis();
            if (WRITE_TIME) {
                LogUtil.writeLog("检测身份证照片人脸耗时：" + (t2 - t1));
            }
        }
        detectFlag = true;
        if (re != 0) {
            LogUtil.writeLog("mxDetectFace = " + re + "_" + mRecord.getCardImg());
            return;
        }
        long t1 = System.currentTimeMillis();
        idFaceFeature = extractFeature(pRGBBuff, 102, 126, pFaceBuffer[0]);
        if (WRITE_TIME) {
            LogUtil.writeLog("提取身份证照片耗时：" + (System.currentTimeMillis() - t1));
        }
    }

    /* 读卡的完成后，和预提取的特征进行比对 */
    private void preExtractAndMatch() {
        Log.e(TAG, "preExtractAndMatch");
        if (curFaceFeature != null && idFaceFeature != null) {
            float[] fScore = new float[1];
            long t1 = System.currentTimeMillis();
            int re = mxFaceAPI.mxFeatureMatch(idFaceFeature, curFaceFeature, fScore);
            long t2 = System.currentTimeMillis();
            if (WRITE_TIME) {
                LogUtil.writeLog("比对耗时：" + (t2 - t1) + " 得分：" + fScore[0]);
                LogUtil.writeLog("一次比对 总耗时：" + (t2 - at1));
                eventBus.post(new ToastEvent("一次比对 总耗时：" + (t2 - at1)));
            }
            if (re == 0 && fScore[0] >= config.getPassScore()) {
//                mRecord.setFaceImgData(MyUtil.getYUV2JPEGBytes(curCameraImg, mCamera.getParameters().getPreviewFormat()));
				mRecord.setFaceImgData(curCameraImg);
                matchFlag = false;
                extractFlag = false;
                eventBus.post(new ResultEvent(ResultEvent.FACE_SUCCESS, mRecord, curFaceInfo));
                if (config.getVerifyMode() == Config.MODE_TWO_FACE_FIRST) {
                    eventBus.post(new ResultEvent(ResultEvent.VERIFY_FINGER, mRecord, curFaceInfo));
                }
            } else {
                extractFlag = true;
                matchFlag = true;
            }
            curFaceFeature = null;
            curCameraImg = null;
        } else {
            extractFlag = true;
            matchFlag = true;
        }
    }

    @OnClick(R.id.iv_record)
    void onRecordClick() {
        toType = 1;
        etPwd.setVisibility(View.VISIBLE);
		advertiseLock = false;
        btnCancel.setVisibility(View.VISIBLE);
        btnConfirm.setVisibility(View.VISIBLE);
        tvWelMsg.setVisibility(View.GONE);
    }

    @OnClick(R.id.tv_title)
    void onTitleClick() {
        long secondTime = System.currentTimeMillis();
        if ((secondTime - firstTime) > 1500) {
            mState = 0;
        } else {
            mState++;
        }
        firstTime = secondTime;
        if (mState > 4) {
            toType = 0;
            etPwd.setVisibility(View.VISIBLE);
			advertiseLock = false;
            btnCancel.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.VISIBLE);
            tvWelMsg.setVisibility(View.GONE);
        } else {
            onCancel();
        }
    }

    @OnClick(R.id.btn_cancel)
    void onCancel() {
        if (mState > 4){
            mState=0;
        }
        etPwd.setText(null);
        etPwd.setVisibility(View.GONE);
		noActionSecond = 0;
        advertiseLock = true;
        btnCancel.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.GONE);
        tvWelMsg.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_confirm)
    void onConfirm() {
        String pwd = etPwd.getText().toString();
        if (pwd.equals(config.getPassword())) {
            etPwd.setText(null);
            etPwd.setVisibility(View.GONE);
			noActionSecond = 0;
            advertiseLock = true;
            btnCancel.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
            tvWelMsg.setVisibility(View.VISIBLE);
            if (toType == 0) {
                startActivity(new Intent(this, SettingActivity.class));
            } else if (toType == 1) {
                startActivity(new Intent(this, RecordActivity.class));
            }
        } else {
            Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
            etPwd.setText(null);
        }
    }

    private int decodeIdPhoto(byte[] wltdata) {
        String filepath = FileUtil.getAvailableWltPath(this);
        try {
            long t1 = System.currentTimeMillis();
            int ret = IDCReaderSDK.wltInit(filepath);
            long t2 = System.currentTimeMillis();
            if (ret != 0) {
                return -1;
            } else {
                ret = IDCReaderSDK.unpack(wltdata);
                long t3 = System.currentTimeMillis();
                Log.e(TAG, "解码身份证照片： wltInit " + (t2 - t1) + " unpack " + (t3 - t2));
                return ret != 1 ? -2 : 0;
            }
        } catch (Exception e) {
            LogUtil.writeLog("decodeIdPhoto exception" + e.getMessage());
            return -3;
        }
    }

    private boolean loadLocalFeature() {
        localFeatureList = Face_App.getInstance().getDaoSession().getLocalFeatureDao().loadAll();
        for (int i = 0; i < localFeatureList.size(); i++) {
            LocalFeature local = localFeatureList.get(i);
            try {
                byte[] bFeature = FileUtil.readFileToBytes(local.getFilePath());
                local.setFeature(bFeature);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @SuppressLint("CheckResult")
    private void checkConfig() {
        final ProgressDialog pdCheck = new ProgressDialog(this);
        pdCheck.setCancelable(false);
        pdCheck.setMessage("正在加载设置...");
        pdCheck.show();
        Observable
                .create(new ObservableOnSubscribe<Config>() {
                    @Override
                    public void subscribe(ObservableEmitter<Config> e) throws Exception {
                        config = Face_App.getInstance().getDaoSession().getConfigDao().loadByRowId(1L);
                        if (config.getVerifyMode() == Config.MODE_LOCAL_FEATURE) {
                            readIdFlag = false;
                            localFlag = true;
                            if (!loadLocalFeature()) {
                                throw new Exception("加载本地特征失败！");
                            }
                            matchFlag = true;
                            detectFlag = true;
                            extractFlag = true;
                            humanInductionFlag = true;
                        }
                        if (config.getWhiteFlag()) {
                            whiteItemList = Face_App.getInstance().getDaoSession().getWhiteItemDao().loadAll();
                        }
                        e.onNext(config);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Config>() {
                    @Override
                    public void accept(Config config) {
//                        if (config.getVerifyMode() == Config.MODE_LOCAL_FEATURE) {
//                            tvPass.setVisibility(View.GONE);
//                            startCount();
//                            startHumanInduction();
//                        } else {
//                            tvPass.setVisibility(View.VISIBLE);
                            readIdFlag = false;
                            extractFlag = false;
//                        }
                        if (config.getQueryFlag()) {
                            ivRecord.setVisibility(View.VISIBLE);
                        } else {
                            ivRecord.setVisibility(View.GONE);
                        }
                        if (config.getWhiteFlag()) {
                            ivImportFromU.setVisibility(View.VISIBLE);
                        } else {
                            ivImportFromU.setVisibility(View.GONE);
                        }
                        tvWelMsg.setText(config.getBanner());
                        monitorFlag = true;
                        pdCheck.dismiss();
                        startReadId();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        pdCheck.setMessage(throwable.getMessage());
                        pdCheck.setCancelable(true);
                        startReadId();
                    }
                });
    }

//    private void startCount() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                while (localFlag) {
//                    try {
//                        Thread.sleep(6000);
//                        detectFlag = true;
//                        matchFlag = true;
//                        extractFlag = true;
//                        EventBus.getDefault().post(new NoCardEvent());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }).start();
//    }
//
//    private void startHumanInduction() {
//        Thread t = new HumanInductionThread();
//        t.start();
//    }

    @OnClick(R.id.iv_import_from_u)
    void onImportClicked() {
        whiteItemDao = Face_App.getInstance().getDaoSession().getWhiteItemDao();
        Flowable
                .create(new FlowableOnSubscribe<WhiteItem>() {
                    @Override
                    public void subscribe(FlowableEmitter<WhiteItem> e) throws Exception {
                        String whiteContent = FileUtil.readFromUSBPath(MainActivity.this, "白名单.txt");
                        if (TextUtils.isEmpty(whiteContent)) {
                            File whiteTxtFile = FileUtil.searchFileFromU(MainActivity.this, "白名单.txt");
                            if (whiteTxtFile != null) {
                                whiteContent = FileUtil.readFileToString(whiteTxtFile);
                            }
                        }
                        if (TextUtils.isEmpty(whiteContent)) {
                            throw new Exception("加载名单失败！请检查U盘和文件是否存在");
                        }
                        whiteContent = whiteContent.replace(" ", "");
                        String[] aWhites = whiteContent.split(",");
                        max = aWhites.length;
                        if (max > 0) {
                            whiteItemList.clear();
                            whiteItemDao.deleteAll();
                            EventBus.getDefault().post(new LoadProgressEvent(max, whiteItemList.size()));
                            for (String aWhite : aWhites) {
                                e.onNext(new WhiteItem(aWhite));
                            }
                        } else {
                            throw new Exception("加载名单失败！白名单内容为空，或格式错误");
                        }
                    }
                }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<WhiteItem>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1);
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(WhiteItem whiteItem) {
                        whiteItemList.add(whiteItem);
                        if (whiteItemList.size() == max) {
                            whiteItemDao.insertInTx(whiteItemList);
                        }
                        EventBus.getDefault().post(new LoadProgressEvent<>(max, whiteItemList.size()));
                        if (mSubscription != null) {
                            mSubscription.request(1);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        AlertDialog a = new AlertDialog();
                        a.setAdContent(t.getMessage());
                        a.show(getFragmentManager(), "a");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadProgressEvent(LoadProgressEvent<WhiteItem> e) {
        if (!monitorFlag) {     //用monitorFlag判断 MainActivity是否显示
            return;
        }
        if (mSubscription == null) {
            return;
        }
        if (loadingDialog != null && !loadingDialog.isAdded() && !loadingDialog.isVisible()) {
            loadingDialog.show(getFragmentManager(), "loading");
            getFragmentManager().executePendingTransactions();
            loadingDialog.setMessage("正在导入...");
            loadingDialog.setButtonName(R.string.cancel);
            loadingDialog.setCancelable(false);
        }
        if (e.getMax() == 0) {
            return;
        }
        loadingDialog.setMax(e.getMax());
        loadingDialog.setProgress(e.getProgress());
        if (e.getMax() == e.getProgress()) {
            loadingDialog.setMessage("导入完成！");
            loadingDialog.setButtonName(R.string.confirm);
            loadingDialog.setCancelable(true);
        }
    }

    /* 连续播放4段音频 提示按指纹的 指位*/
    private void playSound(final int soundId0, final int soundId1, final int soundId2, final int soundId3) {
        continuePlaySoundFlag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (continuePlaySoundFlag) {
                        mCurSoundId = soundPool.play(soundMap.get(soundId0), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
                        Thread.sleep(800);
                    }
                    if (continuePlaySoundFlag) {
                        mCurSoundId = soundPool.play(soundMap.get(soundId1), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
                        Thread.sleep(1000);
                    }
                    if (continuePlaySoundFlag) {
                        mCurSoundId = soundPool.play(soundMap.get(soundId2), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
                        Thread.sleep(800);
                    }
                    if (continuePlaySoundFlag) {
                        mCurSoundId = soundPool.play(soundMap.get(soundId3), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Toast.makeText(this, "onServiceConnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Toast.makeText(this, "onServiceDisconnected", Toast.LENGTH_SHORT).show();
    }

    /* 线程 循环读身份证id 读到id代表有身份证，开始比对流程， 读不到代表没有身份证 */
    class ReadIdThread extends Thread {
        @Override
        public void run() {
            byte[] lastCardId = null;
            byte[] curCardId;
            int re;
            while (true) {
                if (!readIdFlag) {
                    try {
                        if (!noCardFlag) {
                            eventBus.post(new NoCardEvent());
                        }
                        noCardFlag = true;
                        lastCardId = null;
                        if (!usbActive) {
                            usbActive=idCardDriver.mxGetDevNum()!=-1000;
                        }
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                curCardId = new byte[64];
                re = idCardDriver.mxReadCardId(curCardId);
                switch (re) {
                    case GET_CARD_ID:
                        noCardFlag = false;
                        openLed();
                        noActionSecond = 0;
                        if (!Arrays.equals(lastCardId, curCardId)) {
                            eventBus.post(new HasCardEvent());
                            at1 = System.currentTimeMillis();
                            mRecord = new Record();
                            try {
                                mRecord.setCardId(getCardIdStr(curCardId));
                                if (onlyReadCard) {
                                    readCard();
                                    playSound(Constants.SOUND_SUCCESS);
                                    eventBus.post(new CmdIdCardDoneEvent(mRecord));
                                } else {
                                    switch (config.getVerifyMode()) {
                                        case Config.MODE_FACE_ONLY:
                                            detectFlag = true;
                                            extractFlag = true;
                                            readCard();
                                            if (checkIsOutValidate()) {
                                                eventBus.post(new ResultEvent(ResultEvent.VALIDATE_FAIL, mRecord, null));
                                                break;
                                            }
                                            if (config.getWhiteFlag() && !checkInWhiteList(mRecord.getCardNo())) {
                                                eventBus.post(new ResultEvent(ResultEvent.WHITE_LIST_FAIL, mRecord, null));
                                                break;
                                            }
                                            getIdPhotoFeature();
                                            preExtractAndMatch();
                                            break;
                                        case Config.MODE_FINGER_ONLY:
                                            readCard();
                                            if (checkIsOutValidate()) {
                                                eventBus.post(new ResultEvent(ResultEvent.VALIDATE_FAIL, mRecord, null));
                                                break;
                                            }
                                            if (config.getWhiteFlag() && !checkInWhiteList(mRecord.getCardNo())) {
                                                eventBus.post(new ResultEvent(ResultEvent.WHITE_LIST_FAIL, mRecord, null));
                                                break;
                                            }
                                            eventBus.post(new ResultEvent(ResultEvent.VERIFY_FINGER, mRecord));
                                            break;
                                        case Config.MODE_ONE_FACE_FIRST:
                                            detectFlag = true;
                                            extractFlag = true;
                                            readCard();
                                            if (checkIsOutValidate()) {
                                                eventBus.post(new ResultEvent(ResultEvent.VALIDATE_FAIL, mRecord, null));
                                                break;
                                            }
                                            if (config.getWhiteFlag() && !checkInWhiteList(mRecord.getCardNo())) {
                                                eventBus.post(new ResultEvent(ResultEvent.WHITE_LIST_FAIL, mRecord, null));
                                                break;
                                            }
                                            getIdPhotoFeature();
                                            preExtractAndMatch();
                                            break;
                                        case Config.MODE_ONE_FINGER_FIRST:
                                            readCard();
                                            if (checkIsOutValidate()) {
                                                eventBus.post(new ResultEvent(ResultEvent.VALIDATE_FAIL, mRecord, null));
                                                break;
                                            }
                                            if (config.getWhiteFlag() && !checkInWhiteList(mRecord.getCardNo())) {
                                                eventBus.post(new ResultEvent(ResultEvent.WHITE_LIST_FAIL, mRecord, null));
                                                break;
                                            }
                                            getIdPhotoFeature();
                                            eventBus.post(new ResultEvent(ResultEvent.VERIFY_FINGER, mRecord));
                                            break;
                                        case Config.MODE_TWO_FACE_FIRST:
                                            detectFlag = true;
                                            extractFlag = true;
                                            readCard();
                                            if (checkIsOutValidate()) {
                                                eventBus.post(new ResultEvent(ResultEvent.VALIDATE_FAIL, mRecord, null));
                                                break;
                                            }
                                            if (config.getWhiteFlag() && !checkInWhiteList(mRecord.getCardNo())) {
                                                eventBus.post(new ResultEvent(ResultEvent.WHITE_LIST_FAIL, mRecord, null));
                                                break;
                                            }
                                            getIdPhotoFeature();
                                            preExtractAndMatch();
                                            break;
                                        case Config.MODE_TWO_FINGER_FIRST:
                                            readCard();
                                            if (checkIsOutValidate()) {
                                                eventBus.post(new ResultEvent(ResultEvent.VALIDATE_FAIL, mRecord, null));
                                                break;
                                            }
                                            if (config.getWhiteFlag() && !checkInWhiteList(mRecord.getCardNo())) {
                                                eventBus.post(new ResultEvent(ResultEvent.WHITE_LIST_FAIL, mRecord, null));
                                                break;
                                            }
                                            getIdPhotoFeature();
                                            eventBus.post(new ResultEvent(ResultEvent.VERIFY_FINGER, mRecord));
                                            break;
                                    }
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        }
                        lastCardId = curCardId;
                        break;
                    case NO_CARD:
                        if (!noCardFlag) {
                            eventBus.post(new NoCardEvent());
                        }
                        noCardFlag = true;
                        lastCardId = null;
//                        readIdFlag = false;
                        break;
                }
            }
        }
    }

    /* 线程 监控视频流回调 onPreviewFrame  是否有数据返回，设置时间内无数据返回 重启摄像头 */
    private class MonitorThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    noActionSecond++;
                    if (monitorFlag) {
                        long cur = System.currentTimeMillis();
                        if ((cur - lastCameraCallBackTime) >= config.getIntervalTime() * 1000) {
                            LogUtil.writeLog("开始修复视频卡顿");
                            closeCamera();
                            int re=0;
                            if (Constants.VERSION) {
                                re = smdtManager.smdtSetGpioValue(2, false);
                            }else {
                                Face_App.getInstance().sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_CAMERA, false);
                            }
                            LogUtil.writeLog("下电 re = " + re);
                            Thread.sleep(1000);
                            if (Constants.VERSION) {
                                re = smdtManager.smdtSetGpioValue(2, true);
                            }else {
                                Face_App.getInstance().sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_CAMERA, true);
                            }
                            LogUtil.writeLog("上电 re = " + re);
                            Thread.sleep(1000);
                            openCamera();
                            LogUtil.writeLog("修复视频卡顿结束");
                        }
                        if (config.getAdvertiseFlag() && advertiseLock && noActionSecond >= config.getAdvertiseDelayTime()) {
                            if (!advertiseDialog.isAdded() && AdvertiseDialog.isAdExist()) {
                                synchronized (cameraLock) {
                                    advertiseDialog.show(getFragmentManager(), "ad");
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (!advertiseDialog.isAdded()) {
                                        return;
                                    }
                                    shMain.addCallback(MainActivity.this);
                                    closeCamera();
                                    if (Constants.VERSION) {
                                        smdtManager.smdtSetGpioValue(2, false);
                                    }else {
                                        Face_App.getInstance().sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_CAMERA, false);
                                    }
                                    monitorFlag = false;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LogUtil.writeLog("修复视频卡顿线程 异常" + e.getMessage());
                }
            }
        }
    }

    /* 线程 人体感应线程 控制led灯开关 在本地特征比对模式下启用 */
    private class HumanInductionThread extends Thread {
        @Override
        public void run() {
            while (humanInductionFlag) {
                try {
                    Thread.sleep(GPIO_INTERVAL);
                    if (smdtManager.smdtReadGpioValue(1) == 1) {
                        openLed();
                    } else {
                        closeLed();
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    /* 线程 从视频流中提取特征并比对 */
    private class ExtractAndMatch implements Runnable {
        private byte[] pCameraData = null;
        private MXFaceInfo[] pFaceBuffer = null;

        ExtractAndMatch(byte[] pCameraData, MXFaceInfo[] pFaceBuffer) {
            this.pCameraData = pCameraData;
            this.pFaceBuffer = pFaceBuffer;
        }

        @Override
        public void run() {
            long tc = System.currentTimeMillis();
			int w = 0;
            int k = 0;
            for (int i = 0; i < pFaceBuffer.length; i++) {
                if (pFaceBuffer[i].width > w) {
                    w = pFaceBuffer[i].width;
                    k = i;
                }
            }
            curFaceInfo = pFaceBuffer[k];
            curFaceFeature = extractFeature(pCameraData, ZOOM_WIDTH, ZOOM_HEIGHT, curFaceInfo);
            if (WRITE_TIME) {
                LogUtil.writeLog("视频提取特征耗时：" + (System.currentTimeMillis() - tc));
            }
            byte[] bmpFaceData = new byte[ZOOM_WIDTH * ZOOM_HEIGHT * 3 + 10000];
            int[] bmpDataLen = new int[1];
            int re = mxImageTool.ImageEncode(pCameraData, ZOOM_WIDTH, ZOOM_HEIGHT,".jpg" ,bmpFaceData, bmpDataLen);
            if (re == 1) {
                curCameraImg = new byte[bmpDataLen[0]];
                System.arraycopy(bmpFaceData, 0, curCameraImg, 0, bmpDataLen[0]);
//                FileUtil.writeBytesToFile(curCameraImg, FileUtil.getAvailablePath(MainActivity.this), "ImageEncode.jpg");
//                Log.e(TAG, "====== ImageEncode ======");
            } else {
                curCameraImg = pCameraData;
            }
            extractFlag = false;
            if (curFaceFeature != null && matchFlag && !localFlag) {
                float[] fScore = new float[1];
                if (idFaceFeature != null) {
                    long t1 = System.currentTimeMillis();
                    re = mxFaceAPI.mxFeatureMatch(idFaceFeature, curFaceFeature, fScore);
                    long t2 = System.currentTimeMillis();
                    if (WRITE_TIME) {
                        LogUtil.writeLog("比对耗时：" + (t2 - t1));
                        LogUtil.writeLog("二次 比对总耗时：" + (t2 - at1));
                        eventBus.post(new ToastEvent("二次 比对总耗时：" + (t2 - at1)));
                    }
//                    mRecord.setFaceImgData(MyUtil.getYUV2JPEGBytes(curCameraImg, mCamera.getParameters().getPreviewFormat()));
                    mRecord.setFaceImgData(curCameraImg);
                    if (re == 0 && fScore[0] >= config.getPassScore()) {
                        eventBus.post(new ResultEvent(ResultEvent.FACE_SUCCESS, mRecord, curFaceInfo));
                        if (config.getVerifyMode() == Config.MODE_TWO_FACE_FIRST) {
                            eventBus.post(new ResultEvent(ResultEvent.VERIFY_FINGER, mRecord));
                        }
                    } else {
                        eventBus.post(new ResultEvent(ResultEvent.FACE_FAIL, mRecord, curFaceInfo));
                        if (config.getVerifyMode() == Config.MODE_ONE_FACE_FIRST) {
                            eventBus.post(new ResultEvent(ResultEvent.VERIFY_FINGER, mRecord));
                        }
                    }
                    matchFlag = false;
                    curFaceFeature = null;
                    curCameraImg = null;
                }
            } else if (localFlag && matchFlag) {
                float[] fScore = new float[1];
                for (int i = 0; i < localFeatureList.size(); i++) {
                    long t1 = System.currentTimeMillis();
                    re = mxFaceAPI.mxFeatureMatch(localFeatureList.get(i).getFeature(), curFaceFeature, fScore);
                    long t2 = System.currentTimeMillis();
                    Log.e(TAG, "比对耗时：" + (t2 - t1));
                    if (re == 0 && fScore[0] >= config.getPassScore()) {
                        Log.e("passFeature", localFeatureList.get(i).getName() + " _ " + localFeatureList.get(i).getFilePath());
                        mRecord = new Record();
//                        mRecord.setFaceImgData(MyUtil.getYUV2JPEGBytes(curCameraImg, mCamera.getParameters().getPreviewFormat()));
                        mRecord.setFaceImgData(curCameraImg);
                        eventBus.post(new ResultEvent(ResultEvent.FACE_SUCCESS, mRecord, curFaceInfo));
                        isExtractWorking = false;
                        return;
                    }
                }
                mRecord = new Record();
//                mRecord.setFaceImgData(MyUtil.getYUV2JPEGBytes(curCameraImg, mCamera.getParameters().getPreviewFormat()));
                mRecord.setFaceImgData(curCameraImg);
                eventBus.post(new ResultEvent(ResultEvent.FACE_FAIL, mRecord, curFaceInfo));
                matchFlag = false;
                curFaceFeature = null;
                curCameraImg = null;
            }
            isExtractWorking = false;
        }
    }

    /**
     * 检查身份证是否已经过期
     *
     * @return true - 已过期 false - 未过期
     */
    private boolean checkIsOutValidate() {
        try {
            SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd");
            Date validEndDate = myFmt.parse(mRecord.getValidate().split("-")[1]);
            return validEndDate.getTime() < System.currentTimeMillis();
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 检查身份证号码是否在白名单内
     *
     * @param cardNo 身份证号码
     * @return true - 在白名单内  false - 不在白名单内
     */
    private boolean checkInWhiteList(String cardNo) {
        for (WhiteItem item : whiteItemList) {
            if (item.getCardNo().equals(cardNo)) {
                return true;
            }
        }
        return false;
    }

    private class ReadSecondThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(200);
                readIdFlag = true;
                playSound(Constants.SOUND_PUT_CARD);
                for (int i = 15; i >= 0; i--) {
                    Thread.sleep(1000);
                    readSecond(i);
                }
                readIdFlag = false;
                onlyReadCard = false;
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    @SuppressLint("CheckResult")
    private void readSecond(int i) {
        Observable.just(i)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        tvSecond.setVisibility(View.VISIBLE);
                        tvSecond.setText("" + integer);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBtReadCardEvent(BtReadCardEvent e) {
        if (!MyActivityManager.getInstance().isCurrent(this)){
            return;
        }
        onNoCardEvent(null);
        if (readSecondThread == null || !readSecondThread.isAlive()) {
            readSecondThread = new ReadSecondThread();
            readSecondThread.start();
        } else {
            readSecondThread.interrupt();
            readSecondThread = new ReadSecondThread();
            readSecondThread.start();
        }
        onlyReadCard = false;
        if (advertiseDialog.isAdded()) {
            noActionSecond = 0;
            restartCamera();
            advertiseDialog.dismiss();
        }
    }
    private boolean picFlag=false;
    @Subscribe(threadMode = ThreadMode.POSTING )
    public void onCmdShutterEvent(CmdShutterEvent e) {
        if (!MyActivityManager.getInstance().isCurrent(this)){
            return;
        }
        if (advertiseDialog.isAdded()) {
            noActionSecond = 0;
            restartCamera();
            advertiseDialog.dismiss();
        }
        if (mCamera != null) {
            SystemClock.sleep(1000);
            picFlag=true;
//            mCamera.takePicture(
//                    new Camera.ShutterCallback() {
//                        @Override
//                        public void onShutter() {
//
//                        }
//                    },
//                    null
//                    ,
//                    new Camera.PictureCallback() {
//                        @Override
//                        public void onPictureTaken(byte[] bytes, Camera camera) {
//                            Matrix matrix = new Matrix();
//                            matrix.postRotate(ORIENTATION);
//                            Bitmap bmpFace = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                            bmpFace = Bitmap.createBitmap(bmpFace, 0, 0, bmpFace.getWidth(), bmpFace.getHeight(), matrix, true);
//                            eventBus.post(new CmdShutterPhotoEvent(MyUtil.bitmapTo64(bmpFace)));
//                        }
//                    }
//            );
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
    public void onCmdGetFingerEvent(CmdGetFingerEvent e) {
        if (!MyActivityManager.getInstance().isCurrent(this)){
            return;
        }
        playSound(Constants.SOUND_OTHER_FINGER);
        if (advertiseDialog.isAdded()) {
            noActionSecond = 0;
            restartCamera();
            advertiseDialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
    public void onCmdIdCardEvent(CmdIdCardEvent e) {
        if (!MyActivityManager.getInstance().isCurrent(this)){
            return;
        }
        onNoCardEvent(null);
        if (readSecondThread == null || !readSecondThread.isAlive()) {
            readSecondThread = new ReadSecondThread();
            readSecondThread.start();
        } else {
            readSecondThread.interrupt();
            readSecondThread = new ReadSecondThread();
            readSecondThread.start();
        }
        onlyReadCard = true;
        if (advertiseDialog.isAdded()) {
            noActionSecond = 0;
            restartCamera();
            advertiseDialog.dismiss();
        }
    }

}
