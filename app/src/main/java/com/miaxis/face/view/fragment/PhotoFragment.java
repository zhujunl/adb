package com.miaxis.face.view.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.app.OnFragmentInteractionListener;
import com.miaxis.face.bean.Config;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.event.CmdShutterPhotoEvent;
import com.miaxis.face.event.CutDownEvent;
import com.miaxis.face.manager.CameraManager;
import com.miaxis.face.manager.FaceManager;
import com.miaxis.face.manager.SoundManager;
import com.miaxis.face.util.MyUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * @author ZJL
 * @date 2022/5/16 14:15
 * @des
 * @updateAuthor
 * @updateDes
 */
public class PhotoFragment extends BaseFragment{

    @BindView(R.id.sv_main)
    SurfaceView sv_main;
    @BindView(R.id.tv_second)
    TextView tv_second;

    Camera mCamera;
    EventBus eventBus;
    private byte[] buffer;
    private Config config;
    private OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener){
            mListener=(OnFragmentInteractionListener) context;
        }else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }
    public PhotoFragment() {
    }

    @Override
    protected int initLayout() { return R.layout.fragment_preview; }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        eventBus=EventBus.getDefault();
        eventBus.register(this);
        config = Face_App.getInstance().getDaoSession().getConfigDao().loadByRowId(1L);
        CameraManager.getInstance().open(sv_main,mPreviewCallback,config.getRgb());
        mCamera=CameraManager.getInstance().getCamera();
        buffer=CameraManager.getInstance().getBuffer();
        tv_second.bringToFront();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventBus.unregister(this);
        CameraManager.getInstance().close();
    }

    boolean bit=false;

    Camera.PreviewCallback mPreviewCallback=new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            mCamera.addCallbackBuffer(buffer);
            if (!bit){
                Bitmap bitmap=FaceManager.getInstance().takePicture(bytes);
                bit=bitmap!=null;
                if (bit){
                    Matrix matrix = new Matrix();
                    matrix.setScale(0.5f, 0.5f);
                    Bitmap bmpFace = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    eventBus.post(new CmdShutterPhotoEvent(MyUtil.bitmapTo64(bmpFace)));
                    SoundManager.getInstance().playSound(Constants.SOUND_SUCCESS);
                    mListener.backToStack(2);
                }
            }
        }
    };

    @Subscribe(threadMode=ThreadMode.MAIN)
    public void onCutDownEvent(CutDownEvent event){
        tv_second.setVisibility(View.VISIBLE);
        tv_second.setText(String.valueOf(event.getTime()));
    }

}
