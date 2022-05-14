package com.miaxis.face.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.miaxis.face.R;
import com.miaxis.face.constant.Constants;

import java.util.HashMap;
import java.util.Map;

import static com.miaxis.face.constant.Constants.LEFT_VOLUME;
import static com.miaxis.face.constant.Constants.LOOP;
import static com.miaxis.face.constant.Constants.PRIORITY;
import static com.miaxis.face.constant.Constants.RIGHT_VOLUME;
import static com.miaxis.face.constant.Constants.SOUND_RATE;

/**
 * @author ZJL
 * @date 2022/5/12 9:52
 * @des
 * @updateAuthor
 * @updateDes
 */
public class SoundManager {

    private boolean continuePlaySoundFlag = true;
    private int mCurSoundId;
    private SoundPool soundPool;
    private Map<Integer, Integer> soundMap;

    public SoundManager() {
    }


    public static SoundManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final SoundManager instance = new SoundManager();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    public void init(Context context){
        soundPool = new SoundPool(21, AudioManager.STREAM_MUSIC, 0);
        soundMap = new HashMap<>();
        soundMap.put(Constants.SOUND_SUCCESS, soundPool.load(context, R.raw.success, 1));
        soundMap.put(Constants.SOUND_FAIL, soundPool.load(context, R.raw.fail, 1));

        soundMap.put(Constants.PLEASE_PRESS, soundPool.load(context, R.raw.please_press, 1));
        soundMap.put(Constants.SOUND_OR, soundPool.load(context, R.raw.sound_or, 1));
        soundMap.put(Constants.SOUND_OTHER_FINGER, soundPool.load(context, R.raw.please_press_finger, 1));
        soundMap.put(Constants.SOUND_VALIDATE_FAIL, soundPool.load(context, R.raw.validate_fail, 1));

        soundMap.put(Constants.SOUND_PUT_CARD, soundPool.load(context, R.raw.put_card, 1));

        soundMap.put(Constants.FINGER_RIGHT_0, soundPool.load(context, R.raw.finger_right_0, 1));
        soundMap.put(Constants.FINGER_RIGHT_1, soundPool.load(context, R.raw.finger_right_1, 1));
        soundMap.put(Constants.FINGER_RIGHT_2, soundPool.load(context, R.raw.finger_right_2, 1));
        soundMap.put(Constants.FINGER_RIGHT_3, soundPool.load(context, R.raw.finger_right_3, 1));
        soundMap.put(Constants.FINGER_RIGHT_4, soundPool.load(context, R.raw.finger_right_4, 1));
        soundMap.put(Constants.FINGER_LEFT_0, soundPool.load(context, R.raw.finger_left_0, 1));
        soundMap.put(Constants.FINGER_LEFT_1, soundPool.load(context, R.raw.finger_left_1, 1));
        soundMap.put(Constants.FINGER_LEFT_2, soundPool.load(context, R.raw.finger_left_2, 1));
        soundMap.put(Constants.FINGER_LEFT_3, soundPool.load(context, R.raw.finger_left_3, 1));
        soundMap.put(Constants.FINGER_LEFT_4, soundPool.load(context, R.raw.finger_left_4, 1));
    }

    /* 连续播放4段音频 提示按指纹的 指位*/
    public void playSound(final int soundId0, final int soundId1, final int soundId2, final int soundId3) {
        continuePlaySoundFlag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (continuePlaySoundFlag) {
                        mCurSoundId = soundPool.play(soundMap.get(soundId0), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
                        Thread.sleep(800);
                    }
                    if (continuePlaySoundFlag) {
                        mCurSoundId = soundPool.play(soundMap.get(soundId1), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
                        Thread.sleep(1000);
                    }
                    if (continuePlaySoundFlag) {
                        mCurSoundId = soundPool.play(soundMap.get(soundId2), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
                        Thread.sleep(800);
                    }
                    if (continuePlaySoundFlag) {
                        mCurSoundId = soundPool.play(soundMap.get(soundId3), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void playSound(int soundID) {
        continuePlaySoundFlag = false;
        soundPool.stop(mCurSoundId);
        mCurSoundId = soundPool.play(soundMap.get(soundID), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
    }

    public void close(){
        continuePlaySoundFlag=false;
    }
}
