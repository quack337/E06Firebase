package kr.ac.skhu.e06firebase;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.app.Dialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class List3Activity extends AppCompatActivity {

    private ArrayList<DataItem> arrayList; // 데이터 항목(DataItem)의 목록을 보관하는 배열 자료구조
    private ArrayAdapter<DataItem> adapter;
    private int selectedIndex; // 선택된 데이터 항목의 arrayList에서 index

    // 대화상자 관리자 객체를 한 번 만든 다음 계속 사용하기 위해서 멤버 변수로 선언하였다
    private CreateDialogFragment createDialogfragement; // 새 메모 작성 대화상자 관리자
    private EditDialogFragment editDialogfragement; // 수정 대화상자 관리자
    private DeleteDialogFragment deleteDialogfragement; // 삭제 대화상자 관리자

    // firebase DB의 myServerData03데이터 항목에 연결된 객체
    DatabaseReference myServerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list3);

        arrayList = new ArrayList<DataItem>();
        adapter = new DataItemArrayAdapter(this, R.layout.dataitem, arrayList);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // firebase DB의 myServerData03 데이터 항목에 연결된 객체를 생성한다.
        myServerData = FirebaseDatabase.getInstance().getReference("myServerData03");

        // firebase DB의 데이터 항목이 변경될 때 자동으로 호출될 리스너 객체를 생성한다.
        ValueEventListener listener1 = new ValueEventListener() {
            @Override
            // firebase DB의 데이터 항목이 변경되면 이 메소드가 즉시 호출된다.
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 변경된 데이터 항목의 값을 List<DataItem> 타입의 객체로 전달 받는다.
                GenericTypeIndicator<List<DataItem>> typeIndicator = new GenericTypeIndicator<List<DataItem>>() {};
                List<DataItem> temp  = dataSnapshot.getValue(typeIndicator);
                if (temp == null) return;

                // 전달 받은 데이터를 arrayList 멤버 변수에 채운다.
                arrayList.clear();
                arrayList.addAll(temp);

                // ListView가 즉시 다시 그려지도록 한다.
                adapter.notifyDataSetChanged();
            }

            // firebase DB를 사용하다가 에러가 발생할 때 이 메소드가 즉시 호출된다.
            @Override
            public void onCancelled(DatabaseError error) {
                // logcat 창에 에러 로그 메시지를 출력한다.
                Log.e("내태그", "서버 에러: ", error.toException());
            }
        };

        // firebase DB의 데이터 항목에 리스너 객체를 등록한다.
        myServerData.addValueEventListener(listener1);

        // ListView 항목을 클릭했을 때 실행될 메소드 등록하는 코드의 시작
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (editDialogfragement == null) // 대화상자 관리자 객체를 아직 만들지 않았다면
                    editDialogfragement = new EditDialogFragment(); // 대화상자 관리자 객체를 만든다
                selectedIndex = position; // 수정할 항목의 index를 대입한다.
                editDialogfragement.show(getSupportFragmentManager(), "EditDialog"); // 화면에 대화상자 보이기
            }
        });
        // ListView 항목을 길게 클릭했을 때 실행될 메소드 등록하는 코드의 끝
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list3, menu); // 액티비티의 메뉴 생성하기
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create) {
            if (createDialogfragement == null) // 대화상자 관리자 객체를 아직 만들지 않았다면
                createDialogfragement = new CreateDialogFragment(); // 대화상자 관리자 객체를 만든다
            createDialogfragement.show(getSupportFragmentManager(), "CreateDialog"); // 화면에 대화상자 보이기
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 새 메모를 작성하기 위한 대화상자 관리자 클래스
    public static class CreateDialogFragment extends DialogFragment {
        @Override
        // 새 메모 작성 대화상자를 만드는 메소드
        // 이 메소드는 대화상자를 새로 만들어야 할 때에만 호출된다.
        // 한 번 만들어진 대화상자는 계속 재사용 된다.
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final List3Activity activity = (List3Activity)getActivity();  // 액티비티 객체에 대한 참조 얻기
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("메모 입력"); // 대화 상자의 제목 설정하기

            // 대화 상자에 표시될 뷰 객체들을 자동으로 생성함.
            final View rootView = activity.getLayoutInflater().inflate(R.layout.dataitem_edit, null);

            // 자동으로 생성된 EditText 객체들에 대한 참조를 미리 얻어 둔다.
            final EditText editText_title = (EditText)rootView.findViewById(R.id.editText_title);
            final EditText editText_body = (EditText)rootView.findViewById(R.id.editText_body);

            // 자동으로 생성된 뷰 객체들을 대화상자에 추가한다
            builder.setView(rootView);

            // 대화상자에 '저장' 버튼 추가하는 코드의 시작
            builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                @Override
                // 대화상자의 '저장' 버튼이 클릭되면 실행되는 메소드
                public void onClick(DialogInterface dialog, int which) {
                    // 대화 상자의 EditText에 입력된 내용을 꺼낸다.
                    // editText_title, editText_body는 outer 메소드의 final 지역 변수이다.
                    // outer 메소드의 final 지역 변수는 inner 메소드에서 사용할 수 있다.
                    CharSequence s1 = editText_title.getText();
                    CharSequence s2 = editText_body.getText();

                    DataItem dataItem = new DataItem();    // ListView에 추가할 데이터 항목 객체 생성
                    dataItem.setTitle(s1.toString());      // 데이터 항목 객체에 제목 채우기
                    dataItem.setBody(s2.toString());       // 데이터 항목 객체에 내용 채우기
                    dataItem.setModifiedTime(new Date()); // 데이터 항목 객체에 날짜 채우기
                    activity.arrayList.add(dataItem);    // 데이터 목록에 새 객체 추가하기

                    // arrayList 데이터 목록을 firebase DB의 데이터 항목에 저장한다.
                    // DB의 데이터 목록 전체가 교체된다.
                    activity.myServerData.setValue(activity.arrayList);
                } // onClick 메소드의 끝
            });
            // 대화상자에 '저장' 버튼을 추가하는 코드의 끝

            builder.setNegativeButton("취소", null); // 대화상자에 '취소' 버튼을 추가하기
            AlertDialog dialog = builder.create(); // 대화상자 객체 생성하기
            return dialog; // 생성된 대화상자 객체 리턴
        }
    }

    // 데이터 항목을 수정하기 위한 대화상자 관리자 클래스
    public static class EditDialogFragment extends DialogFragment {
        @Override
        // 수정 대화상자를 만드는 메소드
        // 이 메소드는 대화상자를 새로 만들어야 할 때에만 호출된다.
        // 한 번 만들어진 대화상자는 계속 재사용 된다.
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final List3Activity activity = (List3Activity)getActivity();  // 액티비티 객체에 대한 참조 얻기
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("메모 수정"); // 대화 상자의 제목 설정하기

            // 대화 상자에 표시될 뷰 객체들을 자동으로 생성함.
            final View rootView = activity.getLayoutInflater().inflate(R.layout.dataitem_edit, null);

            // 자동으로 생성된 EditText 객체들에 대한 참조를 미리 얻어 둔다.
            final EditText editText_title = (EditText)rootView.findViewById(R.id.editText_title);
            final EditText editText_body = (EditText)rootView.findViewById(R.id.editText_body);

            // arrayList에서 selectedIndex 위치의 데이터 항목 객체에 대한 참조를 얻는다.
            int index = activity.selectedIndex;
            final DataItem dataItem = activity.arrayList.get(index);

            // 데이터 항목의 제목과 내용을 EditText에 채운다.
            editText_title.setText(dataItem.getTitle());
            editText_body.setText(dataItem.getBody());

            // 자동으로 생성된 뷰 객체들을 대화상자에 추가한다
            builder.setView(rootView);

            // 대화상자에 '저장' 버튼 추가하는 코드의 시작
            builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                @Override
                // 대화상자의 '저장' 버튼이 클릭되면 실행되는 메소드
                public void onClick(DialogInterface dialog, int which) {
                    // 대화 상자의 EditText에 입력된 내용을 꺼낸다.
                    // editText_title, editText_body는 outer 메소드의 final 지역 변수이다.
                    // outer 메소드의 final 지역 변수는 inner 메소드에서 사용할 수 있다.
                    CharSequence s1 = editText_title.getText();
                    CharSequence s2 = editText_body.getText();

                    // dataItem은 outer 메소드의 final 지역변수이다.
                    dataItem.setTitle(s1.toString());      // 데이터 항목 객체에 제목 채우기
                    dataItem.setBody(s2.toString());       // 데이터 항목 객체에 내용 채우기
                    dataItem.setModifiedTime(new Date()); // 데이터 항목 객체에 날짜 채우기

                    // arrayList 데이터 목록을 firebase DB의 데이터 항목에 저장한다.
                    // 데이터 목록 전체가 교체된다.
                    activity.myServerData.setValue(activity.arrayList);
                } // onClick 메소드의 끝
            });
            // 대화상자에 '저장' 버튼을 추가하는 코드의 끝

            // 대화상자에 '삭제' 버튼 추가하는 코드의 시작
            builder.setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                @Override
                // 대화상자의 '삭제' 버튼이 클릭되면 실행되는 메소드
                public void onClick(DialogInterface dialog, int which) {

                    // 삭제 대화상자를 생성하여 화면에 보이도록 한다.
                    // this는 List3Activity 객체가 아니기 때문에 activity. 이 필요하다.
                    // activity는 outer 메소드의 지역변수이다. 이 변수는 List3Activity 객체를
                    // 참조한다.
                    if (activity.deleteDialogfragement == null)
                        activity.deleteDialogfragement = new DeleteDialogFragment();
                    activity.deleteDialogfragement.show(activity.getSupportFragmentManager(), "DeleteDialog");
                } // onClick 메소드의 끝
            });
            // 대화상자에 '삭제' 버튼을 추가하는 코드의 끝

            builder.setNegativeButton("취소", null); // 대화상자에 '취소' 버튼을 추가하기
            AlertDialog dialog = builder.create(); // 대화상자 객체 생성하기
            return dialog; // 생성된 대화상자 객체 리턴
        }
    }

    // 데이터 항목을 삭제하기 위한 대화상자 관리자 클래스
    public static class DeleteDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final List3Activity activity = (List3Activity) getActivity(); // 액티비티 객체에 대한 참조 얻기

            // 데이터 항목을 삭제하기 위한 대화상자를 생성하는 코드의 시작
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("확인"); // 대화상자 제목 설정하기
            builder.setMessage("삭제하시겠습니까?"); // 대화상자에 표시할 문자열 설정하기

            // 대화상자에 "예" 버튼 추가하는 코드의 시작
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int index) {
                    // arrayList에서 selectedIndex 위치의 데이터 항목을 제거한다.
                    int itemIndex = activity.selectedIndex;
                    activity.arrayList.remove(itemIndex); // arrayList에서 itemIndex 위치의 데이터 항목 제거하기

                    // arrayList 데이터 목록을 firebase DB의 데이터 항목에 저장한다.
                    // 데이터 목록 전체가 교체된다.
                    activity.myServerData.setValue(activity.arrayList);
                }
            });
            // 대화상자에 "예" 버튼 추가하는 코드의 끝

            builder.setNegativeButton("아니오", null); // 대화상자에 "아니오" 버튼 추가하기
            AlertDialog dialog = builder.create(); // 대화상자 객체 생성하기
            return dialog;
        }
    }
}

