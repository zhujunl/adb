package com.miaxis.face.app;


import android.support.v4.app.Fragment;

public interface OnFragmentInteractionListener {
    void backToStack(Class<? extends Fragment> fragment);
    void showWaitDialog(String message);
    void dismissWaitDialog(String message);
}
