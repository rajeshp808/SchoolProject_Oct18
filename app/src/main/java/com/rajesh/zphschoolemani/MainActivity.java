package com.rajesh.zphschoolemani;

import android.*;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static String CHANNEL_ID="ZPHSchoolEmani";
    public static String CHANNEL_Name="ZPHSchool Emani";
    public static String CHANNEL_Desc="ZPHSchool Emani Notification";

    private static final int PERMISSION_REQUEST_CODE = 1;
    ImageButton ib_alumn;
    ImageButton ib_announcement;
    ImageButton ib_news;
    ImageButton ib_gallery;
    ImageButton ib_location;
    private static  String phone_key="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            checkAppPermissions();
            requestPermission();

            //check for Internet and then login action
            if (haveNetworkConnection() && !isKYCReq()) {
                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Welcome to ZPH School alumni", Toast.LENGTH_LONG).show();
            }

            ib_alumn = findViewById(R.id.ib_alumn);
            ib_announcement = findViewById(R.id.ib_announcement);
            ib_news = findViewById(R.id.ib_news);
            ib_gallery = findViewById(R.id.ib_gallery);
            ib_location = findViewById(R.id.ib_Location);

            ib_alumn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.this, alumnhome.class);
                    intent.putExtra("phone_key", phone_key);
                    startActivity(intent);
                }
            });

            ib_announcement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Announcements starting", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, viewannouncement.class));
                }
            });
            ib_news.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "News starting", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, viewnews.class));
                }
            });
            ib_gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, Gallery.class));
                    //Toast.makeText(getApplicationContext(), "Gallery pics to be added", Toast.LENGTH_LONG).show();
                }
            });
            ib_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chooser = null;
                    Intent intent = null;
                    String latitude = "16.3264";
                    String longitude = "80.6560";
                    intent = new Intent(Intent.ACTION_VIEW);
                    // intent.setData(Uri.parse("geo:16.3264,80.6560?z=16"));
                    intent.setData(Uri.parse("geo:0,0?q=16.3264,80.6560(ZPH School Emani)"));
                    intent.setPackage("com.google.android.apps.maps");

                    //chooser=Intent.createChooser(intent,"Launch Maps");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error in launching maps application", Toast.LENGTH_LONG).show();
                    }


                    //Toast.makeText(getApplicationContext(), "Location to be added", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex) {
            Log.d("Debug", "sowmuch-onCreate: exception: "+ex.getMessage());
            Toast.makeText(getApplicationContext(), "Exception raised in my APP" + ex.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }


    }

    private boolean isKYCReq() {
        boolean isfile_existed = false;
        try {
            File sd = Environment.getExternalStorageDirectory();
            String Fileprefix = "zphschool_";
            File fileKYC = new File(sd, Fileprefix);
            if (sd.isDirectory()) {
                File[] allfiles = sd.listFiles();

                for (File file : allfiles) {
                    if (file.getName().startsWith("zphschool_")) {
                        isfile_existed = true;
                        String split_filename[] = file.getName().split("_");
                        if(split_filename.length>1)
                            phone_key=split_filename[1];

                        // Toast.makeText(getApplicationContext(), "File found", Toast.LENGTH_LONG).show();
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

    /**
     * Check for network connection
     *
     * @return
     */
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                if (ni.isConnected())
                    haveConnectedWifi = true;
                //  Toast.makeText(getApplicationContext(),"haveConnectedWifi "+haveConnectedWifi,Toast.LENGTH_LONG).show();
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected())
                    haveConnectedMobile = true;
                //    Toast.makeText(getApplicationContext(),"haveConnectedMobile "+haveConnectedMobile,Toast.LENGTH_LONG).show();

            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    //Check whether the app is installed on Android 6.0 or higher//
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
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
       // Toast.makeText(getApplicationContext(), "checking permissions.. "+result, Toast.LENGTH_LONG).show();
        //If the app does have this permission, then return true//
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            //If the app doesn’t have this permission, then return false//
            return false;
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_Exit) {
            Toast.makeText(MainActivity.this, "Exiting Applicattion", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        } else if (id == R.id.menu_Help) {
            //startActivity(new Intent(MainActivity.this,Helper.class));
            Toast.makeText(MainActivity.this, "This application HELP menu. ", Toast.LENGTH_LONG).show();

        } else if (id == R.id.menu_About) {
            Toast.makeText(MainActivity.this, "This application is designed for Alumni of Z.P.High School Emani." +
                    " You will find latest news and announcements from the school. Designed & Devloped by Rajesh Perakalapudi.", Toast.LENGTH_LONG).show();
        }  else if(id ==R.id.menu_add_newspost) {
            Intent news_intent = new Intent(MainActivity.this, postnews.class);
            startActivity(news_intent);
        } else if(id== R.id.menu_add_announcement) {
            Intent ann_intent = new Intent(MainActivity.this, add_announcement.class);
            startActivity(ann_intent);
        }


        return super.onOptionsItemSelected(item);
    }

}
 


