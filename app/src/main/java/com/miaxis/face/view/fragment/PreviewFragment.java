package com.miaxis.face.view.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.MxRGBImage;
import com.miaxis.face.bean.PhotoFaceFeature;
import com.miaxis.face.bean.Record;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.event.CutDownEvent;
import com.miaxis.face.event.ReadCardEvent;
import com.miaxis.face.event.ResultEvent;
import com.miaxis.face.greendao.gen.RecordDao;
import com.miaxis.face.manager.CameraManager;
import com.miaxis.face.manager.FaceManager;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.util.MyUtil;
import com.miaxis.face.view.activity.BaseActivity;
import com.miaxis.face.view.custom.RectSurfaceView;
import com.miaxis.face.view.custom.ResultView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zz.api.MXFaceInfoEx;

import butterknife.BindView;

/**
 * @author ZJL
 * @date 2022/5/12 14:12
 * @des
 * @updateAuthor
 * @updateDes
 */
public class PreviewFragment extends BaseFragment {

    @BindView(R.id.tv_second)
    TextView tv_second;
    @BindView(R.id.sv_main)
    SurfaceView sv_main;
    @BindView(R.id.sv_preview_nir)
    SurfaceView sv_preview_nir;
    @BindView(R.id.rsv_rect)
    RectSurfaceView rsv_rect;
    @BindView(R.id.rv_result)
    ResultView rv_result;
    @BindView(R.id.tv_pass)
    TextView tv_pass;
    @BindView(R.id.fl_camera_root)
    FrameLayout fl_camera_root;

    private final String TAG="PreviewFragment";
    private Record record;
    private Bitmap cardimg=null;
    private EventBus eventbus;
    private RecordDao recordDao;
    private Config config;


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

        config = Face_App.getInstance().getDaoSession().getConfigDao().loadByRowId(1L);
        FaceManager.getInstance().startLoop();
        FaceManager.getInstance().setFaceHandleListener(faceListener);
        recordDao = Face_App.getInstance().getDaoSession().getRecordDao();
        CameraManager.getInstance().open(sv_main,config.getRgb());
        CameraManager.getInstance().nir_open(sv_preview_nir,config.getNir(),true);
        tv_second.bringToFront();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventbus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,priority = 1)
    public void onCardImgEvent(ReadCardEvent event){
        Log.e(TAG, "onCardImgEvent" );
        cardimg=event.getFace();
        record=event.getRecord();

        rsv_rect.bringToFront();
        rsv_rect.setRootSize(fl_camera_root.getWidth(), fl_camera_root.getHeight());
        rsv_rect.setZoomRate((float) fl_camera_root.getWidth() / Constants.PRE_WIDTH);

        rv_result.bringToFront();
        rv_result.clear();
        rv_result.showCardImage(cardimg);
        rv_result.setVisibility(View.VISIBLE);
    }

    public void drawFaceRect(MXFaceInfoEx[] faceInfo, int faceNum) {
        rsv_rect.drawRect(faceInfo, faceNum);
    }

    private void onFaceVerify(MxRGBImage mxRGBImage, MXFaceInfoEx mxFaceInfoEx, byte[] feature,Bitmap bitmap) {
            PhotoFaceFeature cardFaceFeature = FaceManager.getInstance().getCardFaceFeatureByBitmapPosting(bitmap);
            float faceMatchScore = FaceManager.getInstance().matchFeature(feature, cardFaceFeature.getFaceFeature());
            Log.e(TAG, "onFaceVerify==" + faceMatchScore);
            if(faceMatchScore>0.1){
                Bitmap bit=null;
                byte[] fileImage = FaceManager.getInstance().imageEncode(mxRGBImage.getRgbImage(), mxRGBImage.getWidth(), mxRGBImage.getHeight());
                Bitmap faceBitmap = BitmapFactory.decodeByteArray(fileImage, 0, fileImage.length);
                Matrix matrix = new Matrix();
                matrix.setScale(0.5f, 0.5f);
                bit = Bitmap.createBitmap(faceBitmap, 0, 0, faceBitmap.getWidth(), faceBitmap.getHeight(), matrix, true);
                record.setFaceImgData(MyUtil.getBytesByBitmap(bit));
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

    @Subscribe(threadMode=ThreadMode.MAIN)
    public void onCutDownEvent(CutDownEvent event){
        tv_second.setVisibility(View.VISIBLE);
        tv_second.setText(String.valueOf(event.getTime()));
    }

}
