package com.rajesh.zphschoolemani;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class postnews extends AppCompatActivity {
    private ImageButton imageBtn;
    private static final int GALLERY_REQUEST_CODE = 2;
    private Uri uri = null;
    private EditText textTitle; private EditText textDesc,textSerial;
    private Button postBtn;
     private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    ProgressDialog pd_postnews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (hasAccesstoPost()) {
            try {
                 super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_postnews);

                // initialize layout object

                postBtn = (Button) findViewById(R.id.bt_postbutton);
                textDesc = (EditText) findViewById(R.id.et_News_Description);
                textTitle = (EditText) findViewById(R.id.et_NewsTitle);
                textSerial = (EditText) findViewById(R.id.tv_postnews);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                storageRef = storage.getReferenceFromUrl("gs://zphschoolemani-d3d13.appspot.com");
                database = FirebaseDatabase.getInstance();
                databaseRef = database.getReference("News");
                pd_postnews = new ProgressDialog(this);

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
                        if (!TextUtils.isEmpty(PostDesc) && !TextUtils.isEmpty(PostTitle) && uri != null) {

                            Toast.makeText(postnews.this, "POSTING…", Toast.LENGTH_LONG).show();
                            //StorageReference filepath = storageRef.child("newsimg").child(uri.getLastPathSegment());
                            //Toast.makeText(postnews.this, "select pic is"+uri.getLastPathSegment().toString(), Toast.LENGTH_LONG).show();

                            pd_postnews.setTitle("Uploading...");
                            pd_postnews.show();
                            final String serialString = textSerial.getText().toString();
                            final StorageReference ref = storageRef.child("newsimg/" + serialString);
                            ref.putFile(uri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            try {

                                                Uri downloadURL;
                                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                while (!urlTask.isSuccessful()) ;
                                                downloadURL = urlTask.getResult();
                                                DatabaseReference newsItem = databaseRef.child(serialString);
                                                Map<String, Object> taskMap = new HashMap<>();
                                                taskMap.put("title", textTitle.getText().toString());
                                                taskMap.put("description", textDesc.getText().toString());
                                                taskMap.put("url", downloadURL.toString());
                                                newsItem.updateChildren(taskMap);
                                                if (pd_postnews != null && pd_postnews.isShowing()) {
                                                    pd_postnews.dismiss();
                                                }
                                                Toast.makeText(postnews.this, "Upload completed, Please check the News Section", Toast.LENGTH_LONG).show();
                                                finish();
                                            } catch (Exception ex) {
                                                Toast.makeText(postnews.this, "exception after uploa…" + ex.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd_postnews.dismiss();
                                            Toast.makeText(postnews.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                    .getTotalByteCount());
                                            pd_postnews.setMessage("Uploaded " + (int) progress + "%");
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
            } catch(Exception ex){
                Log.d("Debug", "sowmuch:postnews exception:" + ex.getMessage());
                Toast.makeText(getApplicationContext(), "Exception at postnews " + ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        } else {
            Log.d("Debug", "You dont have access to post news");
            Toast.makeText(getApplicationContext(), "You don't have access to post the news content on this App", Toast.LENGTH_LONG).show();
            finish();
        }
 }
 @Override
 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 super.onActivityResult(requestCode, resultCode, data);
 //image from gallery result
 if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){

     try {
         uri = data.getData();
         Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
         // Log.d(TAG, String.valueOf(bitmap));

         imageBtn.setImageBitmap(bitmap);
     } catch (IOException e) {
         Log.d("Debug", "sowmuch:exception at selecting image gallery:"+e.getMessage());
         Toast.makeText(getApplicationContext(), "Error in selecting image, please try again", Toast.LENGTH_LONG).show();
         e.printStackTrace();
     }

 }
 }

    private boolean hasAccesstoPost() {
        boolean isfile_existed = false;
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.isDirectory()) {
                File[] allfiles = sd.listFiles();

                for (File file : allfiles) {
                    if (file.getName().equals("zphschooladmin.txt")) {
                       isfile_existed = true;
                       break;
                    }
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Exception in Finding KYC file" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        // Toast.makeText(getApplicationContext(), "isfile_existed "+isfile_not_existed, Toast.LENGTH_LONG).show();
        return isfile_existed;
    }
}



