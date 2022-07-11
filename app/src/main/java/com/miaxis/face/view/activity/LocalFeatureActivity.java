package com.miaxis.face.view.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.adapter.LocalFeatureItemAdapter;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.bean.LocalFeature;
import com.miaxis.face.event.LoadProgressEvent;
import com.miaxis.face.greendao.gen.LocalFeatureDao;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.view.custom.ContentLoadingDialog;
import com.miaxis.face.view.custom.SimpleDialog;
import com.miaxis.face.view.fragment.AlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Subscription;
import org.zz.api.MXFaceAPI;
import org.zz.jni.mxImageLoad;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LocalFeatureActivity extends BaseActivity {

    @BindView(R.id.btn_import_img)
    Button btnImportImg;
    @BindView(R.id.btn_delete_all)
    Button btnDeleteAll;
    @BindView(R.id.lv_local_feature)
    ListView lvLocalFeature;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.tv_count)
    TextView tvCount;
    private MXFaceAPI mxFaceAPI;
    private mxImageLoad dtload;

    private LocalFeatureItemAdapter adapter;

    private Subscription mSubscription;
    private ContentLoadingDialog loadingDialog;
    private int max;
    private int progress;
    private List<LocalFeature> localFeatureList;
    private LocalFeatureDao localFeatureDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_feature);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    private void initData() {
        localFeatureDao = Face_App.getInstance().getDaoSession().getLocalFeatureDao();
        localFeatureList = localFeatureDao.loadAll();
        adapter = new LocalFeatureItemAdapter(this);
        adapter.setLocalFeatureList(localFeatureList);
        EventBus.getDefault().register(this);
//        mxFaceAPI = Face_App.getMxAPI();
        dtload = new mxImageLoad();
    }

    private void initView() {
        lvLocalFeature.setAdapter(adapter);
        loadingDialog = new ContentLoadingDialog();
        loadingDialog.setCancelable(false);
        loadingDialog.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.dismiss();
                mSubscription.cancel();
                mSubscription = null;
                adapter.notifyDataSetChanged();
                tvCount.setText("总计：" + localFeatureList.size() + " 条");
            }
        });
        tvCount.setText("总计：" + localFeatureList.size() + " 条");
    }

    @OnClick(R.id.btn_import_img)
    void onImpImg() {
//        importImg();
    }

    @OnClick(R.id.btn_back)
    void onGoBack() {
        finish();
    }

//    private void importImg() {
//        Flowable
//                .create(new FlowableOnSubscribe<LocalFeature>() {
//                    @Override
//                    public void subscribe(FlowableEmitter<LocalFeature> e) throws Exception {
//                        File imgDir = new File(FileUtil.getUSBPath(LocalFeatureActivity.this), "白名单");
//                        if (!imgDir.exists() || !imgDir.isDirectory()) {
//                            imgDir = FileUtil.searchFileFromU(LocalFeatureActivity.this, "白名单");
//                        }
//                        if (imgDir == null || !imgDir.exists() || !imgDir.isDirectory()) {
//                            throw new Exception("加载图像失败！请检查U盘和文件是否存在");
//                        }
//                        File[] imgArr = imgDir.listFiles();
//                        max = imgArr.length;
//                        progress = 0;
//                        if (max > 0) {
//                            localFeatureList.clear();
//                            localFeatureDao.deleteAll();
//                            FileUtil.delDirectory(new File(FileUtil.getAvailableFeaturePath(getApplicationContext())));
//                            EventBus.getDefault().post(new LoadProgressEvent<LocalFeature>(max, progress));
//                            for (File anImgArr : imgArr) {
//                                e.onNext(new LocalFeature("", anImgArr.getName(), getFeatureFromFile(anImgArr)));
//                            }
//                        } else {
//                            throw new Exception("加载名单失败！白名单内容为空，或格式错误");
//                        }
//                    }
//                }, BackpressureStrategy.BUFFER)
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Subscriber<LocalFeature>() {
//                    @Override
//                    public void onSubscribe(Subscription s) {
//                        s.request(1);
//                        mSubscription = s;
//                    }
//
//                    @Override
//                    public void onNext(LocalFeature localFeature) {
//                        progress ++;
//                        EventBus.getDefault().post(new LoadProgressEvent<>(max, progress, localFeature));
//                        if (mSubscription != null) {
//                            mSubscription.request(1);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//                        AlertDialog a = new AlertDialog();
//                        a.setAdContent(t.getMessage());
//                        a.show(getFragmentManager(), "a");
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//
//    }
//
//    private String getFeatureFromFile(File file) {
//        /**压缩图像 */
////        try {
////            file = Luban.with(this).load(file).get(file.getPath());
////        } catch (IOException e) {
////            return null;
////        }
//
//        int re = dtload.ImageZoom(file.getPath(), 320, file.getPath());
//        if (re != 1) {
//            return null;
//        }
////        int re = -1;
//        /** 加载图像 */
//        int[] oX = new int[1];
//        int[] oY = new int[1];
//        re = dtload.LoadFaceImage(file.getPath(), null, null, oX, oY);
//        if (re != 1) {
//            return null;
//        }
//        byte[] pGrayBuff = new byte[oX[0] * oY[0]];
//        byte[] pRGBBuff = new byte[oX[0] * oY[0] * 3];
//        re = dtload.LoadFaceImage(file.getPath(), pRGBBuff, pGrayBuff, oX, oY);
//        if (re != 1) {
//            return null;
//        }
//        /** 检测人脸 */
//        int[] pFaceNum = new int[1];
//        pFaceNum[0] = 1;                //身份证照片只可能检测到一张人脸
//        MXFaceInfo[] pFaceBuffer = new MXFaceInfo[1];
//        pFaceBuffer[0] = new MXFaceInfo();
//        int iX = oX[0];
//        int iY = oY[0];
//        re = mxFaceAPI.mxDetectFace(pGrayBuff, iX, iY, pFaceNum, pFaceBuffer);
//        if (re != 0) {
//            return null;
//        }
//        /** 提取特征 */
//        byte[] bFeature = new byte[mxFaceAPI.mxGetFeatureSize()];
//        re = mxFaceAPI.mxFeatureExtract(pRGBBuff, 102, 126, 1, pFaceBuffer, bFeature);
//        if (re != 0) {
//            return null;
//        }
//        /** 保存特征文件 */
//        String newFileName;
//        String[] strArr = file.getName().split("\\.");
//        if (strArr.length == 2) {
//            newFileName = strArr[0];
//        } else {
//            newFileName = file.getName();
//        }
//        File featureFile = new File(FileUtil.getAvailableFeaturePath(this), newFileName + ".dat");
//        if (featureFile.exists()) {
//            featureFile.delete();
//        }
//        FileUtil.writeBytesToFile(bFeature, featureFile.getParent(), featureFile.getName());
//        return featureFile.getAbsolutePath();
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadProgressEvent(LoadProgressEvent<LocalFeature> e) {
        if (mSubscription != null) {
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
            LocalFeature feature = e.getItem();
            if (feature != null && feature.getFilePath() != null && feature.getFilePath().length() > 0) {
                localFeatureList.add(e.getItem());
                localFeatureDao.insert(feature);
            }
            loadingDialog.setMax(e.getMax());
            loadingDialog.setProgress(e.getProgress());
            if (e.getMax() == e.getProgress()) {
                loadingDialog.setMessage("导入完成！ 成功 " + localFeatureList.size() + " 个，失败 " + (max - localFeatureList.size()) + " 个");
                loadingDialog.setButtonName(R.string.confirm);
                loadingDialog.setCancelable(true);
            }
        }

        adapter.notifyDataSetChanged();
        tvCount.setText("总计：" + localFeatureList.size() + " 条");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.btn_delete_all)
    void onDeleteAll() {
        final SimpleDialog sDialog = new SimpleDialog();
        sDialog.setMessage("确定要删除所有数据吗？");
        sDialog.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sDialog.dismiss();
            }
        });
        sDialog.setConfirmListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(LocalFeatureActivity.this);
                progressDialog.setMessage("正在删除数据...");
                Observable
                        .create(new ObservableOnSubscribe<Integer>() {
                            @Override
                            public void subscribe(ObservableEmitter<Integer> e) {
                                sDialog.dismiss();
                                progressDialog.show();
                                e.onNext(1);
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(Schedulers.io())
                        .doOnNext(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) {
                                LocalFeatureDao dao = Face_App.getInstance().getDaoSession().getLocalFeatureDao();
                                dao.deleteAll();
                                FileUtil.delDirectory(new File(FileUtil.getAvailableFeaturePath(getApplicationContext())));
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) {
                                progressDialog.dismiss();
                                localFeatureList.clear();
                                adapter.notifyDataSetChanged();
                                tvCount.setText("总计：" + localFeatureList.size() + " 条");
                                AlertDialog alertDialog = new AlertDialog();
                                alertDialog.setAdContent("删除成功！");
                                alertDialog.show(getFragmentManager(), "da");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                tvCount.setText("总计：" + localFeatureList.size() + " 条");
                                progressDialog.dismiss();
                                AlertDialog alertDialog = new AlertDialog();
                                alertDialog.setAdContent("删除失败！\r\n" + throwable.getMessage());
                                alertDialog.show(getFragmentManager(), "da");
                            }
                        });
            }
        });


        sDialog.show(getFragmentManager(), "s");
    }


}
