package com.miaxis.face.view.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.miaxis.face.R;

import butterknife.BindView;

/**
 * @author ZJL
 * @date 2022/5/13 20:21
 * @des
 * @updateAuthor
 * @updateDes
 */
public class HomeFragment extends BaseFragment{

    @BindView(R.id.High_beat_meter)
    Button high;
    @BindView(R.id.scan)
    Button scan;
    @BindView(R.id.signature)
    Button sign;
    @BindView(R.id.homeFrame)
    FrameLayout homeFrame;

    private final String TAG="HomeFragment";

    private HightFragment hightFragment;
    private SignFragment signFragment;


    public static HomeFragment getInstance(){
        return new HomeFragment();
    }

    public HomeFragment() {
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        hightFragment=new HightFragment();
        signFragment=new SignFragment();
        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Hight();
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Scan();
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sign();
            }
        });
    }

    private void Hight(){
        FragmentManager fm=getActivity().getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.homeFrame,hightFragment).commit();
    }

    private void Scan(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        EditText editText=new EditText(getActivity());
        builder.setTitle("扫码内容")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void Sign(){
        FragmentManager fm=getActivity().getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.homeFrame,signFragment).commit();
    }

}
