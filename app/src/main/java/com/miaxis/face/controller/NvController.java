package com.miaxis.face.controller;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.miaxis.face.R;

import java.util.List;



/**
 * GuideNavigationController
 *
 * @author zhangyw
 * Created on 4/29/21.
 */
public class NvController {

    private final FragmentManager fragmentManager;
    private final int containId;

    public NvController(FragmentManager fragmentManager, int containId) {
        this.fragmentManager = fragmentManager;
        this.containId = containId;
    }


    public Fragment top() {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments.size() == 0)
            return null;
        return fragments.get(fragments.size() - 1);
    }

    public void back() {
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        }
    }

    public void nvTo(Fragment fragment, boolean withAnim) {
        if (withAnim) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_right_in, R.anim.fragment_left_out, R.anim.fragment_left_in, R.anim.fragment_right_out)
                    .replace(containId, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(containId, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}