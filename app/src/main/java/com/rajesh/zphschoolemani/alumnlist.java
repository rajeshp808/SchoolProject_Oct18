package com.rajesh.zphschoolemani;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class alumnlist extends AppCompatActivity {
    private  ListView lv_alumnList;
    private DatabaseReference mDatabase;
    private FirebaseListOptions<student> options;
    private FirebaseListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_alumnlist);
        try {
            Toast.makeText(getApplicationContext(), "alumn List started", Toast.LENGTH_LONG).show();
            lv_alumnList = (ListView) findViewById(R.id.listview_alumn);
            mDatabase = FirebaseDatabase.getInstance().getReference().child("alumn_list");
            Query alumnQuery = mDatabase.orderByKey();
             options = new FirebaseListOptions.Builder<student>().setLayout(R.layout.student)
                    .setLifecycleOwner(alumnlist.this)
                    .setQuery(alumnQuery, student.class).build();
            adapter = new FirebaseListAdapter(options) {
                @Override
                protected void populateView(View v, Object model, int position) {
                    TextView FullName = v.findViewById(R.id.tv_stu_name);
                    TextView SSC_Year = v.findViewById(R.id.tv_ssc_year);
                    student std = (student) model;
                    FullName.setText(std.getFullname().toString());
                    SSC_Year.setText(std.getSscyear().toString());
                }
            };
        }catch (Exception ex) {
            Log.d("Debug", "sowmuch alumnList onCreate: error"+ex.getMessage());
            Toast.makeText(getApplicationContext(), "exception "+ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        lv_alumnList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
