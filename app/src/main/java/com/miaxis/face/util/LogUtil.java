package com.miaxis.face.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Date;

/**
 * Created by Administrator on 2017/5/21 0021.
 */

public class LogUtil {

    public static final String LOG_NAME = "faceId_adb_st_log.txt";

    public static void writeLog(String content) {
        Log.e("writeLog", content);
        File log = new File(FileUtil.FACE_MAIN_PATH, LOG_NAME);
        FileUtil.writeFile(log, DateUtil.toAllms(new Date()) + "   " + content + "\r\n", true);
    }

}
