package com.miaxis.face.view.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miaxis.callback.FaceCallback;
import com.miaxis.face.R;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.bean.MxRGBImage;
import com.miaxis.face.bean.PhotoFaceFeature;
import com.miaxis.face.bean.Record;
import com.miaxis.face.constant.CameraConfig;
import com.miaxis.face.constant.CameraPreviewCallback;
import com.miaxis.face.constant.MXCamera;
import com.miaxis.face.constant.MXFrame;
import com.miaxis.face.constant.ZZResponse;
import com.miaxis.face.event.ReadCardEvent;
import com.miaxis.face.event.ResultEvent;
import com.miaxis.face.greendao.gen.RecordDao;
import com.miaxis.face.manager.CameraHelper;
import com.miaxis.face.manager.CameraManager;
import com.miaxis.face.manager.FaceManager;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.util.MyUtil;
import com.miaxis.face.view.activity.BaseActivity;
import com.miaxis.face.view.custom.RectSurfaceView;
import com.miaxis.face.view.custom.ResultLayout;
import com.miaxis.sdt.bean.IdCard;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zz.api.MXFaceInfoEx;

import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author ZJL
 * @date 2022/5/12 14:12
 * @des
 * @updateAuthor
 * @updateDes
 */
public class PreviewFragment extends BaseFragment implements FaceCallback {

    @BindView(R.id.tv_second)
    TextView tv_second;
    @BindView(R.id.sv_main)
    SurfaceView sv_main;
    @BindView(R.id.sv_preview_nir)
    SurfaceView sv_preview_nir;
    @BindView(R.id.rsv_rect)
    RectSurfaceView rsv_rect;
    @BindView(R.id.rv_result)
    ResultLayout rv_result;
    @BindView(R.id.tv_pass)
    TextView tv_pass;
    @BindView(R.id.fl_camera_root)
    FrameLayout fl_camera_root;

    private final String TAG="PreviewFragment";
    private Record record;
    private Bitmap cardimg=null;
    private EventBus eventbus;
    private RecordDao recordDao;



    public static PreviewFragment newIntent(){
        return new PreviewFragment();
    }

    public PreviewFragment() {
    }

    FaceManager.OnFaceHandleListener faceListener=new FaceManager.OnFaceHandleListener() {
        @Override
        public void onFeatureExtract(MxRGBImage mxRGBImage, MXFaceInfoEx mxFaceInfoEx, byte[] feature) {
            if (cardimg!=null){
                onFaceVerify(mxRGBImage, mxFaceInfoEx, feature,cardimg);
            }
        }

        @Override
        public void onFaceDetect(int faceNum, MXFaceInfoEx[] faceInfoExes) {
            Log.e(TAG, "onFaceDetect");
            drawFaceRect(faceInfoExes, faceNum);
        }
    };

    @Override
    protected int initLayout() {
        return R.layout.fragment_preview;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        eventbus=EventBus.getDefault();
        eventbus.register(this);
        FaceManager.getInstance().startLoop();
        FaceManager.getInstance().setFaceHandleListener(faceListener);
        recordDao = Face_App.getInstance().getDaoSession().getRecordDao();
        CameraManager.getInstance().open(sv_main);
//        CameraManager.getInstance().nir_open(sv_preview_nir);
        rsv_rect.bringToFront();

        rsv_rect.setRootSize(fl_camera_root.getWidth(), fl_camera_root.getHeight());
        rsv_rect.setZoomRate((float) fl_camera_root.getWidth() / fl_camera_root.getHeight());

//        CameraManager.getInstance().nir_open(sv_preview_nir);


//        sv_main.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder surfaceHolder) {
////                showRgbCameraPreview(surfaceHolder);
//                CameraManager.getInstance().open(surfaceHolder);
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//
//            }
//        });
//
//        sv_preview_nir.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder surfaceHolder) {
////                SurfaceHolder_Nir.set(surfaceHolder);
//                CameraManager.getInstance().nir_open(surfaceHolder);
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventbus.unregister(this);
    }

    private void setRecord(IdCard idCard){
        record=new Record();

    }

    private final static Handler mHandler = new Handler();


    @Override
    public void onRgbProcessReady() {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                ZZResponse<MXCamera> mxCameraNir = CameraHelper.getInstance().createMXCamera(CameraConfig.Camera_NIR);
//                if (ZZResponse.isSuccess(mxCameraNir)) {
//                    //开启NIR近红外视频流
//                    mxCameraNir.getData().setNextFrameEnable();
//                }
//            }
//        });
    }

    @Override
    public void onLiveReady(MXFrame nirFrame, boolean success) {

    }

    @Override
    public void onMatchReady(boolean success) {

    }

    @Override
    public void onError(ZZResponse<?> response) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN,priority = 1)
    public void onCardImgEvent(ReadCardEvent event){
        Log.e(TAG, "onCardImgEvent" );
        cardimg=event.getFace();
        record=event.getRecord();
    }

    public void drawFaceRect(MXFaceInfoEx[] faceInfo, int faceNum) {
        rsv_rect.drawRect(faceInfo, faceNum);
    }

    private void onFaceVerify(MxRGBImage mxRGBImage, MXFaceInfoEx mxFaceInfoEx, byte[] feature,Bitmap bitmap) {
            PhotoFaceFeature cardFaceFeature = FaceManager.getInstance().getCardFaceFeatureByBitmapPosting(bitmap);
            float faceMatchScore = FaceManager.getInstance().matchFeature(feature, cardFaceFeature.getFaceFeature());
            Log.e(TAG, "onFaceVerify==" + faceMatchScore);
            if(faceMatchScore>0.1){
                byte[] fileImage = FaceManager.getInstance().imageEncode(mxRGBImage.getRgbImage(), mxRGBImage.getWidth(), mxRGBImage.getHeight());
                Bitmap faceBitmap = BitmapFactory.decodeByteArray(fileImage, 0, fileImage.length);
                record.setFaceImgData(MyUtil.getBytesByBitmap(faceBitmap));
                FileUtil.saveRecordImg(record, getActivity());
                recordDao.insert(record);
                FaceManager.getInstance().stopLoop();
                FragmentActivity activity = getActivity();
                eventbus.post(new ResultEvent(ResultEvent.FACE_SUCCESS, record));
                if (activity instanceof BaseActivity) {
                    ((BaseActivity) activity).getNvController().back();
                }
            }
    }

    /**
     * 开启可见光预览
     */
    public void showRgbCameraPreview(final SurfaceHolder surface) {
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<ZZResponse<MXCamera>>() {
            @Override
            public void subscribe(@android.support.annotation.NonNull ObservableEmitter<ZZResponse<MXCamera>> emitter) throws Exception {
                ZZResponse<MXCamera> mxCamera = CameraHelper.getInstance().createMXCamera(CameraConfig.Camera_RGB);
                if (ZZResponse.isSuccess(mxCamera)) {
                    MXCamera camera = mxCamera.getData();
                    int startTexture = camera.start(surface);
                    if (startTexture == 0) {
                        emitter.onNext(mxCamera);
                    } else {

                    }
                } else {
                    emitter.onNext(mxCamera);
                }
                //            } else {
//                                emitter.onNext(1);
                //            }
            }
        }).subscribeOn(Schedulers.from(Face_App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ZZResponse<MXCamera>>() {
                    @Override
                    public void accept(ZZResponse<MXCamera> camera) throws Exception {
                        if (ZZResponse.isSuccess(camera)) {
                            camera.getData().setPreviewCallback(new CameraPreviewCallback() {
                                @Override
                                public void onPreview(MXCamera camera, MXFrame frame) {
                                    Log.e(TAG, "onPreview:  " + "可见光");
                                    //            CameraManager.getInstance().processRgbFrame(frame,PreviewFragment.this);
                                    Process_Rgb(frame);
                                }
                            });
                            startNirPreview(null);
                        }
                        startRgbFrame();
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });
    }

    AtomicReference<SurfaceHolder> SurfaceHolder_Nir = new AtomicReference<>();

    /**
     * 开启近红外预览
     */
    private void startNirPreview(final SurfaceHolder surface) {
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<ZZResponse<MXCamera>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ZZResponse<MXCamera>> emitter) throws Exception {
                ZZResponse<MXCamera> mxCamera = CameraHelper.getInstance().createMXCamera(CameraConfig.Camera_NIR);
                if (ZZResponse.isSuccess(mxCamera)) {
                    MXCamera camera = mxCamera.getData();
                    Log.e(TAG, "startNirPreview  SurfaceHolder_Nir" + SurfaceHolder_Nir.get());
                    int start = camera.start(SurfaceHolder_Nir.get());
                    if (start == 0) {
                        emitter.onNext(mxCamera);
                    } else {

                    }
                } else {
                    emitter.onNext(mxCamera);
                }
            }
        }).subscribeOn(Schedulers.from(Face_App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ZZResponse<MXCamera>>() {
                    @Override
                    public void accept(ZZResponse<MXCamera> camera) throws Exception {
                        if (ZZResponse.isSuccess(camera)) {
                            Log.e(TAG, "onPreview:  " );
                            camera.getData().setPreviewCallback(new CameraPreviewCallback() {
                                @Override
                                public void onPreview(MXCamera camera, MXFrame frame) {
                                    Log.e(TAG, "onPreview:  " + "近红外");
                                    Process_Nir(frame);
                                }
                            });
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 开启可见光视频帧
     */
    private void startRgbFrame() {
        ZZResponse<MXCamera> mxCamera = CameraHelper.getInstance().createMXCamera(CameraConfig.Camera_RGB);
        if (ZZResponse.isSuccess(mxCamera)) {
            int enable = mxCamera.getData().setNextFrameEnable();
//            this.IsCameraEnable_Rgb.setValue(ZZResponse.CreateSuccess());
        } else {

        }
    }

    /**
     * 开启近红外视频帧
     * 主线程
     */
    private void startNirFrame() {
        ZZResponse<MXCamera> mxCamera = CameraHelper.getInstance().createMXCamera(CameraConfig.Camera_NIR);
        if (ZZResponse.isSuccess(mxCamera)) {
            int enable = mxCamera.getData().setNextFrameEnable();
        } else {

        }
    }


    /**
     * 处理可见光视频帧数据
     */
    private synchronized void Process_Rgb(MXFrame frame) {
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@android.support.annotation.NonNull ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
            }
        }).subscribeOn(Schedulers.from(Face_App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer list) throws Exception {
                        startNirFrame();
                        startRgbFrame();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        startRgbFrame();
                    }
                });
    }

    /**
     * 处理近红外视频帧数据
     */
    private void Process_Nir(MXFrame frame) {
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@android.support.annotation.NonNull ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(0);
            }
        }).subscribeOn(Schedulers.from(Face_App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer list) throws Exception {
                        processLiveAndMatch();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 处理活体和比对
     */
    private void processLiveAndMatch() {
//        Disposable subscribe = Observable.create((ObservableOnSubscribe<ZZResponse<AttendanceBean>>) emitter -> {
//            Map.Entry<MxImage, MXFace> rgbEntry = this.CurrentMxImage_Rgb.get();
//            Map.Entry<MxImage, MXFace> nirEntry = this.CurrentMxImage_Nir.get();
//            if (rgbEntry == null || nirEntry == null
//                    || rgbEntry.getKey() == null || nirEntry.getKey() == null
//                    || rgbEntry.getKey().isBufferEmpty() || !rgbEntry.getKey().isSizeLegal()
//                    || rgbEntry.getValue() == null || nirEntry.getValue() == null
//                    || rgbEntry.getKey().isBufferEmpty() || !rgbEntry.getKey().isSizeLegal()) {
//                emitter.onNext(ZZResponse.CreateFail(ZZResponseCode.CODE_ILLEGAL_PARAMETER, "正在检测"));
//            } else {
//                MxImage rgbImage = rgbEntry.getKey();
//                MXFace rgbFace = rgbEntry.getValue();
//                //可见光活体判断
//                MXResult<?> rgbResult = MXFaceIdAPI.getInstance().mxRGBLiveDetect(rgbImage.buffer, rgbImage.width, rgbImage.height, rgbFace);
//                if (!MXResult.isSuccess(rgbResult)) {
//                    emitter.onNext(ZZResponse.CreateFail(rgbResult.getCode(), rgbResult.getMsg()));
//                    return;
//                }
//                //3. 近红外人脸检测
//                //4. 近红外活体检测
//                MxImage nirImage = nirEntry.getKey();
//                MXFace nirFace = nirEntry.getValue();
//                MXResult<Integer> nirResult = MXFaceIdAPI.getInstance().mxNIRLiveDetect(nirImage.buffer, nirImage.width, nirImage.height, nirFace);
//                if (!MXResult.isSuccess(nirResult)) {
//                    emitter.onNext(ZZResponse.CreateFail(nirResult.getCode(), nirResult.getMsg()));
//                    return;
//                }
//                if (nirResult.getData() < MXFaceIdAPI.getInstance().FaceLive) {
//                    emitter.onNext(ZZResponse.CreateFail(-76, "非活体"));
//                    //emitter.onNext(ZZResponse.CreateFail(-76, "非活体，value:" + nirResult.getData()));
//                    return;
//                }
//
//                //5.比对
//                //5.1提取特征
//                MXResult<byte[]> featureExtract = MXFaceIdAPI.getInstance().mxFeatureExtract(rgbImage.buffer, rgbImage.width, rgbImage.height, rgbFace);
//                if (!MXResult.isSuccess(featureExtract)) {
//                    emitter.onNext(ZZResponse.CreateFail(featureExtract.getCode(), featureExtract.getMsg()));
//                    return;
//                }
//                HashMap<String, Face> all = FaceModel.findAll();
//                if (MapUtils.isNullOrEmpty(all)) {
//                    saveFailedAttendance(rgbImage, rgbFace);
//                    emitter.onNext(ZZResponse.CreateFail(-80, "人脸数据库为空"));
//                    return;
//                }
//                Face tempFace = null;
//                float tempFloat = 0F;
//                for (Map.Entry<String, Face> entry : all.entrySet()) {
//                    Face value = entry.getValue();
//                    if (value != null) {
//                        MXResult<Float> result = MXFaceIdAPI.getInstance().mxFeatureMatch(featureExtract.getData(), value.FaceFeature);
//                        if (MXResult.isSuccess(result)) {
//                            if (result.getData() >= tempFloat) {
//                                tempFace = value;
//                                tempFloat = result.getData();
//                            }
//                        }
//                    }
//                }
//                if (tempFloat < MXFaceIdAPI.getInstance().FaceMatch) {
//                    saveFailedAttendance(rgbImage, rgbFace);
//                    emitter.onNext(ZZResponse.CreateFail(-81, "正在识别中，最大匹配值：" + tempFloat));
//                    return;
//                }
//                if (lastUserID != null && lastUserID.equals(tempFace.UserId) && (System.currentTimeMillis() - lastTime) <= AppConfig.verifyTimeOut) {
//                    emitter.onNext(ZZResponse.CreateFail(-83, "重复识别"));
//                    return;
//                }
//                lastUserID=tempFace.UserId;
//                lastTime = System.currentTimeMillis();
//                Person person = PersonModel.findByUserID(tempFace.UserId);
//                if (person == null) {
//                    saveFailedAttendance(rgbImage, rgbFace);
//                    emitter.onNext(ZZResponse.CreateFail(-82, "该人员不存在，人员ID：" + tempFace.UserId));
//                    return;
//                }
//                //识别通过
//
//                String capturePath = AppConfig.Path_CaptureImage + "face" + "_" + person.UserId + "_" + System.currentTimeMillis() + ".jpeg";
//                MXResult<?> save = MXImageToolsAPI.getInstance().ImageSave(capturePath, rgbImage.buffer, rgbImage.width, rgbImage.height, 3);
//                if (!MXResult.isSuccess(save)) {
//                    emitter.onNext(ZZResponse.CreateFail(save.getCode(), save.getMsg()));
//                    return;
//                }
//                LocalImage captureLocalImage = new LocalImage();
//                captureLocalImage.LocalPath = capturePath;
//                captureLocalImage.id = LocalImageModel.insert(captureLocalImage);
//                if (captureLocalImage.id <= 0) {
//                    emitter.onNext(ZZResponse.CreateFail(-70, "保存图片记录失败"));
//                    return;
//                }
//                Rect faceRect = rgbFace.getFaceRect();
//                faceRect.left = Math.max((int) (faceRect.left * 0.8F), 0);
//                faceRect.top = Math.max((int) (faceRect.top * 0.7F), 0);
//                faceRect.right = Math.min((int) (faceRect.right * 1.2F), 480);
//                faceRect.bottom = Math.min((int) (faceRect.bottom * 1.1F), 640);
//                MXResult<MxImage> cutRect = MXImageToolsAPI.getInstance().ImageCutRect(rgbImage, faceRect);
//                if (!MXResult.isSuccess(cutRect)) {
//                    emitter.onNext(ZZResponse.CreateFail(cutRect.getCode(), cutRect.getMsg()));
//                    return;
//                }
//                MxImage cutImage = cutRect.getData();
//                String cutPath = AppConfig.Path_CutImage + person.UserId + "_" + System.currentTimeMillis() + ".jpeg";
//                MXResult<?> cutSave = MXImageToolsAPI.getInstance().ImageSave(cutPath, cutImage.buffer, cutImage.width, cutImage.height, 3);
//                if (!MXResult.isSuccess(cutSave)) {
//                    emitter.onNext(ZZResponse.CreateFail(cutSave.getCode(), cutSave.getMsg()));
//                    return;
//                }
//                LocalImage cutLocalImage = new LocalImage();
//                cutLocalImage.LocalPath = cutPath;
//                cutLocalImage.id = LocalImageModel.insert(cutLocalImage);
//                if (cutLocalImage.id <= 0) {
//                    emitter.onNext(ZZResponse.CreateFail(-71, "保存人脸截图记录失败"));
//                    return;
//                }
//                Attendance attendance = new Attendance();
//                attendance.UserId = person.UserId;
//                //attendance.BaseImage = person.FaceImage;
//                attendance.CaptureImage = captureLocalImage.id;
//                attendance.CutImage = cutLocalImage.id;
//                attendance.Mode = 1;
//                attendance.Status = 1;
//                attendance.id = AttendanceModel.insert(attendance);
//                if (attendance.id <= 0) {
//                    emitter.onNext(ZZResponse.CreateFail(-60, "保存考勤记录失败"));
//                    return;
//                }
//                //List<LocalImage> byID = LocalImageModel.findByID(person.FaceImage);
//                //if (ListUtils.isNullOrEmpty(byID)) {
//                //    emitter.onNext(ZZResponse.CreateFail(-61, "该人员不存在，UserId：" + tempFace.UserId));
//                //    return;
//                //}
//                AttendanceBean attendanceBean = new AttendanceBean();
//                attendanceBean.AttendanceId = attendance.id;
//                attendanceBean.Status = 1;
//                attendanceBean.Mode = 1;
//                attendanceBean.UserId = person.UserId;
//                attendanceBean.CaptureImage = capturePath;
//                attendanceBean.CutImage = cutPath;
//                attendanceBean.UserName = person.Name;
//                //                attendanceBean.tempFloat=new DecimalFormat("##0.00").format(tempFloat);
//                attendanceBean.tempFloat=tempFloat;
//                attendanceBean.tempType=0;
//                //attendanceBean.BaseImage = byID.get(0).LocalPath;
//                //开启门禁
//                emitter.onNext(ZZResponse.CreateSuccess(attendanceBean));
//            }
//        }).subscribeOn(Schedulers.from(App.getInstance().threadExecutor))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(response -> {
//                    Timber.e("processLiveAndMatch: " + response);
//                    this.IsNirEnable.set(true);
//                    this.AttendanceBean.setValue(response);
//                    this.IsNirFrameProcessing.set(false);
//                    this.CurrentMxImage_Rgb.set(null);
//                    this.CurrentMxImage_Nir.set(null);
//                }, throwable -> {
//                    this.IsNirEnable.set(true);
//                    this.AttendanceBean.setValue(ZZResponse.CreateFail(-99, throwable.getMessage()));
//                    this.IsNirFrameProcessing.set(false);
//                    this.CurrentMxImage_Rgb.set(null);
//                    this.CurrentMxImage_Nir.set(null);
//                });
    }
}
