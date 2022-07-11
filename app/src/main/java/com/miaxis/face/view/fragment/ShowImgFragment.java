package com.miaxis.face.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.event.CutDownEvent;
import com.miaxis.face.util.MyUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * @author ZJL
 * @date 2022/5/26 10:38
 * @des
 * @updateAuthor
 * @updateDes
 */
public class ShowImgFragment extends BaseFragment{
    private final String TAG="ShowImgFragment";

    @BindView(R.id.show)
    ImageView show;
    @BindView(R.id.tv_second)
    TextView tv_second;

    private String Base64;
    private EventBus eventbus;

    public static ShowImgFragment getIntent(String base64){
        ShowImgFragment showImgFragment=new ShowImgFragment();
        showImgFragment.setBase64(base64);
        return showImgFragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_show;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        eventbus=EventBus.getDefault();
        eventbus.register(this);
        tv_second.bringToFront();
        Bitmap bitmap = MyUtil.base64ToBitmap(Base64);
        show.setImageBitmap(bitmap);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventbus.unregister(this);
    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    public void onCutDownEvent(CutDownEvent event){
        tv_second.setVisibility(View.VISIBLE);
        tv_second.setText(String.valueOf(event.getTime()));
    }

    public void setBase64(String base64) {
        Base64 = base64;
    }
}
