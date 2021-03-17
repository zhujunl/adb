package com.miaxis.face.view.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.adapter.WhiteItemAdapter;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.WhiteItem;
import com.miaxis.face.event.LoadProgressEvent;
import com.miaxis.face.greendao.gen.WhiteItemDao;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.view.custom.ContentLoadingDialog;
import com.miaxis.face.view.custom.SimpleDialog;
import com.miaxis.face.view.fragment.AlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.util.List;

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

public class WhiteActivity extends BaseActivity {

    @BindView(R.id.rb_white_on)
    RadioButton rbWhiteOn;
    @BindView(R.id.rb_white_off)
    RadioButton rbWhiteOff;
    @BindView(R.id.rg_white)
    RadioGroup rgWhite;
    @BindView(R.id.btn_import_u)
    Button btnImportU;
    @BindView(R.id.btn_delete_all)
    Button btnDeleteAll;
    @BindView(R.id.lv_white)
    ListView lvWhite;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.tv_count)
    TextView tvCount;

    private ContentLoadingDialog loadingDialog;

    private List<WhiteItem> whiteItemList;
    private WhiteItemAdapter adapter;
    private Subscription mSubscription;
    private int max;
    private Config config;
    private WhiteItemDao whiteItemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    @OnClick(R.id.btn_back)
    void onGoBack() {
        config.setWhiteFlag(rbWhiteOn.isChecked());
        Face_App.getInstance().getDaoSession().getConfigDao().insertOrReplace(config);
        finish();
    }

    private void initData() {
        whiteItemDao = Face_App.getInstance().getDaoSession().getWhiteItemDao();
        config = Face_App.getInstance().getDaoSession().getConfigDao().loadByRowId(1L);
        EventBus.getDefault().register(this);
        whiteItemList = whiteItemDao.loadAll();
        adapter = new WhiteItemAdapter(this);
        adapter.setWhiteItemList(whiteItemList);
    }

    private void initView() {
        loadingDialog = new ContentLoadingDialog();
        loadingDialog.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.dismiss();
                mSubscription.cancel();
                mSubscription = null;
                adapter.notifyDataSetChanged();
                tvCount.setText("总计：" + whiteItemList.size() + " 条");
            }
        });
        lvWhite.setAdapter(adapter);
        rbWhiteOn.setChecked(config.getWhiteFlag());
        rbWhiteOff.setChecked(!config.getWhiteFlag());
        tvCount.setText("总计：" + whiteItemList.size() + " 条");
    }

    @OnClick(R.id.btn_import_u)
    void importCardNoFromU() {
        Flowable
                .create(new FlowableOnSubscribe<WhiteItem>() {
                    @Override
                    public void subscribe(FlowableEmitter<WhiteItem> e) throws Exception {
                        String whiteContent = FileUtil.readFromUSBPath(WhiteActivity.this, "白名单.txt");
                        if (TextUtils.isEmpty(whiteContent)) {
                            File whiteTxtFile = FileUtil.searchFileFromU(WhiteActivity.this, "白名单.txt");
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

                .subscribe(new Subscriber<WhiteItem>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1);
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(WhiteItem whiteItem) {
                        EventBus.getDefault().post(new LoadProgressEvent<>(max, whiteItemList.size(), whiteItem));
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
                    }
                });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadProgressEvent(LoadProgressEvent<WhiteItem> e) {
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
            if (e.getItem() != null) {
                whiteItemList.add(e.getItem());
            }
            loadingDialog.setMax(e.getMax());
            loadingDialog.setProgress(whiteItemList.size());
            if (e.getMax() == whiteItemList.size()) {
                loadingDialog.setMessage("导入完成！");
                loadingDialog.setButtonName(R.string.confirm);
                loadingDialog.setCancelable(true);
                whiteItemDao.insertInTx(whiteItemList);
            }
        }
        adapter.notifyDataSetChanged();
        tvCount.setText("总计：" + whiteItemList.size() + " 条");
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
                final ProgressDialog progressDialog = new ProgressDialog(WhiteActivity.this);
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
                                Face_App.getInstance().getDaoSession().getWhiteItemDao().deleteAll();
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) {
                                progressDialog.dismiss();
                                whiteItemList.clear();
                                adapter.notifyDataSetChanged();
                                tvCount.setText("总计：" + whiteItemList.size() + " 条");
                                AlertDialog alertDialog = new AlertDialog();
                                alertDialog.setAdContent("删除成功！");
                                alertDialog.show(getFragmentManager(), "da");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                progressDialog.dismiss();
                                tvCount.setText("总计：" + whiteItemList.size() + " 条");
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
