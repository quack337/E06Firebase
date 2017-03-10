package kr.ac.skhu.e06firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Set;
import java.util.TreeMap;

public class DataItemMapAdapter extends BaseAdapter {
    private Context context;
    private int layoutId; // 데이터 항목의 화면 레이아웃 리소스 ID (즉 dataitem.xml 의 ID)
    private TreeMap<String, DataItem> map; // 데이터 목록.

    public DataItemMapAdapter(Context context, int layoutId, TreeMap<String,DataItem> map) {
        this.context = context;
        this.layoutId = layoutId;
        this.map = map;
    }

    @Override
    public int getCount() {
        return map.size();
    }

    @Override
    public DataItem getItem(int position) {
        String key = map.keySet().toArray()[position].toString();
        return map.get(key);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutId, null); // 생성된 뷰 객체들의 root 객체가 리턴된다
            // 즉 RelativeLayout 객체가 리턴된다
        }
        DataItem item = getItem(position); // 데이터 항목을 꺼낸다
        if (item != null) {
            // 두 개의 TextView에 데이터 항목의 title 값과 modifiedTime 값을 각각 채운다
            TextView textView_title = (TextView) view.findViewById(R.id.textView_title);
            TextView textView_modifiedTime = (TextView) view.findViewById(R.id.textView_modifiedTime);
            textView_title.setText(item.getTitle());
            textView_modifiedTime.setText(item.getModifiedTimeString());
        }
        return view;
    }
}
