package com.rajesh.zphschoolemani;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class add_announcement extends AppCompatActivity {

    private ImageButton imageBtn;
    private static final int GALLERY_REQUEST_CODE = 2;
    private Uri uri = null;
    private EditText textTitle; private EditText textDesc;
    private Button postBtn;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers; private FirebaseUser mCurrentUser;
    private StorageReference storageRef;
    ProgressDialog pd_postnews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_announcement);

            // initialize layout object

            postBtn = (Button) findViewById(R.id.bt_ann_postbutton);
            textDesc = (EditText) findViewById(R.id.et_Ann_Description);
            textTitle = (EditText) findViewById(R.id.et_Ann_Title);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storageRef= storage.getReferenceFromUrl("gs://zphschoolemani-d3d13.appspot.com");
            database = FirebaseDatabase.getInstance();
            databaseRef =  database.getReference("Announcements");
            pd_postnews = new ProgressDialog(this);

            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
            //mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
            imageBtn = (ImageButton) findViewById(R.id.animgButton);
            //picking image from gallery
            imageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(getApplicationContext(), "Select pic to upload", Toast.LENGTH_LONG).show();
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), GALLERY_REQUEST_CODE);
                }
            });
            // posting to Firebase
            postBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    final String PostTitle = textTitle.getText().toString().trim();
                    final String PostDesc = textDesc.getText().toString().trim();
                    // do a check for empty fields
                    if (!TextUtils.isEmpty(PostDesc) && !TextUtils.isEmpty(PostTitle) && uri !=null ) {

                        Toast.makeText(add_announcement.this, "POSTING…", Toast.LENGTH_LONG).show();
                        //StorageReference filepath = storageRef.child("newsimg").child(uri.getLastPathSegment());
                        //Toast.makeText(postnews.this, "select pic is"+uri.getLastPathSegment().toString(), Toast.LENGTH_LONG).show();

                        pd_postnews.setTitle("Uploading...");
                        pd_postnews.show();
                        final String randomString=UUID.randomUUID().toString();
                        final StorageReference ref = storageRef.child("announcement_img/"+ randomString);
                        ref.putFile(uri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        pd_postnews.dismiss();
                                        final Task<Uri> downloadURL=ref.getDownloadUrl();
                                        DatabaseReference newsItem = databaseRef.child(randomString);
                                        Map<String, Object> taskMap = new HashMap<>();
                                        taskMap.put("Announcement_Title", textTitle.getText().toString());
                                        taskMap.put("Announcement_Title_Description", textDesc.getText().toString());
                                        taskMap.put("File_URL", downloadURL.toString());
                                        newsItem.updateChildren(taskMap);
                                        Toast.makeText(add_announcement.this, "Uploaded"+downloadURL.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd_postnews.dismiss();
                                        Toast.makeText(add_announcement.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                                .getTotalByteCount());
                                        pd_postnews.setMessage("Uploaded "+(int)progress+"%");
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "Title or Description should not be left blank.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Exception at post announcement "+ex.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //image from gallery result
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            uri = data.getData();
            imageBtn.setImageURI(uri);

        }
    }
}
