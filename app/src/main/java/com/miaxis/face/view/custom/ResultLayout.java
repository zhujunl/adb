//package com.miaxis.face.view.custom;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.text.TextUtils;
//import android.util.AttributeSet;
//import android.util.Base64;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.miaxis.face.R;
//import com.miaxis.face.bean.Record;
//import com.miaxis.face.event.CmdGetFingerDoneEvent;
//import com.miaxis.face.event.CmdGetFingerEvent;
//import com.miaxis.face.event.CmdIdCardDoneEvent;
//import com.miaxis.face.event.NoCardEvent;
//import com.miaxis.face.event.ResultEvent;
//
//import com.miaxis.face.util.LogUtil;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//import org.zz.api.MXFaceInfoEx;
//
//import butterknife.BindString;
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
///**
// * Created by Administrator on 2017/5/22 0022.
// */
//
//public class ResultLayout extends LinearLayout {
//
//    @BindView(R.id.gif_finger)
//    GifView gifFinger;
//    @BindView(R.id.tv_result)
//    TextView tvResult;
//    @BindView(R.id.iv_camera_photo)
//    ImageView ivCameraPhoto;
//    @BindView(R.id.iv_result)
//    ImageView ivResult;
//    @BindView(R.id.iv_id_photo)
//    ImageView ivIdPhoto;
//
//    @BindString(R.string.result_success)
//    String result_success;
//    @BindString(R.string.result_failure)
//    String result_failure;
//    @BindString(R.string.result_press)
//    String result_press;
//    @BindView(R.id.iv_finger_result)
//    ImageView ivFingerResult;
//    @BindView(R.id.ll_card_img)
//    LinearLayout llCardImg;
//    private String lastCardNo;
//
//    private EventBus eventBus;
//    private boolean isGettingFinger;
//
//    public ResultLayout(Context context) {
//        super(context);
//        init();
//    }
//
//    public ResultLayout(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }
//
//    public ResultLayout(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//    }
//
//    private void init() {
//        View v = inflate(getContext(), R.layout.view_result, this);
//        ButterKnife.bind(this, v);
//
//        bringToFront();
//        gifFinger.setMovieResource(R.raw.put_finger);
//        eventBus = EventBus.getDefault();
//        eventBus.register(this);
//        setVisibility(INVISIBLE);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, priority = 50)
//    public void onResultEvent(ResultEvent e) {
//        Record record = e.getRecord();
//        if (record == null) {
//            return;
//        }
//        if (TextUtils.isEmpty(record.getCardNo()) || !TextUtils.equals(record.getCardNo(), lastCardNo)) {
//            ivIdPhoto.setImageBitmap(null);
//        }
//        if (record.getCardImgData() != null && record.getCardImgData().length > 0) {
//            llCardImg.setVisibility(VISIBLE);
//            ivResult.setVisibility(VISIBLE);
//        } else {
//            llCardImg.setVisibility(GONE);
//            ivResult.setVisibility(GONE);
//        }
//        bringToFront();
//        setVisibility(VISIBLE);
//        switch (e.getResult()) {
//            case ResultEvent.FACE_SUCCESS:
//                ivResult.setImageResource(R.mipmap.result_true);
//                tvResult.setText("人脸通过");
//                tvResult.setVisibility(VISIBLE);
//                gifFinger.setVisibility(GONE);
//                ivFingerResult.setVisibility(GONE);
//                break;
//            case ResultEvent.FACE_FAIL:
//                ivResult.setImageResource(R.mipmap.result_false);
//                tvResult.setText("人脸未通过");
//                tvResult.setVisibility(VISIBLE);
//                gifFinger.setVisibility(GONE);
//                ivFingerResult.setVisibility(GONE);
//                break;
//            case ResultEvent.FINGER_SUCCESS:
//                tvResult.setText("指纹通过");
//                tvResult.setVisibility(VISIBLE);
//                gifFinger.setVisibility(GONE);
//                ivFingerResult.setVisibility(VISIBLE);
//                ivFingerResult.setImageResource(R.mipmap.finger_succes);
//                break;
//            case ResultEvent.FINGER_FAIL:
//                if (TextUtils.isEmpty(Base64.encodeToString(record.getFinger0(),Base64.DEFAULT)) || TextUtils.isEmpty(Base64.encodeToString(record.getFinger1(),Base64.DEFAULT))) {
//                    tvResult.setVisibility(VISIBLE);
//                    gifFinger.setVisibility(GONE);
//                    ivFingerResult.setVisibility(GONE);
//                    tvResult.setText("无指纹");
//                    return;
//                }
//                tvResult.setText("指纹未通过");
//                tvResult.setVisibility(VISIBLE);
//                gifFinger.setVisibility(GONE);
//                ivFingerResult.setVisibility(VISIBLE);
//                ivFingerResult.setImageResource(R.mipmap.finger_fail);
//                break;
//            case ResultEvent.VERIFY_FINGER:
//                tvResult.setText("请按手指");
//                tvResult.setVisibility(VISIBLE);
//                gifFinger.setVisibility(VISIBLE);
//                ivFingerResult.setVisibility(GONE);
////                FingerService.startActionVerifyFinger(getContext(), record);
//                break;
//            case ResultEvent.ID_PHOTO:
//                ivResult.setImageBitmap(null);
//                gifFinger.setVisibility(GONE);
//                tvResult.setVisibility(GONE);
//                ivFingerResult.setVisibility(GONE);
//                if (record.getCardImgData() != null && record.getCardImgData().length > 0) {
//                    Bitmap bmp = BitmapFactory.decodeByteArray(record.getCardImgData(), 0, record.getCardImgData().length);
//                    ivIdPhoto.setImageBitmap(bmp);
//                    ivCameraPhoto.setImageBitmap(null);
//                } else {
//                    LogUtil.writeLog("显示身份证照片失败 carImg = null");
//                }
//                break;
//            case ResultEvent.WHITE_LIST_FAIL:
//                ivResult.setImageResource(R.mipmap.result_false);
//                tvResult.setText("不在名单内");
//                tvResult.setVisibility(VISIBLE);
//                gifFinger.setVisibility(GONE);
//                ivFingerResult.setVisibility(GONE);
//                break;
//            case ResultEvent.VALIDATE_FAIL:
//                ivResult.setImageResource(R.mipmap.result_false);
//                tvResult.setText("身份证过期");
//                tvResult.setVisibility(VISIBLE);
//                gifFinger.setVisibility(GONE);
//                ivFingerResult.setVisibility(GONE);
//                break;
//        }
//
//        if (e.getFaceInfo() != null && record.getFaceImgData() != null && record.getFaceImgData().length > 0) {
//            getFaceRect(record.getFaceImgData(), e.getFaceInfo());
//        }
//        lastCardNo = record.getCardNo();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onNoCardEvent(NoCardEvent e) {
//        setVisibility(INVISIBLE);
//    }
//
//    private void getFaceRect(byte[] faceImgData, MXFaceInfoEx passFace) {
//        Bitmap b = BitmapFactory.decodeByteArray(faceImgData, 0, faceImgData.length);
//        Bitmap rectBitmap = Bitmap.createBitmap(b, passFace.x, passFace.y, passFace.width, passFace.height);//截取
//        ivCameraPhoto.setImageBitmap(rectBitmap);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
//    public void onCmdGetFingerEvent(CmdGetFingerEvent e) {
//        Log.e("==", "onCmdGetFingerEvent");
//        isGettingFinger = true;
//        setVisibility(VISIBLE);
//        tvResult.setText("请按手指");
//        tvResult.setVisibility(VISIBLE);
//        gifFinger.setVisibility(VISIBLE);
//        ivFingerResult.setVisibility(GONE);
//        ivIdPhoto.setImageBitmap(null);
//        ivCameraPhoto.setImageBitmap(null);
//        FingerService.startActionGetFinger(getContext());
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onCmdGetFingerDoneEvent(CmdGetFingerDoneEvent e) {
//        Log.e("==", "onCmdGetFingerDoneEvent");
//        setVisibility(GONE);
//        isGettingFinger = false;
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onCmdIdCardDoneEvent(CmdIdCardDoneEvent e) {
//        Log.e("==", "onCmdGetFingerDoneEvent");
//        ivResult.setImageResource(R.mipmap.result_true);
//        tvResult.setText("读卡成功");
//        tvResult.setVisibility(VISIBLE);
//        gifFinger.setVisibility(GONE);
//        ivFingerResult.setVisibility(GONE);
//    }
//}
