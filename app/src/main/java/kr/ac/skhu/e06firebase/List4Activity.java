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
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class List4Activity extends AppCompatActivity {

    private TreeMap<String, DataItem> treeMap;
    private DataItemMapAdapter adapter;
    private int selectedIndex; 

    private CreateDialogFragment createDialogfragement; 
    private EditDialogFragment editDialogfragement; 
    private DeleteDialogFragment deleteDialogfragement; 

    DatabaseReference myServerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list3);

        treeMap = new TreeMap<String, DataItem>();
        adapter = new DataItemMapAdapter(this, R.layout.dataitem, treeMap);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
        myServerData = FirebaseDatabase.getInstance().getReference("myServerData04");

        ChildEventListener listener1 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String key = dataSnapshot.getKey();
                DataItem dataItem = dataSnapshot.getValue(DataItem.class);
                treeMap.put(key, dataItem);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                String key = dataSnapshot.getKey();
                DataItem dataItem = dataSnapshot.getValue(DataItem.class);
                treeMap.put(key, dataItem);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                treeMap.remove(key);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(List4Activity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        myServerData.addChildEventListener(listener1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (editDialogfragement == null) 
                    editDialogfragement = new EditDialogFragment(); 
                selectedIndex = position; 
                editDialogfragement.show(getSupportFragmentManager(), "EditDialog"); 
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list3, menu); 
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create) {
            if (createDialogfragement == null) 
                createDialogfragement = new CreateDialogFragment(); 
            createDialogfragement.show(getSupportFragmentManager(), "CreateDialog"); 
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    
    public static class CreateDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final List4Activity activity = (List4Activity)getActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("메모 입력"); 

            final View rootView = activity.getLayoutInflater().inflate(R.layout.dataitem_edit, null);
            final EditText editText_title = (EditText)rootView.findViewById(R.id.editText_title);
            final EditText editText_body = (EditText)rootView.findViewById(R.id.editText_body);
            builder.setView(rootView);

            builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CharSequence s1 = editText_title.getText();
                    CharSequence s2 = editText_body.getText();

                    DataItem dataItem = new DataItem();    
                    dataItem.setTitle(s1.toString());      
                    dataItem.setBody(s2.toString());       
                    dataItem.setModifiedTime(new Date()); 

                    String key = activity.myServerData.push().getKey();
                    activity.myServerData.child(key).setValue(dataItem);
                }
            });

            builder.setNegativeButton("취소", null); 
            AlertDialog dialog = builder.create(); 
            return dialog; 
        }
    }

    public static class EditDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final List4Activity activity = (List4Activity)getActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("메모 수정"); 

            final View rootView = activity.getLayoutInflater().inflate(R.layout.dataitem_edit, null);
            final EditText editText_title = (EditText)rootView.findViewById(R.id.editText_title);
            final EditText editText_body = (EditText)rootView.findViewById(R.id.editText_body);
            int index = activity.selectedIndex;
            final String key = activity.treeMap.keySet().toArray()[index].toString();
            final DataItem dataItem = activity.treeMap.get(key);
            editText_title.setText(dataItem.getTitle());
            editText_body.setText(dataItem.getBody());
            builder.setView(rootView);

            builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CharSequence s1 = editText_title.getText();
                    CharSequence s2 = editText_body.getText();
                    dataItem.setTitle(s1.toString());
                    dataItem.setBody(s2.toString());       
                    dataItem.setModifiedTime(new Date());
                    activity.myServerData.child(key).setValue(dataItem);
                } 
            });

            builder.setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (activity.deleteDialogfragement == null)
                        activity.deleteDialogfragement = new DeleteDialogFragment();
                    activity.deleteDialogfragement.show(activity.getSupportFragmentManager(), "DeleteDialog");
                } 
            });
            
            builder.setNegativeButton("취소", null);
            AlertDialog dialog = builder.create(); 
            return dialog; 
        }
    }

    public static class DeleteDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final List4Activity activity = (List4Activity) getActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("확인"); 
            builder.setMessage("삭제하시겠습니까?"); 

            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int index) {
                    int itemIndex = activity.selectedIndex;
                    String key = activity.treeMap.keySet().toArray()[itemIndex].toString();
                    activity.myServerData.child(key).removeValue();
                }
            });
            builder.setNegativeButton("아니오", null);
            AlertDialog dialog = builder.create(); 
            return dialog;
        }
    }
}
