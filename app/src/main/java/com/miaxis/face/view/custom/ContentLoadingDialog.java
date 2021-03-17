package com.miaxis.face.view.custom;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.miaxis.face.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by xu.nan on 2016/9/12.
 */
public class ContentLoadingDialog extends DialogFragment {

    Unbinder unbinder;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.pb_loading)
    ContentLoadingProgressBar pbLoading;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.tv_max)
    TextView tvMax;

    private View.OnClickListener listener;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.77), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_loading, container);
        Log.e("dialog", "onCreateView");
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public void setMax(int max) {
        if (pbLoading != null) {
            pbLoading.setMax(max);
        }
        if (tvMax != null) {
            tvMax.setText(" " + max);
        }
    }

    public void setProgress(int progress) {
        if (pbLoading != null) {
            pbLoading.setProgress(progress);
        }
        if (tvProgress != null) {
            tvProgress.setText(progress+ " ");
        }
    }

    public void setMessage(String message) {
        if (tvMessage != null)
            tvMessage.setText(message);
    }

    @OnClick(R.id.tv_cancel)
    void onCancel(View view) {
        if (listener != null) {
            listener.onClick(view);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setButtonName(String name) {
        if (tvCancel != null) {
            tvCancel.setText(name);
        }
    }

    public void setButtonName(int resId) {
        if (tvCancel != null) {
            tvCancel.setText(resId);
        }
    }


}
