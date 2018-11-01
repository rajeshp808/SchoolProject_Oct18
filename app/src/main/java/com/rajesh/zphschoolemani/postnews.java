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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class postnews extends AppCompatActivity {
    private ImageButton imageBtn;
    private static final int GALLERY_REQUEST_CODE = 2;
    private Uri uri = null;
    private EditText textTitle; private EditText textDesc;
    private Button postBtn;
     private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private DatabaseReference mDatabaseUsers;
    private StorageReference storageRef;
    ProgressDialog pd_postnews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postnews);

        // initialize layout object

        postBtn = (Button) findViewById(R.id.bt_postbutton);
        textDesc = (EditText) findViewById(R.id.et_News_Description);
        textTitle = (EditText) findViewById(R.id.et_NewsTitle);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef= storage.getReferenceFromUrl("gs://zphschoolemani-d3d13.appspot.com");
        database = FirebaseDatabase.getInstance();
        databaseRef =  database.getReference("News");
            pd_postnews = new ProgressDialog(this);

        //mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        imageBtn = (ImageButton) findViewById(R.id.imageButton);
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

                    Toast.makeText(postnews.this, "POSTING…", Toast.LENGTH_LONG).show();
                    //StorageReference filepath = storageRef.child("newsimg").child(uri.getLastPathSegment());
                    //Toast.makeText(postnews.this, "select pic is"+uri.getLastPathSegment().toString(), Toast.LENGTH_LONG).show();

                    pd_postnews.setTitle("Uploading...");
                    pd_postnews.show();
                    final String randomString=UUID.randomUUID().toString();
                    final StorageReference ref = storageRef.child("newsimg/"+ randomString);
                    ref.putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                   try {

                                       Uri downloadURL ;
                                       Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                       while (!urlTask.isSuccessful());
                                        downloadURL = urlTask.getResult();
                                       DatabaseReference newsItem = databaseRef.child(randomString);
                                       Map<String, Object> taskMap = new HashMap<>();
                                       taskMap.put("News_Title", textTitle.getText().toString());
                                       taskMap.put("News_Description", textDesc.getText().toString());
                                       taskMap.put("File_URL", downloadURL.toString());
                                       newsItem.updateChildren(taskMap);

                                       pd_postnews.dismiss();
                                       Toast.makeText(postnews.this, "Upload completed, Please check the News Section" , Toast.LENGTH_LONG).show();
                                       finish();
                                   }catch (Exception ex) {
                                       Toast.makeText(postnews.this, "exception after uploa…" + ex.getMessage(), Toast.LENGTH_LONG).show();
                                   }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd_postnews.dismiss();
                                    Toast.makeText(postnews.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                            .getTotalByteCount());
                                    pd_postnews.setMessage("Uploaded "+(int)progress+"%");
                                }
                            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            //Toast.makeText(postnews.this, "completed" , Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Title or Description should not be left blank.", Toast.LENGTH_LONG).show();
                }
            }
        });
    } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Exception at postnews "+ex.getMessage(), Toast.LENGTH_LONG).show();
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



