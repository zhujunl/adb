package com.miaxis.face.app;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.widget.Toast;

import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.Record;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.event.InitCWEvent;
import com.miaxis.face.event.TimerResetEvent;
import com.miaxis.face.event.ToastEvent;
import com.miaxis.face.greendao.gen.ConfigDao;
import com.miaxis.face.greendao.gen.DaoMaster;
import com.miaxis.face.greendao.gen.DaoSession;
import com.miaxis.face.greendao.gen.RecordDao;
import com.miaxis.face.manager.FaceManager;
import com.miaxis.face.service.AdbCommService;
import com.miaxis.face.service.ClearService;
import com.miaxis.face.service.UpLoadRecordService;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.view.fragment.AdvertiseDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zz.api.MXFaceAPI;
import org.zz.mxhidfingerdriver.MXFingerDriver;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Administrator on 2017/5/16 0016.
 */
public class Face_App extends Application implements ServiceConnection {

    private static MXFaceAPI mxAPI;
    private EventBus eventBus;
    private static Config config;
    private static Timer timer;
    public static TimerTask timerTask;
    public static final int GROUP_SIZE = 100;
    private DaoSession mDaoSession;
    private static Face_App app;
    private ExecutorService threadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    @Override
    public void onCreate() {
        super.onCreate();
//        Intent intent1 = new Intent(getApplicationContext(), GPIOService.class);
//        bindService(intent1, mConnection, BIND_AUTO_CREATE);
        app=this;
        bindService(new Intent(this, AdbCommService.class), this, BIND_AUTO_CREATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();
                initDbHelp();
                initConfig();
                initDirectory();
                initDefaultPicture();
                initHsIdPhotoDecodeLib();
                startTask();
                FileUtil.initDirectory(app);
//                initCW();
                int re=FaceManager.getInstance().init(app);
                eventBus.postSticky(new InitCWEvent(re));
            }
        }).start();

    }

    private void initData() {
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

//    private void initCW() {
//        final String sLicence = FileUtil.readLicence();
//        if (TextUtils.isEmpty(sLicence)) {
//            eventBus.postSticky(new InitCWEvent(InitCWEvent.INIT_SUCCESS));
//            return;
//        }
//        int re = initFaceModel();
//        if (re == 0) {
//            re = mxAPI.mxInitAlg(getApplicationContext(), FileUtil.getFaceModelPath(), sLicence);
//        }
//        eventBus.postSticky(new InitCWEvent(re));
//    }

    private void initDbHelp() {
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(new GreenDaoContext(this), "FaceAdb_ST.db", null);
        MyOpenHelper helper = new MyOpenHelper(new GreenDaoContext(this), "FaceAdb_ST.db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    public void initConfig() {
        try {
            ConfigDao configDao = mDaoSession.getConfigDao();
            config = configDao.loadByRowId(1);
            if (config == null) {
                config = new Config();
                config.setId(1);
                config.setIp(Constants.DEFAULT_IP);
                config.setPort(Constants.DEFAULT_PORT);
                config.setIntervalTime(Constants.DEFAULT_INTERVAL);
                config.setBanner(Constants.DEFAULT_BANNER);
                config.setUpTime(Constants.DEFAULT_UPTIME);
                config.setPassScore(Constants.PASS_SCORE);
                config.setVerifyMode(Config.MODE_FACE_ONLY);
                config.setNetFlag(Constants.DEFAULT_NET);
                config.setQueryFlag(Constants.DEFAULT_NET);
                config.setPassword(Constants.DEFAULT_PASSWORD);
                config.setAdvertiseFlag(Constants.DEFAULT_ADVERTISE_FLAG);
                config.setAdvertiseDelayTime(Constants.DEFAULT_ADVERTISE_DELAY_TIME);
                configDao.insert(config);
            } else if (config.getAdvertiseFlag() == null || config.getAdvertiseDelayTime() == null) {
                config.setAdvertiseFlag(Constants.DEFAULT_ADVERTISE_FLAG);
                config.setAdvertiseDelayTime(Constants.DEFAULT_ADVERTISE_DELAY_TIME);
                configDao.update(config);
            } else {
                if (!checkHasFingerDevice() && config.getVerifyMode() != Config.MODE_LOCAL_FEATURE) {
                    config.setVerifyMode(Config.MODE_FACE_ONLY);
                    configDao.save(config);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        eventBus.unregister(this);
        mxAPI.mxFreeAlg();
        super.onTerminate();
    }

    public static MXFaceAPI getMxAPI() {
        return mxAPI;
    }

    private void upLoad() {
        RecordDao recordDao = mDaoSession.getRecordDao();
        long count = recordDao.count();
        long page = (count % GROUP_SIZE == 0) ? count / GROUP_SIZE : (count / GROUP_SIZE + 1);
        for (int i = 0; i < page; i++) {
            List<Record> recordList = recordDao.queryBuilder().offset(i * GROUP_SIZE).limit(GROUP_SIZE).orderAsc(RecordDao.Properties.Id).list();
            for (int j = 0; j < recordList.size(); j++) {
                Record record = recordList.get(j);
                if (!record.isHasUp()) {
                    UpLoadRecordService.startActionUpLoad(getApplicationContext(), record, config);
                }
            }
        }
    }

    private void startTask() {
        initTask();
        Date start = new Date();
        start.setHours(Integer.valueOf(config.getUpTime().split(" : ")[0]));
        start.setMinutes(Integer.valueOf(config.getUpTime().split(" : ")[1]));
        long tStart = start.getTime();
        long t1 = new Date().getTime();
        if (tStart < t1) {
            start.setDate(new Date().getDate() + 1);
        }
        timer.schedule(timerTask, start, Constants.TASK_DELAY);
    }



    private void initTask() {
        timer = new Timer(true);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (config.getNetFlag()) {
                    upLoad();
                }
                ClearService.startActionClear(getApplicationContext());
            }
        };
    }

    public void reSetTimer() {
        timerTask.cancel();
        initTask();
        timer.cancel();
        timer.purge();
        timer = new Timer();
        Date start = new Date();
        start.setHours(Integer.valueOf(config.getUpTime().split(" : ")[0]));
        start.setMinutes(Integer.valueOf(config.getUpTime().split(" : ")[1]));
        start.setSeconds(0);
        long tStart = start.getTime();
        long t1 = new Date().getTime();
        if (tStart < t1) {
            start.setDate(new Date().getDate() + 1);
        }
        timer.schedule(timerTask, start, Constants.TASK_DELAY);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerResetEvent(TimerResetEvent e) {
        reSetTimer();
    }

    /**
     * @return
     */
//    private int initFaceModel() {
//        String hsLibDirName = "zzFaceModel";
//        String modelFile1 = "MIAXISFaceDetector5.1.2.m9d6.640x480.ats";
//        String modelFile2 = "MIAXISFaceDewave1.1.PA.raw.ats";
//        String modelFile3 = "MIAXISFaceRecognizer5.0.RN30.m5d14.ID.ats";
//        String modelFile4 = "MIAXISPointDetector5.0.pts5.ats";
//        File modelDir = new File(FileUtil.getFaceModelPath());
//        if (modelDir.exists()) {
//            FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + modelFile1, modelDir + File.separator + modelFile1);
//            FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + modelFile2, modelDir + File.separator + modelFile2);
//            FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + modelFile3, modelDir + File.separator + modelFile3);
//            FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + modelFile4, modelDir + File.separator + modelFile4);
//            return 0;
//        } else {
//            return -1;
//        }
//
//    }

    private void initDefaultPicture() {
        String filename = "default_picture.jpg";
        String assetsFilepath = "default" + File.separator + filename;
        File adFileDir = new File(FileUtil.getAdvertisementFilePath());
        if(adFileDir.exists() && !AdvertiseDialog.isAdExist()) {
            FileUtil.copyAssetsFile(this, assetsFilepath, adFileDir + File.separator + filename);
        }
    }

    private void initDirectory() {
        File modelDir = new File(FileUtil.getFaceModelPath());
        if (!modelDir.exists()) {
            modelDir.mkdirs();
        }
        File zzFacesDir = new File(FileUtil.getAvailableImgPath(this));
        if (!zzFacesDir.exists()) {
            zzFacesDir.mkdirs();
        }
        File adFileDir = new File(FileUtil.getAdvertisementFilePath());
        if (!adFileDir.exists()) {
            adFileDir.mkdirs();
        }
        File wltlibDir = new File(FileUtil.getAvailableWltPath(this));
        if (!wltlibDir.exists()) {
            wltlibDir.mkdirs();
        }
    }

    /**
     * 复制宇松二代证解码库的授权文件到指定目录
     *
     * @return
     */
    private int initHsIdPhotoDecodeLib() {
        String hsLibDirName = "wltlib";
        String hsFile1 = "base.dat";
        String hsFile2 = "license.lic";
        String hsFile3 = "test.dat";
        String hsFile4 = "zp.wlt";
        File wltlibDir = new File(FileUtil.getAvailableWltPath(this));
        if (!wltlibDir.exists()) {
            if (!wltlibDir.mkdirs()) {
                return -1;
            }
        }
        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile1, wltlibDir + File.separator + hsFile1);
        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile2, wltlibDir + File.separator + hsFile2);
        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile3, wltlibDir + File.separator + hsFile3);
        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile4, wltlibDir + File.separator + hsFile4);
        return 0;
    }

    public static Face_App getInstance() {
        return app;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToastEvent(ToastEvent e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private boolean checkHasFingerDevice() {
        int re;
        int pid = 0x0202;
        int vid = 0x821B;
        MXFingerDriver fingerDriver = new MXFingerDriver(getApplicationContext(), pid, vid);
        for (int i = 0; i < 20; i++) {
            re = fingerDriver.mxGetDevVersion(new byte[120]);
            if (re == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Toast.makeText(this, "ADB通信服务开启", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Toast.makeText(this, "ADB通信服务断开", Toast.LENGTH_LONG).show();
    }

    public ExecutorService getThreadExecutor() {
        return threadExecutor;
    }


}