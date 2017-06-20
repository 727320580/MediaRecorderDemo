package com.xika.mediarecorderdemo.activity;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.xika.mediarecorderdemo.R;
import com.xika.mediarecorderdemo.audio.AudioRecorderButton;
import com.xika.mediarecorderdemo.audio.MediaPlayerManager;
import com.xika.mediarecorderdemo.recorder.RecorderAdapter;
import com.xika.mediarecorderdemo.recorder.RecorderModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Email 727320580@qq.com
 * Created by xika on 2017/6/20
 * Vwesion 1.0
 * Dsscription:  语音聊天列表界面
 */

public class MessageActivity extends Activity {
    // 消息列表布局的对象
    private ListView mListView;
    // 录音数据加载类
    private ArrayAdapter<RecorderModel> mAdapter;
    // 录音数据对象
    private List<RecorderModel> mDatas = new ArrayList<RecorderModel>();
    // 录音按钮
    private AudioRecorderButton mAudioRecorderButton;
    // 录音文件的对象
    private View animView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mListView = (ListView) findViewById(R.id.id_listview);
        mAudioRecorderButton = (AudioRecorderButton) findViewById(R.id.id_recorder_button);
        mAudioRecorderButton.setAudioFinishRecorderCallBack(new AudioRecorderButton.AudioFinishRecorderCallBack() {

            public void onFinish(float seconds, String filePath) {
                RecorderModel RecorderModel = new RecorderModel(seconds, filePath);
                mDatas.add(RecorderModel);
                //更新数据
                mAdapter.notifyDataSetChanged();
                //设置位置
                mListView.setSelection(mDatas.size() - 1);
            }
        });

        mAdapter = new RecorderAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);

        //listView的item点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                // 声音播放动画
                if (animView != null) {
                    animView.setBackgroundResource(R.drawable.adj);
                    animView = null;
                    MediaPlayerManager.pause();
                    MediaPlayerManager.release();
                }else {
                    animView = view.findViewById(R.id.id_recoder_anim);
                    animView.setBackgroundResource(R.drawable.play_anim);
                    AnimationDrawable animation = (AnimationDrawable) animView.getBackground();
                    animation.start();
                    // 播放录音
                    MediaPlayerManager.playSound(mDatas.get(position).getFilePath(), new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {
                            //播放完成后修改图片
                            animView.setBackgroundResource(R.drawable.adj);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaPlayerManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerManager.release();
    }
}
