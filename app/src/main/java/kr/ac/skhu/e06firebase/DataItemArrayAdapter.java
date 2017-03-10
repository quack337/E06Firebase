package kr.ac.skhu.e06firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DataItemArrayAdapter extends ArrayAdapter<DataItem> {
    private ArrayList<DataItem> arrayList; // 데이터 목록
    private int layoutId; // 데이터 항목의 화면 레이아웃 리소스 ID (즉 dataitem.xml 의 ID)

    public DataItemArrayAdapter(Context context, int layoutId, ArrayList<DataItem> arrayList) {
        super(context, layoutId, arrayList); // 부모 클래스 생성자를 반드시 호출해야 함
        this.layoutId = layoutId;
        this.arrayList = arrayList;
    }

    /*
    getView 메소드의 파라미터
        position 파라미터: 생성할 데이터 항목의 index (ArrayList에서 index)
        view 파라미터:     데이터 항목 한 개를 화면에 표시할 뷰(view) 객체
        parent:            ListView 객체
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            // 아래의 코드는 레이아웃 인플레이션을 실행한다.
            // 즉 데이터 항목 한 개를 표시할 뷰 객체들을 생성한다
            // RelativeLayout 객체와 TextView 객체 두 개가 생성된다.
            // RelativeLayout 객체가 root 이고, TextView 객체 두 개는 그 자식이다.
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutId, null); // 생성된 뷰 객체들의 root 객체가 리턴된다
            // 즉 RelativeLayout 객체가 리턴된다
        }
        DataItem item = arrayList.get(position); // 데이터 항목을 꺼낸다
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
