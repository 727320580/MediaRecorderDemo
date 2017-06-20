package com.xika.mediarecorderdemo.recorder;

/**
 * Email 727320580@qq.com
 * Created by xika on 2017/6/20
 * Vwesion 1.0
 * Dsscription:  录音实体类
 */

public class RecorderModel {

    float time;//时间长度
    String filePath;//文件路径

    public RecorderModel(float time, String filePath) {
        super();
        this.time = time;
        this.filePath = filePath;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
