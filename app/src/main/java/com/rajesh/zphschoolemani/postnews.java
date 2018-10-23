package com.rajesh.zphschoolemani;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class postnews extends AppCompatActivity {
    private ImageButton imageBtn; private static final int GALLERY_REQUEST_CODE = 2;
    private Uri uri = null; private EditText textTitle; private EditText textDesc;
    private Button postBtn; private StorageReference storage; private FirebaseDatabase database;
    private DatabaseReference databaseRef; private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers; private FirebaseUser mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postnews);
        // initialize layout object

        postBtn = (Button)findViewById(R.id.bt_postbutton);
        textDesc = (EditText)findViewById(R.id.et_News_Description);
        textTitle = (EditText)findViewById(R.id.et_NewsTitle);
        storage = FirebaseStorage.getInstance().getReference();
        databaseRef = database.getInstance().getReference().child(“Blogzone”);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child(“Users”).child(mCurrentUser.getUid());
        imageBtn = (ImageButton)findViewById(R.id.imageBtn);
        //picking image from gallery
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType(“image/*”);
 startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
 } });
 // posting to Firebase
 postBtn.setOnClickListener(new View.OnClickListener() {
 @Override
 public void onClick(View view) {
 Toast.makeText(PostActivity.this, “POSTING…”, Toast.LENGTH_LONG).show();
 final String PostTitle = textTitle.getText().toString().trim();
 final String PostDesc = textDesc.getText().toString().trim();
 // do a check for empty fields
 if (!TextUtils.isEmpty(PostDesc) && !TextUtils.isEmpty(PostTitle)){
 StorageReference filepath = storage.child(“post_images”).child(uri.getLastPathSegment());
 filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
 @Override
 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
 @SuppressWarnings(“VisibleForTests”)
 //getting the post image download url
 final Uri downloadUrl = taskSnapshot.getDownloadUrl();
 Toast.makeText(getApplicationContext(), “Succesfully Uploaded”, Toast.LENGTH_SHORT).show();
 final DatabaseReference newPost = databaseRef.push();
 //adding post contents to database reference
 mDatabaseUsers.addValueEventListener(new ValueEventListener() {
 @Override
 public void onDataChange(DataSnapshot dataSnapshot) {
 newPost.child(“title”).setValue(PostTitle);
 newPost.child(“desc”).setValue(PostDesc);
 newPost.child(“imageUrl”).setValue(downloadUrl.toString());
 newPost.child(“uid”).setValue(mCurrentUser.getUid());
 newPost.child(“username”).setValue(dataSnapshot.child(“name”).getValue())
 .addOnCompleteListener(new OnCompleteListener<Void>() {
 @Override
 public void onComplete(@NonNull Task<Void> task) {
 if (task.isSuccessful()){
 Intent intent = new Intent(PostActivity.this, MainActivity.class);
 startActivity(intent);
 }}});}
 @Override
 public void onCancelled(DatabaseError databaseError) {
 } }); } }); }}}); }
 @Override
 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 super.onActivityResult(requestCode, resultCode, data);
 //image from gallery result
 if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
 uri = data.getData();
 imageBtn.setImageURI(uri);
 }}}

    }
}
