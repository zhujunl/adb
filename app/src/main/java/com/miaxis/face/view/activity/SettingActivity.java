package com.miaxis.face.view.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miaxis.face.R;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.bean.AjaxResponse;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.Version;
import com.miaxis.face.event.CountRecordEvent;
import com.miaxis.face.event.TimerResetEvent;
import com.miaxis.face.greendao.gen.ConfigDao;
import com.miaxis.face.greendao.gen.RecordDao;
import com.miaxis.face.net.UpdateVersion;
import com.miaxis.face.util.MyUtil;
import com.miaxis.face.view.fragment.UpdateDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
//import org.zz.mxhidfingerdriver.MXFingerDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.et_ip)
    EditText etIp;
    @BindView(R.id.et_port)
    EditText etPort;
    @BindView(R.id.et_org)
    EditText etOrg;
    @BindView(R.id.et_pass_score)
    EditText etPassScore;
    @BindView(R.id.rb_net_on)
    RadioButton rbNetOn;
    @BindView(R.id.rb_net_off)
    RadioButton rbNetOff;
    @BindView(R.id.rg_net)
    RadioGroup rgNet;
    @BindView(R.id.rb_query_on)
    RadioButton rbQueryOn;
    @BindView(R.id.rb_query_off)
    RadioButton rbQueryOff;
    @BindView(R.id.rg_query)
    RadioGroup rgQuery;
    @BindView(R.id.tv_select_time)
    TextView tvSelectTime;
    @BindView(R.id.tv_result_count)
    TextView tvResultCount;
    @BindView(R.id.et_monitor_interval)
    EditText etMonitorInterval;
    @BindView(R.id.et_banner)
    EditText etBanner;
    @BindView(R.id.btn_save_config)
    Button btnSaveConfig;
    @BindView(R.id.btn_cancel_config)
    Button btnCancelConfig;
    @BindView(R.id.btn_clear_now)
    Button btnClearNow;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.s_verify_mode)
    Spinner sVerifyMode;
    @BindView(R.id.rb_advertise_on)
    RadioButton rbAdvertiseOn;
    @BindView(R.id.rb_advertise_off)
    RadioButton rbAdvertiseOff;
    @BindView(R.id.rg_advertise)
    RadioGroup rgAdvertise;
    @BindView(R.id.et_advertise_delay_time)
    EditText etAdvertiseDelayTime;
    @BindView(R.id.et_quality_score)
    EditText etQualityScore;
    @BindView(R.id.rg_img)
    RadioGroup rgImg;
    @BindView(R.id.rb_scene)
    RadioButton rbScene;
    @BindView(R.id.rb_face)
    RadioButton rbFace;
    @BindView(R.id.rb_live_on)
    RadioButton rbLiveOn;
    @BindView(R.id.rb_live_off)
    RadioButton rbLiveOff;
    @BindView(R.id.rg_live)
    RadioGroup rgLive;
    @BindView(R.id.rb_finger_ori)
    RadioButton rbFingerOri;
    @BindView(R.id.rb_finger_dark)
    RadioButton rbFingerDark;
    @BindView(R.id.rb_finger_red)
    RadioButton rbFingerRed;
    @BindView(R.id.rg_finger)
    RadioGroup rgFinger;
    @BindView(R.id.rb_rgb0)
    RadioButton rbRgb0;
    @BindView(R.id.rb_rgb1)
    RadioButton rbRgb1;
    @BindView(R.id.rb_rgb2)
    RadioButton rbRgb2;
    @BindView(R.id.rg_rgb)
    RadioGroup rgRgb;
    @BindView(R.id.rb_nir0)
    RadioButton rbNir0;
    @BindView(R.id.rb_nir1)
    RadioButton rbNir1;
    @BindView(R.id.rb_nir2)
    RadioButton rbNir2;
    @BindView(R.id.rg_nir)
    RadioGroup rgNir;
    @BindView(R.id.rb_sm0)
    RadioButton rbSm0;
    @BindView(R.id.rb_sm1)
    RadioButton rbSm1;
    @BindView(R.id.rb_sm2)
    RadioButton rbSm2;
    @BindView(R.id.rg_sm)
    RadioGroup rgSm;

    private Config config;
    private UpdateDialog updateDialog;
    private boolean hasFingerDevice;
    private int fingerImg=0;
    private int rgb=0;
    private int nir=0;
    private int sm=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initWindow();
        initData();
        initModeSpinner();
        initView();
    }

    void initModeSpinner() {
        List<String> verifyModeList = Arrays.asList(getResources().getStringArray(R.array.verifyMode));
        if (!hasFingerDevice) {
            String faceOnly = verifyModeList.get(0);
//            String local = verifyModeList.get(6);
            verifyModeList = new ArrayList<>();
            verifyModeList.add(faceOnly);
//            verifyModeList.add(local);
        }
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, R.layout.spinner_style_display, R.id.tvDisplay, verifyModeList);
        sVerifyMode.setAdapter(myAdapter);
        sVerifyMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (hasFingerDevice) {
                    config.setVerifyMode(position);
                } else {
                    if (position == 1) {
                        config.setVerifyMode(Config.MODE_LOCAL_FEATURE);
                    } else {
                        config.setVerifyMode(Config.MODE_FACE_ONLY);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                config.setVerifyMode(0);
            }
        });
    }

    private void initData() {
        config = Face_App.getInstance().getDaoSession().getConfigDao().loadByRowId(1L);
        hasFingerDevice = Face_App.getInstance().checkHasFingerDevice();
        fingerImg=config.getFingerImg();
        rgb=config.getRgb();
        nir=config.getNir();
        sm=config.getSm();
    }

    private void initView() {
        etIp.setText(config.getIp());
        etPort.setText(config.getPort() + "");
        etBanner.setText(config.getBanner());
        etPassScore.setText(config.getPassScore() + "");
        etOrg.setText(config.getOrgName());
        tvSelectTime.setText(config.getUpTime());
        etMonitorInterval.setText(config.getIntervalTime() + "");
        tvVersion.setText(MyUtil.getCurVersion(this).getVersion());
        rbQueryOn.setChecked(config.getQueryFlag());
        rbQueryOff.setChecked(!config.getQueryFlag());
        rbNetOn.setChecked(config.getNetFlag());
        rbNetOff.setChecked(!config.getNetFlag());
        rbAdvertiseOn.setChecked(config.getAdvertiseFlag());
        rbAdvertiseOff.setChecked(!config.getAdvertiseFlag());
        etAdvertiseDelayTime.setText(config.getAdvertiseDelayTime() + "");
        etQualityScore.setText(String.valueOf(config.getQuality()));
        switch (config.getFingerImg()) {
            case 1:
                rbFingerOri.setChecked(false);
                rbFingerDark.setChecked(true);
                rbFingerRed.setChecked(false);
                break;
            case 2:
                rbFingerOri.setChecked(false);
                rbFingerDark.setChecked(false);
                rbFingerRed.setChecked(true);
                break;
            default:
                rbFingerOri.setChecked(true);
                rbFingerDark.setChecked(false);
                rbFingerRed.setChecked(false);
                break;
        }
        rbScene.setChecked(config.isScence());
        rbFace.setChecked(!config.isScence());
        rbLiveOn.setChecked(config.isLiveness());
        rbLiveOff.setChecked(!config.isLiveness());
        switch (config.getRgb()) {
            case 1:
                rbRgb0.setChecked(false);
                rbRgb1.setChecked(true);
                rbRgb2.setChecked(false);
                break;
            case 2:
                rbRgb0.setChecked(false);
                rbRgb1.setChecked(false);
                rbRgb2.setChecked(true);
                break;
            default:
                rbRgb0.setChecked(true);
                rbRgb1.setChecked(false);
                rbRgb2.setChecked(false);
                break;
        }
        switch (config.getNir()) {
            case 1:
                rbNir0.setChecked(false);
                rbNir1.setChecked(true);
                rbNir2.setChecked(false);
                break;
            case 2:
                rbNir0.setChecked(false);
                rbNir1.setChecked(false);
                rbNir2.setChecked(true);
                break;
            default:
                rbNir0.setChecked(true);
                rbNir1.setChecked(false);
                rbNir2.setChecked(false);
                break;
        }
        switch (config.getSm()) {
            case 1:
                rbSm0.setChecked(false);
                rbSm1.setChecked(true);
                rbSm2.setChecked(false);
                break;
            case 2:
                rbSm0.setChecked(false);
                rbSm1.setChecked(false);
                rbSm2.setChecked(true);
                break;
            default:
                rbSm0.setChecked(true);
                rbSm1.setChecked(false);
                rbSm2.setChecked(false);
                break;
        }
        if (hasFingerDevice) {
            sVerifyMode.setSelection(config.getVerifyMode());
        } else {
            sVerifyMode.setSelection(config.getVerifyMode() / 6);           //无指纹模块时， 验证模式 只有0 或 6
        }

        etPwd.setText(config.getPassword());
        new Thread(new Runnable() {
            @Override
            public void run() {
                RecordDao recordDao = Face_App.getInstance().getDaoSession().getRecordDao();
                long t1 = System.currentTimeMillis();
                long notUpCount = recordDao.queryBuilder().where(RecordDao.Properties.HasUp.eq(false)).count();
                long t2 = System.currentTimeMillis();
                long count = recordDao.count();
                long t3 = System.currentTimeMillis();
                Log.e("==count", "耗时" + (t2 - t1) + " _ " + (t3 - t2));
                EventBus.getDefault().post(new CountRecordEvent(notUpCount, count));
            }
        }).start();

        updateDialog = new UpdateDialog();
        updateDialog.setContext(this);

        rgFinger.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                for (int i = 1; i < radioGroup.getChildCount(); i=i+2) {
                    RadioButton rb = (RadioButton)radioGroup.getChildAt(i);
                    if (rb.isChecked()){
                        fingerImg=Integer.parseInt(radioGroup.getChildAt(i).getTag().toString());
                        break;
                    }
                }
            }
        });
        rgRgb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                for (int i = 1; i < radioGroup.getChildCount(); i=i+2) {
                    RadioButton rb = (RadioButton)radioGroup.getChildAt(i);
                    if (rb.isChecked()){
                        rgb=Integer.parseInt(radioGroup.getChildAt(i).getTag().toString());
                        break;
                    }
                }
            }
        });
        rgNir.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                for (int i = 1; i < radioGroup.getChildCount(); i=i+2) {
                    RadioButton rb = (RadioButton)radioGroup.getChildAt(i);
                    if (rb.isChecked()){
                        nir=Integer.parseInt(radioGroup.getChildAt(i).getTag().toString());
                        break;
                    }
                }
            }
        });
        rgSm.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                for (int i = 1; i < radioGroup.getChildCount(); i=i+2) {
                    RadioButton rb = (RadioButton)radioGroup.getChildAt(i);
                    if (rb.isChecked()){
                        sm=Integer.parseInt(radioGroup.getChildAt(i).getTag().toString());
                        break;
                    }
                }
            }
        });
    }


    @OnClick(R.id.tv_select_time)
    void onSelectTime(View view) {
        String[] strs = tvSelectTime.getText().toString().split(" : ");
        int h = Integer.valueOf(strs[0]);
        int m = Integer.valueOf(strs[1]);
        TimePickerDialog d = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String h = hourOfDay + "";
                String m = minute + "";
                if (hourOfDay < 10) {
                    h = "0" + h;
                }
                if (minute < 10) {
                    m = "0" + m;
                }
                tvSelectTime.setText(h + " : " + m);
            }
        }, h, m, true);
        d.show();
    }

    @OnClick(R.id.btn_save_config)
    void save() {
        if (TextUtils.isEmpty(etPassScore.getText().toString().trim())){{
            Toast.makeText(this,"比对阈值不能为空",Toast.LENGTH_SHORT).show();
            return;
        }}
        if (TextUtils.isEmpty(etQualityScore.getText().toString().trim())){{
            Toast.makeText(this,"质量阈值不能为空",Toast.LENGTH_SHORT).show();
            return;
        }}
        if (Float.parseFloat(etPassScore.getText().toString().trim())<0||Float.parseFloat(etPassScore.getText().toString().trim())>1){{
            Toast.makeText(this,"比对阈值设置错误",Toast.LENGTH_SHORT).show();
            return;
        }}
        if (Float.parseFloat(etQualityScore.getText().toString().trim())<0||Float.parseFloat(etQualityScore.getText().toString().trim())>100){{
            Toast.makeText(this,"质量阈值设置错误",Toast.LENGTH_SHORT).show();
            return;
        }}
        config.setIp(etIp.getText().toString());
        config.setPort(Integer.parseInt(etPort.getText().toString().trim()));
        config.setOrgName(etOrg.getText().toString());
        config.setPassScore(Float.parseFloat(etPassScore.getText().toString().trim()));
        config.setNetFlag(rbNetOn.isChecked());
        config.setQueryFlag(rbQueryOn.isChecked());
        config.setUpTime(tvSelectTime.getText().toString());
        config.setIntervalTime(Integer.parseInt(etMonitorInterval.getText().toString().trim()));
        config.setBanner(etBanner.getText().toString());
        config.setAdvertiseFlag(rbAdvertiseOn.isChecked());
        config.setAdvertiseDelayTime(Integer.parseInt(etAdvertiseDelayTime.getText().toString()));
        config.setQuality(Float.parseFloat(etQualityScore.getText().toString().trim()));
        config.setSm(sm);
        config.setNir(nir);
        config.setRgb(rgb);
        config.setLiveness(rbLiveOn.isChecked());
        config.setScence(rbScene.isChecked());
        config.setFingerImg(fingerImg);
        if (etPwd.getText().length() != 6) {
            Toast.makeText(this, "请填写6位数字密码", Toast.LENGTH_SHORT).show();
            return;
        }
        config.setPassword(etPwd.getText().toString());
        ConfigDao configDao = Face_App.getInstance().getDaoSession().getConfigDao();
        configDao.update(config);
        EventBus.getDefault().post(new TimerResetEvent());
        finish();
    }

    @OnClick(R.id.btn_cancel_config)
    void cancel() {
        finish();
    }

    @OnClick(R.id.btn_clear_now)
    void upLoad() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Face_App.timerTask.run();
            }
        }).start();
    }

    @OnClick(R.id.btn_update)
    void update() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + config.getIp() + ":" + config.getPort() + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UpdateVersion uv = retrofit.create(UpdateVersion.class);
        Call<AjaxResponse> call = uv.checkVersion();
        call.enqueue(new Callback<AjaxResponse>() {
            @Override
            public void onResponse(Call<AjaxResponse> call, Response<AjaxResponse> rsp) {
                try {
                    Version lastVersion = null;
                    Gson g = new Gson();
                    AjaxResponse response = rsp.body();
                    if (response.getCode() == AjaxResponse.FAILURE) {
                        MyUtil.alert(getFragmentManager(), response.getMessage());
                        return;
                    } else if (response.getCode() == AjaxResponse.SUCCESS) {
                        lastVersion = g.fromJson(g.toJson(response.getData()), Version.class);
                    }
                    Version curVersion = MyUtil.getCurVersion(getApplicationContext());
                    if (lastVersion.getVersionCode() > curVersion.getVersionCode()) {
                        updateDialog.setLastVersion(lastVersion);
                        updateDialog.show(getFragmentManager(), "update_dialog");
                    } else {
                        MyUtil.alert(getFragmentManager(), "您已经是最新版了！");
                    }
                } catch (Exception e) {
                    MyUtil.alert(getFragmentManager(), "解析数据失败");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AjaxResponse> call, Throwable t) {
                MyUtil.alert(getFragmentManager(), "更新失败！");
            }
        });
    }

    @OnClick(R.id.btn_exit)
    void singOut() {
//        finish();
//        throw new RuntimeException();
        Intent intent = new Intent("com.miaxis.face.view.activity");
        intent.putExtra("closeAll", 1);
        sendBroadcast(intent);
    }

    @OnClick(R.id.btn_white_manage)
    void onWhiteManage() {
        startActivity(new Intent(this, WhiteActivity.class));
    }

    @OnClick(R.id.btn_local_feature_manage)
    void onLocalFeatureManage() {
        startActivity(new Intent(this, LocalFeatureActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCountRecordEvent(CountRecordEvent e) {
        tvResultCount.setText(e.getNotUpCount() + " / " + e.getCount());
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
