package com.miaxis.face.view.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;

import com.miaxis.face.R;
import com.miaxis.face.event.CmdShutterPhotoEvent;
import com.miaxis.face.manager.CameraManager;
import com.miaxis.face.util.MyUtil;

import org.greenrobot.eventbus.EventBus;

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

    Camera mCamera;
    EventBus eventBus;

    public PhotoFragment() {
    }

    @Override
    protected int initLayout() { return R.layout.fragment_preview; }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        eventBus=EventBus.getDefault();
        CameraManager.getInstance().open(sv_main);
        mCamera=CameraManager.getInstance().getCamera();
        if (mCamera!= null) {
            CameraManager.getInstance().takePicture(new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {
                    Matrix matrix = new Matrix();
                    matrix.setScale(0.5f, 0.5f);
                    Bitmap bmpFace = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    bmpFace = Bitmap.createBitmap(bmpFace, 0, 0, bmpFace.getWidth(), bmpFace.getHeight(), matrix, true);
                    eventBus.post(new CmdShutterPhotoEvent(MyUtil.bitmapTo64(bmpFace)));
                }
            });
            mCamera.startPreview();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CameraManager.getInstance().close();
    }


}
