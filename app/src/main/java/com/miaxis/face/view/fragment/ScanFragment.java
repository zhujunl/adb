package com.miaxis.face.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.event.CutDownEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * @author ZJL
 * @date 2022/5/25 9:31
 * @des
 * @updateAuthor
 * @updateDes
 */
public class ScanFragment extends BaseFragment {
    private final String TAG = "ScanFragment";

    @BindView(R.id.tv_second)
    TextView tv_second;

    private EventBus eventBus;


    @Override
    protected int initLayout() {
        return R.layout.fragment_scan;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        eventBus=EventBus.getDefault();
        eventBus.register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventBus.unregister(this);
    }

    @Subscribe(threadMode= ThreadMode.MAIN,priority = 2)
    public void onCutDownEvent(CutDownEvent event){
        tv_second.setVisibility(View.VISIBLE);
        tv_second.setText(String.valueOf(event.getTime()));
    }

}