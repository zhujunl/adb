package com.miaxis.face.view.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.miaxis.face.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultView extends LinearLayout {

    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.iv_finger_result)
    ImageView ivFingerResult;
    @BindView(R.id.iv_camera_photo)
    ImageView ivCameraPhoto;
    @BindView(R.id.iv_result)
    ImageView ivResult;
    @BindView(R.id.iv_id_photo)
    ImageView ivIdPhoto;

    public ResultView(Context context) {
        super(context);
        init();
    }

    public ResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View v =  inflate(getContext(), R.layout.view_result, this);
        ButterKnife.bind(this, v);
        bringToFront();
        Glide.with(this).load(R.raw.put_finger).into(ivFingerResult);
//        GlideApp.with(this).load(R.raw.put_finger).into(ivFingerResult);
        setVisibility(INVISIBLE);
    }

    public void clear() {
        Glide.with(this).load(R.raw.put_finger).into(ivFingerResult);
        Glide.with(this).clear(ivResult);
//        GlideApp.with(this).load(R.raw.put_finger).into(ivFingerResult);
//        GlideApp.with(this).clear(ivResult);
        tvResult.setText("");
        showCardImage(null);
        showCameraImage(null);
    }

    public void setFingerMode(boolean mode) {
        ivFingerResult.setVisibility(mode ? View.VISIBLE : View.GONE);
    }

    public void setResultMessage(String message) {
        tvResult.setText(message);
    }

    public void showCardImage(Bitmap bitmap) {
        if (bitmap != null) {
            Glide.with(this).load(bitmap).into(ivIdPhoto);
//            GlideApp.with(this).load(bitmap).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(ivIdPhoto);
        } else {
            Glide.with(this).clear(ivIdPhoto);
        }
    }

    public void showCameraImage(Bitmap bitmap) {
        if (bitmap != null) {
            Glide.with(this).load(bitmap).into(ivCameraPhoto);
        } else {
            Glide.with(this).clear(ivCameraPhoto);
        }
    }

    public void setFaceResult(boolean result) {
        Glide.with(this).load(result ? R.drawable.result_true : R.drawable.result_false).into(ivResult);
    }

    public void setFingerResult(boolean result) {
        Glide.with(this).load(result ? R.drawable.finger_succes : R.drawable.finger_fail).into(ivFingerResult);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

}
