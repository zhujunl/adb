package com.miaxis.face.view.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.miaxis.face.R;
import com.miaxis.face.util.BitmapUtils;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.view.custom.DrawTextView;

import java.io.File;

import butterknife.BindView;

/**
 * @author ZJL
 * @date 2022/5/17 16:57
 * @des
 * @updateAuthor
 * @updateDes
 */
public class SignFragment extends BaseFragment{
    
    @BindView(R.id.layout_content)
    FrameLayout layoutContent;
    @BindView(R.id.drv_text)
    DrawTextView drvText;
    @BindView(R.id.btn_resign)
    Button btnResign;
    @BindView(R.id.btn_print)
    Button btnPrint;

    private final String TAG="SignFragment";
    private String BasePath = FileUtil.FACE_MAIN_PATH + File.separator + "Sign";
    private String savePath;
    
    @Override
    protected int initLayout() { return R.layout.fragment_sign; }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        
         btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 drvText.clearFocus();
                 drvText.setCursorVisible(false);
                 layoutContent.buildDrawingCache();
                Bitmap bitmap =  layoutContent.getDrawingCache();
                boolean saveBitmap = BitmapUtils.saveBitmap(bitmap, savePath = (BasePath + File.separator + "Sign_" + System.currentTimeMillis() + ".png"));
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setMessage("保存：" + (saveBitmap ? "成功" : "失败") + "  ，路径：" + savePath);
                if (saveBitmap) {
                    builder.setPositiveButton("预览", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //                            Intent intent = new Intent(getContext(), PhotoActivity.class);
                            //                            intent.putExtra("ImagePath", savePath);
                            //                            startActivity(intent);
                            PhotoView imageView = new PhotoView(getContext());
                            imageView.enable();
                            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            imageView.setLayoutParams(layoutParams);
                            Bitmap bitmap = BitmapFactory.decodeFile(savePath);
                            imageView.setImageBitmap(bitmap);
                            // 获取图片信息
                            Info info = imageView.getInfo();
                            // 从一张图片信息变化到现在的图片，用于图片点击后放大浏览，具体使用可以参照demo的使用
                            imageView.animaFrom(info);

                            // 获取/设置 动画持续时间
                            imageView.setAnimaDuring(1);
                            // 获取/设置 最大缩放倍数
                            imageView.setMaxScale(10);
                            // 设置动画的插入器
                            imageView.setInterpolator(new LinearInterpolator());

                            new AlertDialog.Builder(getContext()).setView(imageView).create().show();
                        }
                    });
                }
                builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });

         btnResign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 drvText.clear();
            }
        });
    }
}
