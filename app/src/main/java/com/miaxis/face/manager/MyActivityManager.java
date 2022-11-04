package com.miaxis.face.manager;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * @author ZJL
 * @date 2022/9/2 14:21
 * @des
 * @updateAuthor
 * @updateDes
 */
public class MyActivityManager {
    private static MyActivityManager sInstance = new MyActivityManager();

    private WeakReference<Activity> sCurrentActivityWeakRef;


    private MyActivityManager() {

    }

    public static MyActivityManager getInstance() {
        return sInstance;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
    }

    public boolean isCurrent(Activity activity){
        return getCurrentActivity()==activity;
    }
}
