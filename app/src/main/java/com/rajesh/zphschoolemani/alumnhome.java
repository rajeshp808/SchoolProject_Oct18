package com.rajesh.zphschoolemani;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private FirebaseDatabase fDatabase;
    private DatabaseReference refDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnhome);

        try {
            Intent intent = getIntent();

            final String phone_key = intent.getStringExtra("phone_key");
            imgview_profpic = (ImageView) findViewById(R.id.iv_profile_pic_holder);
            refDatabase = FirebaseDatabase.getInstance().getReference().child("Alumn_List");

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
                            String profile_pic_url=(String) singleSnapshot.child("ProfPic_URL").getValue();
                           profile_pic_url = profile_pic_url.replaceAll("^\"|\"$", "");
                           Picasso.with(getApplicationContext()).load(profile_pic_url).into(imgview_profpic);
                           Log.e("Debug", "Sowmuch_actual url is" + profile_pic_url);
                           //imgview_profpic.setImageBitmap(getBitmapFromURL(profile_pic_url));
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
