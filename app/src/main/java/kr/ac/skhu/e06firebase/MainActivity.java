package kr.ac.skhu.e06firebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        final DatabaseReference myServerData01 = FirebaseDatabase.getInstance().getReference("myServerData01");
        ValueEventListener listener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                TextView textView = (TextView)findViewById(R.id.textView);
                textView.setText(value);
                Log.d("내태그", "받은 데이터: " + value);
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
                myServerData01.setValue(s);
            }
        };
        button.setOnClickListener(listener2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Class clsObj = null;
        switch (id) {
            case R.id.action_list1:
                clsObj = List1Activity.class;
                break;
            case R.id.action_list2:
                clsObj = List2Activity.class;
                break;
            case R.id.action_list3:
                clsObj = List3Activity.class;
                break;
            case R.id.action_list4:
                clsObj = List4Activity.class;
                break;
        }
        if (clsObj != null) {
            Intent intent  = new Intent(this, clsObj);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
