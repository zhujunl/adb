package com.miaxis.face.view.activity;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.event.InitCWEvent;
import com.miaxis.face.event.ReInitEvent;
import com.miaxis.face.util.LogUtil;
import com.miaxis.face.view.custom.GifView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoadingActivity extends BaseActivity {

    @BindView(R.id.tv_loading)
    TextView tvLoading;
    @BindView(R.id.gif_loading)
    GifView gifLoading;

    @BindColor(R.color.white)
    int white;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_record)
    ImageView ivRecord;
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

    private EventBus eventBus;

    private final String[] PM_MULTIPLE={
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.GET_TASKS,
            Manifest.permission.KILL_BACKGROUND_PROCESSES,
            Manifest.permission.GET_PACKAGE_SIZE,
            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.READ_LOGS,
            Manifest.permission.ACCOUNT_MANAGER,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.DISABLE_KEYGUARD,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
    };

    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        applyForMultiplePermissions();
        ButterKnife.bind(this);
        initWindow();
        initTitle();
        gifLoading.setMovieResource(R.raw.loading);
        eventBus = EventBus.getDefault();
        eventBus.register(this);



    }

    //申请多个权限
    public void applyForMultiplePermissions(){
        try{
            //如果操作系统SDK级别在23之上（android6.0），就进行动态权限申请
            if(Build.VERSION.SDK_INT>=23){
                ArrayList<String> pmList=new ArrayList<>();
                //获取当前未授权的权限列表
                for(String permission:PM_MULTIPLE){
                    int nRet=ContextCompat.checkSelfPermission(this,permission);
                    if(nRet!= PackageManager.PERMISSION_GRANTED){
                        pmList.add(permission);
                    }
                }

                if(pmList.size()>0){
                    String[] sList=pmList.toArray(new String[0]);
                    ActivityCompat.requestPermissions(this,sList,10000);
                }
                else{

                }
            }else {
                Face_App.getInstance().initApplication();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    void initTitle() {
        llTop.setVisibility(View.GONE);
        tvTitle.setText("退出");
        tvWelMsg.setVisibility(View.INVISIBLE);
        tvTime.setVisibility(View.INVISIBLE);
        tvWeather.setVisibility(View.INVISIBLE);
        tvDate.setVisibility(View.INVISIBLE);
        ivRecord.setVisibility(View.INVISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onInitCWEvent(InitCWEvent e) {
        switch (e.getResult()) {
            case InitCWEvent.ERR_FILE_COMPARE:
                tvLoading.setText("文件校验失败");
                LogUtil.writeLog("文件校验失败");
                eventBus.post(new ReInitEvent());
                llTop.setVisibility(View.VISIBLE);
                break;
            case InitCWEvent.ERR_LICENCE:
                tvLoading.setText("读取授权文件失败");
                LogUtil.writeLog("读取授权文件失败");
                llTop.setVisibility(View.VISIBLE);
                break;
            case InitCWEvent.INIT_SUCCESS:
                tvLoading.setText("初始化算法成功");
                LogUtil.writeLog("初始化算法成功");
                startActivity(new Intent(this, MainActivity2.class));
                finish();
                break;
            default:
                tvLoading.setText("初始化算法失败");
                LogUtil.writeLog("初始化算法失败" + e.getResult());
                llTop.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
        Face_App.getInstance().onTerminate();
//        System.exit(0);
    }

    @OnClick(R.id.tv_title)
    void onErrorExit() {
//        SmdtManager smdtManager = SmdtManager.create(this);
//        smdtManager.smdtSetStatusBar(this, true);
//        smdtManager.smdtSetGpioValue(2, false);
//        smdtManager.smdtSetGpioValue(3, false);
//        throw new RuntimeException();
    }

    //申请权限结果返回处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                //已授权
                if (grantResults[i] ==  PackageManager.PERMISSION_GRANTED) {
                    if(i==permissions.length-1){
                        Face_App.getInstance().initApplication();
                    }
                    continue;
                }
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    //选择禁止
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setTitle("授权");
                    builder.setMessage("需要允许授权才可使用");
                    final int finalI = i;
                    builder.setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if (mDialog != null && mDialog.isShowing()) {
                                mDialog.dismiss();
                            }
                            ActivityCompat.requestPermissions(LoadingActivity.this, new String[]{permissions[finalI]}, 1);
                        }
                    });
                    mDialog = builder.create();
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                }
                else {
                    //选择禁止并勾选禁止后不再询问
                    android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("授权");
                    builder.setMessage("需要允许授权才可使用");
                    builder.setPositiveButton("去授权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if (mDialog != null && mDialog.isShowing()) {
                                mDialog.dismiss();
                            }
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", LoadingActivity.this.getPackageName(), null);
                            intent.setData(uri);
                            //调起应用设置页面
                            LoadingActivity.this.startActivityForResult(intent, 2);
                        }
                    });
                    mDialog = builder.create();
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                }
            }
        }else {
            Face_App.getInstance().initApplication();
        }
    }

}
