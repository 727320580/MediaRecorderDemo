package com.xika.mediarecorderdemo.recorder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xika.mediarecorderdemo.R;

import java.util.List;

/**
 * Email 727320580@qq.com
 * Created by xika on 2017/6/20
 * Vwesion 1.0
 * Dsscription: 小心ListView的适配工作
 */

public class RecorderAdapter extends ArrayAdapter<RecorderModel> {
    // 上下文
    private Context mContext;
    // 数据list
    private List<RecorderModel> mDatas;
    // item的最大宽度
    private int mMinWidth;
    // item的最小高度
    private int mMaxWidth;
    // item适配器
    private LayoutInflater mInflater;

    public RecorderAdapter(Context context, List<RecorderModel> objects) {
        super(context, -1, objects);

        this.mContext = context;
        this.mDatas = objects;

        // 获取屏幕宽度 (通过调用系统的WindowService 来获取窗口管理器)
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        // 获取最大宽度为屏幕的0.7
        mMaxWidth = (int) (outMetrics.widthPixels * 0.7f);
        // 获取最大高度为屏幕的0.15
        mMinWidth = (int) (outMetrics.heightPixels * 0.15);

        mInflater = LayoutInflater.from(context);
    }

    final class ViewHolder {
        // 显示时间
        TextView seconds;
        //控件Item显示的长度
        View length;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_recoder, null);
            viewHolder = new ViewHolder();
            viewHolder.seconds = (TextView) convertView.findViewById(R.id.id_recoder_time);
            viewHolder.length = convertView.findViewById(R.id.id_recoder_lenght);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.seconds.setText(Math.round(mDatas.get(position).getTime()) + "\"");
        ViewGroup.LayoutParams lp = viewHolder.length.getLayoutParams();
        lp.width = (int) (mMinWidth + (mMaxWidth / 60f) * mDatas.get(position).getTime());
        return convertView;
    }
}
