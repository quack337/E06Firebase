package kr.ac.skhu.e06firebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class List1Activity extends AppCompatActivity {

    List<String> stringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list1);

        final DatabaseReference myServerData01 = FirebaseDatabase.getInstance().getReference("myServerData02");
        ValueEventListener listener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> typeIndicator = new GenericTypeIndicator<List<String>>() {};
                stringList  = dataSnapshot.getValue(typeIndicator);
                if (stringList == null) return;
                TextView textView = (TextView)findViewById(R.id.textView);
                StringBuilder builder = new StringBuilder();
                for (String s : stringList)
                    builder.append(s).append(", ");
                String s = builder.toString();
                textView.setText(s);
                Log.d("내태그", "받은 데이터: " + s);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // 주석 추가
                Log.e("내태그", "서버 에러: ", error.toException());
            }
        };
        myServerData01.addValueEventListener(listener1);
        Button button = (Button)findViewById(R.id.button);
        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText)findViewById(R.id.editText);
                String s = editText.getText().toString();
                editText.setText("");
                if (stringList == null) stringList = new ArrayList<String>();
                stringList.add(s);
                myServerData01.setValue(stringList);
            }
        };
        button.setOnClickListener(listener2);
    }
}
