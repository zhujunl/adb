package com.miaxis.face.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.miaxis.face.R;

/**
 * @author ZJL
 * @date 2022/5/13 20:21
 * @des
 * @updateAuthor
 * @updateDes
 */
public class HomeFragment extends BaseFragment{

    private static HomeFragment homeFragment;

    public static HomeFragment getInstance(){
        if(homeFragment==null){
            homeFragment=new HomeFragment();
        }
        return homeFragment;
    }

    public HomeFragment() {
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {

    }
}
