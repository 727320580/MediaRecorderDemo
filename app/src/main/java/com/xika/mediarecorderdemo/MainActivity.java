package com.xika.mediarecorderdemo;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xika.mediarecorderdemo.activity.MessageActivity;

import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener {
    private final static String LOG_TAG = "MainActivity";

    private Button startRecorder, pauseRecorder, stopRecorder, startMedia, pauseMedia, stopMedia, next;
    // 录音
    private MediaRecorder mRecorder;
    // 音频播放器
    private MediaPlayer mPlayer;
    // 是否在播放
    private boolean isPlay = false;

    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRecorder();
        initView();
    }

    private void initView() {
        startRecorder = (Button) findViewById(R.id.btn_start_recorder);
        pauseRecorder = (Button) findViewById(R.id.btn_pause_recorder);
        stopRecorder = (Button) findViewById(R.id.btn_stop_recorder);
        startMedia = (Button) findViewById(R.id.btn_play_media);
        pauseMedia = (Button) findViewById(R.id.btn_pause_media);
        stopMedia = (Button) findViewById(R.id.btn_stop_media);
        next = (Button) findViewById(R.id.next);

        startRecorder.setOnClickListener(this);
        pauseRecorder.setOnClickListener(this);
        stopRecorder.setOnClickListener(this);
        startMedia.setOnClickListener(this);
        pauseMedia.setOnClickListener(this);
        stopMedia.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 开始录音
            case R.id.btn_start_recorder:
                mRecorder.start();
                break;
            // 暂停录音
            case R.id.btn_pause_recorder:
                break;
            // 录音完成
            case R.id.btn_stop_recorder:
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                break;
            // 开始播放
            case R.id.btn_play_media:
                initMeidaPlayer();
                break;
            // 暂停播放
            case R.id.btn_pause_media:
                if (!isPlay) {
                    mPlayer.reset();
                    isPlay = true;
                } else {
                    mPlayer.pause();
                    isPlay = false;
                }
                break;
            // 停止播放
            case R.id.btn_stop_media:

                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
                break;
            case R.id.next:
                // 跳转到聊天列表界面
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                startActivity(intent);
                break;

        }
    }

    /**
     * 初始化录音控件
     */
    private void initRecorder() {
        mRecorder = new MediaRecorder();
        // 设置声音来源为麦克风
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置输出的文件的格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 设置输出的文件
        mRecorder.setOutputFile(createFileName());
        // 设置音频的编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 录音对象准备
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化音频播放
     */
    private void initMeidaPlayer() {
        try {
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(videoPath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建
     *
     * @return
     */
    private String createFileName() {
        String filePath = "";
        File file = new File(Environment.getExternalStorageDirectory(), "Recorder");
        // 文件不存在
        if (!file.exists()) {
            file.mkdir();
        }
        // 如果文件已经存在删除重新创建
        File recorderFile = new File(file, System.currentTimeMillis() + ".3gp");
        try {
            if (recorderFile.exists()) {
                recorderFile.delete();
            }
            recorderFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        filePath = recorderFile.getAbsolutePath();
        videoPath = filePath;
        return filePath;
    }
}
