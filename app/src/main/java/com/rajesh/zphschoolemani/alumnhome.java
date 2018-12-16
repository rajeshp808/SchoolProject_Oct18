package com.rajesh.zphschoolemani;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class alumnhome extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private ImageView imgview_profpic;
    private DatabaseReference refDatabase;
    private ImageButton alumnList;
    private ImageButton alumnMap;
    private  String phone_key;
    private TextView tv_profName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnhome);
        Intent intent = getIntent();
        phone_key = intent.getStringExtra("phone_key");
        alumnList=(ImageButton) findViewById(R.id.ib_prof_menu1);
        alumnMap=(ImageButton) findViewById(R.id.ib_prof_menu4);
        tv_profName=(TextView) findViewById(R.id.tv_profile_name);
        alumnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), " alumn list to be done", Toast.LENGTH_LONG).show();
                //startActivity(new Intent(alumnhome.this, alumnlist.class));
            }
        });
        alumnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(alumnhome.this, mapview.class);
                intent.putExtra("phone_key", phone_key);
                startActivity(intent);


            }
        });
        try {


            imgview_profpic = (ImageView) findViewById(R.id.iv_profile_pic_holder);

            refDatabase = FirebaseDatabase.getInstance().getReference().child("alumn_list");

            //refDatabase = fDatabase.getReference().child("Alumn_List").child(phone_key).child("ProfPic_URL");

            Toast.makeText(getApplicationContext(), "received phone number value"+phone_key, Toast.LENGTH_LONG).show();
            //Query phoneQuery = refDatabase.orderByChild(phone_key).equalTo(phone_key);
            refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //String url=  dataSnapshot.toString();
                    //url = url.replaceAll("^\"|\"$", "");
                    //Toast.makeText(getApplicationContext(), "url value"+url, Toast.LENGTH_LONG).show();
                   //
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                       if((singleSnapshot.getKey()).equals(phone_key)){
                          // Toast.makeText(getApplicationContext(), "user value 666 found"+singleSnapshot.toString(), Toast.LENGTH_LONG).show();
                            String profile_pic_url=(String) singleSnapshot.child("profpicurl").getValue();
                           profile_pic_url = profile_pic_url.replaceAll("^\"|\"$", "");
                           Picasso.with(getApplicationContext()).load(profile_pic_url).into(imgview_profpic);
                           Log.e("Debug", "Sowmuch_actual url is" + profile_pic_url);
                           //imgview_profpic.setImageBitmap(getBitmapFromURL(profile_pic_url));
                           String fullName=(String) singleSnapshot.child("fullname").getValue();
                           tv_profName.setText(fullName);
                           tv_profName.setTextIsSelectable(true);
                           tv_profName.setBackgroundColor(Color.parseColor("#8e8ef9"));

                        }

                        //Log.e("Debug", "Sowmuch_alumnhome_onDataChange= " + user);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Debug", "Sowmuch_alumnhome_onCancelled", databaseError.toException());
                }
            });
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Exception alumnhome"+ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Debug", "Sowmuch_Exception "+ ex.getMessage());
        }
    }

    private Bitmap getBitmapFromURL(String url) {
        try {

            URL imgURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imgURL.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Debug","sowmuch expcetion"+e.getMessage());
            return null;
        }
    }
}
