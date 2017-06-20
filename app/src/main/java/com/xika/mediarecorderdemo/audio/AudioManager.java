package com.xika.mediarecorderdemo.audio;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

/**
 * Email 727320580@qq.com
 * Created by xika on 2017/6/20
 * Vwesion 1.0
 * Dsscription: 录音操作管理类
 * <p>
 * 步骤:
 * 1.创建单例
 * 2.创建保存录音的文件名字
 * 3.管理类对象的初始化
 * 4.获取声音的级别
 * 5.录音对象资源回收
 * 6.取消录音
 */

public class AudioManager {
    //AudioRecord: 主要是实现边录边播（AudioRecord+AudioTrack）以及对音频的实时处理。
    // 优点：可以语音实时处理，可以实现各种音频的封装
    private MediaRecorder mRecorder;
    // 单例的使用
    private static AudioManager mInstance;
    // 录音文件夹名字
    private String mDir;
    // 录音文件的绝对路径
    private String mCurrentFilePath;
    // 判断是否在录音
    private boolean ispPrepare;

    /**
     * 构造函数初始化
     *
     * @param filePath 传入的保存录音的文件夹名字
     */
    public AudioManager(String filePath) {
        this.mDir = filePath;
    }

    /**
     * 录音管理类的实体对象(单例操作)
     *
     * @param filePath 保存录音文件的文件夹路径
     * @return
     */
    public static AudioManager getInstance(String filePath) {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager(filePath);
                }
            }
        }
        return mInstance;
    }

    public AudioStateListenner mAudioSataeListenner;

    /**
     * 录音准备好的调用接口
     */
    public interface AudioStateListenner {
        void wellPrepare();
    }

    /**
     * 外部调用的准备操作
     *
     * @param AudioStateListenner
     */
    public void setmAudioSataeListenner(AudioStateListenner AudioStateListenner) {
        this.mAudioSataeListenner = AudioStateListenner;
    }


    /**
     * 录音对象的初始化
     */
    public void prepareAudio() {
        try {
            // 录音没有准备好
            ispPrepare = false;
            mRecorder = new MediaRecorder();
            // 设置从麦克风接收消息
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 文件音频的格式
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            // 音频编码格式
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            /**
             * 设置录音文件
             */
            // 找到录音文件夹文件
            File file = new File(mDir);
            // 文件夹不存在就创建一个文件夹
            if (!file.exists()) {
                file.mkdir();
            }
            File voideFile = new File(file, getVoideFileName());
            // 获取录音文件的绝对路径
            mCurrentFilePath = voideFile.getAbsolutePath();
            // 设置录音文件输出的位置
            mRecorder.setOutputFile(voideFile.getAbsolutePath());
            // 准备录音
            mRecorder.prepare();
            // 开始录音
            mRecorder.start();
            // 准备开始录音
            ispPrepare = true;
            if (mAudioSataeListenner != null) {
                mAudioSataeListenner.wellPrepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取录音文件的名字
     *
     * @return
     */
    private String getVoideFileName() {
        return System.currentTimeMillis() + "Voide.amr";
    }

    /**
     * 获取录音声音的级别
     *
     * @param maxLevel 分为几个级别
     * @return
     */
    public int getVoiceLevel(int maxLevel) {
        if (ispPrepare) {
            try {
                // getMaxAmplitude返回的数值最大是32767
                return maxLevel * mRecorder.getMaxAmplitude() / 32768 + 1;//返回结果1-7之间
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 1;
    }


    /**
     * 录音结束释放资源
     */
    public void relaseAndio() {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder = null;
    }

    /**
     * 取消录音操作
     */
    public void clanceAndio() {
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    /**
     * 获取录音资源路径
     *
     * @return
     */
    public String getmCurrentFilePath() {
        return mCurrentFilePath;
    }


}
