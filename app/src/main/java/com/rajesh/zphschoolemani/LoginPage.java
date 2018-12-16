package com.rajesh.zphschoolemani;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginPage extends AppCompatActivity {

    EditText et_phone;
    EditText et_Name;
    Button bt_save_details;
    private static final int GALLERY_REQUEST_CODE = 3;
    ImageButton ib_add_profpic;
    private static final int MULTI_PERMISSION_REQUEST_CODE = 1;
    String saveData = "";
    String str_Year_of_SCC = "";
    Spinner spin_ssc_year;
    boolean kyc_on = false;
    private Uri uri_img = null;
    ProgressDialog pd_addprofile;
    private StorageReference storageRef;
    private DatabaseReference rootRef;
    private FirebaseDatabase database ;
    private String timeStamp="";
    protected float MAX_IMAGE_SIZE=150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(getApplicationContext(), "Enter your details", Toast.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
        checkAppPermissions();
        requestPermission();
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference("alumn_list");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://zphschoolemani-d3d13.appspot.com");
        setContentView(R.layout.activity_login_page);
        timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        et_phone = findViewById(R.id.et_phno);
        et_Name = findViewById(R.id.et_Name);
        ib_add_profpic=findViewById(R.id.ib_add_prof_pic);
        pd_addprofile = new ProgressDialog(this);
        ib_add_profpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "Select pic to upload", Toast.LENGTH_LONG).show();
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), GALLERY_REQUEST_CODE);
            }
        });
        //et_SSC_Year=findViewById(R.id.et_Name);;
        spin_ssc_year = findViewById(R.id.spinner_ssc);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spin_SSC_year_values, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spin_ssc_year.setAdapter(adapter);
        spin_ssc_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text
                    Log.d("Debug", "sowmuch ssc year selected: "+selectedItemText);
                    str_Year_of_SCC = selectedItemText;
                } else {

                    //
                    str_Year_of_SCC = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                str_Year_of_SCC = "";
            }
        });

        bt_save_details = findViewById(R.id.button);
        bt_save_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!et_phone.getText().toString().equals("")) && (!et_Name.getText().toString().equals("")) &&
                        (!str_Year_of_SCC.equals(""))) {
                    //Write user details to DB
                    // Write a message to the database

                    Toast.makeText(getApplicationContext(), "Write user details to DB", Toast.LENGTH_SHORT).show();
                    try{
                        final String phoneNumber = et_phone.getText().toString();


                    final String profpic_downloadable_URL = "";
                    pd_addprofile.setTitle("Saving your Profile...");
                    pd_addprofile.show();

                    final String randomString = UUID.randomUUID().toString();
                     StorageReference ref = storageRef.child("profilepics/" + randomString);
                        saveData = et_phone.getText() + "#" + et_Name.getText();
                    if (uri_img != null && !uri_img.equals("")) {
                        Toast.makeText(getApplicationContext(), "upload started " + et_Name.getText() + "", Toast.LENGTH_LONG).show();

                        ref.putFile(uri_img)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        try {
                                            if (pd_addprofile != null && pd_addprofile.isShowing()) {
                                                pd_addprofile.dismiss();
                                            }
                                            Log.d("Debug", "sowmuch: starting upload");
                                            Toast.makeText(getApplicationContext(), "upload success " + et_Name.getText() + "", Toast.LENGTH_LONG).show();
                                            Uri profpic_downloadable_URL;
                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!urlTask.isSuccessful()) ;
                                            profpic_downloadable_URL = urlTask.getResult();
                                            String location_XY=getLocationDetails();
                                            Log.d("Debug", "sowmuch: profpic_downloadable_URL is ready ="+profpic_downloadable_URL);
                                             DatabaseReference usersRef = rootRef.child(phoneNumber);
                                            //myRef.setValue(et_Name.getText().toString(),et_SSC_Year.getText().toString());
                                            Map<String, Object> taskMap = new HashMap<>();
                                            //taskMap.put("PhNo",phoneNumber);

                                            taskMap.put("fullname", et_Name.getText().toString());
                                            taskMap.put("sscyear", str_Year_of_SCC);
                                            taskMap.put("profpicurl", profpic_downloadable_URL.toString());
                                            taskMap.put("timestamp",timeStamp);
                                            usersRef.updateChildren(taskMap);
                                            Log.d("Debug", "sowmuch: db update is done ="+taskMap.toString());
                                            Toast.makeText(getApplicationContext(), "Thank you " + et_Name.getText() + "", Toast.LENGTH_LONG).show();


                                        } catch (Exception ex) {
                                            Toast.makeText(getApplicationContext(), "sowmuch exception in creating your profile" + ex.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd_addprofile.dismiss();
                                        Toast.makeText(getApplicationContext(), "Failed to load your profile pin into database " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                .getTotalByteCount());
                                        Log.d("Debug", "sowmuch: uploading in progress="+progress);
                                        pd_addprofile.setMessage("Uploading profile" + (int) progress + "%");
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    Log.d("Debug", "sowmuch: task is fully completed");

                            }
                        });
                        markKYCDone();
                        finish();

                    } else {

                        DatabaseReference usersRef = rootRef.child(phoneNumber);
                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put("fullname", et_Name.getText().toString());
                        taskMap.put("sscyear", str_Year_of_SCC);
                        taskMap.put("profpicurl", "");
                        taskMap.put("timestamp",timeStamp);
                        usersRef.updateChildren(taskMap);
                        Toast.makeText(getApplicationContext(), "Thank you " + et_Name.getText() + "", Toast.LENGTH_LONG).show();
                        markKYCDone();
                        finish();
                    }
                } catch (Exception ex) {
                        Log.d("Debug", "sowmuch2: exception"+ex.getMessage());
                    Toast.makeText(getApplicationContext(), "sowmuch exception in creating your profile" + ex.getMessage(), Toast.LENGTH_LONG).show();

                }

                } else {
                    Toast.makeText(getApplicationContext(), "Dear  " + et_Name.getText() + ", Please enter all details.", Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    private String getLocationDetails() {
        String location_XY="";
        //TODO write code to get locationdetails..

        return  location_XY;
    }

    @Override
    protected void onDestroy() {
        pd_addprofile.dismiss();
        super.onDestroy();
    }
    private void markKYCDone() {
        // MainActivity mainActivity = new MainActivity();
        //mainActivity.checkAppPermissions();
        File sd = Environment.getExternalStorageDirectory();

        if (sd.canWrite() && sd.isDirectory()) {
            String FileName = "zphschool_" + et_phone.getText().toString() + "_" + et_Name.getText().toString() + "_" + str_Year_of_SCC + ".txt";
            File fileKYC = new File(sd, FileName);
            try {
                fileKYC.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestPermission() {
        //TODO write code request permissions for location
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION}, MULTI_PERMISSION_REQUEST_CODE);
    }

    protected void checkAppPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {

            //Check whether your app has access to the READ permission//
            if (checkPermission()) {
                //If your app has access to the device’s storage, then print the following message to Android Studio’s Logcat//
                Log.d("Write Permission", "Permission already granted.");
                //   Toast.makeText(getApplicationContext(),"Permission already granted",Toast.LENGTH_LONG).show();
            } else {
                //If your app doesn’t have permission to access external storage, then call requestPermission//
                requestPermission();
            }
        } else {
            Toast.makeText(getApplicationContext(), "check app permission not done", Toast.LENGTH_LONG).show();
        }

    }
    public boolean checkPermission() {
        //Check for READ_EXTERNAL_STORAGE access, using ContextCompat.checkSelfPermission()//
        int result = ContextCompat.checkSelfPermission(LoginPage.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Toast.makeText(getApplicationContext(), "checking permissions.. "+result, Toast.LENGTH_LONG).show();
        //If the app does have this permission, then return true//
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            //If the app doesn’t have this permission, then return false//
            return false;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //image from gallery result
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            uri_img = data.getData();
            ib_add_profpic.setImageURI(uri_img);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri_img);
                bitmap=adjustImage(bitmap,MAX_IMAGE_SIZE,true);
                // Log.d(TAG, String.valueOf(bitmap));

                ib_add_profpic.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error in selecting image, please try again", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }
    }
    /**
     * This class used to reduced size of image
     * @param realImage
     * @return
     */
    protected Bitmap adjustImage(Bitmap realImage,float maxImageSize,
                                 boolean filter) {
        Log.d("Debug", "sowmuch-loginpage: realImage.getWidth()"+realImage.getWidth());
        Log.d("Debug", "sowmuch-loginpage:  realImage.getHeight()"+ realImage.getHeight());
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}
