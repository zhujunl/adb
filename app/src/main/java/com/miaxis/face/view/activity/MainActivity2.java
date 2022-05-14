package com.miaxis.face.view.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
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
import com.miaxis.face.R;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.Record;
import com.miaxis.face.bean.WhiteItem;
import com.miaxis.face.constant.ApiResult;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.event.BtReadCardEvent;
import com.miaxis.face.event.ReadCardEvent;
import com.miaxis.face.event.LoadProgressEvent;
import com.miaxis.face.event.NoCardEvent;
import com.miaxis.face.event.TimeChangeEvent;
import com.miaxis.face.greendao.gen.ConfigDao;
import com.miaxis.face.greendao.gen.RecordDao;
import com.miaxis.face.greendao.gen.WhiteItemDao;
import com.miaxis.face.manager.CardManager;
import com.miaxis.face.manager.SoundManager;
import com.miaxis.face.receiver.TimeReceiver;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.util.MyUtil;
import com.miaxis.face.view.fragment.AlertDialog;
import com.miaxis.face.view.fragment.HomeFragment;
import com.miaxis.face.view.fragment.PreviewFragment;
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
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity2  extends BaseActivity implements AMapLocationListener, WeatherSearch.OnWeatherSearchListener, ServiceConnection {

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
    private final int count=15;


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
        nvController.nvTo(HomeFragment.getInstance(),false);
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
        nvController.nvTo(PreviewFragment.newIntent(),false);
        SoundManager.getInstance().playSound(Constants.SOUND_PUT_CARD);
        Face_App.getInstance().getThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                while (isActive){
                    ApiResult<IdCard> result = CardManager.getInstance().read();
                    Log.i(TAG, "ReadCardThrea,ID Card=" +result );
                    if (result.isSuccessful()) {
                        result.getData().idCardMsg.nation_str= TextUtils.isEmpty(result.getData().idCardMsg.nation_str)?"其他":result.getData().idCardMsg.nation_str;
                        Record record=IdCardToRecord(result.getData(),location,latitude,longitude);
                        eventBus.postSticky(new ReadCardEvent(record,result.getData().face));
                        break;
                    }
                }
            }
        });
        readSecond();;
        //        if (advertiseDialog.isAdded()) {
        //            noActionSecond = 0;
        //            restartCamera();
        //            advertiseDialog.dismiss();
        //        }
    }

    @SuppressLint("CheckResult")
    private void readSecond() {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(count+1 )
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
                        Log.e(TAG, "倒计时==" +aLong);
                        //                        tvSecond.setVisibility(View.VISIBLE);
                        //                        tvSecond.setText("" + aLong);
                        if (aLong==0){
                            isActive=false;
                            nvController.back();
                        }
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoCardEvent(NoCardEvent e) {
        Log.e("===", "onNoCardEvent");
        SoundManager.getInstance().close();
        //            soundPool.stop(mCurSoundId);
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

    private Record IdCardToRecord(IdCard card,String location,double latitude,double longitude){
        Record record=new Record(card,location,String.valueOf(latitude),String.valueOf(longitude));
        String cardImgFilePath = FileUtil.getAvailableImgPath(MainActivity2.this) + File.separator +  card.idCardMsg.id_num + "_" + card.idCardMsg.name + System.currentTimeMillis() + ".bmp";
        MyUtil.saveBitmap(cardImgFilePath, card.face);
        record.setFaceImg(cardImgFilePath);
        return record;
    }
}