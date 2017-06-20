package com.xika.mediarecorderdemo.audio;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.xika.mediarecorderdemo.R;
import com.xika.mediarecorderdemo.dialog.DialogManager;

/**
 * Email 727320580@qq.com
 * Created by xika on 2017/6/20
 * Vwesion 1.0
 * Dsscription: 自定义的录音按钮
 * <p>
 * <p>
 * 步骤
 * 1.开新线程完成录音计时功能
 * 2.按钮录音完成操作的接口回调  --->(用于Activity的录音完成的回调操作)
 * 3.按钮的onTouch手指操作监听
 * 4.复原状态
 * 5.取消录音的操作
 * 6.录音状态修正的dialog状态和按钮状态
 */

public class AudioRecorderButton extends Button {
    // 正常状态
    private static final int STATE_NORMAL = 1;
    // 录音状态
    private static final int STATE_RECORDER = 2;
    // 取消录音状态
    private static final int STATE_CANCEL = 3;
    // 记录当前状态
    private int mCurrentState = STATE_NORMAL;
    // 是否录音标志
    private boolean isRecorder;
    // 判断手指上滑距离已判断 是否取消录音
    private static final int DISTANCE_Y_CANCEL = 50;
    // 对话框管理工具类
    private DialogManager mDialogManager;
    // 录音管理工具类
    private AudioManager mAudioManager;
    // 记录录音的时间
    private float mTime;
    // 是否触发LongClick
    private boolean mReady;
    //录音准备
    private static final int MSG_AUDIO_PREPARED = 0x110;
    //音量发生改变
    private static final int MSG_VOICE_CHANGED = 0x111;
    //取消提示对话框
    private static final int MSG_DIALOG_DIMISS = 0x112;


    /**
     * 开启一个获取音量大小的线程
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {

        @Override
        public void run() {
            // 循环判断是否录音
            while (isRecorder) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    // 声音修改的状态来给该dialog的声音大小
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    /**
     * 执行关于声音的操作
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // todo 录音准备时候的操作
                case MSG_AUDIO_PREPARED:
                    // 开始录音显示录音消息提示框
                    mDialogManager.showRecordingDialog();
                    // 更改录音的标记为录音状态
                    isRecorder = true;
                    // 开启一个新的线程来记录录音的时间   --->创建了一个自己的Runnable 执行录音计时操作
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                // todo 声音改变时候的操作
                case MSG_VOICE_CHANGED:
                    // 更改dialog的声音提示的级别
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                // todo 不再录音的操作
                case MSG_DIALOG_DIMISS:
                    // 取消对话框
                    mDialogManager.dimissDialog();
                    break;
            }
        }
    };

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    /**
     * dialog和录音管理器的初始化工作
     *
     * @param context
     * @param attrs
     */
    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化消息提示框
        mDialogManager = new DialogManager(context);
        // 录音文件存放地址
        String dir = Environment.getExternalStorageDirectory() + "/ldm_voice";
        mAudioManager = AudioManager.getInstance(dir);
        // 录音的准备操作 -------->(包含显示录音dialog的提示,录音计时操作)
        mAudioManager.setmAudioSataeListenner(new AudioManager.AudioStateListenner() {
            @Override
            public void wellPrepare() {
                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
            }
        });
        // 由于这个类是button所以在构造方法中添加监听事件
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                // 开始执行录音操作
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    public AudioFinishRecorderCallBack mAudioFinishRecorderCallBack;

    public interface AudioFinishRecorderCallBack {
        void onFinish(float seconds, String path);
    }

    // 录音完成的接口回调
    public void setAudioFinishRecorderCallBack(AudioFinishRecorderCallBack callBack) {
        this.mAudioFinishRecorderCallBack = callBack;
    }

    /**
     * 手指在按下录音按钮button的操作
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获得x轴坐标
        int x = (int) event.getX();
        // 获得y轴坐标
        int y = (int) event.getY();

        switch (event.getAction()) {
            // todo 手指按下的操作
            case MotionEvent.ACTION_DOWN:
                // 开始录音
                changeState(STATE_RECORDER);
                break;
            // todo 手指移动的操作
            case MotionEvent.ACTION_MOVE:
                if (wantToCancle(x, y)) {
                    changeState(STATE_CANCEL);
                } else {
                    changeState(STATE_RECORDER);
                }
                break;
            // todo 手指离开的操作
            case MotionEvent.ACTION_UP:
                // -------表示准备好录音 ---------------
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                // --------不录音或者声音小于0.6秒 ------------
                if (!isRecorder || mTime < 0.6f) {
                    // 时间太短了
                    mDialogManager.tooShort();
                    mAudioManager.clanceAndio();
                    // 延迟一秒钟显示对话框
                    mHandler.sendEmptyMessageAtTime(MSG_DIALOG_DIMISS, 1000);
                    // ----------- 录音状态 -------------
                } else if (mCurrentState == STATE_RECORDER) {
                    // dialog消失
                    mDialogManager.dimissDialog();
                    // 释放录音对象资源
                    mAudioManager.relaseAndio();
                    // 录音完成之后的回调
                    if (mAudioFinishRecorderCallBack != null) {
                        mAudioFinishRecorderCallBack.onFinish(mTime, mAudioManager.getmCurrentFilePath());
                    }
                    // ------------- 录音取消状态 -------------
                } else if (mCurrentState == STATE_CANCEL) {
                    mDialogManager.dimissDialog();
                    mAudioManager.clanceAndio();
                }
                reset();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 状态复位   1.是否在录音 2.录音时间 3.是否准备好录音 4.改变按钮的状态
     */
    private void reset() {
        isRecorder = false;
        mTime = 0;
        mReady = false;
        changeState(STATE_NORMAL);
    }

    /**
     * 判断是否取消录音
     *
     * @param x
     * @param y
     * @return
     */
    private boolean wantToCancle(int x, int y) {
        // 超过按钮的宽度
        if (x < 0 || x > getWidth()) {
            return true;
        }
        // 超过按钮的高度
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }

        return false;
    }

    /**
     * 根据状态改变Button显示
     */
    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                // 正常状态下 按钮的字体样式
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;
                // 录音状态下的按钮的 dialog样式
                case STATE_RECORDER:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    setText(R.string.str_recorder_recording);
                    if (isRecorder) {
                        mDialogManager.recording();
                    }
                    break;
                // 取消录音状态的dialog 的样式
                case STATE_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    mDialogManager.wantToCancel();
                    setText(R.string.str_recorder_want_cancel);
                    break;
            }

        }

    }
}
