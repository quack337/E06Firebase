package kr.ac.skhu.e06firebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class List2Activity extends AppCompatActivity {

    List<String> stringList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);

        stringList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringList);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        final DatabaseReference myServerData01 = FirebaseDatabase.getInstance().getReference("myServerData02");
        ValueEventListener listener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> typeIndicator = new GenericTypeIndicator<List<String>>() {};
                List<String> temp  = dataSnapshot.getValue(typeIndicator);
                if (temp == null) return;
                stringList.clear();
                stringList.addAll(temp);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
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
