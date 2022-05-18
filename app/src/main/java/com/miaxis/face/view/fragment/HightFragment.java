package com.miaxis.face.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.miaxis.face.R;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.constant.CameraConfig;
import com.miaxis.face.constant.CameraPreviewCallback;
import com.miaxis.face.constant.MXCamera;
import com.miaxis.face.constant.MXFrame;
import com.miaxis.face.constant.ZZResponse;
import com.miaxis.face.manager.CameraHelper;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.view.custom.PreviewPictureEntity;

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
//    @BindView(R.id.rv_content)
//    RecyclerView rvContent;

    private final String TAG="HightFragment";
    private List<PreviewPictureEntity> pathList = new ArrayList<>();
//    private PreviewPageAdapter mAdapter;
    private File mFilePath;
    private final static Handler mHandler = new Handler();
    private Disposable subscribe;

    public HightFragment() {
    }

    @Override
    protected int initLayout() { return R.layout.fragment_high; }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mFilePath=new File(FileUtil.FACE_MAIN_PATH+ File.separator+"height_camera");
        sv.getHolder().addCallback(callback);
        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ZZResponse<MXCamera> mxCamera = CameraHelper.getInstance().find(CameraConfig.Camera_SM);
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
                CameraHelper.getInstance().free();
            }
        });

//        mAdapter=new PreviewPageAdapter(getActivity(), pathList, new PreviewPageAdapter.PreViewPageClick() {
//            @Override
//            public void Click(int position) {
//                PreviewPictureEntity str = mAdapter.getPathList().get(position);
//                showBigPicture(str.getPath());
//            }
//
//            @Override
//            public void LongClck(final int position){
//                new AlertDialog.Builder(getContext())
//                        .setTitle("注意")
//                        .setMessage("是否删除此图片")
//                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                pathList.remove(position);
//                                mAdapter.setList(pathList);
//                            }
//                        })
//                        .show();
//            }
//        });
//        rvContent.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
//        rvContent.setAdapter(mAdapter);

    }


    private void showBigPicture(String path) {
//        android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(getActivity());
//        View v= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_to_view_big_picture,null,false);
////        ZoomImageView img=v.findViewById(R.id.img);
////        Glide.with(getContext()).load(new File(path)).into(img);
//        builder.setView(v);
//        android.support.v7.app.AlertDialog alert=builder.create();
//        alert.show();
//        new ToViewBigPictureDialog(getContext(), new ToViewBigPictureDialog.ClickListener() {
//        }, new ToViewBigPictureDialog.Builder().setPathFile(path)).show();
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
                                                            final String base64Path = FileUtil.pathToBase64(absolutePath);
                                                            mHandler.post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (pathList.size() >= 3) {
                                                                        Toast.makeText(getActivity(), "最多只能拍摄3张照片", Toast.LENGTH_SHORT).show();
                                                                        return;
                                                                    }
                                                                    Log.e(TAG, "获取图片 %s" + absolutePath);
                                                                    PreviewPictureEntity entity = new PreviewPictureEntity();
                                                                    entity.setBase64(base64Path);
                                                                    entity.setPath(absolutePath);
                                                                    pathList.add(entity);
//                                                                    mAdapter.setList(pathList);
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


}
