package com.miaxis.face.view.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageSwitcher;
import android.widget.VideoView;

import com.miaxis.face.R;
import com.miaxis.face.event.PlayAdvertisementEvent;
import com.miaxis.face.util.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by xu.nan on 2016/10/14.
 */

public class AdvertiseDialog extends BaseDialogFragment {

    Unbinder unbinder;
    private View.OnTouchListener listener;

    @BindView(R.id.vv_advertisement)
    VideoView vvAdvertisement;

    private Bitmap bitmap;
    private File[] adFiles;
    private int fileNo = 0;

    public View.OnTouchListener getListener() {
        return listener;
    }

    public void setListener(View.OnTouchListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        hideNavigationBar();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_advertise, null);
        view.setOnTouchListener(listener);
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light);
        dialog.setContentView(view);
        dialog.show();
        unbinder = ButterKnife.bind(this, view);
        showAdvertisement();
        EventBus.getDefault().register(this);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        return dialog;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    private void showAdvertisement() {
        File videoDir = new File(FileUtil.getAdvertisementFilePath());
        if (videoDir.exists() && videoDir.isDirectory()) {
            adFiles = videoDir.listFiles();
            if (adFiles != null && adFiles.length > 0) {
                play(adFiles[0].getAbsolutePath());
            }
        }
    }

    public static boolean isAdExist() {
        File videoDir = new File(FileUtil.getAdvertisementFilePath());
        File[] adFiles;
        if (videoDir.exists() && videoDir.isDirectory()) {
            adFiles = videoDir.listFiles();
            if (adFiles != null && adFiles.length > 0) {
                return true;
            }
        }
        return false;
    }

    private void play(String path) {
        fileNo ++;
        if (fileNo >= adFiles.length) {
            fileNo = 0;
        }
        vvAdvertisement.setBackgroundResource(0);
        if (bitmap != null) {
            bitmap.recycle();
        }

        if (View.VISIBLE != vvAdvertisement.getVisibility()) {
            vvAdvertisement.setVisibility(View.VISIBLE);
        }
        /* if (isVideo(path)) {
            try {
                vvAdvertisement.setVideoPath(path);
                vvAdvertisement.requestFocus();
                vvAdvertisement.start();
                vvAdvertisement.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        EventBus.getDefault().post(new PlayAdvertisementEvent());
                    }
                });
            } catch (Exception e) {
                EventBus.getDefault().post(new PlayAdvertisementEvent());
            }
        } else */ if (isImg(path)) {
            vvAdvertisement.setVisibility(View.VISIBLE);
            bitmap = decodeBitmap(path);
            Drawable _drawable = new BitmapDrawable(bitmap);
            vvAdvertisement.setBackground(_drawable);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(8000);
                        EventBus.getDefault().post(new PlayAdvertisementEvent());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private Bitmap decodeBitmap(String filePath)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 通过这个bitmap获取图片的宽和高&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        float realWidth = options.outWidth;
        float realHeight = options.outHeight;
        // 计算缩放比&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
        int scale = (int) ((realHeight > realWidth ? realHeight : realWidth) / 1000);
        if (scale <= 0) {
            scale = 1;
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        // 注意这次要把options.inJustDecodeBounds 设为 false,这次图片是要读取出来的。&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
        bitmap = BitmapFactory.decodeFile(filePath, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        return bitmap;
    }

    private boolean isVideo(String path) {
        if (path.toLowerCase().endsWith(".mov")
                || path.toLowerCase().endsWith(".mkv")
                || path.toLowerCase().endsWith(".mp4")
                || path.toLowerCase().endsWith(".avi")) {
            return true;
        }
        return false;
    }

    // 判断是否为图片文件
    private boolean isImg(String path) {
        if (path.toLowerCase().endsWith(".jpg")
                || path.toLowerCase().endsWith(".gif")
                || path.toLowerCase().endsWith(".png")
                || path.toLowerCase().endsWith(".jpeg")) {

            return true;
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayAdvertisementEvent(PlayAdvertisementEvent e) {
        play(adFiles[fileNo].getAbsolutePath());
    }

}
