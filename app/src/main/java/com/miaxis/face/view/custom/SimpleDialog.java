package com.miaxis.face.view.custom;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.miaxis.face.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by xu.nan on 2017/7/10.
 */

public class SimpleDialog extends DialogFragment {

    Unbinder unbinder;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;

    private String message;
    private View.OnClickListener confirmListener;
    private View.OnClickListener cancelListener;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_simple, container);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        tvConfirm.setOnClickListener(confirmListener);
        tvCancel.setOnClickListener(cancelListener);
        tvMessage.setText(message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public View.OnClickListener getConfirmListener() {
        return confirmListener;
    }

    public void setConfirmListener(View.OnClickListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public View.OnClickListener getCancelListener() {
        return cancelListener;
    }

    public void setCancelListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }
}
