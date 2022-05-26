package com.miaxis.face.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.miaxis.face.R;
import com.miaxis.face.adapter.PreviewPageAdapter;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.app.OnFragmentInteractionListener;
import com.miaxis.face.bean.Config;
import com.miaxis.face.constant.CameraConfig;
import com.miaxis.face.constant.CameraPreviewCallback;
import com.miaxis.face.constant.MXCamera;
import com.miaxis.face.constant.MXFrame;
import com.miaxis.face.constant.ZZResponse;
import com.miaxis.face.event.CmdSmDoneEvent;
import com.miaxis.face.event.CutDownEvent;
import com.miaxis.face.manager.CameraHelper;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.util.MyUtil;
import com.miaxis.face.view.custom.PreviewPictureEntity;
import com.miaxis.face.view.custom.ZoomImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.Luban;

/**
 * @author ZJL
 * @date 2022/5/17 16:09
 * @des
 * @updateAuthor
 * @updateDes
 */
public class HightFragment extends BaseFragment{

    @BindView(R.id.sv)
    SurfaceView sv;
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

    private final String TAG="HightFragment";
    private List<PreviewPictureEntity> pathList = new ArrayList<>();
    private PreviewPageAdapter mAdapter;
    private File mFilePath;
    private final static Handler mHandler = new Handler();
    private Disposable subscribe;
    private Config config;
    private EventBus eventbus;
    private OnFragmentInteractionListener mListener;

    public HightFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener){
            mListener=(OnFragmentInteractionListener) context;
        }else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    protected int initLayout() { return R.layout.fragment_high; }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mFilePath=new File(FileUtil.FACE_MAIN_PATH+ File.separator+"height_camera");
        eventbus=EventBus.getDefault();
        eventbus.register(this);
        config=Face_App.getInstance().getDaoSession().getConfigDao().loadByRowId(1L);
        sv.getHolder().addCallback(callback);
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
                    Toast.makeText(HightFragment.this.getActivity(), "最多只能拍摄3张照片", Toast.LENGTH_SHORT).show();
                    return;
                }
                ZZResponse<MXCamera> mxCamera = CameraHelper.getInstance().find(CameraConfig.Camera_SM);
                if (ZZResponse.isSuccess(mxCamera)) {
                    mxCamera.getData().setNextFrameEnable();
                }
            }
        });

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pathList.size()>0){
                    Bitmap bitmap =pathList.get(0).getBase64();
                    Matrix matrix = new Matrix();
                    matrix.setScale(0.5f, 0.5f);
                    Bitmap bit = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    eventbus.post(new CmdSmDoneEvent(MyUtil.bitmapTo64(bit)));
                    CameraHelper.getInstance().free();
                   Face_App.getInstance().getThreadExecutor().execute(new Runnable() {
                       @Override
                       public void run() {
                           mListener.showWaitDialog("正在上传中，请稍后");
                           SystemClock.sleep(1000);
                           mListener.dismissWaitDialog("上传成功");
                           mListener.backToStack(null);
                       }
                   });
//                    Toast.makeText(getActivity(), "上传完成", Toast.LENGTH_SHORT).show();
//                    FragmentActivity activity = getActivity();
//                    if (activity instanceof BaseActivity) {
//                        ((BaseActivity) activity).getNvController().back();
//                    }
                }
            }
        });

        mAdapter=new PreviewPageAdapter(getActivity(), pathList, new PreviewPageAdapter.PreViewListner() {
            @Override
            public void onClick(int position) {
                PreviewPictureEntity str = mAdapter.getPathList().get(position);
                showBigPicture(str.getPath());
            }

            @Override
            public void onLongClick(final int position){
                new android.app.AlertDialog.Builder(getContext())
                        .setTitle("注意")
                        .setMessage("是否删除此图片")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PreviewPictureEntity entity=pathList.get(position);
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


    private void showBigPicture(String path) {
        android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(getActivity());
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_to_view_big_picture,null,false);
        ZoomImageView img=v.findViewById(R.id.img);
        Glide.with(getContext()).load(new File(path)).into(img);
        builder.setView(v);
        android.support.v7.app.AlertDialog alert=builder.create();
        alert.show();
    }

    private final SurfaceHolder.Callback callback= new SurfaceHolder.Callback(){

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            ZZResponse<?> init = CameraHelper.getInstance().init();
            if (ZZResponse.isSuccess(init)) {
                ZZResponse<MXCamera> mxCamera = CameraHelper.getInstance().createMXCamera(CameraConfig.Camera_SM);
                if (ZZResponse.isSuccess(mxCamera)) {
                    sBar.setMax(mxCamera.getData().getMaxZoom());
                    mxCamera.getData().setPreviewCallback(new CameraPreviewCallback() {
                        @Override
                        public void onPreview(MXCamera camera, final MXFrame frame) {
                            if (MXFrame.isNullCamera(frame)) {
                                return;
                            }
                            Face_App.getInstance().getThreadExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    String fileName = "sm" + System.currentTimeMillis() + ".jpg";
                                    File file = new File(mFilePath, fileName);
                                    //保存图片
                                    boolean frameImage = frame.camera.saveFrameImage(file.getAbsolutePath());
                                    if (frameImage) {
                                        //再次进行压缩图片
                                        subscribe = Flowable.just(file)
                                                .observeOn(Schedulers.io())
                                                .map(new Function<File, Object>() {
                                                    @Override
                                                    public Object apply(@android.support.annotation.NonNull File f) throws Exception {
                                                        // 同步方法直接返回压缩后的文件
                                                        List<File> files = Luban.with(getContext()).load(f).setTargetDir(mFilePath.getAbsolutePath()).get();
                                                        if (files != null && files.size() != 0) {
                                                            final String absolutePath = files.get(0).getAbsolutePath();
                                                            final Bitmap base64Path = FileUtil.pathToBit(absolutePath);
                                                            mHandler.post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (pathList.size() >= 3) {
                                                                        Toast.makeText(getActivity(), "最多只能拍摄3张照片", Toast.LENGTH_SHORT).show();
                                                                        return;
                                                                    }
                                                                    Log.e(TAG, "获取图片 %s" + absolutePath);
                                                                    PreviewPictureEntity entity = new PreviewPictureEntity(absolutePath,base64Path);
                                                                    pathList.add(entity);
//                                                                    mAdapter.addPathList(entity);
                                                                    mAdapter.notifyDataSetChanged();
                                                                }
                                                            });
                                                        }
                                                        return files;
                                                    }
                                                })
                                                .subscribe();
                                    }
                                }
                            });
                        }
                    });
                    mxCamera.getData().start(holder);
                }
            }
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            CameraHelper.getInstance().free();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventbus.unregister(this);
        CameraHelper.getInstance().free();
        pathList.clear();
    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    public void onCutDownEvent(CutDownEvent event){
        tv_second.setVisibility(View.VISIBLE);
        tv_second.setText(String.valueOf(event.getTime()));
    }
}
