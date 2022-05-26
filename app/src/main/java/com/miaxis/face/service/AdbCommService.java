package com.miaxis.face.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.miaxis.face.app.Face_App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.Record;
import com.miaxis.face.event.BtReadCardEvent;
import com.miaxis.face.event.CmdFingerImgDoneEvent;
import com.miaxis.face.event.CmdFingerImgEvent;
import com.miaxis.face.event.CmdGetFingerDoneEvent;
import com.miaxis.face.event.CmdGetFingerEvent;
import com.miaxis.face.event.CmdIdCardDoneEvent;
import com.miaxis.face.event.CmdIdCardEvent;
import com.miaxis.face.event.CmdScanDoneEvent;
import com.miaxis.face.event.CmdScanEvent;
import com.miaxis.face.event.CmdShowEvent;
import com.miaxis.face.event.CmdShutterEvent;
import com.miaxis.face.event.CmdShutterPhotoEvent;
import com.miaxis.face.event.CmdSignDoneEvent;
import com.miaxis.face.event.CmdSignEvent;
import com.miaxis.face.event.CmdSmDoneEvent;
import com.miaxis.face.event.CmdSmEvent;
import com.miaxis.face.event.ResultEvent;
import com.miaxis.face.util.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class AdbCommService extends Service {

    private static final String TAG = AdbCommService.class.getSimpleName();
    private MyBinder myBinder = new MyBinder();

    private ServerSocket mServerSocket;


    public AdbCommService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        startServer();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e(TAG, "onStart");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Log.e(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return myBinder;
    }

    void startServer() {
        ServerThread st = new ServerThread();
        st.start();
    }

    private class MyBinder extends Binder {
        public AdbCommService getService() {
            return AdbCommService.this;
        }
    }

    private class ServerThread extends Thread {
        @Override
        public void run() {
            try {
                mServerSocket = new ServerSocket(2235);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    Log.e(TAG, "socket accept start");
                    Socket socket = mServerSocket.accept();
                    Log.e(TAG, "socket accepted");
                    new ServerReceiveThread(socket).start();

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

        }
    }

    public class ServerReceiveThread extends Thread {

        Socket socket;
        boolean imgFlag=false;
        StringBuffer sb=new StringBuffer();

        ServerReceiveThread(Socket socket) {
            EventBus.getDefault().register(this);
            this.socket = socket;
        }

        @Override
        public void run() {
            InputStream reader;
            try {
                reader = socket.getInputStream();
                byte[] buffer = new byte[1024];
                while (true) {
                    int n = reader.read(buffer);        //监听输入流
                    if (n != -1) {
                        byte[] text = new byte[n];
                        System.arraycopy(buffer, 0, text, 0, n);
                        String s = new String(text);    //接收到的数据
                        Log.e("receive", "s = " + s);
                        if(s.length()>9){
                            String start = s.substring(0, 9);
                            String end = s.substring(s.length() - 4);
                            if (TextUtils.equals(start,"$=ImgShow")){
                                imgFlag=true;
                            }
                            if (TextUtils.equals(end,"$end")){
                                imgFlag=false;
                                sb.append(s);
                                Log.e(TAG, "sb===" +sb.toString() );
                                EventBus.getDefault().post(new CmdShowEvent(sb.toString()));
                            }
                        }
                        if (!imgFlag){
                            if (TextUtils.equals(s, "$=Action")) {
                                EventBus.getDefault().post(new BtReadCardEvent());
                            } else if (TextUtils.equals(s, "$=Shutter")) {
                                EventBus.getDefault().post(new CmdShutterEvent());
                            } else if (TextUtils.equals(s, "$=GetFinger")) {
                                EventBus.getDefault().post(new CmdGetFingerEvent());
                            } else if (TextUtils.equals(s, "$=IdCard")) {
                                EventBus.getDefault().post(new CmdIdCardEvent());
                            }else if (TextUtils.equals(s, "$=GetFingerImg")) {
                                EventBus.getDefault().post(new CmdFingerImgEvent());
                            }else if (TextUtils.equals(s, "$=GetSm")) {
                                EventBus.getDefault().post(new CmdSmEvent());
                            }else if (TextUtils.equals(s, "$=GetSign")) {
                                EventBus.getDefault().post(new CmdSignEvent());
                            }else if (TextUtils.equals(s, "$=GetScan")) {
                                EventBus.getDefault().post(new CmdScanEvent());
                            }
                        }else {
                            sb.append(s);
                        }
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                LogUtil.writeLog("ServerReceiveThread Exception " + e.getMessage());
                Log.e(TAG, "ServerReceiveThread Exception " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                        socket = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                EventBus.getDefault().unregister(this);
                Log.e(TAG, "finally");
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
        public void onResultEvent(ResultEvent e) {
            try {
                Config config = Face_App.getInstance().getDaoSession().getConfigDao().loadByRowId(1L);
                Log.e("ResultLayout", "result = " + e.getResult());
                Record record = e.getRecord();
                if (record == null) {
                    return;
                }
                record.setBusEntity(config.getOrgName());
                StringBuilder sendMsgSb = new StringBuilder();
                switch (e.getResult()) {
                    case ResultEvent.FACE_SUCCESS:
                        switch (config.getVerifyMode()) {
                            case Config.MODE_FACE_ONLY:
                                sendMsgSb.append("$result=#=通过");
                                break;
                            case Config.MODE_ONE_FACE_FIRST:
                                sendMsgSb.append("$result=#=通过");
                                break;
                            case Config.MODE_ONE_FINGER_FIRST:
                                sendMsgSb.append("$result=#=通过");
                                break;
                            case Config.MODE_TWO_FINGER_FIRST:
                                sendMsgSb.append("$result=#=通过");
                                break;
                            default:
                                return;
                        }
                        break;
                    case ResultEvent.FACE_FAIL:
                        switch (config.getVerifyMode()) {
                            case Config.MODE_FACE_ONLY:
                                sendMsgSb.append("$result=#=未通过");
                                break;
                            case Config.MODE_ONE_FINGER_FIRST:
                                sendMsgSb.append("$result=#=未通过");
                                break;
                            case Config.MODE_TWO_FACE_FIRST:
                                sendMsgSb.append("$result=#=未通过");
                                break;
                            case Config.MODE_TWO_FINGER_FIRST:
                                sendMsgSb.append("$result=#=未通过");
                                break;
                            default:
                                return;
                        }
                        break;
                    case ResultEvent.FINGER_SUCCESS:
                        switch (config.getVerifyMode()) {
                            case Config.MODE_FINGER_ONLY:
                                sendMsgSb.append("$result=#=通过");
                                break;
                            case Config.MODE_ONE_FACE_FIRST:
                                sendMsgSb.append("$result=#=通过");
                                break;
                            case Config.MODE_ONE_FINGER_FIRST:
                                sendMsgSb.append("$result=#=通过");
                                break;
                            case Config.MODE_TWO_FACE_FIRST:
                                sendMsgSb.append("$result=#=通过");
                                break;
                            default:
                                return;
                        }
                        break;
                    case ResultEvent.FINGER_FAIL:
                        switch (config.getVerifyMode()) {
                            case Config.MODE_FINGER_ONLY:
                                sendMsgSb.append("$result=#=未通过");
                                break;
                            case Config.MODE_ONE_FACE_FIRST:
                                sendMsgSb.append("$result=#=未通过");
                                break;
                            case Config.MODE_TWO_FACE_FIRST:
                                sendMsgSb.append("$result=#=未通过");
                                break;
                            case Config.MODE_TWO_FINGER_FIRST:
                                sendMsgSb.append("$result=#=未通过");
                                break;
                            default:
                                return;
                        }
                        break;
                    case ResultEvent.VALIDATE_FAIL:
                        sendMsgSb.append("$result=#=未通过");
                        break;
                    case ResultEvent.WHITE_LIST_FAIL:
                        sendMsgSb.append("$result=#=未通过");
                        break;
                    default:
                        return;
                }
                appendOtherMsg(sendMsgSb, record);
                sendMsg(sendMsgSb.toString().trim());
            } catch (Exception ex) {
                LogUtil.writeLog("ADBService onResultEvent Exception " + ex.getMessage());
            }
        }

        private void appendOtherMsg(StringBuilder sendMsgSb, Record record) throws Exception {
            sendMsgSb.append("$name=#=").append(record.getName());
            sendMsgSb.append("$sex=#=").append(record.getSex());
            sendMsgSb.append("$cardNo=#=").append(record.getCardNo());
            sendMsgSb.append("$address=#=").append(record.getAddress());
            sendMsgSb.append("$birthday=#=").append(record.getBirthday());
            if (!TextUtils.isEmpty(record.getDevsn())){
                sendMsgSb.append("$deviceName=#=").append(record.getDevsn());
            }
            sendMsgSb.append("$busEntity=#=").append(record.getBusEntity());
            if (null != record.getCardImgData()) {
                sendMsgSb.append("$cardImg=#=").append(Base64.encodeToString(record.getCardImgData(), Base64.DEFAULT));
            } else {
                sendMsgSb.append("$cardImg=#=").append("");
            }
            if (null != record.getFaceImgData()) {
                sendMsgSb.append("$faceImg=#=").append(Base64.encodeToString(record.getFaceImgData(), Base64.DEFAULT));
            } else {
                sendMsgSb.append("$faceImg=#=").append("");
            }
            sendMsgSb.append("$race=#=").append(record.getRace());
            sendMsgSb.append("$regOrg=#=").append(record.getRegOrg());
            sendMsgSb.append("$validTime=#=").append(record.getValidate());
            sendMsgSb.append("$finger0=#=").append(record.getFinger0());
            sendMsgSb.append("$finger1=#=").append(record.getFinger1());
            sendMsgSb.append("$end");
        }

        @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
        public void onCmdShutterPhotoEvent(CmdShutterPhotoEvent e) {
            if (!TextUtils.isEmpty(e.getPhoto64())) {
                sendMsg(String.format("$photo=#=%s$end", e.getPhoto64()));
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
        public void onCmdFingerImgDoneEvent(CmdFingerImgDoneEvent e) {
            if (!TextUtils.isEmpty(e.getBase64())) {
                sendMsg(String.format("$FingerImg=#=%s$end", e.getBase64()));
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
        public void onCmdScanDoneEvent(CmdScanDoneEvent e) {
            if (!TextUtils.isEmpty(e.getContent())) {
                sendMsg(String.format("$Scan=#=%s$end", e.getContent()));
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
        public void onCmdSignDoneEvent(CmdSignDoneEvent e) {
            if (!TextUtils.isEmpty(e.getBase64())) {
                sendMsg(String.format("$Sign=#=%s$end", e.getBase64()));
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
        public void onCmdSmDoneEvent(CmdSmDoneEvent e) {
            if (!TextUtils.isEmpty(e.getBase64())) {
                sendMsg(String.format("$Sm=#=%s$end", e.getBase64()));
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
        public void onCmdGetFingerDoneEvent(CmdGetFingerDoneEvent e) {
            if (!TextUtils.isEmpty(e.getFinger64())) {
                sendMsg(String.format("$finger=#=%s$end", e.getFinger64()));
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
        public void onCmdIdCardDoneEvent(CmdIdCardDoneEvent e) {
            try {
                StringBuilder sendMsgSb = new StringBuilder();
                appendIdCardMsg(sendMsgSb, e.getRecord());
                sendMsg(sendMsgSb.toString().trim());
            } catch (Exception exception) {
                exception.printStackTrace();
                LogUtil.writeLog("ADBService onCmdIdCardDoneEvent Exception " + exception.getMessage());
            }
        }

        private void appendIdCardMsg(StringBuilder sendMsgSb, Record record) throws Exception {
            sendMsgSb.append("$name=#=").append(record.getName());
            sendMsgSb.append("$sex=#=").append(record.getSex());
            sendMsgSb.append("$cardNo=#=").append(record.getCardNo());
            sendMsgSb.append("$address=#=").append(record.getAddress());
            sendMsgSb.append("$birthday=#=").append(record.getBirthday());
            sendMsgSb.append("$busEntity=#=").append(record.getBusEntity());
            if (null != record.getCardImgData()) {
                sendMsgSb.append("$cardImg=#=").append(Base64.encodeToString(record.getCardImgData(), Base64.DEFAULT));
            } else {
                sendMsgSb.append("$cardImg=#=").append("");
            }
            sendMsgSb.append("$race=#=").append(record.getRace());
            sendMsgSb.append("$regOrg=#=").append(record.getRegOrg());
            sendMsgSb.append("$validTime=#=").append(record.getValidate());
            sendMsgSb.append("$finger0=#=").append(record.getFinger0());
            sendMsgSb.append("$finger1=#=").append(record.getFinger1());
            sendMsgSb.append("$end");
        }

        @SuppressLint("CheckResult")
        private void sendMsg(String msg) {
            Observable
                    .just(msg)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            try {
                                if (socket != null) {
                                    Log.e(TAG, "sssss=" +s );
                                    OutputStream writer = socket.getOutputStream();
                                    if (writer != null)
                                        writer.write(s.getBytes("GBK"));
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }

}
