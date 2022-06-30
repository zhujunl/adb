package com.miaxis.face.view.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.miaxis.face.R;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.app.OnFragmentInteractionListener;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.Record;
import com.miaxis.face.bean.WhiteItem;
import com.miaxis.face.constant.ApiResult;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.constant.ScanResultLenster;
import com.miaxis.face.event.BtReadCardEvent;
import com.miaxis.face.event.CmdFingerImgEvent;
import com.miaxis.face.event.CmdGetFingerEvent;
import com.miaxis.face.event.CmdScanDoneEvent;
import com.miaxis.face.event.CmdScanEvent;
import com.miaxis.face.event.CmdShowDoneEvent;
import com.miaxis.face.event.CmdShowEvent;
import com.miaxis.face.event.CmdShutterEvent;
import com.miaxis.face.event.CmdSignEvent;
import com.miaxis.face.event.CmdSmEvent;
import com.miaxis.face.event.CutDownEvent;
import com.miaxis.face.event.LoadProgressEvent;
import com.miaxis.face.event.NoCardEvent;
import com.miaxis.face.event.ReadCardEvent;
import com.miaxis.face.event.ResultEvent;
import com.miaxis.face.event.TimeChangeEvent;
import com.miaxis.face.greendao.gen.ConfigDao;
import com.miaxis.face.greendao.gen.RecordDao;
import com.miaxis.face.greendao.gen.WhiteItemDao;
import com.miaxis.face.manager.CameraManager;
import com.miaxis.face.manager.CardManager;
import com.miaxis.face.manager.SoundManager;
import com.miaxis.face.receiver.TimeReceiver;
import com.miaxis.face.service.UpLoadRecordService;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.util.MyUtil;
import com.miaxis.face.view.fragment.AlertDialog;
import com.miaxis.face.view.fragment.HightFragment;
import com.miaxis.face.view.fragment.PhotoFragment;
import com.miaxis.face.view.fragment.ScanFragment;
import com.miaxis.face.view.fragment.ShowImgFragment;
import com.miaxis.face.view.fragment.SignFragment;
import com.miaxis.face.view.fragment.VerifyFragment;
import com.miaxis.sdt.bean.IdCard;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.zz.api.MXFaceAPI;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity2  extends BaseActivity implements AMapLocationListener, WeatherSearch.OnWeatherSearchListener, ServiceConnection , OnFragmentInteractionListener {

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
    @BindView(R.id.ll_top)
    LinearLayout llTop;
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

    private double latitude;
    private double longitude;
    private String location;

    private int mState = 0;         // 记录点击次数
    private long firstTime = 0;
    private int toType;             // 0 SettingActivity   1 RecordActivity
    private Config config;
    private RecordDao recordDao;
    private WhiteItemDao whiteItemDao;
    public AMapLocationClient mLocationClient;
    private TimeReceiver timeReceiver;
    private EventBus eventBus;
    private int max;
    private List<WhiteItem> whiteItemList;
    private Subscription mSubscription;
    private final String TAG="MainActivity2";
    private boolean isActive = true;
    private int count=15;
    private boolean imStateSaved=false;
    private boolean ScanFlag=false;
    private MaterialDialog waitDialog;
    private ScanResultLenster scanResultLenster;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        initAMapSDK();
        initTimeReceiver();
        initData();
        MXFaceAPI mxFaceAPI=new MXFaceAPI();
        Log.e(TAG, "人脸算法版本=" +mxFaceAPI.mxAlgVersion() );
        imStateSaved=false;
    }

    private void initData() {
        CardManager.getInstance().init(this);
        whiteItemDao = Face_App.getInstance().getDaoSession().getWhiteItemDao();
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        recordDao = Face_App.getInstance().getDaoSession().getRecordDao();
        SoundManager.getInstance().init(this);
        ConfigDao configDao = Face_App.getInstance().getDaoSession().getConfigDao();
        config = configDao.loadByRowId(1);
        count=config.getAdvertiseDelayTime();
        waitDialog = new MaterialDialog.Builder(this)
                .progress(true, 100)
                .content("请稍后")
                .cancelable(false)
                .autoDismiss(false)
                .build();
//        nvController.nvTo(HomeFragment.getInstance(),false);
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

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Toast.makeText(this, "onServiceConnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Toast.makeText(this, "onServiceDisconnected", Toast.LENGTH_SHORT).show();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBtReadCardEvent(BtReadCardEvent e) {
        onNoCardEvent(null);
        isActive=true;
        final int verifyMode = config.getVerifyMode();
        nvController.back();
        nvController.nvTo(VerifyFragment.getInstance(verifyMode),false);
        SoundManager.getInstance().playSound(Constants.SOUND_PUT_CARD);
        Face_App.getInstance().getThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                while (isActive){
                    final ApiResult<IdCard> result = CardManager.getInstance().read();
                    Log.i(TAG, "ReadCardThrea,ID Card=" +result );
                    if (result.isSuccessful()) {
                        result.getData().idCardMsg.nation_str= TextUtils.isEmpty(result.getData().idCardMsg.nation_str)?"其他":result.getData().idCardMsg.nation_str;
                        final Record record=IdCardToRecord(result.getData(),location,latitude,longitude);
                        eventBus.postSticky(new ReadCardEvent(record,result.getData().face,verifyMode));
                        break;
                    }
                }
            }
        });
        readSecond(count);
    }
    Disposable subscribe=null;
    @SuppressLint("CheckResult")
    private void readSecond(final int count) {
        final CutDownEvent cutDownEvent=new CutDownEvent();
        if(subscribe != null && !subscribe.isDisposed()){
            subscribe.dispose();
            subscribe=null;
        }
        subscribe= Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(count + 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return count - aLong;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//ui线程中进行控件更新
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "倒计时==" + aLong);
                        cutDownEvent.setTime(aLong);
                        eventBus.post(cutDownEvent);
                        if (aLong == 0) {
                            ScanFlag=false;
                            isActive = false;
                            if (!imStateSaved)
                                nvController.back();
                        }
                    }
                });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoCardEvent(NoCardEvent e) {
        Log.e("===", "onNoCardEvent");
        SoundManager.getInstance().close();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCmdShutterEvent(CmdShutterEvent e) {
        nvController.back();
        nvController.nvTo(new PhotoFragment(),false);
        readSecond(count);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
    public void onCmdGetFingerEvent(CmdGetFingerEvent e) {
        nvController.back();
        SoundManager.getInstance().playSound(Constants.SOUND_OTHER_FINGER);
        nvController.nvTo(VerifyFragment.getInstance(false,false),false);
        readSecond(count);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
    public void onCmdShowEvent(CmdShowEvent e) {
        nvController.back();
        Log.e(TAG, "onCmdShowEvent" );
        try {
            String data=e.getData();
            String[] splits = data.split("\\$");
            String[] times = splits[2].split("=#=");
            String[] datas = splits[3].split("=#=");
            nvController.nvTo(ShowImgFragment.getIntent(datas[1]),false);
            readSecond(Integer.parseInt(times[1]));
            eventBus.post(new CmdShowDoneEvent("OK",""));
        }catch (Exception ex){
            eventBus.post(new CmdShowDoneEvent("ERROR",ex.getMessage()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCmdFingerImgEvent(CmdFingerImgEvent e){
        nvController.back();
        SoundManager.getInstance().playSound(Constants.SOUND_OTHER_FINGER);
        nvController.nvTo(VerifyFragment.getInstance(true,true),false);
        readSecond(count);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCmdSmEvent(CmdSmEvent e){
        nvController.back();
        nvController.nvTo(new HightFragment(),false);
        readSecond(count);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCmdSignEvent(CmdSignEvent e){
        nvController.back();
        nvController.nvTo(new SignFragment(),false);
        readSecond(count);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCmdScanEvent(CmdScanEvent e){
        nvController.back();
        nvController.nvTo(new ScanFragment(),false);
        ScanFlag=true;
        readSecond(count);
    }

    @OnClick(R.id.iv_record)
    void onRecordClick() {
        toType = 1;
        etPwd.setVisibility(View.VISIBLE);
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
            btnCancel.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.VISIBLE);
            tvWelMsg.setVisibility(View.GONE);
        } else {
            onCancel();
        }
    }

    @OnClick(R.id.btn_cancel)
    void onCancel() {
        etPwd.setText(null);
        etPwd.setVisibility(View.GONE);
        //		noActionSecond = 0;
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
            //			noActionSecond = 0;
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

    @OnClick(R.id.iv_import_from_u)
    void onImportClicked() {
        Flowable
                .create(new FlowableOnSubscribe<WhiteItem>() {
                    @Override
                    public void subscribe(FlowableEmitter<WhiteItem> e) throws Exception {
                        String whiteContent = FileUtil.readFromUSBPath(MainActivity2.this, "白名单.txt");
                        if (TextUtils.isEmpty(whiteContent)) {
                            File whiteTxtFile = FileUtil.searchFileFromU(MainActivity2.this, "白名单.txt");
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


    @SuppressLint("CheckResult")
    @Subscribe(threadMode = ThreadMode.ASYNC, priority = 100)
    public void onResultEvent(ResultEvent e) {
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
                SoundManager.getInstance().playSound(Constants.SOUND_SUCCESS);
                break;
            case ResultEvent.FINGER_SUCCESS:
                if (config.getVerifyMode() == Config.MODE_FINGER_ONLY
                        || config.getVerifyMode() == Config.MODE_ONE_FINGER_FIRST
                        || config.getVerifyMode() == Config.MODE_ONE_FACE_FIRST) {
                    record.setStatus("指纹通过");
                } else if (config.getVerifyMode() == Config.MODE_TWO_FACE_FIRST) {
                    record.setStatus("人脸、指纹通过");
                } else {
                    return;
                }
                SoundManager.getInstance().playSound(Constants.SOUND_SUCCESS);
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
                SoundManager.getInstance().playSound(Constants.SOUND_FAIL);
                break;
            case ResultEvent.FINGER_FAIL:
                if (config.getVerifyMode() == Config.MODE_FINGER_ONLY
                        || config.getVerifyMode() == Config.MODE_ONE_FACE_FIRST) {
                    record.setStatus("指纹不通过");
                    SoundManager.getInstance().playSound(Constants.SOUND_FAIL);
                }
                break;
            case ResultEvent.WHITE_LIST_FAIL:
                record.setStatus("白名单检验失败");
                SoundManager.getInstance().playSound(Constants.SOUND_FAIL);
                break;
            case ResultEvent.BLACK_LIST_FAIL:
                record.setStatus("黑名单检验失败");
                SoundManager.getInstance().playSound(Constants.SOUND_FAIL);
                break;
            case ResultEvent.VALIDATE_FAIL:
                SoundManager.getInstance().playSound(Constants.SOUND_VALIDATE_FAIL);
                record.setStatus("身份证过期");
                break;
            default:
                return;
        }
        record.setCreateDate(new Date());
        record.setBusEntity(config.getOrgName());
//        record.setLocation(location);
//        record.setLatitude(latitude + "");
//        record.setLongitude(longitude + "");
        List<Record> records = recordDao.loadAll();
        int cout=records.size()-Constants.SQLSIZE;
        if(cout>0){
            for (int i = 0; i < cout; i++) {
                String face=records.get(i).getFaceImg();
                String card=records.get(i).getCardImg();
                boolean b = FileUtil.deleteImg(face);
                boolean b1 = FileUtil.deleteImg(card);
                if (b&&b1){
                    recordDao.delete(records.get(i));
                }
            }
        }
        FileUtil.saveRecordImg(record, this);
        recordDao.insert(record);
        if (config.getNetFlag()) {
            UpLoadRecordService.startActionUpLoad(this, record, config);
        }
    }

    /** 处理 时间变化 事件， 实时更新时间*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeEvent(TimeChangeEvent e) {
        DateFormat dateFormat = new SimpleDateFormat("E  yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String date = dateFormat.format(new Date());
        String time = timeFormat.format(new Date());
        tvTime.setText(time);
        tvDate.setText(date);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        imStateSaved=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        imStateSaved = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        imStateSaved = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        imStateSaved = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause"  );
        CameraManager.getInstance().nirClose();
        CameraManager.getInstance().close();
        nvController.back();
        readSecond(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timeReceiver);
        CardManager.getInstance().free(this);
    }

    private Record IdCardToRecord(IdCard card, String location, double latitude, double longitude){
        Record record=new Record(card,location,String.valueOf(latitude),String.valueOf(longitude));
        String cardImgFilePath = FileUtil.getAvailableImgPath(MainActivity2.this) + File.separator +  card.idCardMsg.id_num + "_" + card.idCardMsg.name + System.currentTimeMillis() + ".bmp";
        MyUtil.saveBitmap(cardImgFilePath, card.face);
        record.setFaceImg(cardImgFilePath);
        return record;
    }


    private StringBuilder sb = new StringBuilder();

    boolean isScaning = false;
    int len = 0;
    int oldLen = 0;
    int counts=0;

    //二维码扫码
    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        if (ScanFlag) {
            if (action == KeyEvent.ACTION_DOWN) {
                if (event.getKeyCode()==KeyEvent.KEYCODE_SHIFT_LEFT||event.getKeyCode()==KeyEvent.KEYCODE_SHIFT_RIGHT
                ||event.getKeyCode()==KeyEvent.KEYCODE_VOLUME_UP||event.getKeyCode()==KeyEvent.KEYCODE_VOLUME_DOWN){
                    return super.dispatchKeyEvent(event);
                }else if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                    readSecond(0);
                    return true;
                }
                char unicodeChar = (char) event.getUnicodeChar();
                sb.append(unicodeChar);
                len++;
                startScan();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void startScan() {
        counts++;
        if (isScaning) {
            return;
        }
        isScaning = true;
        timerScanCal();
    }

    private void timerScanCal() {
        Log.e(TAG, "timerScanCal"  );
        oldLen = len;
        Face_App.getInstance().getThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                if (oldLen != len) {
                    timerScanCal();
                    return;
                }
                isScaning = false;
                if (sb.length() > 0) {
                    String str=sb.toString();
                    Log.e(TAG,"扫码:" + str);
                    eventBus.post(new CmdScanDoneEvent(str.trim()));
                    counts=0;
                    ScanFlag=false;
                    showWaitDialog("正在上传中，请稍后");
                    SystemClock.sleep(1000);
                    dismissWaitDialog("上传成功");
                    backToStack(10);
                    sb.setLength(0);
                }
            }
        });
    }

    @Override
    public void backToStack(int coutdown) {
        readSecond(coutdown);
    }

    @Override
    public void showWaitDialog(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                waitDialog.getContentView().setText(message);
                waitDialog.show();
            }
        });
    }

    @Override
    public void dismissWaitDialog(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (waitDialog.isShowing()) {
                    waitDialog.dismiss();
                    Toast.makeText(MainActivity2.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}