package com.miaxis.face.view.activity;

import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.event.InitCWEvent;
import com.miaxis.face.event.ReInitEvent;
import com.miaxis.face.util.LogUtil;
import com.miaxis.face.view.custom.GifView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        if (!Constants.VERSION&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( !Settings.canDrawOverlays(this)) {
                //若未授权则请求权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
        }
        ButterKnife.bind(this);
        initWindow();
        initTitle();
        gifLoading.setMovieResource(R.raw.loading);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
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
                startActivity(new Intent(this, MainActivity.class));
//                finish();
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
        System.exit(0);
    }

    @OnClick(R.id.tv_title)
    void onErrorExit() {
        Face_App.getInstance().unableDog();
        SmdtManager smdtManager = SmdtManager.create(this);
        smdtManager.smdtSetStatusBar(this, true);
        smdtManager.smdtSetGpioValue(2, false);
        smdtManager.smdtSetGpioValue(3, false);
        throw new RuntimeException();
    }
}
